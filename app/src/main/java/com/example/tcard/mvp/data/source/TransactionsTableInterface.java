package com.example.tcard.mvp.data.source;

import com.example.tcard.mvp.data.Transaction;

import java.util.List;

/**
 * Created by sshirima on 9/6/16.
 */
public interface TransactionsTableInterface {

    interface LoadTransactionsCallback{
        void onTrasactionsLoaded(List<Transaction> transactions);
        void onDataNotAvailable();
    }

    interface GetTransactionCallback{
        void onTransactionLoaded(Transaction transaction);
        void onDataNotAvailable();
    }

    void getTransactions(LoadTransactionsCallback callback);

    void getTransaction(String transactionId, GetTransactionCallback callback);

    void saveTransaction(Transaction transaction);

    void updateTransaction(Transaction transaction);

    void refreshTransactions();

    void deleteAllTransactions();

    void deleteTransaction(String transactionId);
}
