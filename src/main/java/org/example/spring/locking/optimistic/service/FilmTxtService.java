package org.example.spring.locking.optimistic.service;

import org.example.spring.locking.optimistic.model.Film;
import org.example.spring.locking.optimistic.repository.FilmRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FilmTxtService {
    private final FilmRepository filmRepository;

    public FilmTxtService(FilmRepository filmRepository) {
        this.filmRepository = filmRepository;
    }

    @Transactional
    public Film updateName(Long id, String name) {
        Film film = filmRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Film yok: " + id));
        film.setName(name);
        return film;
    }
}
