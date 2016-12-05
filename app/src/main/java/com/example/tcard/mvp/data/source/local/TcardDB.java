package com.example.tcard.mvp.data.source.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sshirima on 9/5/16.
 */
public class TcardDB extends SQLiteOpenHelper {

    private Context context;

    public TcardDB(Context context) {
        super(context, DBConsts.DATABASE_NAME, null,
                DBConsts.DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBConsts.QRY_CRT_TRANSACTIONS_TABLE);
        db.execSQL(DBConsts.QRY_CRT_ACCOUNTS_TABLE);
        db.execSQL(DBConsts.QRY_CRT_DESCRIPTION_TABLE);
        db.execSQL(DBConsts.QRY_CRT_CURRENCY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
