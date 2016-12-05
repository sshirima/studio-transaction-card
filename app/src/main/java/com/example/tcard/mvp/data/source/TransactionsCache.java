package com.example.tcard.mvp.data.source;

import com.example.tcard.mvp.data.Transaction;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sshirima on 9/7/16.
 * Concrete implementation to load transaction(s) from the data source into a cache
 */
public class TransactionsCache implements  TransactionsTableInterface{

    private static TransactionsCache INSTANCE = null;

    private TransactionsTableInterface localTransactionTable;

    /**
     *Make cache as invalid, to force an update the next time data is requested
     */
    boolean cacheisDirty = false;


    Map<String, Transaction> cachedTransactions;

    private TransactionsCache(TransactionsTableInterface localTransactionTable){
        this.localTransactionTable = localTransactionTable;

    }

    /**
     * Return single instance of this class, creating it if necessary
     *
     * @param localTransactionTable
     * @return the {@link TransactionsCache} instance
     */

    public static TransactionsCache getInstance(TransactionsTableInterface localTransactionTable){

        if (INSTANCE == null){
            INSTANCE = new TransactionsCache(localTransactionTable);
        }
        return INSTANCE;
    }

    /**
     * Used to force {@link #getInstance(TransactionsTableInterface)} to create new isntance next time
     * is called
     */
    public static void destroyInstance(){ INSTANCE = null;}

    /**
     * Gets tasks from cache, local data source (SQLiteDatabase) or remote database, which ever is
     * available first
     * @param callback
     *
     */
    @Override
    public void getTransactions(final LoadTransactionsCallback callback) {

        if (callback != null){
            //If cache is empty, look at the local source
            if (cachedTransactions == null && cacheisDirty){
                //Query local storage if available
                localTransactionTable.getTransactions(new LoadTransactionsCallback() {
                    @Override
                    public void onTrasactionsLoaded(List<Transaction> transactions) {
                        refreshCache(transactions);
                        callback.onTrasactionsLoaded(new ArrayList<Transaction>(cachedTransactions.values()));
                    }
                    @Override
                    public void onDataNotAvailable() {
                        //Nothing for now
                    }
                });
                return;
            }

            //Respond immediately to cache if available and not dirty
            if (cachedTransactions != null && !cacheisDirty){
                callback.onTrasactionsLoaded(new ArrayList<Transaction>(cachedTransactions.values()));
                return;
            }

            if (cacheisDirty){
                //If cache is dirty we need to fetch new data from the network
                localTransactionTable.getTransactions(new LoadTransactionsCallback() {
                    @Override
                    public void onTrasactionsLoaded(List<Transaction> transactions) {
                        refreshCache(transactions);
                        callback.onTrasactionsLoaded(new ArrayList<Transaction>(cachedTransactions.values()));
                    }

                    @Override
                    public void onDataNotAvailable() {
                        //Nothing for now
                    }
                });
            } else {
                //Query local storage if available, if not query the network

            }

        }else{
            throw new NullPointerException("TransactionDataRepository@getTransactions: Callback cannot be null");}

    }

    private void refreshCache(List<Transaction> transactions){
        if (cachedTransactions == null){
            cachedTransactions = new LinkedHashMap<String, Transaction>();
        }
        cachedTransactions.clear();
        for(Transaction transaction: transactions){
            cachedTransactions.put(transaction.getId(), transaction);
        }
        cacheisDirty = false;
    }

    /**
     * Get transactions from local data source (sqlite) unless the table is new or empty. In that case
     * it uses the network data source
     * @param transactionId
     * @param callback
     */
    @Override
    public void getTransaction(String transactionId, final GetTransactionCallback callback) {
        if (transactionId== null || callback == null){
            throw new NullPointerException("TransactionsRepository#getTransaction:" +
                    "transactionId or/and callback cannot be null");
        } else {
            Transaction cachedTransaction = getTransactionWithId(transactionId);

            //Respond immediately with cache if available
            if (cachedTransaction != null){
                callback.onTransactionLoaded(cachedTransaction);
                return;
            }

            //Load from server, persisted if needed

            //Is transaction on the local database? If not, query the network
            localTransactionTable.getTransaction(transactionId, new GetTransactionCallback() {
                @Override
                public void onTransactionLoaded(Transaction transaction) {
                    callback.onTransactionLoaded(transaction);
                }

                @Override
                public void onDataNotAvailable() {
                    callback.onDataNotAvailable();
                }
            });
        }
    }


    @Override
    public void saveTransaction(Transaction transaction) {
        if (transaction != null){
            localTransactionTable.saveTransaction(transaction);

            //Do in memory cache update to keep the app UI up to date
            if (cachedTransactions == null){
                cachedTransactions = new LinkedHashMap<String, Transaction>();
            }

            cachedTransactions.put(transaction.getId(), transaction);

        }else {
            throw new NullPointerException("TransactionsRepository#saveTransactions: transaction" +
                    "cannot be null");
        }
    }

    @Override
    public void updateTransaction(Transaction transaction) {
            if (transaction != null){
                localTransactionTable.updateTransaction(transaction);

                //Do in memory cache update to keep the app UI up to date
                if (cachedTransactions == null){
                    cachedTransactions = new LinkedHashMap<String, Transaction>();
                }

                cachedTransactions.put(transaction.getId(), transaction);

            }else{
                throw new NullPointerException("TransactionsRepository#updateTransactions: transaction" +
                        "cannot be null");
            }
    }

    @Override
    public void refreshTransactions() {
        cacheisDirty = true;
    }

    @Override
    public void deleteAllTransactions() {

    }

    @Override
    public void deleteTransaction(String transactionId) {

        localTransactionTable.deleteTransaction(transactionId);

        cachedTransactions.remove(transactionId);
    }

    private Transaction getTransactionWithId(String id) {
        if (cachedTransactions == null || cachedTransactions.isEmpty()){
            return null;
        } else {
            return cachedTransactions.get(id);
        }
    }
}
