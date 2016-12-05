package com.example.tcard.mvp.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.tcard.mvp.data.source.AccountTableInterface;
import com.example.tcard.mvp.data.Account;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sshirima on 12/3/16.
 */
public class AccountTable implements AccountTableInterface{

    private static AccountTable mAccountTable;
    private SQLiteDatabase mDatabase;
    private TcardDB mTcardDB;

    //Prevent direct instatiation
    public AccountTable(Context context){
        if (context != null){
            this.mTcardDB = new TcardDB(context);
        }else {
            throw new NullPointerException("instantiate TransactionTable: AppContext cannot be null");
        }

    }

    public static AccountTable getInstance(Context context){
        if (mAccountTable == null){
            mAccountTable = new AccountTable(context);
            return mAccountTable;
        } else {
            return mAccountTable;
        }
    }

    public void open() throws SQLException{
        this.mDatabase = mTcardDB.getWritableDatabase();
    }

    public void close(){
        mTcardDB.close();
    }

    /**
     * @param callback
     * Note: {@link LoadAccountCallback#onDataNotAvailable()} is fired if the database doesnot exist
     * the table is empty
     */
    @Override
    public void getAccounts(LoadAccountCallback callback) {
        List<Account> accounts = new ArrayList<>();

        String[] returnColumns = {DBConsts.ACCOUNT_ID, DBConsts.ACCOUNT_NAME};

        Cursor cursor = mDatabase.query(DBConsts.ACCOUNT_TABLE, returnColumns, null, null, null, null, null);

        if (cursor != null && cursor.getCount()>0){
            cursor.moveToFirst();
           while(cursor.moveToNext()){
               String accName = cursor.getString(cursor.getColumnIndexOrThrow(DBConsts.ACCOUNT_NAME));
               String accId = cursor.getString(cursor.getColumnIndexOrThrow(DBConsts.ACCOUNT_ID));

               Account account = new Account(accId, accName);
               accounts.add(account);
           }
        }

        if (cursor != null){
            cursor.close();
        }

        if (accounts.isEmpty()){
            //This will be called if the database is empty or the new
            callback.onDataNotAvailable();
        } else {
            callback.onAccountsLoaded(accounts);
        }

    }

    /**
     * @param accountId
     * @param callback
     * Note:{@link GetAccountCallback#onDataNotAvailable()} is fired if the {@link Account} isn't found
     */
    @Override
    public void getAccount(String accountId, GetAccountCallback callback) {
        String[] returnColumns={DBConsts.ACCOUNT_ID, DBConsts.ACCOUNT_NAME};

        String selection = DBConsts.ACCOUNT_ID + " LIKE ?";
        String[] selectionArgs = {accountId};

        Cursor cursor = mDatabase.query(DBConsts.ACCOUNT_TABLE, returnColumns,selection, selectionArgs, null, null, null);

        Account account = null;

        if (cursor != null && cursor.getCount() >0){
            cursor.moveToFirst();

            String accName = cursor.getString(cursor.getColumnIndexOrThrow(DBConsts.ACCOUNT_NAME));
            String accId = cursor.getString(cursor.getColumnIndexOrThrow(DBConsts.ACCOUNT_ID));

            account = new Account(accId, accName);
        }

        if (cursor != null){
            cursor.close();
        }

        if (account != null){
            callback.onAccountLoaded(account);
        } else {
            callback.onDataNotAvailable();
        }

    }

    @Override
    public void saveAccount(Account account) {
        if (account == null){
            ContentValues cv = new ContentValues();

            cv.put(DBConsts.ACCOUNT_ID, account.getId());
            cv.put(DBConsts.ACCOUNT_NAME, account.getName());

            mDatabase.insert(DBConsts.ACCOUNT_TABLE, null, cv);

        } else {throw new NullPointerException("instantiate TransactionTable: AppContext cannot be null");}
    }

    @Override
    public void refreshAccount() {
        //Not required because AccountCache handles all the logic of refreshing the
        //accounts from all available datasources
    }

    @Override
    public void deleteAllAccounts() {
        mDatabase.delete(DBConsts.ACCOUNT_TABLE, null, null);
    }

    @Override
    public void deleteAccount(String accountId) {
        String selection = DBConsts.ACCOUNT_ID + " LIKE ?";

        String[] selectionsArgs = {accountId};

        mDatabase.delete(DBConsts.ACCOUNT_TABLE, selection, selectionsArgs);
    }
}
