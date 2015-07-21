package com.example.transactioncard;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.example.transactioncard.HomeActivity.HomeListViewAdapter;
import com.example.transactioncard.database.CashFlowDB;
import com.example.transactioncard.database.ConstsDatabase;
import com.example.transactioncard.database.TransactionTable;
import com.example.transactioncard.object.CacheDescription;
import com.example.transactioncard.object.Description;
import com.example.transactioncard.object.Transaction;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class ViewbyDescription extends Activity{

	private ListView lvViewbyDesc;
	private ActionBar actionBar;
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
		setContentView(R.layout.activity_view_by_descr);
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
		long id = bundle.getLong(Consts.STRBUNDLE_DESC_ID);
		CacheDescription cache = new CacheDescription(getApplicationContext());
		Description description = cache.getDescription(id);
		/*
		 * Query transaction from the DB based on the selected description Id
		 */
		transactionList = getTransactions(description);
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
		actionBar.setTitle(description.getDescription());
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
	
	private ArrayList<Transaction> getTransactions(Description description) {
		// TODO Auto-generated method stub
		String selection = "(" + ConstsDatabase.TRANSACTION_DESCRIPTION + " =?"
				+ ")";
		TransactionTable transactionTable = new TransactionTable(
				getApplicationContext());
		transactionTable.open();
		ArrayList<Transaction> returnList = new ArrayList<Transaction>();
		String[] selectionArgs = new String[] {""+description.getId()};
		returnList.addAll(transactionTable.getTransactionsByCondition(selection,
				selectionArgs));
		transactionTable.close();
		ArrayList<Transaction> sortedList = HomeActivity
				.sortListDescendingByTime(returnList);
		return sortedList;
	}
	
}
