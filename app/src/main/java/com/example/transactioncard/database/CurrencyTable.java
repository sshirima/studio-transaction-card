package com.example.transactioncard.database;

import java.util.ArrayList;
import java.util.List;

import com.example.transactioncard.BuildConfig;
import com.example.transactioncard.object.Currency;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.ViewDebug.FlagToString;

public class CurrencyTable {

	private static final String STR_Classname = CurrencyTable.class.getName();
	private CashFlowDB cashFlowDB;
	private SQLiteDatabase sqliteDatabase;
	private Context applicationContext;
	/*
	 * Constructor
	 */
	public CurrencyTable (Context context){
		String methodName = "CurrencyTable";
		String operation = "Create Currency table constructor";
		ConstsDatabase.logINFO(STR_Classname, methodName, operation);
		
		cashFlowDB = new CashFlowDB(context);
		this.applicationContext = context;
		
	}
	/*
	 * Open the Db
	 */
	public void open() throws SQLException {
		
		sqliteDatabase = cashFlowDB.getWritableDatabase();
	}
	/*
	 * Close the DB
	 *
	 */
	public void close() {
		cashFlowDB.close();
	}
	/*
	 * Insert new currency to DB
	 */
	public Currency insertCurrencySqliteDB(Currency currency) {
		String methodName = "insertCurrencySqliteDB";
		String operation = "Insert new currency into the DB";
		
		/*
		 * Insert new currency into the sqliteDB
		 */
		ContentValues contentValues = new ContentValues();
		Currency newCurrency = null;
		try {
			ConstsDatabase.logINFO(STR_Classname, methodName, operation);
			/*
			 * Put currency values
			 */
			contentValues = setContentValues(currency);
			
			//Insert new currency into currency table
			long insertId = sqliteDatabase.insert(ConstsDatabase.CURRENCY_TABLE, null,
					contentValues);
			
			newCurrency = getCurrencyById(insertId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			ConstsDatabase.logERROR(methodName, operation);
			e.printStackTrace();
		}
		return newCurrency;
	}
	/*
	 * Update currency to Db
	 */
	public boolean updateTransactioSqliteDB(Currency currency) {
		String methodName = "updateCurrencySqliteDB";
		String operation = "Update currency by Id";
		/*
		 * Add currency parameters to contentValues
		 */
		ContentValues contentValues = setContentValues(currency);
		/*
		 * Update currency on the sqliteDB
		 */
		String tableName = ConstsDatabase.CURRENCY_TABLE;
		String condition = String.format(ConstsDatabase.SQLSYNTX_CONDITION_EQUALS, ConstsDatabase.CURRENCY_ID);
		String[] conditionArgs = new String[]{""+currency.getCurrencyId()};
		int updatedRows = 0;
		try {
			updatedRows = sqliteDatabase
					.update(tableName, contentValues,condition,conditionArgs);
			if (BuildConfig.DEBUG){
				String logMsg = ">>>"+operation;
				Log.d(STR_Classname+"."+methodName, logMsg+": "+currency
						.getCurrencyId());
			}
		} catch (Exception e) {
			ConstsDatabase.logERROR(methodName, operation);
		}
		
		return updatedRows == 1 ? true:false;
	}
	
	private ContentValues setContentValues(Currency currency) {
		ContentValues contentValues = new ContentValues();
		
		if (currency != null) {
			/*
			 * Put currency parameters to the ContentValues instance
			 */
			contentValues.put(ConstsDatabase.CURRENCY_CODE,
					currency.getCurrencyCode());
			contentValues.put(ConstsDatabase.CURRENCY_NAME,
					currency.getCurrencyName());
			contentValues.put(ConstsDatabase.CURRENCY_RATE,
					currency.getRateFromUSD());
			contentValues.put(ConstsDatabase.CURRENCY_FLAG,
					currency.getFlagId());
			}
		
		return contentValues;
	}
	
	/*
	 * Read one currency by code
	 */public Currency getCurrencyById(Currency currency) {
			String methodName = "getCurrencyById";
			String operation = "Get currency from the currency table";
			/*
			 * Prepare the query
			 */
			Currency returnCurrency = null;
			String retunColumns = "*";
			String tableName = ConstsDatabase.CURRENCY_TABLE;
			String condition = String.format(ConstsDatabase.SQLSYNTX_CONDITION_EQUALS, ConstsDatabase.CURRENCY_CODE);
			String[] conditionArgs = new String[]{""+currency.getCurrencyCode()};
			/*
			 * Query the database from the given ID
			 */
			String query = String.format(ConstsDatabase.SQLSYNTX_QUERY_SELECT_WHERE,
					retunColumns, tableName, condition);
			Cursor cursor= null;
			try {
				ConstsDatabase.logINFO(STR_Classname, methodName, operation);
				
				cursor = sqliteDatabase.rawQuery(query,conditionArgs);
				if (cursor.moveToFirst()) {
					while (!cursor.isAfterLast()) {
						returnCurrency = cursorToCurrency(cursor);
						cursor.moveToNext();
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			cursor.close();
			return returnCurrency;
		}
	
	/*
	 * Read all currency
	 */
	 public List<Currency> getCurrenciesAll(){
			String methodName = "getCurrenciesAll";
			String operation = "Get all currency from the account table";
			/*
			 * Prepare the query
			 */
			String retunColumns = "*";
			String tableName = ConstsDatabase.CURRENCY_TABLE;
			String query = String.format(ConstsDatabase.SQLSYNTX_QUERY_SELECT, retunColumns, tableName);
			ConstsDatabase.logINFO(STR_Classname, methodName, operation);
			
			Cursor cursor = sqliteDatabase.rawQuery(query,null);
			
			List<Currency> returnList = new ArrayList<Currency>();
			
			/*
			 * Extract currency(s) from the cursor
			 */
			if (cursor.moveToFirst()){
				
				while (!cursor.isAfterLast()) {
					Currency newCurrency = cursorToCurrency(cursor);
					returnList.add(newCurrency);
					cursor.moveToNext();
				}
			}
			return returnList;
		}
	/*
	 * Get currency by Id
	 */
	public Currency getCurrencyById(long id) {
		String methodName = "getCurrencyById";
		String operation = "Get currency from the currency table";
		/*
		 * Prepare the query
		 */
		
		Currency currency = null;
		String retunColumns = "*";
		String tableName = ConstsDatabase.CURRENCY_TABLE;
		String condition = String.format(ConstsDatabase.SQLSYNTX_CONDITION_EQUALS, ConstsDatabase.CURRENCY_ID);
		String[] conditionArgs = new String[]{""+id};
		/*
		 * Query the database from the given ID
		 */
		String query = String.format(ConstsDatabase.SQLSYNTX_QUERY_SELECT_WHERE,
				retunColumns, tableName, condition);
		Cursor cursor= null;
		try {
			ConstsDatabase.logINFO(STR_Classname, methodName, operation);
			
			cursor = sqliteDatabase.rawQuery(query,conditionArgs);
			if (cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					currency = cursorToCurrency(cursor);
					cursor.moveToNext();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cursor.close();
		return currency;
	}
	/*
	 * Delete currency to DB
	 */
	public boolean deleteCurrencySqliteDB(Currency currency){
		String methodName = "deleteCurrencySqliteDB";
		String operation = "Delete currency from currencyTable";
		ConstsDatabase.logINFO(STR_Classname, methodName, operation);
		
		/*
		 * First delete currencys with the given currency
		 */
		String currencyCode = currency.getCurrencyCode();
		String tableName = ConstsDatabase.CURRENCY_TABLE;
		
		String condition = String.format(ConstsDatabase.SQLSYNTX_CONDITION_EQUALS, ConstsDatabase.CURRENCY_CODE);
		String[] conditionArgs = new String[]{currencyCode};
		int deletedCurrencies = sqliteDatabase.delete(tableName, condition, conditionArgs);
		ConstsDatabase.logINFO(STR_Classname, methodName, deletedCurrencies+": Currency were deleted");
		
		return deletedCurrencies == 1?true:false;
	}
	/*
	 * Cursor to currency
	 */
	public Currency cursorToCurrency(Cursor cursor) {
		// TODO Auto-generated method stub
		String methodName = "cursorToCurrency";
		String operation = "Convert cursor to cursorToCurrency instance";
		ConstsDatabase.logINFO(STR_Classname, methodName, operation);
		/*
		 * Get field index for the cursor
		 */
		int idIndex = cursor.getColumnIndex(ConstsDatabase.CURRENCY_ID);
		int CodeIndex = cursor
				.getColumnIndex(ConstsDatabase.CURRENCY_CODE);
		int nameIndex = cursor
				.getColumnIndex(ConstsDatabase.CURRENCY_NAME);
		int rateIndex = cursor
				.getColumnIndex(ConstsDatabase.CURRENCY_RATE);
		int flagIdIndex = cursor
				.getColumnIndex(ConstsDatabase.CURRENCY_FLAG);
		/*
		 * Set currency values
		 */
		Currency currency = new Currency(applicationContext, cursor.getString(CodeIndex));
		currency.setCurrencyId(cursor.getInt(idIndex));
		currency.setCurrencyName(cursor.getString(nameIndex));
		currency.setCurrencyRate(cursor.getDouble(rateIndex));
		currency.setFlagId(cursor.getInt(flagIdIndex));
		
		return currency;
	}
}
