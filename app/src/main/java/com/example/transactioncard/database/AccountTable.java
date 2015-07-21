package com.example.transactioncard.database;

import java.util.ArrayList;

import com.example.transactioncard.object.Accounts;
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

	public ArrayList<Accounts> getAccountAll() {
		String methodName = "getAccountAll";
		String operation = "Get all accounts from the sqlite DB";

		/*
		 * Preparing the query
		 */
		String query = "select * from " + ConstsDatabase.VIEW_ACCOUNT_SUMMARY;

		Cursor cursor = null;

		ArrayList<Accounts> accountListView = new ArrayList<Accounts>();
		ArrayList<Accounts> accountListTable = new ArrayList<Accounts>();
		/*
		 * Query account in the sqliteDB
		 */
		try {
			// Log INFO
			ConstsDatabase.logINFO(CLASSNAME, methodName, operation);

			// Query transactions
			cursor = sqliteDatabase.rawQuery(query, null);
			accountListView = new ArrayList<Accounts>();
			/*
			 * Check if the the accountView is empty Cursor format view form the
			 * query SELECT * FROM viewAccount Account id | Account name | Sum |
			 * Category 1 | Personal | 1200 | Expense 1 | Personal | 300 |
			 * Income 2 | Work | 600 | Expense 2 | Work | 700 | Income
			 */
			if (cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					/*
					 * Get index for each account table field
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
					 * Get account parameters from the specified field index
					 */
					long id = cursor.getLong(indexId);
					String name = cursor.getString(indexName);
					String category = cursor.getString(indexCategory);
					double total = cursor.getDouble(indexTotal);
					/*
					 * Move to the next row of the cursor
					 */
					cursor.moveToNext();

					if (!cursor.isAfterLast()) {
						/*
						 * Get id and category of the next row
						 */
						long id1 = cursor.getLong(indexId);
						String category1 = cursor.getString(indexCategory);
						/*
						 * Compare the id and category between two rows
						 */
						if (id == id1 & !category.equals(category1)) {
							/*
							 * Two consecutive rows have the same account id,
							 * but different categories
							 */
							double total1 = cursor.getDouble(indexTotal);
							/*
							 * Create new account instance, and fills in the
							 * parameters
							 */
							Accounts account = new Accounts(context, name);
							account.setId(id);
							if (category
									.equals(ConstsDatabase.CATEGORY_EXPENSES)) {
								account.setExpenditure(total);
								account.setIncome(total1);
							} else {
								account.setExpenditure(total1);
								account.setIncome(total);
							}

							accountListView.add(account);
						} else {
							/*
							 * Two consecutive rows has different account id,
							 * create instance of the previous row, and fill in
							 * the parameters
							 */
							Accounts account = new Accounts(context, name);
							account.setId(id);
							if (category
									.equals(ConstsDatabase.CATEGORY_EXPENSES)) {
								account.setExpenditure(total);
							} else if (category
									.equals(ConstsDatabase.CATEGORY_INCOME)) {
								account.setIncome(total);
							}
							accountListView.add(account);
							cursor.moveToPrevious();
						}
					} else if (cursor.isAfterLast()) {
						/*
						 * Last row account details which is different from the
						 * previous
						 */
						Accounts account = new Accounts(context, name);
						account.setId(id);
						if (category.equals(ConstsDatabase.CATEGORY_EXPENSES)) {
							account.setExpenditure(total);
						} else if (category
								.equals(ConstsDatabase.CATEGORY_INCOME)) {
							account.setIncome(total);
						}
						accountListView.add(account);
					}
					cursor.moveToNext();
				}

			}
			/*
			 * Query for all account in the table
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
			/*
			 * WORK AROUND to get all the accounts
			 */

			for (int i = 0; i < accountListTable.size(); i++) {
				long iID = accountListTable.get(i).getId();
				for (int j = 0; j < accountListView.size(); j++) {
					long jID = accountListView.get(j).getId();
					if (iID == jID) {
						accountListTable.remove(i);
						accountListTable.add(i, accountListView.get(j));
						break;
					}
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		cursor.close();
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
