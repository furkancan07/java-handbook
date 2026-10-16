package org.example.spring.idempotency.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.spring.errorhandling.BusinessValidationException;
import org.example.spring.errorhandling.BusinessValidationRule;
import org.example.spring.idempotency.entity.IdempotencyRecord;
import org.example.spring.idempotency.enums.Status;
import org.example.spring.idempotency.repository.IdempotencyRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.function.Supplier;

/**
 * burada aynı anda yapılan opara transferi konusunu yapcaz
 * işte functional inteface supplier vs de var o reposda var bakarsın
 * onun dışında idempotency deddiğimiz şey para gönder senaryousu üzüerinden
 * yapcaksak şu demek : aynı anda 2 defa para göndere tıkladık diyelim 1 tanesi işlenmeli diğeri işlenmemeli
 *
 * gayet güzel bir yapı kurduk ama best practise bunu aop tabanlı yapmak
 * */
@Service
@RequiredArgsConstructor
public class IdempotencyService {
    private final IdempotencyRepository repository;
    private final ObjectMapper mapper;
   /**
    * burada kısaca ne yaptık anlatalım bu key bize headerdan geliyor
    * diyekim ki aynı key geldi zaten save yapamayacak replaya düşecek
    * replaya düşerse eğer ki process deemek ki daha bitmemiş işleniyor
    * completed ise json olarak responsu dönüyoz zaten önceki recordan kalan
    * asıl iş actiom.get de
    * */
    public <T> T execute(String key, Class<T> type, Supplier<T> action){
        IdempotencyRecord record=IdempotencyRecord.builder().idempotencyKey(key).status(Status.PROCESSING).build();

        try {
            repository.saveAndFlush(record); // baba flush persistence contexe direk gönderdik hızlı kayıt
        }catch (DataIntegrityViolationException e){
            return replay(key,type);
        }
        T result;
        try{
            result=action.get();
        }catch (RuntimeException exception){
            repository.delete(record);
            throw  exception;
        }
        record.setStatus(Status.COMPLETED);
        record.setResponseBody(toJson(result));
        repository.save(record);
        return result;

    }

    private <T> T replay(String key, Class<T> type) {
        IdempotencyRecord record=repository.findByIdempotencyKey(key).orElseThrow(()-> new BusinessValidationException(BusinessValidationRule.NOT_FOUND));
        if(record.getStatus()==Status.PROCESSING){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Request is still being processed");
        }

        try{
            return mapper.readValue(record.getResponseBody(),type);
        }catch (JsonProcessingException exception){
            throw new RuntimeException(exception.getMessage());
        }


    }

    private String toJson(Object o) {
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }
}
