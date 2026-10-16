package org.example.spring.idempotency.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.spring.idempotency.entity.IdempotencyRecord;
import org.example.spring.idempotency.repository.IdempotencyRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Method;

/**
 * daha önce bunu service üzerinden yaptık ama best practise aop tabanlı yapmak
 * */
@Aspect
@Component
@RequiredArgsConstructor
public class IdempotencyAspect {
    private final IdempotencyRepository repo;
    private final ObjectMapper mapper;
    private final TransactionTemplate tx; // transactionalı kendimiz yönetcez

    /**
     * burada ne yapıyoz anlatayım :
     *  headerdan keyi aldık bunu save etmeye çalışınca eğer bu varsa 0 döner 0 dönerse replaya düşer
     *  replay da mevcut kayıd ne dönmüşe bunu döner yoksa anatasyonun çalıştığı ilgili method un döndüğü cevabı döner
     *  gerekli apite sadece Idempotent anatasyonunun koyulması yeter
     * */
    @Around("@annotation(idempotent)")
    public Object around(ProceedingJoinPoint jp,Idempotent idempotent) throws Throwable{
        String key=currentKey();
        Method method=((MethodSignature) jp.getSignature()).getMethod();
        JavaType type=mapper.getTypeFactory().constructType(method.getGenericReturnType());
        try {
         return tx.execute(s->{
             if(repo.insertIfAbsent(key)==0){
                 return replay(key,type);
             }
             Object result;
             try {
                 result=jp.proceed();
             }catch (Throwable t){
                 throw new RuntimeException();
             }

             repo.markCompleted(key,toJson(result));
             return result;
         });
        }catch (RuntimeException ex){
            throw ex.getCause();
        }
    }

    private Object replay(String key, JavaType type) {
        IdempotencyRecord record=repo.findByIdempotencyKey(key).orElseThrow();
        try {
            return mapper.readValue(record.getResponseBody(),type);
        }catch(JsonProcessingException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private String currentKey(){
        String key=getRequests().getHeader("Idempotency-Key");
        if(key==null || key.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Idempotency-Key header is required");
        }
        return key;
    }
    private HttpServletRequest getRequests(){
          return (HttpServletRequest) RequestContextHolder.currentRequestAttributes();
    }
    private String toJson(Object result){
        try {
            return mapper.writeValueAsString(result);
        }catch(JsonProcessingException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
