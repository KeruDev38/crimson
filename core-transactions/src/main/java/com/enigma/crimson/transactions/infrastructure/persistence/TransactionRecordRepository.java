package com.enigma.crimson.transactions.infrastructure.persistence;

import com.enigma.crimson.transactions.domain.transaction.TransactionRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface TransactionRecordRepository extends JpaRepository<TransactionRecord, UUID> {

    @Query("""
            select transaction
            from TransactionRecord transaction
            where transaction.senderAccountId = :accountId
               or transaction.receiverAccountId = :accountId
            order by transaction.createdAt desc
            """)
    Page<TransactionRecord> findByAccountId(@Param("accountId") UUID accountId, Pageable pageable);
}
