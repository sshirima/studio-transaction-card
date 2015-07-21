package com.example.transactioncard.database;

import com.example.transactioncard.BuildConfig;
import com.example.transactioncard.object.Accounts;
import com.example.transactioncard.object.Currencies;
import com.example.transactioncard.object.Currency;
import com.example.transactioncard.object.Description;
import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CashFlowDB extends SQLiteOpenHelper {

	private static final String CLASSNAME = CashFlowDB.class.getName();

	/*
	 * Create transactionTable statement
	 */
	private static final String CREATE_TRANSACTION_TABLE = "create table "
			+ ConstsDatabase.TRANSACTION_TABLE + "("
			+ ConstsDatabase.TRANSACTION_ID
			+ " integer primary key autoincrement, "
			+ ConstsDatabase.TRANSACTION_AMOUNT + " text not null, "
			+ ConstsDatabase.TRANSACTION_DESCRIPTION + " integer, "
			+ ConstsDatabase.TRANSACTION_TIME + " integer not null, "
			+ ConstsDatabase.TRANSACTION_ACCOUNT + " integer not null, "
			+ ConstsDatabase.TRANSACTION_CATEGORY + " text not null, "
			+ ConstsDatabase.TRANSACTION_CURRENCY + " text," + " FOREIGN KEY"
			+ " (" + ConstsDatabase.TRANSACTION_DESCRIPTION + ") REFERENCES "
			+ ConstsDatabase.DESCRIPTION_TABLE + "("
			+ ConstsDatabase.DESCRIPTION_ID + ")," + " FOREIGN KEY("
			+ ConstsDatabase.TRANSACTION_ACCOUNT + ") REFERENCES "
			+ ConstsDatabase.ACCOUNT_TABLE + "(" + ConstsDatabase.ACCOUNT_ID
			+ "));";
	/*
	 * Create accountTable statement
	 */
	private static final String CREATE_ACCOUNT_TABLE = "create table "
			+ ConstsDatabase.ACCOUNT_TABLE + "(" + ConstsDatabase.ACCOUNT_ID
			+ " integer primary key autoincrement, "
			+ ConstsDatabase.ACCOUNT_BALANCE + " integer, "
			+ ConstsDatabase.ACCOUNT_NAME + " text not null, "
			+ ConstsDatabase.ACCOUNT_CURRENCY + " text, "
			+ ConstsDatabase.ACCOUNT_INCOME + " integer, "
			+ ConstsDatabase.ACCOUNT_EXPENDITURE + " integer,"
			+ ConstsDatabase.ACCOUNT_IS_DEFAULT + " text default false);";
	/*
	 * Create descriptionTable statement
	 */
	private static final String CREATE_DESCRIPTION_TABLE = "create table "
			+ ConstsDatabase.DESCRIPTION_TABLE + "("
			+ ConstsDatabase.DESCRIPTION_ID
			+ " integer primary key autoincrement, "
			+ ConstsDatabase.DESCRIPTION_NAME + " text not null);";
	/*
	 * Create currencyTable statement
	 */
	private static final String CREATE_CURRENCY_TABLE = " create table " 
			+ConstsDatabase.CURRENCY_TABLE +"("
			+ConstsDatabase.CURRENCY_ID
			+" integer primary key autoincrement, "
			+ConstsDatabase.CURRENCY_CODE + " text not null, "
			+ConstsDatabase.CURRENCY_NAME + " text, "
			+ConstsDatabase.CURRENCY_FLAG + " text, "
			+ConstsDatabase.CURRENCY_RATE + " text not null"+
			");";
	private Context context;

	public CashFlowDB(Context context) {
		super(context, ConstsDatabase.DATABASE_NAME, null,
				ConstsDatabase.DATABASE_VERSION);
		// TODO Auto-generated constructor stub
		String methodName = "CashFlowDB";
		String operation  = "Create instance";
		/*
		 * Initializes class global variable
		 */
		this.context = context;
		logINFO(methodName, operation);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		// TODO Auto-generated method stub
		String methodName = "onCreate";
		String operation = "Create database tables";
		String pointer = "Create new tables on sqliteDB";
		try {
			database.execSQL(CREATE_TRANSACTION_TABLE);
			pointer = "Create account table";
			database.execSQL(CREATE_ACCOUNT_TABLE);
			pointer = "Create descripition table";
			database.execSQL(CREATE_DESCRIPTION_TABLE);
			pointer = "Create views";
			database.execSQL(createView());
			logINFO(methodName, operation);
			/*
			 * Creating default account, Personal account and Work account
			 */
			createDefaultAccount(database);
			createDefaultDescription(database);
		} catch (SQLException e) {
			// TODO: handle exception
			if (BuildConfig.DEBUG) {
				Log.e(methodName, String.format(ConstsDatabase.STRERR_FailedTo, pointer));
			}
		}
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		String methodName = "onUpgrade";
		String operation = "Upgrade sqliteDB from "+oldVersion +" to "+newVersion;
		Log.w(CLASSNAME, operation);
		
		try {
			int nextVersion = oldVersion+1;
			while (nextVersion <= newVersion){
				
			    	switch (nextVersion) {
					case 2:
						/*
						 * Add currency table
						 */
						db.execSQL(CREATE_CURRENCY_TABLE);
						
						nextVersion = nextVersion +1;
						break;
					case 3:
						/*
						 * Add default currency
						 */
						ContentValues contentValues = new ContentValues();
						
						String[] currencyCodes = Currencies.getAllCurrencyCode();
						String[] currencyNames = Currencies.getDetailedCurrencyList();
						for (int i = 0; i < currencyCodes.length; i++) {
							String currencyCode = currencyCodes[i];
							String currencyName = currencyNames[i];
							int flagId = Currencies.getCurrecnyFlag(currencyCode);
							double rate = Currencies.getDefaultRates(currencyCode);
							
							Currency currency = new Currency(context, currencyCode);
							currency.setCurrencyName(currencyName);
							currency.setFlagId(flagId);
							currency.setCurrencyRate(rate);
							
							setDefaultContentValueCurrency(contentValues, currency);
							
							db.insert(ConstsDatabase.CURRENCY_TABLE, null,
									contentValues);
							
						}
						nextVersion = nextVersion +1;
						break;
						
					}
				
			}
			
			
		    
		} catch (SQLException e) {
			// TODO: handle exception
			if (BuildConfig.DEBUG) {
				String errorMsg = "Upgrade the DB from"+oldVersion+" to "+newVersion;
				Log.e(methodName, String.format(ConstsDatabase.STRERR_FailedTo, errorMsg));
			}
		}
		
	}

	private void setDefaultContentValueCurrency(ContentValues contentValues,
			Currency currency) {
		contentValues.put(ConstsDatabase.CURRENCY_CODE,
				currency.getCurrencyCode());
		contentValues.put(ConstsDatabase.CURRENCY_NAME,
				currency.getCurrencyName());
		contentValues.put(ConstsDatabase.CURRENCY_RATE,
				currency.getRateFromUSD());
		contentValues.put(ConstsDatabase.CURRENCY_FLAG,
				currency.getFlagId());
	}

	

	private void createDefaultAccount(SQLiteDatabase database) {
		String methodName = "createDefaultAccount";
		String operation = "Create default accounts";
		String DEFAULT_ACCOUNT_NAME1 = "Personal";
		String DEFAULT_ACCOUNT_NAME2 = "Work";
		try {
			database.insert(ConstsDatabase.ACCOUNT_TABLE, null,
					defaultAccountValues(DEFAULT_ACCOUNT_NAME1));
			database.insert(ConstsDatabase.ACCOUNT_TABLE, null,
					defaultAccountValues(DEFAULT_ACCOUNT_NAME2));
			logINFO(methodName, operation);

		} catch (Exception e) {
			// TODO: handle exception
			if (BuildConfig.DEBUG) {
				String errorMsg = "Insert default account into DB";
				Log.e(methodName, String.format(ConstsDatabase.STRERR_FailedTo, errorMsg));
			}
		}

	}

	private ContentValues defaultAccountValues(String ACCOUNT_N) {
		String methodName = "defaultAccountValues";
		String operation = "Create new default account, fill in contentValues";
		ContentValues values = new ContentValues();
		try {
			Accounts account = new Accounts(this.context, ACCOUNT_N);
			account.setDefault(true);
			values.put(ConstsDatabase.ACCOUNT_NAME, account.getAccountName());
			values.put(ConstsDatabase.ACCOUNT_BALANCE, 0);
			values.put(ConstsDatabase.ACCOUNT_INCOME, 0);
			values.put(ConstsDatabase.ACCOUNT_EXPENDITURE, 0);
			values.put(ConstsDatabase.ACCOUNT_IS_DEFAULT, account.isDefault());
			logINFO(methodName, operation);
		} catch (Exception e) {
			// TODO: handle exception
			if (BuildConfig.DEBUG) {
				String errorMsg = "Create ContectValues for default account";
				Log.e(methodName, String.format(ConstsDatabase.STRERR_FailedTo, errorMsg));
			}
		}

		return values;
	}

	private ContentValues defaultDescriptionValues(String descriptionName) {
		String methodName = "defaultDescriptionValues";
		String operation = "Fill in contentValues with default Description parameters";
		/*
		 * Create default description instance
		 */
		Description description = new Description();
		description.setDescription(descriptionName);
		
		ContentValues values = new ContentValues();
		/*
		 * Fill in the contentValue with description parameters
		 */
		try {
			values.put(ConstsDatabase.DESCRIPTION_NAME,
					description.getDescription());
			logINFO(methodName, operation);
		} catch (Exception e) {
			// TODO: handle exception
			String errorMsg = "Create ContectValues for default description";
			Log.e(methodName, String.format(ConstsDatabase.STRERR_FailedTo, errorMsg));
		}
		
		return values;
	}

	private void createDefaultDescription(SQLiteDatabase database) {
		String methodName = "createDefaultDescription";
		String operation = "Insert default description to sqliteDB";
		final String defaultDesc = "No description";
		try {
			database.insert(ConstsDatabase.DESCRIPTION_TABLE, null,
					defaultDescriptionValues(defaultDesc));
			logINFO(methodName, operation);
		} catch (Exception e) {
			// TODO: handle exception
			String errorMsg = "Insert default description to DB";
			Log.e(methodName, String.format(ConstsDatabase.STRERR_FailedTo, errorMsg));
		}
		

	}

	/**
	 * @param methodName
	 * @param operation
	 */
	private void logINFO(String methodName, String operation) {
		if (BuildConfig.DEBUG){
			String logMsg = ">>>"+operation;
			Log.d(CLASSNAME+"."+methodName, logMsg);
		}
	}

	private String createView() {
		String accIdV = ConstsDatabase.ACCOUNT_TABLE + "."
				+ ConstsDatabase.ACCOUNT_ID + " AS "
				+ ConstsDatabase.VIEW_ACCOUNT_ID;
		String accNameV = ConstsDatabase.ACCOUNT_TABLE + "."
				+ ConstsDatabase.ACCOUNT_NAME + " AS "
				+ ConstsDatabase.VIEW_ACCOUNT_NAME;
		String categoryV = ConstsDatabase.TRANSACTION_TABLE + "."
				+ ConstsDatabase.TRANSACTION_CATEGORY + " AS "
				+ ConstsDatabase.VIEW_TRANSACTION_CATEGORY;
		String columns = accIdV + ", " + accNameV + ", " + categoryV + ", "
				+ "SUM(" + ConstsDatabase.TRANSACTION_AMOUNT + ") AS "
				+ ConstsDatabase.VIEW_TOTAL;
		String table = ConstsDatabase.TRANSACTION_TABLE + " INNER JOIN "
				+ ConstsDatabase.ACCOUNT_TABLE + " ON "
				+ ConstsDatabase.ACCOUNT_TABLE + "."
				+ ConstsDatabase.ACCOUNT_ID + " = "
				+ ConstsDatabase.TRANSACTION_TABLE + "."
				+ ConstsDatabase.TRANSACTION_ACCOUNT;
		String groupBy = ConstsDatabase.VIEW_ACCOUNT_ID + ", "
				+ ConstsDatabase.VIEW_TRANSACTION_CATEGORY;
		String query = " SELECT " + columns + " FROM " + table + " GROUP BY "
				+ groupBy;
		String createView = "CREATE VIEW "
				+ ConstsDatabase.VIEW_ACCOUNT_SUMMARY + " AS ";
		String createViewStatement = createView + query;
		return createViewStatement;
	}

}
