package com.example.tcard.mvp.data.source.local;

/**
 * Created by sshirima on 9/5/16.
 */
public class DBConsts {
    /*
	 * Database attributes
	 */
    public static final String DATABASE_NAME = "tcard.db";
    public static final String TRANSACTION_TABLE = "Transactions";
    public static final String ACCOUNT_TABLE = "Accounts";
    public static final String DESCRIPTION_TABLE = "Descriptions";
    public static final String CURRENCY_TABLE = "Currencies";
    public static final int DATABASE_VERSION = 1;
    /*
     * Transaction table attributes
     */
    public static final String TRANSACTION_ID = "t_id";
    public static final String TRANSACTION_AMOUNT = "t_amount";
    public static final String TRANSACTION_DESCRIPTION = "t_des";
    public static final String TRANSACTION_TIME = "t_time";
    public static final String TRANSACTION_ACCOUNT = "t_account";
    public static final String TRANSACTION_CURRENCY = "t_currency";
    /*
     * Account table attributes
     */
    public static final String ACCOUNT_ID = "a_id";
    public static final String ACCOUNT_NAME = "a_name";
    /*
     * Description table attributes
     */
    public static final String DESCRIPTION_ID = "d_id";
    public static final String DESCRIPTION_NAME = "d_name";
    public static final String DESCRIPTION_CATEGORY = "d_category";

    /*
     * Description table attributes
     */
    public static final String CURRENCY_CODE = "c_code";
    public static final String CURRENCY_NAME = "c_name";
    public static final String CURRENCY_RATETOUSD = "c_ratetousd";
    public static final String CURRENCY_MODIFIEDTIME = "c_timemodified";
    /*
    SQL STATEMENTS FOR CREATING TABLES
     */
    public static final String QRY_CRT_TRANSACTIONS_TABLE = "create table "
            + TRANSACTION_TABLE + "("
            + TRANSACTION_ID
            + " integer primary key, "
            + TRANSACTION_AMOUNT + " text not null, "
            + TRANSACTION_DESCRIPTION + " integer, "
            + TRANSACTION_TIME + " integer not null, "
            + TRANSACTION_ACCOUNT + " integer not null, "
            + TRANSACTION_CURRENCY + " text," + " FOREIGN KEY"
            + " (" + TRANSACTION_DESCRIPTION + ") REFERENCES "
            + DESCRIPTION_TABLE + "("
            + DESCRIPTION_ID + ")," + " FOREIGN KEY("
            + TRANSACTION_ACCOUNT + ") REFERENCES "
            + ACCOUNT_TABLE + "(" + ACCOUNT_ID
            + "));";

    public static final String QRY_CRT_ACCOUNTS_TABLE = "create table "
            + ACCOUNT_TABLE + "(" + ACCOUNT_ID
            + " integer primary key autoincrement, "
            + ACCOUNT_NAME + " text not null); ";

    public static final String QRY_CRT_DESCRIPTION_TABLE = "create table "
            + DESCRIPTION_TABLE + "("
            + DESCRIPTION_ID + " integer primary key autoincrement, "
            + DESCRIPTION_CATEGORY + " text, "
            + DESCRIPTION_NAME + " text not null);";

    public static final String QRY_CRT_CURRENCY_TABLE = "create table "
            + CURRENCY_TABLE + "("
            + CURRENCY_CODE + " text primary key, "
            + CURRENCY_NAME + " text, "
            + CURRENCY_MODIFIEDTIME + " integer, "
            + CURRENCY_RATETOUSD + " text not null);";


}
