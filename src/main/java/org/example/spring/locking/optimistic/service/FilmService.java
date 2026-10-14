package org.example.spring.locking.optimistic.service;

import org.example.spring.locking.optimistic.model.Film;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class FilmService {
    private final FilmTxtService txtService;

    public FilmService(FilmTxtService txtService) {
        this.txtService = txtService;
    }
    @Retryable(
            retryFor = RuntimeException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 50,multiplier = 2,random = true)
    )
    public Film updateName(Long id, String name) {
        return txtService.updateName(id, name);
    }
    @Recover
    public Film recover(RuntimeException e, Long id, String name) {
        System.out.println("Film başka biri tarafından güncellendi: " + e.getMessage());
        return null;
    }
}
