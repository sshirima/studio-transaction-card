package com.example.transactioncard.database;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.example.transactioncard.BuildConfig;
import com.example.transactioncard.object.CurrencyCashFlow;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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
		String operation = "Create CurrencyCashFlow table constructor";
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
	 * Insert new currencyCashFlow to DB
	 */
	public CurrencyCashFlow insertCurrencySqliteDB(CurrencyCashFlow currencyCashFlow) {
		String methodName = "insertCurrencySqliteDB";
		String operation = "Insert new currencyCashFlow into the DB";
		
		/*
		 * Insert new currencyCashFlow into the sqliteDB
		 */
		ContentValues contentValues = new ContentValues();
		CurrencyCashFlow newCurrencyCashFlow = null;
		try {
			ConstsDatabase.logINFO(STR_Classname, methodName, operation);
			/*
			 * Put currencyCashFlow values
			 */
			contentValues.put(ConstsDatabase.CURRENCY_CODE,
					currencyCashFlow.getCurrencyCode());
			contentValues.put(ConstsDatabase.CURRENCY_NAME,
					currencyCashFlow.getCurrencyName());
			contentValues.put(ConstsDatabase.CURRENCY_UPDATETIME,
					currencyCashFlow.getUpdateTime());
			contentValues.put(ConstsDatabase.CURRENCY_FLAG,
					currencyCashFlow.getFlagId());
			contentValues.put(ConstsDatabase.CURRENCY_RATE,
					currencyCashFlow.getRateFromUSD());
			//Insert new currencyCashFlow into currencyCashFlow table
			long insertId = sqliteDatabase.insert(ConstsDatabase.CURRENCY_TABLE, null,
					contentValues);
			
			newCurrencyCashFlow = getCurrencyById(insertId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			ConstsDatabase.logERROR(methodName, operation);
			e.printStackTrace();
		}
		return newCurrencyCashFlow;
	}
	/*
	 * Update currencyCashFlow to Db
	 */
	public boolean updateRateByCurrencyCode(CurrencyCashFlow currencyCashFlow) {
		String methodName = "updateCurrencySqliteDB";
		String operation = "Update currencyCashFlow by Id";
		/*
		 * Add currencyCashFlow parameters to contentValues
		 */

		ContentValues contentValues = setContentValues(currencyCashFlow);
		/*
		 * Update currencyCashFlow on the sqliteDB
		 */
		String tableName = ConstsDatabase.CURRENCY_TABLE;
		String condition = String.format(ConstsDatabase.SQLSYNTX_CONDITION_EQUALS, ConstsDatabase.CURRENCY_CODE);
		String[] conditionArgs = new String[]{""+ currencyCashFlow.getCurrencyCode()};
		int updatedRows = 0;
		try {
			updatedRows = sqliteDatabase
					.update(tableName, contentValues, condition, conditionArgs);
			if (BuildConfig.DEBUG){
				String logMsg = ">>>"+operation;
				Log.d(STR_Classname+"."+methodName, logMsg+": "+ currencyCashFlow
						.getCurrencyCode());
			}
		} catch (Exception e) {
			ConstsDatabase.logERROR(methodName, operation);
		}
		
		return updatedRows == 1 ? true:false;
	}
	
	private ContentValues setContentValues(CurrencyCashFlow currencyCashFlow) {
		ContentValues contentValues = new ContentValues();
		long updatedTime = Calendar.getInstance().getTimeInMillis();

		if (currencyCashFlow != null) {
			/*
			 * Put currencyCashFlow parameters to the ContentValues instance
			 */
			contentValues.put(ConstsDatabase.CURRENCY_UPDATETIME,
					updatedTime);
			contentValues.put(ConstsDatabase.CURRENCY_RATE,
					currencyCashFlow.getRateFromUSD());
			}
		
		return contentValues;
	}
	
	/*
	 * Read one currencyCashFlow by code
	 */

	public CurrencyCashFlow getCurrencyByCode(String currencyCode) {
		String methodName = "getCurrencyById";
		String operation = "Get currencyCashFlow from the currencyCashFlow table";
			/*
			 * Prepare the query
			 */
		CurrencyCashFlow returnCurrencyCashFlow = null;
		String retunColumns = "*";
		String tableName = ConstsDatabase.CURRENCY_TABLE;
		String condition = String.format(ConstsDatabase.SQLSYNTX_CONDITION_EQUALS, ConstsDatabase.CURRENCY_CODE);
		String[] conditionArgs = new String[]{""+ currencyCode};
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
					returnCurrencyCashFlow = cursorToCurrency(cursor);
					cursor.moveToNext();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cursor.close();
		return returnCurrencyCashFlow;
	}
	
	/*
	 * Read all currency
	 */
	 public List<CurrencyCashFlow> getCurrenciesAll(){
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
			
			List<CurrencyCashFlow> returnList = new ArrayList<CurrencyCashFlow>();
			
			/*
			 * Extract currency(s) from the cursor
			 */
			if (cursor.moveToFirst()){
				
				while (!cursor.isAfterLast()) {
					CurrencyCashFlow newCurrencyCashFlow = cursorToCurrency(cursor);
					returnList.add(newCurrencyCashFlow);
					cursor.moveToNext();
				}
			}
			return returnList;
		}
	/*
	 * Get currency by Id
	 */
	public CurrencyCashFlow getCurrencyById(long id) {
		String methodName = "getCurrencyById";
		String operation = "Get currencyCashFlow from the currencyCashFlow table";
		/*
		 * Prepare the query
		 */
		
		CurrencyCashFlow currencyCashFlow = null;
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
					currencyCashFlow = cursorToCurrency(cursor);
					cursor.moveToNext();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cursor.close();
		return currencyCashFlow;
	}
	/*
	 * Delete currencyCashFlow to DB
	 */
	public boolean deleteCurrencySqliteDB(CurrencyCashFlow currencyCashFlow){
		String methodName = "deleteCurrencySqliteDB";
		String operation = "Delete currencyCashFlow from currencyTable";
		ConstsDatabase.logINFO(STR_Classname, methodName, operation);
		
		/*
		 * First delete currencys with the given currencyCashFlow
		 */
		String currencyCode = currencyCashFlow.getCurrencyCode();
		String tableName = ConstsDatabase.CURRENCY_TABLE;
		
		String condition = String.format(ConstsDatabase.SQLSYNTX_CONDITION_EQUALS, ConstsDatabase.CURRENCY_CODE);
		String[] conditionArgs = new String[]{currencyCode};
		int deletedCurrencies = sqliteDatabase.delete(tableName, condition, conditionArgs);
		ConstsDatabase.logINFO(STR_Classname, methodName, deletedCurrencies+": CurrencyCashFlow were deleted");
		
		return deletedCurrencies == 1?true:false;
	}
	/*
	 * Cursor to currency
	 */
	public CurrencyCashFlow cursorToCurrency(Cursor cursor) {
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
		int updateTimeIndex = cursor
				.getColumnIndex(ConstsDatabase.CURRENCY_UPDATETIME);
		/*
		 * Set currencyCashFlow values
		 */
		CurrencyCashFlow currencyCashFlow = new CurrencyCashFlow(applicationContext, cursor.getString(CodeIndex));
		currencyCashFlow.setCurrencyId(cursor.getInt(idIndex));
		currencyCashFlow.setCurrencyName(cursor.getString(nameIndex));
		currencyCashFlow.setCurrencyRate(cursor.getDouble(rateIndex));
		currencyCashFlow.setFlagId(cursor.getInt(flagIdIndex));
		currencyCashFlow.setUpdateTime(cursor.getLong(updateTimeIndex));
		
		return currencyCashFlow;
	}
}
