package com.example.transactioncard.database;

import java.util.ArrayList;
import java.util.List;

import com.example.transactioncard.object.Description;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DescriptionTable {
	
	private static final String CLASSNAME = DescriptionTable.class.getName();

	private SQLiteDatabase sqliteDatabase;
	private CashFlowDB cashFlowDB;

	public DescriptionTable(Context context) {
		String methodName = "DescriptionTable";
		String operation = "Create new instance of the description table";
		
		ConstsDatabase.logINFO(CLASSNAME, methodName, operation);
		
		cashFlowDB = new CashFlowDB(context);
	}

	public void open() throws SQLException {
		sqliteDatabase = cashFlowDB.getWritableDatabase();
	}

	public void close() {
		cashFlowDB.close();
	}
	
	public Description getDefaultDescription(){
		String methodName = "getDefaultDescription";
		String operation = "Get default description";
		/*
		 * Get default description
		 */
		Description description = null;
		String defaultDescription = "No description";
		String inTables = joinTableStatement();
		
		ConstsDatabase.logINFO(CLASSNAME, methodName, operation);
		/*
		 * Prepare a query
		 */
		String query = "SELECT * FROM " + inTables + " WHERE "
				+ ConstsDatabase.DESCRIPTION_NAME +" =? ";
		Cursor cursor = sqliteDatabase.rawQuery(query, new String[]{defaultDescription});
		/*
		 * Fetch the description from the cursor
		 */
		if (cursor.moveToFirst()){
			description = cursorToDescription(cursor);
		}
		cursor.close();
		return description;
	}

	public Description insertDescriptionSqliteDB(Description description) {
		String methodName = "insertDescriptionSqliteDB";
		String operation = "Insert new description into the DB";
		
		/*
		 * Insert new Description into the sqliteDB
		 */
		ContentValues contentValues = new ContentValues();
		Description newDescription = null;
		try {
			ConstsDatabase.logINFO(CLASSNAME, methodName, operation);
			
			contentValues.put(ConstsDatabase.DESCRIPTION_NAME,
					description.getDescription());
			//Insert new description into Description table
			long insertId = sqliteDatabase.insert(ConstsDatabase.DESCRIPTION_TABLE, null,
					contentValues);
			
			newDescription = getDescriptionById(insertId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			ConstsDatabase.logERROR(methodName, operation);
			e.printStackTrace();
		}
		return newDescription;
	}

	public void deleteDescriptionSqliteDB(Description description){
		String methodName = "deleteDescriptionSqliteDB";
		String operation = "Delete description from descriptionTable";
		ConstsDatabase.logINFO(CLASSNAME, methodName, operation);
		
		/*
		 * First delete transactions with the given description
		 */
		int deletedTransactions = sqliteDatabase.delete(ConstsDatabase.TRANSACTION_TABLE, ConstsDatabase.TRANSACTION_DESCRIPTION+" =?", new String[]{description.getDescription()});
		ConstsDatabase.logINFO(CLASSNAME, methodName, deletedTransactions+": Transactions were deleted");
		
		/*
		 * Then delete the description from the table
		 */
		long id = description.getId();
		
		int deletedDescription = sqliteDatabase.delete(ConstsDatabase.DESCRIPTION_TABLE,
				ConstsDatabase.DESCRIPTION_ID + " =?",
				new String[] { Long.toString(id) });
		
		if (deletedDescription == 1) {
			ConstsDatabase.logINFO(CLASSNAME, methodName, "Description: "
					+ description.getDescription() + " was deleted successful");
		}else {
			ConstsDatabase.logERROR(methodName, operation);
		}
	
	}

	public List<Description> getDescriptionsAll(){
		String methodName = "getDescriptionsAll";
		String operation = "Get all description from the account table";
		/*
		 * Prepare the query
		 */
		String inTables = joinTableStatement();
		String query = "SELECT * FROM " + inTables;
		
		ConstsDatabase.logINFO(CLASSNAME, methodName, operation);
		
		Cursor cursor = sqliteDatabase.rawQuery(query,null);
		List<Description> returnList = new ArrayList<Description>();
		
		/*
		 * Extract description(s) from the cursor
		 */
		if (cursor.moveToFirst()){
			
			while (!cursor.isAfterLast()) {
				Description newDescription = cursorToDescription(cursor);
				returnList.add(newDescription);
				cursor.moveToNext();
			}
		}
		return returnList;
	}
	
	public Description getDescriptionByName(String name) {
		String methodName = "getDescriptionByName";
		String operation = "Get single description by name";
		/*
		 * Prepare the query 
		 */
		Description description = null;
		String inTables = joinTableStatement();
		String selections = ConstsDatabase.DESCRIPTION_TABLE + "."
				+ ConstsDatabase.DESCRIPTION_NAME + " = ?";
		/*
		 * Query for the description from sqlite DB
		 */
		String query = "SELECT * FROM " + inTables + " WHERE " + selections;
		Cursor cursor = null;
		try {
			cursor = sqliteDatabase.rawQuery(query, new String[] { name });
			/*
			 * Extract description from the cursor
			 */
			if (cursor.moveToFirst()) {
				
				ConstsDatabase.logINFO(CLASSNAME, methodName, operation);
				while (!cursor.isAfterLast()) {
					/*
					 * Convert cursor to description
					 */
					description = cursorToDescription(cursor);
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

	public Description getDescriptionById(long id) {
		String methodName = "getDescriptionById";
		String operation = "Get description from the description table";
		/*
		 * Prepare the query
		 */
		Description description = null;
		String inTables = joinTableStatement();
		String selections = ConstsDatabase.DESCRIPTION_TABLE + "."
				+ ConstsDatabase.DESCRIPTION_ID + " = ?";
		/*
		 * Query the database from the given ID
		 */
		String query = "SELECT * FROM " + inTables + " WHERE " + selections;
		Cursor cursor= null;
		try {
			ConstsDatabase.logINFO(CLASSNAME, methodName, operation);
			cursor = sqliteDatabase.rawQuery(query,
					new String[] { Long.toString(id) });
			if (cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					description = cursorToDescription(cursor);
					cursor.moveToNext();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cursor.close();
		return description;
	}

	public static Description cursorToDescription(Cursor cursor) {
		// TODO Auto-generated method stub
		String methodName = "cursorToDescription";
		String operation = "Convert cursor to description instance";
		ConstsDatabase.logINFO(CLASSNAME, methodName, operation);
		/*
		 * Get field index for the cursor
		 */
		int idIndex = cursor.getColumnIndex(ConstsDatabase.DESCRIPTION_ID);
		int descriptionIndex = cursor
				.getColumnIndex(ConstsDatabase.DESCRIPTION_NAME);
		Description description = new Description();
		description.setId(cursor.getInt(idIndex));
		description.setDescription(cursor.getString(descriptionIndex));
		return description;
	}

	public static String joinTableStatement() {
		return ConstsDatabase.DESCRIPTION_TABLE ;
	}
}