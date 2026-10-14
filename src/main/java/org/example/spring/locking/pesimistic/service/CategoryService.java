package org.example.spring.locking.pesimistic.service;

import lombok.RequiredArgsConstructor;
import org.example.spring.locking.pesimistic.model.Category;
import org.example.spring.locking.pesimistic.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;




    @Transactional
    public Category findById(Long id) {
        return categoryRepository.findById(id).orElseThrow();
    }

}
