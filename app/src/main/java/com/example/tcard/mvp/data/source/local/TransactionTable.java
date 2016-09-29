package com.example.tcard.mvp.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.tcard.mvp.utils.Transaction;
import com.example.tcard.mvp.data.source.TransactionsTableInterface;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sshirima on 9/7/16.
 */
public class TransactionTable implements TransactionsTableInterface {

    private static TransactionTable transactionTable;
    private SQLiteDatabase database;
    private TcardDB tcardDB;

    //Prevent direct instantiation
    public TransactionTable(Context context){

        if (context != null){
            this.tcardDB = new TcardDB(context);
        }else {
            throw new NullPointerException("instantiate TransactionTable: AppContext cannot be null");
        }
    }

    public static TransactionTable getInstance(Context context){
        if (transactionTable == null){
            transactionTable = new TransactionTable(context);
        }
        return transactionTable;
    }

    public void open() throws SQLException{
        database = tcardDB.getWritableDatabase();
    }

    public void close() {
        tcardDB.close();
    }

    @Override
    public void getTransactions(LoadTransactionsCallback callback) {
        List<Transaction> transactions = new ArrayList<Transaction>();

        String[] columns = {
                Consts.TRANSACTION_ID,
                Consts.TRANSACTION_AMOUNT,
                Consts.TRANSACTION_TIME,
                Consts.TRANSACTION_ACCOUNT,
                Consts.TRANSACTION_DESCRIPTION,
                Consts.TRANSACTION_CURRENCY
        };

        Cursor cursor = database.query(Consts.TRANSACTION_TABLE,columns,null,null, null,null,null,null);

        if (cursor != null && cursor.getCount() >0){
            cursor.moveToFirst();

            while (cursor.moveToNext()){
                String id = cursor.getString(cursor.getColumnIndexOrThrow(Consts.TRANSACTION_ID));
                float amount = cursor.getFloat(cursor.getColumnIndexOrThrow(Consts.TRANSACTION_AMOUNT));
                long timeInMills = cursor.getLong(cursor.getColumnIndexOrThrow(Consts.TRANSACTION_TIME));
                int accountId = cursor.getInt(cursor.getColumnIndexOrThrow(Consts.TRANSACTION_ACCOUNT));
                int descriptionId = cursor.getInt(cursor.getColumnIndexOrThrow(Consts.TRANSACTION_DESCRIPTION));
                String currencyCode = cursor.getString(cursor.getColumnIndexOrThrow(Consts.TRANSACTION_CURRENCY));

                Transaction transaction = new Transaction(id,amount,timeInMills,accountId,descriptionId,currencyCode);
                transactions.add(transaction);
            }
        }

        if (cursor == null){
            cursor.close();
        }

        if (transactions.isEmpty()){
            //This will be called if the table is new or just empty.
            callback.onDataNotAvailable();
        } else{
            callback.onTrasactionsLoaded(transactions);
        }
    }

    @Override
    public void getTransaction(String transactionId, GetTransactionCallback callback) {
        String[] columns = {
                Consts.TRANSACTION_ID,
                Consts.TRANSACTION_AMOUNT,
                Consts.TRANSACTION_TIME,
                Consts.TRANSACTION_ACCOUNT,
                Consts.TRANSACTION_DESCRIPTION,
                Consts.TRANSACTION_CURRENCY
        };

        String selection = Consts.TRANSACTION_ID+ " LIKE ?";
        String[] selectionArgs = {transactionId};

        Cursor cursor = database.query(Consts.TRANSACTION_TABLE, columns, selection, selectionArgs, null, null,null);

        Transaction transaction = null;

        if (cursor != null && cursor.getCount() >0){
            cursor.moveToFirst();
            String id = cursor.getString(cursor.getColumnIndexOrThrow(Consts.TRANSACTION_ID));
            float amount = cursor.getFloat(cursor.getColumnIndexOrThrow(Consts.TRANSACTION_AMOUNT));
            long timeInMills = cursor.getLong(cursor.getColumnIndexOrThrow(Consts.TRANSACTION_TIME));
            int accountId = cursor.getInt(cursor.getColumnIndexOrThrow(Consts.TRANSACTION_ACCOUNT));
            int descriptionId = cursor.getInt(cursor.getColumnIndexOrThrow(Consts.TRANSACTION_DESCRIPTION));
            String currencyCode = cursor.getString(cursor.getColumnIndexOrThrow(Consts.TRANSACTION_CURRENCY));

            transaction = new Transaction(id,amount,timeInMills,accountId,descriptionId,currencyCode);
        }

        if (transaction != null){
            callback.onTransactionLoaded(transaction);
        } else{
            callback.onDataNotAvailable();
        }
    }

    @Override
    public void saveTransaction(Transaction transaction) {
        if (transaction != null){
            ContentValues values = new ContentValues();
            values.put(Consts.TRANSACTION_ID, transaction.getId());
            values.put(Consts.TRANSACTION_AMOUNT, transaction.getAmount());
            values.put(Consts.TRANSACTION_TIME, transaction.getTimeInMills());
            values.put(Consts.TRANSACTION_ACCOUNT, transaction.getAccountId());
            values.put(Consts.TRANSACTION_DESCRIPTION, transaction.getDescriptionId());
            values.put(Consts.TRANSACTION_CURRENCY, transaction.getCurrencyCode());

            database.insert(Consts.TRANSACTION_TABLE, null, values);

        } else {
            throw new NullPointerException("insert Transaction: Transaction cannot be null");
        }
    }

    @Override
    public void updateTransaction(Transaction transaction) {
        if (transaction != null){
            ContentValues values = new ContentValues();
            values.put(Consts.TRANSACTION_AMOUNT, transaction.getAmount());
            values.put(Consts.TRANSACTION_TIME, transaction.getTimeInMills());
            values.put(Consts.TRANSACTION_ACCOUNT, transaction.getAccountId());
            values.put(Consts.TRANSACTION_DESCRIPTION, transaction.getDescriptionId());
            values.put(Consts.TRANSACTION_CURRENCY, transaction.getCurrencyCode());

            String selection = Consts.TRANSACTION_ID+" LIKE ?";
            String[] selectionArgs = {transaction.getId()};

            database.update(Consts.TRANSACTION_TABLE, values, selection, selectionArgs);

        }else{
            throw new NullPointerException("update Transaction: Transaction cannot be null");
        }
    }

    @Override
    public void refreshTransactions() {

    }

    @Override
    public void deleteAllTransactions() {
        database.delete(Consts.TRANSACTION_TABLE, null, null);
    }

    @Override
    public void deleteTransaction(String transactionId) {
        String selection = Consts.TRANSACTION_ID + " LIKE ?";
        String[] selectionArgs = {transactionId};

        database.delete(Consts.TRANSACTION_TABLE, selection, selectionArgs);
    }
}
