package org.example.spring.idempotency.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.spring.idempotency.enums.Status;

import java.time.Instant;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdempotencyRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true)
    private String idempotencyKey;
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(columnDefinition = "text")
    private String responseBody;

    private Instant createdAt;

    @PrePersist
    public void prePersist(){
        this.createdAt=Instant.now();
    }




}
