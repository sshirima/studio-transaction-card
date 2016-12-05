package com.example.tcard.mvp.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.tcard.mvp.data.source.CurrencyTableInterface;
import com.example.tcard.mvp.data.Currency;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sshirima on 12/3/16.
 */
public class CurrencyTable implements CurrencyTableInterface {

    private static CurrencyTable mCurrencyTable;
    private SQLiteDatabase mDatabase;
    private TcardDB mTcardDB;

    //Prevent direct instantiation
    public CurrencyTable(Context context){
        if (context != null){
            this.mTcardDB = new TcardDB(context);
        }else {
            throw new NullPointerException("instantiate CurrencyTable: AppContext cannot be null");
        }
    }

    public static CurrencyTable getInstance(Context context){
        if (mCurrencyTable == null){
            mCurrencyTable = new CurrencyTable(context);
            return mCurrencyTable;
        }else {
            return mCurrencyTable;
        }
    }

    public void open() throws SQLException {
        this.mDatabase = mTcardDB.getWritableDatabase();
    }

    public void close(){
        mTcardDB.close();
    }

    /**
     * @param callBack
     *Note: {@link LoadCurrencyCallBack#onDataNotAvailable()} is fired if the database does not exist
     * or the table is just empty
     */
    @Override
    public void getCurrencies(LoadCurrencyCallBack callBack) {
        List<Currency> currencies = new ArrayList<>();

        String[] returnColumns= {DBConsts.CURRENCY_CODE, DBConsts.CURRENCY_NAME, DBConsts.CURRENCY_MODIFIEDTIME, DBConsts.CURRENCY_RATETOUSD};

        Cursor cursor = mDatabase.query(DBConsts.CURRENCY_TABLE, returnColumns, null, null, null,null, null);

        if (cursor != null && cursor.getCount()>0){
            while(cursor.moveToNext()){
                String curCode = cursor.getString(cursor.getColumnIndexOrThrow(DBConsts.CURRENCY_CODE));
                String curName = cursor.getString(cursor.getColumnIndexOrThrow(DBConsts.CURRENCY_NAME));
                long curDatemodified = cursor.getLong(cursor.getColumnIndexOrThrow(DBConsts.CURRENCY_MODIFIEDTIME));
                double curRatetousd = Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(DBConsts.CURRENCY_RATETOUSD)));

                Currency currency = new Currency(curCode, curName);
                currency.setModifiedDate(curDatemodified);
                currency.setRatetousd(curRatetousd);

                currencies.add(currency);
            }
        }

        if (cursor != null){
            cursor.close();
        }

        if (currencies.isEmpty()){
            //This will be called if the table is empty or just new
            callBack.onDataNotAvailable();
        }else {
            callBack.onCurrenciesLoaded(currencies);
        }
    }

    @Override
    public void getCurrency(String currencyCode, GetCurrencyCallback callback) {
        String[] returnColumns= {DBConsts.CURRENCY_CODE, DBConsts.CURRENCY_NAME, DBConsts.CURRENCY_MODIFIEDTIME, DBConsts.CURRENCY_RATETOUSD};

        String selection = DBConsts.CURRENCY_CODE + " LIKE ?";
        String[] selectionsArgs = {currencyCode};

        Cursor cursor = mDatabase.query(DBConsts.CURRENCY_TABLE,returnColumns ,selection, selectionsArgs, null, null, null);

        Currency currency = null;
        if (cursor != null && cursor.getCount()>0){
            cursor.moveToFirst();
            String curCode = cursor.getString(cursor.getColumnIndexOrThrow(DBConsts.CURRENCY_CODE));
            String curName = cursor.getString(cursor.getColumnIndexOrThrow(DBConsts.CURRENCY_NAME));
            long curDatemodified = cursor.getLong(cursor.getColumnIndexOrThrow(DBConsts.CURRENCY_MODIFIEDTIME));
            double curRatetousd = Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(DBConsts.CURRENCY_RATETOUSD)));

            currency = new Currency(curCode, curName);
            currency.setModifiedDate(curDatemodified);
            currency.setRatetousd(curRatetousd);
        }

        if (cursor != null){
            cursor.close();
        }

        if (currency != null){
            callback.onCurrencyLoaded(currency);
        } else {
            callback.onDataNotLoaded();
        }
    }

    @Override
    public void saveCurrency(Currency currency) {
        ContentValues cv = new ContentValues();

        cv.put(DBConsts.CURRENCY_CODE, currency.getCurrencyCode());
        cv.put(DBConsts.CURRENCY_NAME, currency.getCurrencyName());
        cv.put(DBConsts.CURRENCY_MODIFIEDTIME, currency.getModifiedDate());
        cv.put(DBConsts.CURRENCY_RATETOUSD, currency.getRatetoUSD());

        mDatabase.insert(DBConsts.CURRENCY_TABLE, null, cv);

    }

    @Override
    public void deleteAllCurrencies() {
        mDatabase.delete(DBConsts.CURRENCY_TABLE, null, null);
    }

    @Override
    public void deleteCurrency(String currencyCode) {
        String selection = DBConsts.CURRENCY_CODE + " LIKE ?";
        String[] selectionArgs = {currencyCode};

        mDatabase.delete(DBConsts.CURRENCY_TABLE, selection, selectionArgs);
    }

    @Override
    public void refreshCurrencies() {

    }
}
