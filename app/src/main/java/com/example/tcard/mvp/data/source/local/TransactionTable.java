package com.example.tcard.mvp.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.tcard.mvp.data.Account;
import com.example.tcard.mvp.data.Description;
import com.example.tcard.mvp.data.Transaction;
import com.example.tcard.mvp.data.source.TransactionsTableInterface;
import com.example.transactioncard.R;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

/**
 * Created by sshirima on 9/7/16.
 */
public class TransactionTable implements TransactionsTableInterface {

    private static TransactionTable transactionTable;
    private SQLiteDatabase database;
    private TcardDB tcardDB;
    private Context mContext;

    //Prevent direct instantiation
    public TransactionTable(Context context){

        if (context != null){
            this.tcardDB = new TcardDB(context);
        }else {
            throw new NullPointerException("instantiate TransactionTable: AppContext cannot be null");
        }
        this.mContext = context;
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

        /*String[] columns = {
                DBConsts.TRANSACTION_ID,
                DBConsts.TRANSACTION_AMOUNT,
                DBConsts.TRANSACTION_TIME,
                DBConsts.TRANSACTION_ACCOUNT,
                DBConsts.TRANSACTION_DESCRIPTION,
                DBConsts.TRANSACTION_CURRENCY
        };

        Cursor cursor = database.query(DBConsts.TRANSACTION_TABLE,columns,null,null, null,null,null,null);

        if (cursor != null && cursor.getCount() >0){
            cursor.moveToFirst();

            while (cursor.moveToNext()){
                String id = cursor.getString(cursor.getColumnIndexOrThrow(DBConsts.TRANSACTION_ID));
                float amount = cursor.getFloat(cursor.getColumnIndexOrThrow(DBConsts.TRANSACTION_AMOUNT));
                long timeInMills = cursor.getLong(cursor.getColumnIndexOrThrow(DBConsts.TRANSACTION_TIME));
                int accountId = cursor.getInt(cursor.getColumnIndexOrThrow(DBConsts.TRANSACTION_ACCOUNT));
                int descriptionId = cursor.getInt(cursor.getColumnIndexOrThrow(DBConsts.TRANSACTION_DESCRIPTION));
                String currencyCode = cursor.getString(cursor.getColumnIndexOrThrow(DBConsts.TRANSACTION_CURRENCY));

                Transaction transaction = new Transaction(id,amount,timeInMills,accountId,descriptionId,currencyCode);
                transactions.add(transaction);
            }
        }

        if (cursor == null){
            cursor.close();
        }*/

        //Temporarily load new transactions
        for (int i=0;i<10;i++){
            Description des = new Description("No description", mContext.getResources().getString(R.string.text_expense));
            double amount = (2200.0*2);
            Account account = new Account("Persaonal account");
            String id = UUID.randomUUID().toString();
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, i);
            Transaction transaction = new Transaction(id,amount, calendar.getTimeInMillis(),
            1, 1, "TSH");

            transaction.setDescription(des);
            transaction.setAccount(account);
            transactions.add(transaction);
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
                DBConsts.TRANSACTION_ID,
                DBConsts.TRANSACTION_AMOUNT,
                DBConsts.TRANSACTION_TIME,
                DBConsts.TRANSACTION_ACCOUNT,
                DBConsts.TRANSACTION_DESCRIPTION,
                DBConsts.TRANSACTION_CURRENCY
        };

        String selection = DBConsts.TRANSACTION_ID+ " LIKE ?";
        String[] selectionArgs = {transactionId};

        Cursor cursor = database.query(DBConsts.TRANSACTION_TABLE, columns, selection, selectionArgs, null, null,null);

        Transaction transaction = null;

        if (cursor != null && cursor.getCount() >0){
            cursor.moveToFirst();
            String id = cursor.getString(cursor.getColumnIndexOrThrow(DBConsts.TRANSACTION_ID));
            float amount = cursor.getFloat(cursor.getColumnIndexOrThrow(DBConsts.TRANSACTION_AMOUNT));
            long timeInMills = cursor.getLong(cursor.getColumnIndexOrThrow(DBConsts.TRANSACTION_TIME));
            int accountId = cursor.getInt(cursor.getColumnIndexOrThrow(DBConsts.TRANSACTION_ACCOUNT));
            int descriptionId = cursor.getInt(cursor.getColumnIndexOrThrow(DBConsts.TRANSACTION_DESCRIPTION));
            String currencyCode = cursor.getString(cursor.getColumnIndexOrThrow(DBConsts.TRANSACTION_CURRENCY));

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
            values.put(DBConsts.TRANSACTION_ID, transaction.getId());
            values.put(DBConsts.TRANSACTION_AMOUNT, transaction.getAmount());
            values.put(DBConsts.TRANSACTION_TIME, transaction.getTimeInMills());
            values.put(DBConsts.TRANSACTION_ACCOUNT, transaction.getAccountId());
            values.put(DBConsts.TRANSACTION_DESCRIPTION, transaction.getDescriptionId());
            values.put(DBConsts.TRANSACTION_CURRENCY, transaction.getCurrencyCode());

            database.insert(DBConsts.TRANSACTION_TABLE, null, values);

        } else {
            throw new NullPointerException("insert Transaction: Transaction cannot be null");
        }
    }

    @Override
    public void updateTransaction(Transaction transaction) {
        if (transaction != null){
            ContentValues values = new ContentValues();
            values.put(DBConsts.TRANSACTION_AMOUNT, transaction.getAmount());
            values.put(DBConsts.TRANSACTION_TIME, transaction.getTimeInMills());
            values.put(DBConsts.TRANSACTION_ACCOUNT, transaction.getAccountId());
            values.put(DBConsts.TRANSACTION_DESCRIPTION, transaction.getDescriptionId());
            values.put(DBConsts.TRANSACTION_CURRENCY, transaction.getCurrencyCode());

            String selection = DBConsts.TRANSACTION_ID+" LIKE ?";
            String[] selectionArgs = {transaction.getId()};

            database.update(DBConsts.TRANSACTION_TABLE, values, selection, selectionArgs);

        }else{
            throw new NullPointerException("update Transaction: Transaction cannot be null");
        }
    }

    @Override
    public void refreshTransactions() {

    }

    @Override
    public void deleteAllTransactions() {
        database.delete(DBConsts.TRANSACTION_TABLE, null, null);
    }

    @Override
    public void deleteTransaction(String transactionId) {
        String selection = DBConsts.TRANSACTION_ID + " LIKE ?";
        String[] selectionArgs = {transactionId};

        database.delete(DBConsts.TRANSACTION_TABLE, selection, selectionArgs);
    }
}
