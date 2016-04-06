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
	public static final int DATABASE_VERSION = 1;
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

	public static final String SQLSYNTX_CONDITION_EQUALS = "(%s = ?)";
	public static final String SQLSYNTX_CONDITION_LESSTHAN = "(%s < ?)";
	public static final String SQLSYNTX_CONDITION_GREATTHAN = "(%s > ?)";
	public static final String SQLSYNTX_AND = " AND ";
	public static final String SQLSYNTX_OR = " OR ";
	public static final String SQLSYNTX_QUERY_SELECT_WHERE = "SELECT %s FROM %s WHERE %s";
	public static final String SQLSYNTX_QUERY_SELECT = "SELECT %s FROM %s";
	/*
	 * CurrencyCashFlow table
	 */
	public static final String CURRENCY_TABLE = "CurrencyTable";
	public static final String CURRENCY_ID = "CurrencyId";
	public static final String CURRENCY_CODE = "CurrencyCode";
	public static final String CURRENCY_NAME = "CurrencyName";
	public static final String CURRENCY_RATE = "CurrencyRate";
	public static final String CURRENCY_FLAG = "FlagId";
	public static final String CURRENCY_UPDATETIME = "UpdatedTime";

	public static final String SUM_IN_USD = "sumInUSD";

	public static final String QUERY_GET_SUM_BY_DESC_ALL = "select " +
			DESCRIPTION_ID+", " +
			DESCRIPTION_NAME+", " +
			"total("+TRANSACTION_AMOUNT+"/"+CURRENCY_RATE+") as "+SUM_IN_USD+" " +
			"from " +
			""+TRANSACTION_TABLE+" " +
			"inner join " +
			"(select * from "+CURRENCY_TABLE+" group by "+CURRENCY_CODE+") " +
			"on "+TRANSACTION_CURRENCY+" = "+CURRENCY_CODE+" " +
			"inner join "+DESCRIPTION_TABLE+" " +
			"on "+TRANSACTION_DESCRIPTION+" = "+DESCRIPTION_ID+" " +
			"where "+TRANSACTION_CATEGORY+" = ? group by "+DESCRIPTION_ID+"";

	public static final String SELECTION_TIME_RANGE = "(" + ConstsDatabase.TRANSACTION_TIME + " >?" + ")"
			+ " and " + "(" + ConstsDatabase.TRANSACTION_TIME + " <?" + ")";

	public static final String QUERY_GET_SUM_BY_DESC_TIME_RANGE = "select " +
			DESCRIPTION_ID+", " +
			DESCRIPTION_NAME+", " +
			"total("+TRANSACTION_AMOUNT+"/"+CURRENCY_RATE+") as "+SUM_IN_USD+" " +
			"from " +
			""+TRANSACTION_TABLE+" " +
			"inner join " +
			"(select * from "+CURRENCY_TABLE+" group by "+CURRENCY_CODE+") " +
			"on "+TRANSACTION_CURRENCY+" = "+CURRENCY_CODE+" " +
			"inner join "+DESCRIPTION_TABLE+" " +
			"on "+TRANSACTION_DESCRIPTION+" = "+DESCRIPTION_ID+" " +
			"where ("+TRANSACTION_CATEGORY+" = ?) and "+SELECTION_TIME_RANGE+" group by "+DESCRIPTION_ID+"";

	/*
	Query, select all description from description table
	 */

	public static final String 	QUERY_SELECT_ALL_DESC = "SELECT * FROM "+DESCRIPTION_TABLE;
}
