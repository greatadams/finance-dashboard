package com.pm.greatadamu.transactionservice.service;


import com.pm.greatadamu.transactionservice.model.Transaction;
import com.pm.greatadamu.transactionservice.model.TransactionStatus;
import com.pm.greatadamu.transactionservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionPersistenceService {

    private final TransactionRepository transactionRepository;

    // ✅ FIX: save PENDING in its own transaction
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Transaction savePending(Transaction transaction) {
        transaction.setTransactionStatus(TransactionStatus.PENDING);
        transaction.setTransactionDate(LocalDateTime.now());
        return transactionRepository.save(transaction);
    }

    // ✅ FIX: mark COMPLETED in its own transaction
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Transaction markCompleted(Transaction tx) {
        tx.setTransactionStatus(TransactionStatus.COMPLETED);
        tx.setTransactionDate(LocalDateTime.now());
        return transactionRepository.save(tx);
    }

    // ✅ FIX: mark FAILED in its own transaction
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Transaction markFailed(Transaction tx, String reason) {
        tx.setTransactionStatus(TransactionStatus.FAILED);
        tx.setTransactionDate(LocalDateTime.now());
        // (Optional) store reason somewhere if you have a column for it
        return transactionRepository.save(tx);
    }
}

