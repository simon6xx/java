package com.lxx.custom;

import org.springframework.stereotype.Component;
import org.springframework.transaction.*;
import org.springframework.transaction.support.DefaultTransactionStatus;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class SelfTransactionManager implements PlatformTransactionManager {
    private ConcurrentHashMap<TransactionStatus, AtomicInteger> store = new ConcurrentHashMap<>();

    // 开启事务
    public TransactionStatus begin() {
        TransactionStatus transactionStatus = new DefaultTransactionStatus(new Object(), false, false, false, false, null);
        store.put(transactionStatus, new AtomicInteger(0));
        return transactionStatus;
    }

    @Override
    public TransactionStatus getTransaction(TransactionDefinition definition) throws TransactionException {
        TransactionStatus transactionStatus = new DefaultTransactionStatus(new Object(), false, false, false, false, null);
        store.put(transactionStatus, new AtomicInteger(0));
        return transactionStatus;
    }

    @Override
    public void commit(TransactionStatus status) throws TransactionException {
        AtomicInteger value = store.get(status);
        if (value != null && value.get() == 0) {
            store.remove(status);
        } else {
            throw new UnexpectedRollbackException("Cannot commit. Transaction is already rolled back.");
        }
    }

    @Override
    public void rollback(TransactionStatus status) throws TransactionException {
        AtomicInteger value = store.get(status);
        if (value != null) {
            value.set(1);
        } else {
            throw new NoTransactionException("No transaction associated with current thread.");
        }
    }
}
