package org.example.spring.idempotency.repository;

import org.example.spring.idempotency.entity.IdempotencyRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface IdempotencyRepository extends JpaRepository<IdempotencyRecord,Long> {
    Optional<IdempotencyRecord> findByIdempotencyKey(String key);
    void deleteByCreatedAtBefore(Instant date);
    @Modifying
    @Query(value = """
            insert into idempotency_record (idempotency_key, status, created_at)
            values (:key, 'PROCESSING', now())
            on conflict (idempotency_key) do nothing
            """, nativeQuery = true)
    int insertIfAbsent(@Param("key") String key);

    @Modifying
    @Query(value = """
            update idempotency_record
               set status = 'COMPLETED',
                   response_body = :body
             where idempotency_key = :key
            """, nativeQuery = true)
    int markCompleted(@Param("key") String key, @Param("body") String body);

}
