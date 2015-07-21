package com.example.transactioncard;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.example.transactioncard.HomeActivity.HomeListViewAdapter;
import com.example.transactioncard.database.AccountTable;
import com.example.transactioncard.database.ConstsDatabase;
import com.example.transactioncard.database.TransactionTable;
import com.example.transactioncard.object.Accounts;
import com.example.transactioncard.object.Transaction;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class AccountDetailActivity extends Activity{

	private final String CLASSNAMME = AccountDetailActivity.class.getName();
	private ListView lvViewbyDesc;
	private ActionBar actionBar;
	private Accounts selectedAccount;
	private int expenseSum = 0;
	private int incomeSum = 0;
	private final String SUBTITLE_MSG = "Income: %s, Expenses: %s";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		/*
		 * Set up content view for the activity
		 */
		setContentView(R.layout.activity_account_details);
		/*
		 * Setup the action bar
		 */
		actionBar = getActionBar();
		/*
		 * Set up the activity list view
		 */
		setUpListView();
		/*
		 * Get the selected description id
		 */
		Bundle bundle = getIntent().getExtras();
		long id = bundle.getLong(Consts.STRBUNDLE_ACCOUNT_ID);
		AccountTable accountTable = new AccountTable(getApplicationContext());
		accountTable.open();
		selectedAccount = accountTable.getAccountById(id);
		accountTable.close();
		/*
		 * Query transaction from the DB based on the selected description Id
		 */
		transactionList = getTransactioByAccount(selectedAccount);
		/*
		 * Add the transaction list to the listtview adapter
		 */
		listAdapter.addAll(transactionList);
		
		String currency = Settings.getDefaultCurrency(getApplicationContext());
		
		calculateTotal();
		/*
		 * Set action bar subtitle message
		 */
		String expenseTotal = new DecimalFormat().format((int) Math.round(expenseSum))+ " "+currency ;
		String incomeTotal =new DecimalFormat().format((int) Math.round(incomeSum))+ " "+currency;
		
		String suTitleMsg = String.format(SUBTITLE_MSG, incomeTotal, expenseTotal);
		actionBar.setTitle(selectedAccount.getAccountName());
		actionBar.setSubtitle(suTitleMsg);
		/*
		 * Set adapter to the activity list view
		 */
		lvViewbyDesc.setAdapter(listAdapter);
	}

	private void calculateTotal() {
		for (int i = 0; i < transactionList.size(); i++) {
			if (transactionList.get(i).getCategory().equals(ConstsDatabase.CATEGORY_EXPENSES)){
				expenseSum += transactionList.get(i).getAmount(getApplicationContext());
			} else if (transactionList.get(i).getCategory().equals(ConstsDatabase.CATEGORY_INCOME)){
				incomeSum += transactionList.get(i).getAmount(getApplicationContext());
			}
		}
	}
	
	private HomeListViewAdapter listAdapter;
	private ArrayList<Transaction> transactionList;

	private void setUpListView() {
		lvViewbyDesc = (ListView)findViewById(R.id.lvViewbyDescription);
		listAdapter = new HomeListViewAdapter(getApplicationContext());
		
	}
	
	private ArrayList<Transaction> getTransactioByAccount(Accounts account) {
		// TODO Auto-generated method stub
		String methodName = "getTransactioByAccount";
		String operation = "Get transactions from DB based on the account";
		ConstsDatabase.logINFO(CLASSNAMME, methodName, operation);
		
		/*
		 * Prepare a query
		 */
		String selection = "(" + ConstsDatabase.TRANSACTION_ACCOUNT + " =?"
				+ ")";
		/*
		 * Open the transactions table for  querying
		 */
		TransactionTable transactionTable = new TransactionTable(
				getApplicationContext());
		transactionTable.open();
		ArrayList<Transaction> returnList = new ArrayList<Transaction>();
		String[] selectionArgs = new String[] {""+account.getId()};
		/*
		 * Get the transaction by account
		 */
		returnList.addAll(transactionTable.getTransactionsByCondition(selection,
				selectionArgs));
		/*
		 * Close the transaction table 
		 */
		transactionTable.close();
		/*
		 * Sort the list ascending
		 */
		ArrayList<Transaction> sortedList = HomeActivity
				.sortListDescendingByTime(returnList);
		
		return sortedList;
	}
	
}
