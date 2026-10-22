package org.example.spring;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;

@MappedSuperclass
@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column( nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column( nullable = false)
    private OffsetDateTime updatedAt;



    @PrePersist
    public void prePersist() {
        createdAt = OffsetDateTime.now();
        updatedAt=OffsetDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt=OffsetDateTime.now();
    }
}
