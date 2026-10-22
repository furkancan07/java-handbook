package org.example.spring.errorhandling;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.lang.reflect.Field;
import java.net.URI;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessValidationException.class)
    ProblemDetail handler(BusinessValidationException ex){
       ProblemDetail detail=ProblemDetail.forStatusAndDetail(ex.getStatus(),ex.getMessage());
       detail.setProperty("timestamp", Instant.now());
       return detail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail validationHandler(MethodArgumentNotValidException ex){
        Map<String,String> map=new HashMap<>();
        for(FieldError error : ex.getFieldErrors()){
            map.put(error.getField(),error.getDefaultMessage());
        }
        ProblemDetail detail=ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,"Validation Failed");
        detail.setProperty("errors",map);
        detail.setTitle("Validation Exception");
        detail.setProperty("timestamp",Instant.now());
        return detail;
    }
}
