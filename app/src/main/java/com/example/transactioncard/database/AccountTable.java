package com.example.transactioncard.database;

import java.util.ArrayList;

import com.example.transactioncard.object.Accounts;
import com.example.transactioncard.object.Currencies;
import com.example.transactioncard.object.Transaction;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class AccountTable {

	private static final String CLASSNAME = AccountTable.class.getName();
	private SQLiteDatabase sqliteDatabase;
	private CashFlowDB cashFlowDB;
	private Context context;

	public AccountTable(Context context) {
		String methodName = "AccountTable";
		String operation = "Create account table instance";
		// Log INFO
		ConstsDatabase.logINFO(CLASSNAME, methodName, operation);

		this.context = context;
		cashFlowDB = new CashFlowDB(context);
	}

	public void open() throws SQLException {
		String methodName = "open";
		String operation = "Get writable account table";

		try {
			// Log INFO
			ConstsDatabase.logINFO(CLASSNAME, methodName, operation);

			sqliteDatabase = cashFlowDB.getWritableDatabase();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			ConstsDatabase.logERROR(methodName, operation);
			e.printStackTrace();
		}
	}

	public void close() {
		cashFlowDB.close();
	}

	public Accounts insertAccountSqliteDB(Accounts account) {
		String methodName = "insertAccountSqliteDB";
		String operation = "Insert new account into sliteDB";

		/*
		 * Prepare content value for the account
		 */
		ContentValues contentValues = setContentValueAccount(account);

		Accounts acc = null;
		try {
			// Log INFO
			ConstsDatabase.logINFO(CLASSNAME, methodName, operation);

			// Insert new account to sqliteDB
			long insertId = sqliteDatabase.insert(ConstsDatabase.ACCOUNT_TABLE,
					null, contentValues);
			acc = getAccountById(insertId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			ConstsDatabase.logERROR(methodName, operation);
			e.printStackTrace();
		}
		return acc;
	}

	public Accounts editAccountsSqliteDB(Accounts account) {
		String methodName = "editAccountsSqliteDB";
		String operation = "Edit account into sliteDB";
		ConstsDatabase.logINFO(CLASSNAME, methodName, operation);
		Accounts returnAccounts = null;
		/*
		 * Get account to edit
		 */
		if (account != null) {
			/*
			 * Prepare the query
			 */
			ContentValues contentValues = setContentValueAccount(account);

			sqliteDatabase.update(ConstsDatabase.ACCOUNT_TABLE, contentValues,
					ConstsDatabase.ACCOUNT_ID + " =?", new String[] { ""
							+ account.getId() });
			/*
			 * Get the updates account
			 */
			Accounts editedAccount = getAccountById(account.getId());
			/*
			 * Check if the account has been updated
			 */
			if (editedAccount != null) {
				/*
				 * Compare account names
				 */
				if (editedAccount.getAccountName().equals(
						account.getAccountName())) {
					returnAccounts = editedAccount;
				} else {
					returnAccounts = null;
				}
			}
		}
		return returnAccounts;
	}

	public ArrayList<Accounts> getAccountAllBasic(){
		String methodName = "";
		String operation = "Get all account names from database";
		ArrayList<Accounts> accountList = null;

		String tableName = ConstsDatabase.ACCOUNT_TABLE;
		String returnColumn = ConstsDatabase.ACCOUNT_ID + ", " +ConstsDatabase.ACCOUNT_NAME;
		String query = String.format(ConstsDatabase.SQLSYNTX_QUERY_SELECT, returnColumn, tableName);

		ConstsDatabase.logINFO(CLASSNAME, methodName,operation);

		try{
			Cursor cursor = sqliteDatabase.rawQuery(query,null);

			if (cursor.moveToFirst()){
				int nameIndex = cursor
						.getColumnIndex(ConstsDatabase.ACCOUNT_NAME);
				int idIndex = cursor
						.getColumnIndex(ConstsDatabase.ACCOUNT_ID);
				accountList = new ArrayList<Accounts>();

				while (!cursor.isAfterLast()) {
					Accounts accounts = new Accounts(cursor.getLong(idIndex), cursor.getString(nameIndex));
					accountList.add(accounts);
					cursor.moveToNext();
				}


			}
		}catch (Exception ex){
			ConstsDatabase.logERROR(methodName,operation);
			ex.printStackTrace();
		}

		return accountList;
	}

	public ArrayList<Accounts> getAccountAll() {
		String methodName = "getAccountAll";
		String operation = "Get all accounts from the sqlite DB";

		/*
		 * Preparing the query
		 */

		ArrayList<Accounts> accountListView ;
		ArrayList<Accounts> accountListTable = new ArrayList<Accounts>();
		/*
		 * Query account in the sqliteDB
		 */
		try {
			// Log INFO
			ConstsDatabase.logINFO(CLASSNAME, methodName, operation);

			/*
			Quary all the account from the DB
			 */
			String accId = ConstsDatabase.ACCOUNT_ID;
			String accName = ConstsDatabase.ACCOUNT_NAME;
			String expense = ConstsDatabase.ACCOUNT_EXPENDITURE;
			String income = ConstsDatabase.ACCOUNT_INCOME;
			Cursor cursor1 = sqliteDatabase.query(ConstsDatabase.ACCOUNT_TABLE,
					new String[] { accId, accName, income, expense }, null,
					null, null, null, null);
			if (cursor1.moveToFirst()) {
				while (!cursor1.isAfterLast()) {
					Accounts accounts = cursorToAccountWhenNoData(cursor1);
					accountListTable.add(accounts);
					cursor1.moveToNext();
				}
			}
			cursor1.close();
			/*
			Iterate through the account and find the sum for each account
			 */
			for (int i = 0; i < accountListTable.size(); i++) {
				long id = accountListTable.get(i).getId();
				/*
				Query transactiontable for all transaction with the given ID
				 */
				String condition = String.format(ConstsDatabase.SQLSYNTX_CONDITION_EQUALS, ConstsDatabase.TRANSACTION_ACCOUNT);
				String[] conditionArgs = new String[]{""+id};

				Cursor cursor2 = sqliteDatabase.query(ConstsDatabase.TRANSACTION_TABLE,
						null,condition,conditionArgs,null,null,null);


				ArrayList<Transaction> list = new ArrayList<Transaction>();
				if (cursor2.moveToFirst()){
					while (!cursor2.isAfterLast()) {
						int amountIndex = cursor2
								.getColumnIndex(ConstsDatabase.TRANSACTION_AMOUNT);
						int timeIndex = cursor2.getColumnIndex(ConstsDatabase.TRANSACTION_TIME);
						int categoryIndex = cursor2
								.getColumnIndex(ConstsDatabase.TRANSACTION_CATEGORY);
						int currencyCodeIndex = cursor2.getColumnIndex(ConstsDatabase.TRANSACTION_CURRENCY);
						String currencyCode = cursor2.getString(currencyCodeIndex);
						if (currencyCode == null){
							currencyCode = Currencies.CURRENCY_CODE_US;
						}
						Transaction transaction = new Transaction(context, cursor2.getDouble(amountIndex),
								cursor2.getLong(timeIndex), currencyCode);

						transaction.setCategory(cursor2.getString(categoryIndex));

						list.add(transaction);
						cursor2.moveToNext();
					}
				}

				/*
				Calculate sum base on category
				 */
				double expenseSum = 0;
				double incomeSum = 0;
				/*

				 */
				if (list.size() > 0){

					for (int j = 0; j < list.size(); j++) {
						if (list.get(j).getCategory().equals(ConstsDatabase.CATEGORY_EXPENSES)){
							expenseSum += list.get(j).getAmountInDefaultCurrency(context);
						}else if(list.get(j).getCategory().equals(ConstsDatabase.CATEGORY_INCOME)) {
							incomeSum += list.get(j).getAmountInDefaultCurrency(context);
						}
					}
				}

				accountListTable.get(i).setExpenditure(expenseSum);
				accountListTable.get(i).setIncome(incomeSum);
				cursor2.close();
			}



		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return accountListTable;
	}

	public Accounts getAccountByName(String name) {
		String methodName = "getAccountByName";
		String operation = "Get account by name from the sqlite DB";
		/*
		 * Prepare the query
		 */
		String query = "select * from " + ConstsDatabase.VIEW_ACCOUNT_SUMMARY
				+ " where " + ConstsDatabase.VIEW_ACCOUNT_NAME + " =?";
		Accounts account = null;
		Cursor cursor = null;
		/*
		 * Query account from DB
		 */
		try {
			/*
			 * Query from the viewTable
			 */
			cursor = sqliteDatabase.rawQuery(query, new String[] { name });
			if (cursor.moveToFirst()) {
				ConstsDatabase.logINFO(CLASSNAME, methodName, operation);
				/*
				 * Point on the start of the row
				 */
				if (cursor.getCount() == 2) {
					/*
					 * View account table is not empty, get the index fields
					 */
					int indexId = cursor
							.getColumnIndex(ConstsDatabase.VIEW_ACCOUNT_ID);
					int indexName = cursor
							.getColumnIndex(ConstsDatabase.VIEW_ACCOUNT_NAME);
					int indexCategory = cursor
							.getColumnIndex(ConstsDatabase.VIEW_TRANSACTION_CATEGORY);
					int indexTotal = cursor
							.getColumnIndex(ConstsDatabase.VIEW_TOTAL);
					/*
					 * Get account parameters from the field indexes
					 */
					long id = cursor.getLong(indexId);
					String accountName = cursor.getString(indexName);
					String category = cursor.getString(indexCategory);
					double total = cursor.getDouble(indexTotal);
					account = new Accounts(context, accountName);
					account.setId(id);
					/*
					 * Get total expenditure or total income
					 */
					if (category.equals(ConstsDatabase.CATEGORY_EXPENSES)) {
						account.setExpenditure(total);
					} else if (category.equals(ConstsDatabase.CATEGORY_INCOME)) {
						account.setIncome(total);
					}
					cursor.moveToNext();
					if (!cursor.isAfterLast()) {
						/*
						 * Point to the second row
						 */
						String accountName1 = cursor.getString(indexName);
						String category1 = cursor.getString(indexCategory);
						double total1 = cursor.getDouble(indexTotal);
						if (accountName.equals(accountName1)) {
							/*
							 * Get total expenditure or total income
							 */
							if (category1
									.equals(ConstsDatabase.CATEGORY_EXPENSES)) {
								account.setExpenditure(total1);
							} else if (category
									.equals(ConstsDatabase.CATEGORY_INCOME)) {
								account.setIncome(total1);
							}
						}
					}
				}
				ConstsDatabase.logINFO(CLASSNAME, methodName, "Account: "
						+ name + " was found!!!");
				cursor.close();
			}
			if (account == null) {
				ConstsDatabase.logINFO(CLASSNAME, methodName, "Account: "
						+ name + " not found in: "
						+ ConstsDatabase.VIEW_ACCOUNT_SUMMARY);
				/*
				 * ViewAccount table is empty, query from the account table
				 */
				String accId = ConstsDatabase.ACCOUNT_ID;
				String accName = ConstsDatabase.ACCOUNT_NAME;
				String expense = ConstsDatabase.ACCOUNT_EXPENDITURE;
				String income = ConstsDatabase.ACCOUNT_INCOME;
				Cursor cursor1 = sqliteDatabase.query(
						ConstsDatabase.ACCOUNT_TABLE, new String[] { accId,
								accName, income, expense }, accName + " = ?",
						new String[] { name }, null, null, null);
				if (cursor1.moveToFirst()) {
					while (!cursor1.isAfterLast()) {
						account = cursorToAccountWhenNoData(cursor1);
						cursor1.moveToNext();
					}
					cursor1.close();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			ConstsDatabase.logERROR(methodName, operation);
			e.printStackTrace();
		}
		return account;
	}

	public Accounts getAccountById(long accountId) {
		String methodName = "getAccountById";
		String operation = "Query for a single account from the DB by ID";
		/*
		 * Prepare the query
		 */
		String id = Long.toString(accountId);
		String query = "select * from " + ConstsDatabase.VIEW_ACCOUNT_SUMMARY
				+ " where " + ConstsDatabase.VIEW_ACCOUNT_ID + " =?";
		Accounts account = null;
		Cursor cursor = null;
		try {
			/*
			 * Query for the account from the viewAccount table
			 */
			ConstsDatabase.logINFO(CLASSNAME, methodName, operation);

			cursor = sqliteDatabase.rawQuery(query, new String[] { id });
			if (cursor.moveToFirst()) {
				if (cursor.getCount() == 2) {
					/*
					 * View account table is not empty, get field table indexes
					 */
					ConstsDatabase.logINFO(CLASSNAME, methodName,
							ConstsDatabase.VIEW_ACCOUNT_SUMMARY + " not empty");
					int indexId = cursor
							.getColumnIndex(ConstsDatabase.VIEW_ACCOUNT_ID);
					int indexName = cursor
							.getColumnIndex(ConstsDatabase.VIEW_ACCOUNT_NAME);
					int indexCategory = cursor
							.getColumnIndex(ConstsDatabase.VIEW_TRANSACTION_CATEGORY);
					int indexTotal = cursor
							.getColumnIndex(ConstsDatabase.VIEW_TOTAL);
					/*
					 * Get account parameter from the cursor
					 */
					long accId = cursor.getLong(indexId);
					String accountName = cursor.getString(indexName);
					String category = cursor.getString(indexCategory);
					double total = cursor.getDouble(indexTotal);
					account = new Accounts(context, accountName);
					/*
					 * Fill in income and expenses parameter
					 */
					account.setId(accId);
					if (category.equals(ConstsDatabase.CATEGORY_EXPENSES)) {
						account.setExpenditure(total);
					} else if (category.equals(ConstsDatabase.CATEGORY_INCOME)) {
						account.setIncome(total);
					}
					/*
					 * Point to the next row of the cursor
					 */
					cursor.moveToNext();
					if (!cursor.isAfterLast()) {
						String accountName1 = cursor.getString(indexName);
						String category1 = cursor.getString(indexCategory);
						double total1 = cursor.getDouble(indexTotal);
						if (accountName.equals(accountName1)) {
							if (category1
									.equals(ConstsDatabase.CATEGORY_EXPENSES)) {
								account.setExpenditure(total1);
							} else if (category
									.equals(ConstsDatabase.CATEGORY_INCOME)) {
								account.setIncome(total1);
							}
						}
					}
				}
				cursor.close();
			}
			if (account == null) {
				/*
				 * View account table is empt
				 */
				ConstsDatabase.logINFO(CLASSNAME, methodName,
						ConstsDatabase.VIEW_ACCOUNT_SUMMARY + " is empty");

				/*
				 * Query account from the account table
				 */
				String accId = ConstsDatabase.ACCOUNT_ID;
				String accName = ConstsDatabase.ACCOUNT_NAME;
				String expense = ConstsDatabase.ACCOUNT_EXPENDITURE;
				String income = ConstsDatabase.ACCOUNT_INCOME;
				Cursor cursor1 = sqliteDatabase.query(
						ConstsDatabase.ACCOUNT_TABLE, new String[] { accId,
								accName, income, expense }, accId + " = ?",
						new String[] { id }, null, null, null);
				if (cursor1.moveToFirst()) {
					while (!cursor1.isAfterLast()) {
						account = cursorToAccountWhenNoData(cursor1);
						cursor1.moveToNext();
					}
					cursor1.close();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			ConstsDatabase.logERROR(methodName, operation);
			e.printStackTrace();
		}
		return account;
	}

	public void deleteAccountSqliteDB(Accounts account) {
		String methodName = "deleteAccountSqliteDB";
		String operation = "Delete specified account from the DB";

		ConstsDatabase.logINFO(CLASSNAME, methodName, operation);
		/*
		 * First delete transaction with the given account ID
		 */

		long id = account.getId();

		try {
			int deletedRowsTransactions = sqliteDatabase.delete(
					ConstsDatabase.TRANSACTION_TABLE,
					ConstsDatabase.TRANSACTION_ACCOUNT + " = ?",
					new String[] { Long.toString(id) });
			ConstsDatabase.logINFO(CLASSNAME, methodName,
					deletedRowsTransactions + ": Transaction(s) deleted");
			/*
			 * Delete the specified account from the DB
			 */
			sqliteDatabase.delete(ConstsDatabase.ACCOUNT_TABLE,
					ConstsDatabase.ACCOUNT_ID + " = ?",
					new String[] { Long.toString(id) });
			ConstsDatabase.logINFO(CLASSNAME, methodName,
					"Account deleted successful");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			ConstsDatabase.logERROR(methodName, operation);
			e.printStackTrace();
		}

	}

	/**
	 * @param account
	 * @return
	 */
	private ContentValues setContentValueAccount(Accounts account) {
		String methodName = "setContentValueAccount";
		String operation = "Set content value for the account";

		// Log INFO
		ConstsDatabase.logINFO(CLASSNAME, methodName, operation);

		ContentValues contentValues = new ContentValues();

		// put values to the content value
		contentValues
				.put(ConstsDatabase.ACCOUNT_NAME, account.getAccountName());
		contentValues.put(ConstsDatabase.ACCOUNT_BALANCE, 0);
		contentValues.put(ConstsDatabase.ACCOUNT_INCOME, 0);
		contentValues.put(ConstsDatabase.ACCOUNT_EXPENDITURE, 0);
		return contentValues;
	}

	private Accounts cursorToAccountWhenNoData(Cursor cursor) {
		String methodName = "cursorToAccountWhenNoData";
		String operation = "Convert cursor to account, when there is no data";
		/*
		 * Get account field index
		 */
		ConstsDatabase.logINFO(CLASSNAME, methodName, operation);
		int idIndex = cursor.getColumnIndex(ConstsDatabase.ACCOUNT_ID);
		int nameIndex = cursor.getColumnIndex(ConstsDatabase.ACCOUNT_NAME);
		int incomeIndex = cursor.getColumnIndex(ConstsDatabase.ACCOUNT_INCOME);
		int expensesIndex = cursor
				.getColumnIndex(ConstsDatabase.ACCOUNT_EXPENDITURE);
		/*
		 * Create new account instance and fills in the account param(s)
		 */
		int accId = cursor.getInt(idIndex);
		String name = cursor.getString(nameIndex);
		Double income = cursor.getDouble(incomeIndex);
		Double expense = cursor.getDouble(expensesIndex);
		Accounts accounts = new Accounts(context, name);
		accounts.setId(accId);
		accounts.setExpenditure(expense);
		accounts.setIncome(income);
		return accounts;
	}
}
