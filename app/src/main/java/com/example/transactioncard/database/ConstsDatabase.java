package com.example.transactioncard.database;

import android.util.Log;

import com.example.transactioncard.BuildConfig;

public class ConstsDatabase {
	/*
	 * Database attributes
	 */
	public static final String DATABASE_NAME = "CashFlow.db";
	public static final String TRANSACTION_TABLE = "TransactionTable";
	public static final String ACCOUNT_TABLE = "AccountTable";
	public static final String DESCRIPTION_TABLE = "DescriptionTable";
	public static final String DESCRIPTION_CATEGORY_TABLE = "DescriptionCategoryTable";
	public static final int DATABASE_VERSION = 3;
	/*
	 * Transaction table attributes
	 */
	public static final String TRANSACTION_ID = "TransactionId";
	public static final String TRANSACTION_AMOUNT = "TransactionAmount";
	public static final String TRANSACTION_DESCRIPTION = "TransactionDescription";
	public static final String TRANSACTION_TIME = "TransactionTime";
	public static final String TRANSACTION_ACCOUNT = "TransactionAccount";
	public static final String TRANSACTION_CATEGORY = "TransactionCategory";
	public static final String TRANSACTION_CURRENCY = "TransactionCurrency";
	/*
	 * Account table attributes
	 */
	public static final String ACCOUNT_ID = "AccountId";
	public static final String ACCOUNT_NAME = "AccountName";
	public static final String ACCOUNT_BALANCE = "AccountBalance";
	public static final String ACCOUNT_CURRENCY = "AccountCurrency";
	public static final String ACCOUNT_INCOME = "AccountIncome";
	public static final String ACCOUNT_EXPENDITURE = "AccountExpenditure";
	public static final String ACCOUNT_IS_DEFAULT = "AccountisDefault";
	/*
	 * Description table attributes
	 */
	public static final String DESCRIPTION_ID = "DescId";
	public static final String DESCRIPTION_NAME = "DescName";
	/*
	 * Views attributes
	 */
	public static String VIEW_ACCOUNT_ID = ConstsDatabase.ACCOUNT_ID + "V";
	public static String VIEW_ACCOUNT_NAME = ConstsDatabase.ACCOUNT_NAME + "V";
	public static String VIEW_TRANSACTION_CATEGORY = ConstsDatabase.TRANSACTION_CATEGORY + "V";
	public static final String total = "Total";
	public static final String VIEW_ACCOUNT_SUMMARY = "AccountSummary";
	public static String VIEW_TOTAL = total + "V";
	/*
	 * Error messages
	 */
	public static final String STRERR_FailedTo = "Failed to: %s";
	public static final String STRPKG_DATABASE = "com.example.transactioncard.database:";
	
	/**
	 * @param methodName
	 * @param operation
	 */
	public static void logERROR(String methodName, String operation) {
		if (BuildConfig.DEBUG) {
			String errorMsg = String.format(ConstsDatabase.STRERR_FailedTo,
					operation);
			Log.v(methodName, errorMsg);
		}
	}
	
	/**
	 * @param methodName
	 * @param operation
	 */
	public static void logINFO(String className, String methodName, String operation) {
		if (BuildConfig.DEBUG){
			String logMsg = ">>>"+operation;
			Log.d(className+"."+methodName, logMsg);
		}
	}

	public static final String CATEGORY_EXPENSES = "Expenses";
	
	public static final String CATEGORY_INCOME = "Income";
	
	public static final String CATEGORY_ALL = "All";
	
	/*
	 * SQLite syntax
	 */
	public static final String SQLSYNTX_SELECT = " SELECT ";
	public static final String SQLSYNTX_FROM = " FROM ";
	public static final String SQLSYNTX_WHERE = " WHERE ";
	public static final String SQLSYNTX_CONDITION_EQUALS = "(%s = ?)";
	public static final String SQLSYNTX_CONDITION_LESSTHAN = "(%s < ?)";
	public static final String SQLSYNTX_CONDITION_GREATTHAN = "(%s > ?)";
	public static final String SQLSYNTX_AND = " AND ";
	public static final String SQLSYNTX_OR = " OR ";
	public static final String SQLSYNTX_QUERY_SELECT_WHERE = "SELECT %s FROM %s WHERE %s";
	public static final String SQLSYNTX_QUERY_SELECT = "SELECT %s FROM %s";
	/*
	 * Currency table
	 */
	public static final String CURRENCY_TABLE = "CurrencyTable";
	public static final String CURRENCY_ID = "CurrencyId";
	public static final String CURRENCY_CODE = "CurrencyCode";
	public static final String CURRENCY_NAME = "CurrencyName";
	public static final String CURRENCY_RATE = "CurrencyRate";
	public static final String CURRENCY_FLAG = "FlagId";
	
	
}
