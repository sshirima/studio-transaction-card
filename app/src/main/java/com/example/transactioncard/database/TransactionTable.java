package com.example.transactioncard.database;

import java.util.ArrayList;
import java.util.Calendar;

import com.example.transactioncard.BuildConfig;
import com.example.transactioncard.Consts;
import com.example.transactioncard.object.Currencies;
import com.example.transactioncard.object.Description;
import com.example.transactioncard.object.Transaction;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TransactionTable {

	private static final String CLASSNAME = TransactionTable.class.getName();

	private SQLiteDatabase sqliteDatabase;
	private CashFlowDB cashFlowDB;
	private Context context;

	public TransactionTable(Context context) {
		String methodName = "TransactionTable";
		String operation = "Create transaction table instance";
		//Log INFO
		ConstsDatabase.logINFO(CLASSNAME, methodName, operation);
		
		cashFlowDB = new CashFlowDB(context);
		this.context = context;
	}

	public void open() throws SQLException {
		String methodName = "open";
		String operation = "Get writable database";
		try {
			sqliteDatabase = cashFlowDB.getWritableDatabase();
			ConstsDatabase.logINFO(CLASSNAME, methodName, operation);
		} catch (SQLException e) {
			// TODO: handle exception
			ConstsDatabase.logERROR(methodName, operation);
		}

	}

	public void close() {
		cashFlowDB.close();
	}

	public Transaction insertTransactionSqliteDB(Transaction transaction) {
		String methodName = "insertNewTransactionToDB";
		String operation = "Insert new transaction to sqliteDB";
		ContentValues contentValues = setContentValues(transaction);
		long returnId = 0;
		/*
		 * Inserting new transaction to the database
		 */
		try {
			returnId = sqliteDatabase.insert(ConstsDatabase.TRANSACTION_TABLE,
					null, contentValues);
			ConstsDatabase.logINFO(CLASSNAME, methodName, operation);
		} catch (Exception e) {
			ConstsDatabase.logERROR(methodName, operation);
		}
		return getTransactionById(returnId);
	}

	public void updateTransactioSqliteDB(Transaction transaction) {
		String methodName = "updateTransactioSqliteDB";
		String operation = "Update transaction by Id";
		/*
		 * Add transaction parameters to contentValues
		 */
		ContentValues contentValues = setContentValues(transaction);
		/*
		 * Update transaction on the sqliteDB
		 */
		try {
			
			sqliteDatabase
					.update(ConstsDatabase.TRANSACTION_TABLE, contentValues,
							ConstsDatabase.TRANSACTION_ID + " = ?",
							new String[] { Long.toString(transaction
									.getTransactionId()) });
			if (BuildConfig.DEBUG){
				String logMsg = ">>>"+operation;
				Log.d(CLASSNAME+"."+methodName, logMsg+": "+transaction
						.getTransactionId());
			}
		} catch (Exception e) {
			ConstsDatabase.logERROR(methodName, operation);
		}
	}

	public void deleteTransactionSqliteDB(Transaction transaction) {
		String methodName = "deleteTransactionSqliteDB";
		String operation = "Delete transaction from SqliteDB";
		/*
		 * Delete transaction from the sqliteDB
		 */
		try {
			sqliteDatabase.delete(ConstsDatabase.TRANSACTION_TABLE,
					ConstsDatabase.TRANSACTION_ID + " =?", new String[] { Long
							.toString(transaction.getTransactionId()) });
			ConstsDatabase.logINFO(CLASSNAME, methodName, operation);
			
		} catch (Exception e) {
			ConstsDatabase.logERROR(methodName, operation);
			e.printStackTrace();
		}
	}

	

	public ArrayList<Transaction> getTransactionsByCondition(String selection,
			String[] selectionArgs) {
		String methodName = "getTransactionsByCondition";
		String operation = "Get transactions with the given condition(s):"+selection;
		String inTables = joinTableStatement();
		/*
		 * Prepare a return list and query
		 */
		ArrayList<Transaction> mTransactionList = new ArrayList<Transaction>();
		String query = "SELECT * FROM " + inTables + " WHERE " + selection;
		Cursor cursor = null;
		/*
		 * Query for the transaction as per prepared query
		 */
		try {
			ConstsDatabase.logINFO(CLASSNAME, methodName, operation);
			cursor = sqliteDatabase.rawQuery(query, selectionArgs);
			/*
			 * Loop through the cursor to get all transaction(s)
			 */
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				mTransactionList.add(cursorToTransaction(cursor));
				cursor.moveToNext();
			}
		} catch (Exception e) {
			ConstsDatabase.logERROR(methodName, operation);
			e.printStackTrace();
		}
		cursor.close();
		return mTransactionList;
	}

	public Description getDescriptionByName(String name) {
		String methodName = "getDescriptionByName";
		String operation = "Get dscription from sqliteDB by name";
		/*
		 * Prepare query
		 */
		Description description = null;
		String inTables = DescriptionTable.joinTableStatement();
		String selections = ConstsDatabase.DESCRIPTION_TABLE + "."
				+ ConstsDatabase.DESCRIPTION_NAME + " = ?";
		String query = "SELECT * FROM " + inTables + " WHERE " + selections;
		Cursor cursor = null;
		/*
		 * Query for the description on sqlite DB
		 */
		try {
			ConstsDatabase.logINFO(CLASSNAME, methodName, operation);
			
			cursor = sqliteDatabase.rawQuery(query, new String[] { name });
			
			if (cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					description = DescriptionTable.cursorToDescription(cursor);
					cursor.moveToNext();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			ConstsDatabase.logERROR(methodName, operation);
			e.printStackTrace();
		}
		cursor.close();
		return description;
	}

	public Transaction getTransactionById(long id) {
		String methodName = "getTransactionById";
		String operation = "Get transactions with the given Id:"+id;
		String inTables = joinTableStatement();
		/*
		 * Prepare the query
		 */
		String selections = ConstsDatabase.TRANSACTION_ID + " = ?";
		String[] selectionArgs = new String[] { Long.toString(id) };
		String query = "SELECT * FROM " + inTables + " WHERE " + selections;
		Cursor cursor = null;
		Transaction transaction = null;
		/*
		 * Query for the transaction with a given ID
		 */
		try {
			ConstsDatabase.logINFO(CLASSNAME, methodName, operation);
			cursor = sqliteDatabase.rawQuery(query, selectionArgs);
			transaction = null;
			if (cursor.moveToFirst()) {
				transaction = cursorToTransaction(cursor);
			}
		} catch (Exception e) {
			ConstsDatabase.logERROR(methodName, operation);
			e.printStackTrace();
		}

		cursor.close();
		return transaction;
	}

	public ArrayList<Transaction> getTransactionsAll() {
		String methodName = "getTransactionsAll";
		String operation = "Get all transactions";
		String inTables = joinTableStatement();
		/*
		 * Prepare the query
		 */
		ArrayList<Transaction> transactionList = new ArrayList<Transaction>();
		String query = "SELECT * FROM " + inTables;
		/*
		 * Query all transaction from the SqliteDB
		 */
		Cursor cursor = null;
		try {
			ConstsDatabase.logINFO(CLASSNAME, methodName, operation);
			cursor = sqliteDatabase.rawQuery(query, null);
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Transaction newTransaction = cursorToTransaction(cursor);
				transactionList.add(newTransaction);
				cursor.moveToNext();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			ConstsDatabase.logERROR(methodName, operation);
			e.printStackTrace();
		}
		cursor.close();
		return transactionList;
	}

	

	public ArrayList<Transaction> getTransactionsByTimeRange(Calendar start,
			Calendar end) {
		String methodName = "getTransactionsByTimeRange";
		String operation = "Get transactions by given time range";
		String inTables = joinTableStatement();
		/*
		 * Prepare the query for SqliteDB
		 */
		String selection = "(" + ConstsDatabase.TRANSACTION_TIME + " >?" + ")"
				+ " and " + "(" + ConstsDatabase.TRANSACTION_TIME + " <?" + ")";
		String[] selectionArgs = new String[] {
				Long.toString(start.getTimeInMillis()),
				Long.toString(end.getTimeInMillis()) };
		String query = "SELECT * FROM " + inTables + " WHERE " + selection;
		/*
		 * Query transaction from sliteDB
		 */
		Cursor cursor = null;
		ArrayList<Transaction> transactionList=null;
		try {
			ConstsDatabase.logINFO(CLASSNAME, methodName, operation);
			
			cursor = sqliteDatabase.rawQuery(query, selectionArgs);
			transactionList = new ArrayList<Transaction>();
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				transactionList.add(cursorToTransaction(cursor));
				cursor.moveToNext();
			}
		} catch (Exception e) {
			ConstsDatabase.logERROR(methodName, operation);
			
			e.printStackTrace();
		}
		cursor.close();
		return transactionList;
	}
	
	private ContentValues setContentValues(Transaction transaction) {
		ContentValues contentValues = new ContentValues();
		
		if (transaction != null) {
			/*
			 * Put transaction parameters to the ContentValues instance
			 */
			contentValues.put(ConstsDatabase.TRANSACTION_AMOUNT,
					transaction.getAmount());
			contentValues.put(ConstsDatabase.TRANSACTION_TIME,
					transaction.getTimeInMillis());
			contentValues.put(ConstsDatabase.TRANSACTION_DESCRIPTION,
					transaction.getDescription().getId());
			contentValues.put(ConstsDatabase.TRANSACTION_ACCOUNT,
					transaction.getAccountId());
			contentValues.put(ConstsDatabase.TRANSACTION_CATEGORY,
					transaction.getCategory());
			contentValues.put(ConstsDatabase.TRANSACTION_CURRENCY,
					transaction.getCurrencyCode());
		}
		
		return contentValues;
	}

	public Transaction cursorToTransaction(Cursor cursor) {
		// TODO Auto-generated method stub
		String methodName = "cursorToTransaction";
		String operation = "Convert cursor to transaction object";
		//log info
		ConstsDatabase.logINFO(CLASSNAME, methodName, operation);
		/*
		 * Extract Index for each transaction parameters from cursor
		 */
		int idIndex = cursor.getColumnIndex(ConstsDatabase.TRANSACTION_ID);
		int amountIndex = cursor
				.getColumnIndex(ConstsDatabase.TRANSACTION_AMOUNT);
		int timeIndex = cursor.getColumnIndex(ConstsDatabase.TRANSACTION_TIME);
		int desNameIndex = cursor.getColumnIndex(ConstsDatabase.DESCRIPTION_NAME);
		int desIdIndex = cursor.getColumnIndex(ConstsDatabase.DESCRIPTION_ID);
		int accountNameIndex = cursor
				.getColumnIndex(ConstsDatabase.ACCOUNT_NAME);
		int categoryIndex = cursor
				.getColumnIndex(ConstsDatabase.TRANSACTION_CATEGORY);
		int accIdIndex = cursor.getColumnIndex(ConstsDatabase.ACCOUNT_ID);
		int currencyCodeIndex = cursor.getColumnIndex(ConstsDatabase.TRANSACTION_CURRENCY);
		double transactionAmount = cursor.getDouble(amountIndex);
		/*
		 * Extract transaction parameters by the given Index
		 */
		String currency = cursor.getString(currencyCodeIndex);
		if (currency == null){
			currency = Currencies.CURRENCY_CODE_US;
		}
		final long transactionTimeMillis = cursor.getLong(timeIndex);
		Transaction transaction = new Transaction(context, transactionAmount,
				transactionTimeMillis, currency);
		// Set transaction ID
		transaction.setTransactionId(cursor.getInt(idIndex));
		
		// Query description by name from the sqliteDB as set

		Description description = new Description();
		description.setId(cursor.getLong(desIdIndex));
		description.setDescription(cursor.getString(desNameIndex));
		transaction.setDescription(description);
		
		// Set account
		transaction.setAccountName(cursor.getString(accountNameIndex));
		transaction.setAccountId(cursor.getInt(accIdIndex));
		//Set category
		transaction.setCategory(cursor.getString(categoryIndex));
		//Set currency code
		
		return transaction;
	}

	

	private String joinTableStatement() {
		return ConstsDatabase.ACCOUNT_TABLE + " INNER JOIN "
				+ ConstsDatabase.TRANSACTION_TABLE + " ON "
				+ ConstsDatabase.ACCOUNT_ID + " = "
				+ ConstsDatabase.TRANSACTION_ACCOUNT + " INNER JOIN "
				+ ConstsDatabase.DESCRIPTION_TABLE + " ON "
				+ ConstsDatabase.DESCRIPTION_ID + " = "
				+ ConstsDatabase.TRANSACTION_DESCRIPTION;
	}

	
}
