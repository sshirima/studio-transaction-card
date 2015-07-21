package com.example.transactioncard;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import com.example.transactioncard.database.ConstsDatabase;
import com.example.transactioncard.database.TransactionTable;
import com.example.transactioncard.dialogs.DialogTransactionNew;
import com.example.transactioncard.dialogs.DialogTransactionNew.NoticeDialogListerner;
import com.example.transactioncard.dialogs.DialogSingleChoice.ChoiceSelectedListener;
import com.example.transactioncard.dialogs.DialogTimeChange.DateSetListener;
import com.example.transactioncard.manager.HomeActivityManager;
import com.example.transactioncard.object.Accounts;
import com.example.transactioncard.object.Description;
import com.example.transactioncard.object.Transaction;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.SparseArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity implements OnNavigationListener,
		NoticeDialogListerner, DateSetListener, ChoiceSelectedListener,
		OnItemClickListener {
	
	public static final String CLASSNAME = HomeActivity.class.getName();

	private ListView listviewHome;
	private HomeListViewAdapter homeListViewAdapter;
	private TextView tvTotalSumExpensesLabel;
	private TextView tvTotalSumIncomeLabel;
	private TextView tvActivityHomeNoData;
	private ImageView ivActivityHomeNoData;
	private RelativeLayout mainLayoutHomeFooter;
	private int listNavigationIndex = 0;
	private ActionBar actionBar;
	private HomeActivityManager homeActivityManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String methodName = "onCreate";
		String operation = "Creating activity interface";
		ConstsDatabase.logINFO(CLASSNAME, methodName, operation);
		/*
		 * Set content view of the home, initializes home view(s) variables
		 */
		setContentView(R.layout.activity_home);
		initializeViewCompnentVariables();
		homeActivityManager = new HomeActivityManager(this);
		/*
		 * Set up the action bar
		 */
		ConstsDatabase.logINFO(CLASSNAME, methodName, "Set up: Home actionbar");
		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setDisplayShowTitleEnabled(false);
		
		/*
		 * Creating  action bar dropdown menu
		 */
		ConstsDatabase.logINFO(CLASSNAME, methodName, "Set up: Action bar dropdown spinner");
		ArrayAdapter<CharSequence> actionbarDropdownSpinner = getDropDownSpinner();
		actionBar.setListNavigationCallbacks(actionbarDropdownSpinner, this);
		
		/*
		 * Set up home listView
		 */
		ConstsDatabase.logINFO(CLASSNAME, methodName, "Set up:Home listview adapter");
		homeListViewAdapter = new HomeListViewAdapter(this);
		
		/*
		 * Get transactions from the sqlite DB and populate it to homeListViewAdapter
		 */
		addTransactionsToAdapter(listNavigationIndex);
		listviewHome.setAdapter(homeListViewAdapter);
		
		/*
		 * Set listView on click listerner
		 */
		listviewHome.setOnItemClickListener(this);
		listviewHome.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		listviewHome.setMultiChoiceModeListener(multichoiceListener());
	}

	private MultiChoiceModeListener multichoiceListener() {
		return new MultiChoiceModeListener() {

			@Override
			public void onItemCheckedStateChanged(ActionMode mode,
					int position, long id, boolean checked) {
				if (checked) {
					homeListViewAdapter.addTransactionToList(mode, position);
				} else {
					homeListViewAdapter.removeTransactionToList(mode, position);
				}
			}

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				// Respond to clicks on the actions in the CAB
				switch (item.getItemId()) {
				case R.id.action_delete:
					showDeleteDialog();
					mode.finish();
					return true;
				default:
					return false;
				}
			}

			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				MenuInflater inflater = mode.getMenuInflater();

				inflater.inflate(R.menu.action_mode_menu, menu);
				int itemCount = homeListViewAdapter.listSelectedTransactions.size();
				homeListViewAdapter.setTextMenuText(mode, itemCount + " "
						+ homeListViewAdapter.selectedCount);
				return true;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {

			}

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				// Here you can perform updates to the CAB due to
				// an invalidate() request
				return false;
			}
		};
	}

	private void initializeViewCompnentVariables() {
		// TODO Auto-generated method stub
		listviewHome = (ListView) findViewById(R.id.lvHome);
		tvTotalSumExpensesLabel = (TextView) findViewById(R.id.tvHomeSummaryExpensesLabel);
		tvTotalSumIncomeLabel = (TextView) findViewById(R.id.tvHomeSummaryIncomeLabel);
		tvActivityHomeNoData = (TextView) findViewById(R.id.tvActivityHomeNoData);
		ivActivityHomeNoData = (ImageView) findViewById(R.id.ivActivityHomeNoData);
		mainLayoutHomeFooter = (RelativeLayout) findViewById(R.id.mainLayoutHomeFooter);
	}

	private ArrayAdapter<CharSequence> getDropDownSpinner() {
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.time_filter_list,
				R.layout.listview_actionbar_home);
		adapter.setDropDownViewResource(R.layout.listview_actionbar_home_dropdownview);
		return adapter;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_home, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.newRecord:
			setNewRecordDialog();
			break;

		case R.id.account:
			Intent intent = new Intent(HomeActivity.this, AccountActivity.class);
			startActivity(intent);
			break;

		case R.id.viewSummary:
			Intent intent1 = new Intent(HomeActivity.this, Summary.class);
			startActivity(intent1);
			break;

		case R.id.currency_rates:
			Intent intent2 = new Intent(HomeActivity.this,
					CurrencyActivity.class);
			startActivity(intent2);
			break;

		case R.id.settings:
			Intent intent3 = new Intent(HomeActivity.this, Settings.class);
			startActivity(intent3);
			break;
		case R.id.action_description:
			Intent intent4 = new Intent(HomeActivity.this, DescriptionActivity.class);
			startActivity(intent4);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onNavigationItemSelected(int position, long arg1) {
		// TODO Auto-generated method stub
		listNavigationIndex = position;
		addTransactionsToAdapter(listNavigationIndex);
		return false;
	}

	@Override
	public void onDialogPositiveClick(DialogFragment dialog,
			Transaction transaction) {
		// TODO Auto-generated method stub
		Transaction newTransaction = transaction;
		if (calendarNewTransaction != null) {
			newTransaction.setTime(calendarNewTransaction.getTimeInMillis());
			calendarNewTransaction = null;
		}
		if (accountsNewTransaction != null) {
			newTransaction.setAccountId(accountsNewTransaction.getId());
			newTransaction.setAccountName(accountsNewTransaction
					.getAccountName());
			accountsNewTransaction = null;
		}
		if (newTransactionCategory != null) {
			newTransaction.setCategory(newTransactionCategory);
			newTransactionCategory = null;
		}
		TransactionTable transactionTable = new TransactionTable(
				getApplicationContext());
		transactionTable.open();
		Transaction addedTransaction = transactionTable
				.insertTransactionSqliteDB(newTransaction);
		if (addedTransaction != null) {
			transactionTable.close();
			Calendar calendar = Calendar.getInstance();
			int today = calendar.get(Calendar.DAY_OF_YEAR);
			calendar.setTimeInMillis(transaction.getTimeInMillis());
			int day = calendar.get(Calendar.DAY_OF_YEAR);
			int diff = today - day;
			if (diff == 0) {
				listNavigationIndex = VALUE_TODAY;
			} else if (diff == 1) {
				listNavigationIndex = VALUE_YESTERDAY;
			} else if (2 <= diff && 6 >= diff) {
				listNavigationIndex = VALUE_WEEK;
			} else if (7 <= diff && 29 >= diff) {
				listNavigationIndex = VALUE_MONTH;
			} else {
				listNavigationIndex = VALUE_TODAY;
			}
			addTransactionsToAdapter(listNavigationIndex);
			actionBar.setSelectedNavigationItem(listNavigationIndex);
		} else {
			System.out.println("***Transaction not saved****");
		}
		dialog.dismiss();
	}

	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), "Canceled", Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	public void onDateSet(DialogFragment dialogFrag, int year, int month,
			int day, boolean isStart) {
		// TODO Auto-generated method stub
		Dialog dialog = dialogFrag.getDialog();
		TextView textView = (TextView) dialog
				.findViewById(R.id.tvNewTransactionValueDate);
		if (calendarNewTransaction == null) {
			calendarNewTransaction = Calendar.getInstance();
		}
		calendarNewTransaction.set(Calendar.YEAR, year);
		calendarNewTransaction.set(Calendar.MONTH, month);
		calendarNewTransaction.set(Calendar.DAY_OF_MONTH, day);
		String time = Transaction.getFormatedDate(calendarNewTransaction);
		textView.setText(time);
	}

	@Override
	public void onChoiceSelected(DialogFragment dialogFragment, Accounts account) {
		// TODO Auto-generated method stub
		Dialog dialog = dialogFragment.getDialog();
		TextView textView = (TextView) dialog
				.findViewById(R.id.tvNewTransactionValueAccount);
		textView.setText(account.getAccountName());
		accountsNewTransaction = account;
	}

	@Override
	public void onChoiceSelected(DialogFragment dialogFragment,
			String selectedItem) {
		// TODO Auto-generated method stub
		Dialog dialog = dialogFragment.getDialog();
		TextView textView = (TextView) dialog
				.findViewById(R.id.tvNewTransactionValueCategory);
		textView.setText(selectedItem);
		newTransactionCategory = selectedItem;
	}

	private void setNewRecordDialog() {
		// TODO Auto-generated method stub
		FragmentManager fragmentManager = getFragmentManager();
		DialogTransactionNew newRecordDialogAlert = new DialogTransactionNew();
		newRecordDialogAlert.show(fragmentManager, "NewRecordDialog");
	}

	private static final int VALUE_TODAY = 0;
	private static final int VALUE_YESTERDAY = 1;
	private static final int VALUE_WEEK = 2;
	private static final int VALUE_MONTH = 3;

	@Override
	public void onTimeChange(DialogFragment dialogFrag, int hour, int minute,
			boolean isStart) {
		// TODO Auto-generated method stub
		Dialog dialog = dialogFrag.getDialog();
		TextView textView = (TextView) dialog
				.findViewById(R.id.tvNewTransactionValueTime);
		if (calendarNewTransaction == null) {
			calendarNewTransaction = Calendar.getInstance();
		}
		calendarNewTransaction.set(Calendar.HOUR_OF_DAY, hour);
		calendarNewTransaction.set(Calendar.MINUTE, minute);
		String time = Transaction.getFormatedTime(calendarNewTransaction);
		textView.setText(time);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub
		if (!(homeListViewAdapter.mList.get(position) instanceof String)) {
			selectedTransaction = (Transaction) homeListViewAdapter.mList.get(position);
			long id = selectedTransaction.getTransactionId();
			Bundle bundle = new Bundle();
			bundle.putLong("id", id);
			Intent intent = new Intent(HomeActivity.this, TransactionDetails.class);
			intent.putExtras(bundle);
			startActivityForResult(intent, 0);
		}
	}

	@Override
	public void onDescriptionSelected(Description description) {
		// TODO Auto-generated method stub
	
	}

	private void displayDataToScreeen() {
		if (transactionList.size() == 0) {
			listviewHome.setVisibility(View.GONE);
			mainLayoutHomeFooter.setVisibility(View.GONE);
			tvActivityHomeNoData.setVisibility(View.VISIBLE);
			ivActivityHomeNoData.setVisibility(View.VISIBLE);
		} else {
			listviewHome.setVisibility(View.VISIBLE);
			mainLayoutHomeFooter.setVisibility(View.VISIBLE);
			tvActivityHomeNoData.setVisibility(View.GONE);
			ivActivityHomeNoData.setVisibility(View.GONE);
		}
	}

	private void updateFooterSumLabel() {
		String methodName = "updateFooterSumLabel";
		String operation = "Update sum for the footer label";
		ConstsDatabase.logINFO(CLASSNAME, methodName, operation);
		
		/*
		 * Get sum for expenses and income category
		 */
		double expensesSum = homeListViewAdapter.getSumCategory(transactionList,
				ConstsDatabase.CATEGORY_EXPENSES, getApplicationContext());
		double incomeSum = homeListViewAdapter.getSumCategory(transactionList,
				ConstsDatabase.CATEGORY_INCOME, getApplicationContext());
		String timeRangeString = homeActivityManager.getNameFromListNavIndex(this, listNavigationIndex);
		String defaultCurreny = Settings.getDefaultCurrency(getApplicationContext());
		/*
		 * Assigning values to the textView footer
		 */
		tvTotalSumExpensesLabel
				.setText(timeRangeString+" "+ConstsDatabase.CATEGORY_EXPENSES+" "+
						new DecimalFormat().format(expensesSum)+" "+defaultCurreny);
		tvTotalSumIncomeLabel
				.setText(timeRangeString+" "+ConstsDatabase.CATEGORY_INCOME+" "+
						new DecimalFormat().format(incomeSum)+" "+ defaultCurreny);
	}

	private ArrayList<Transaction> transactionList = new ArrayList<Transaction>();
	private ArrayList<Transaction> transactionListAll = new ArrayList<Transaction>();

	private void addTransactionsToAdapter(int listNavigationIndex) {
		String methodName = "addTransactionsToAdapter";
		String operation = "Fill in transaction(s) into home listview adapter";
		ConstsDatabase.logINFO(CLASSNAME, methodName, operation);
		/*
		 * Clear homeListView adapter
		 */
		homeListViewAdapter.clearAdapter();
		transactionList.clear();
		transactionListAll.clear();
		switch (listNavigationIndex) {
		case 0:
		case 1:
			String separator = listNavigationIndex == 0 ? Consts.STR_TODAY : Consts.STR_YESTERDAY;
			/*
			 * Add separator label to the homeListViewAdapter
			 */
			homeListViewAdapter.addSeparatorItem(separator);
			/*
			 * Get transactions from the sqlite DB
			 */
			transactionList = queryTransactionByTime(homeActivityManager.getStartTimeFromSelectIndex(listNavigationIndex),
					homeActivityManager.getEndTimeFromSelectIndex(listNavigationIndex));
			
			/*
			 * Add transaction list from the DB to listview adapter
			 */
			homeListViewAdapter.addAll(transactionList);
			
			transactionListAll.addAll(transactionList);
			break;
		case 2:
		case 3:
		case 4:
		case 5:
			/*
			 * Query transaction from the sqlite DB
			 */
			if (listNavigationIndex != 5){
				transactionList = queryTransactionByTime(homeActivityManager.getStartTimeFromSelectIndex(listNavigationIndex),
						homeActivityManager.getEndTimeFromSelectIndex(listNavigationIndex));
			}else {
				transactionList = this.queryTransactionsAll();
			}
			
			
			
			/*
			 * Put transaction into listView adapter
			 */
			Calendar calCurrent = Calendar.getInstance();
			Calendar calNext = Calendar.getInstance();
			ArrayList<Transaction> tList = new ArrayList<Transaction>();
			if (transactionList.size() != 0){
				for (int i = 0; i < transactionList.size();i++){
					calCurrent.setTimeInMillis(transactionList.get(i).getTimeInMillis());
					if (listNavigationIndex == 2){
						separator = homeActivityManager.getDayOfWeekFromCalendar(calCurrent);
					} else {
						separator = homeActivityManager.getDateOfMonthFromCalendar(calCurrent);
					}
					
					tList.add(transactionList.get(i));
					/*
					 * Check if there is the next transaction
					 */
					if ((i+1) < transactionList.size()){
						calNext.setTimeInMillis(transactionList.get(i+1).getTimeInMillis());
						/*
						 * Check if the next transaction has the same date as this transaction
						 */
						if (calCurrent.get(Calendar.DAY_OF_YEAR) != calNext.get(Calendar.DAY_OF_YEAR)){
							ConstsDatabase.logINFO(CLASSNAME, methodName, "Date not the same:");
							homeListViewAdapter.addSeparatorItem(separator);
							homeListViewAdapter.addAll(tList);
							tList.clear();
						}
					} else if ((i+1) == transactionList.size()){
						ConstsDatabase.logINFO(CLASSNAME, methodName, "Last transaction in the list");
						homeListViewAdapter.addSeparatorItem(separator);
						homeListViewAdapter.addAll(tList);
					}
				}
			}
			
			break;
		}
		updateFooterSumLabel();
		displayDataToScreeen();
	}

	private ArrayList<Transaction> queryTransactionsAll() {
		String methodName = "queryTransactionsByDate";
		String operation = "Query transactions from sqlite DB by date range";
		ConstsDatabase.logINFO(CLASSNAME, methodName, operation);
		
		/*
		 * Get condition set
		 */
		String categoryChoice = getCategoryChoice();
		String accountIdChoice = ""+Settings.getDefaultAccount(getApplicationContext()).getId();
		/*
		 * Create selection statement
		 */
		ConstsDatabase.logINFO(CLASSNAME, methodName, "Create selection:\n");
		
		String condCategory = "(";
		String[] selectionArgs = null;
		if (categoryChoice.equals(ConstsDatabase.CATEGORY_ALL)){
			condCategory += String.format(ConstsDatabase.SQLSYNTX_CONDITION_EQUALS, ConstsDatabase.TRANSACTION_CATEGORY);
			condCategory += ConstsDatabase.SQLSYNTX_OR;
			condCategory += String.format(ConstsDatabase.SQLSYNTX_CONDITION_EQUALS, ConstsDatabase.TRANSACTION_CATEGORY);
			condCategory += ")";
			selectionArgs = new String[]{
					accountIdChoice, ConstsDatabase.CATEGORY_EXPENSES, ConstsDatabase.CATEGORY_INCOME
			};
		} else if (categoryChoice.equals(ConstsDatabase.CATEGORY_EXPENSES)){
			condCategory = String.format(ConstsDatabase.SQLSYNTX_CONDITION_EQUALS, ConstsDatabase.TRANSACTION_CATEGORY);
			selectionArgs = new String[]{
					accountIdChoice, ConstsDatabase.CATEGORY_EXPENSES
			};
		}else if (categoryChoice.equals(ConstsDatabase.CATEGORY_INCOME)){
			condCategory = String.format(ConstsDatabase.SQLSYNTX_CONDITION_EQUALS, ConstsDatabase.TRANSACTION_CATEGORY);
			selectionArgs = new String[]{
					accountIdChoice, ConstsDatabase.CATEGORY_INCOME
			};
		}
		
		String condAccountId = String.format(ConstsDatabase.SQLSYNTX_CONDITION_EQUALS, ConstsDatabase.ACCOUNT_ID);
		
		String selection = condAccountId + ConstsDatabase.SQLSYNTX_AND + 
							condCategory ;
		
		/*
		 * Open transaction table to query from
		 */
		ArrayList<Transaction> sortedList = homeActivityManager.queryTransactionsBySelection(
				this, methodName, selectionArgs, selection);
		return sortedList;
	}

	public static final CharSequence[] CATEGORY_LIST = { "All", "Expenses",
			"Income" };

	private ArrayList<Transaction> queryTransactionByTime(Calendar start, Calendar end) {
		String methodName = "queryTransactionsByDate";
		String operation = "Query transactions from sqlite DB by date range";
		ConstsDatabase.logINFO(CLASSNAME, methodName, operation);
		
		/*
		 * Get condition set
		 */
		String categoryChoice = getCategoryChoice();
		String accountIdChoice = ""+Settings.getDefaultAccount(getApplicationContext()).getId();
		String startTimeChoice = ""+start.getTimeInMillis();
		String endTimeChoice = ""+end.getTimeInMillis();
		/*
		 * Create selection statement
		 */
		ConstsDatabase.logINFO(CLASSNAME, methodName, "Create selection:\n");
		
		String condCategory = "(";
		String[] selectionArgs = null;
		if (categoryChoice.equals(ConstsDatabase.CATEGORY_ALL)){
			condCategory += String.format(ConstsDatabase.SQLSYNTX_CONDITION_EQUALS, ConstsDatabase.TRANSACTION_CATEGORY);
			condCategory += ConstsDatabase.SQLSYNTX_OR;
			condCategory += String.format(ConstsDatabase.SQLSYNTX_CONDITION_EQUALS, ConstsDatabase.TRANSACTION_CATEGORY);
			condCategory += ")";
			selectionArgs = new String[]{
					accountIdChoice, ConstsDatabase.CATEGORY_EXPENSES, ConstsDatabase.CATEGORY_INCOME,
					startTimeChoice, endTimeChoice
			};
		} else if (categoryChoice.equals(ConstsDatabase.CATEGORY_EXPENSES)){
			condCategory = String.format(ConstsDatabase.SQLSYNTX_CONDITION_EQUALS, ConstsDatabase.TRANSACTION_CATEGORY);
			selectionArgs = new String[]{
					accountIdChoice, ConstsDatabase.CATEGORY_EXPENSES, startTimeChoice, endTimeChoice
			};
		}else if (categoryChoice.equals(ConstsDatabase.CATEGORY_INCOME)){
			condCategory = String.format(ConstsDatabase.SQLSYNTX_CONDITION_EQUALS, ConstsDatabase.TRANSACTION_CATEGORY);
			selectionArgs = new String[]{
					accountIdChoice, ConstsDatabase.CATEGORY_INCOME, startTimeChoice, endTimeChoice
			};
		}
		
		String condAccountId = String.format(ConstsDatabase.SQLSYNTX_CONDITION_EQUALS, ConstsDatabase.ACCOUNT_ID);
		String condStartTime = String.format(ConstsDatabase.SQLSYNTX_CONDITION_GREATTHAN, ConstsDatabase.TRANSACTION_TIME);
		String condEndTime = String.format(ConstsDatabase.SQLSYNTX_CONDITION_LESSTHAN, ConstsDatabase.TRANSACTION_TIME);
		
		String selection = condAccountId + ConstsDatabase.SQLSYNTX_AND + 
							condCategory + ConstsDatabase.SQLSYNTX_AND + 
							condStartTime +ConstsDatabase.SQLSYNTX_AND + 
							condEndTime;
		
		ArrayList<Transaction> sortedList = homeActivityManager.queryTransactionsBySelection(
				this, methodName, selectionArgs, selection);
		return sortedList;
	}

	public static ArrayList<Transaction> sortListDescendingByTime(
			ArrayList<Transaction> list) {
		// TODO Auto-generated method stub
		ArrayList<Long> mTimeList = new ArrayList<Long>();
		ArrayList<Transaction> mSortedList = new ArrayList<Transaction>();
		for (int i = 0; i < list.size(); i++) {
			mTimeList.add(list.get(i).getTimeInMillis());
		}
		Collections.sort(mTimeList);
		Collections.reverse(mTimeList);
		for (long time : mTimeList) {
			for (int i = 0; i < list.size(); i++) {
				if (time == list.get(i).getTimeInMillis()) {
					mSortedList.add(list.get(i));
					break;
				}
			}
		}
		return mSortedList;
	}

	public static class HomeListViewAdapter extends BaseAdapter {
		private static final String CLASSNAME = HomeListViewAdapter.class.getName() ;
		private static final int TYPE_ITEM = 0;
		private static final int TYPE_SEPARATOR = 1;
		private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;
		@SuppressWarnings("rawtypes")
		private TreeSet separatorPos = new TreeSet();
		
		private List<Object> mList = new ArrayList<Object>();
		private LayoutInflater inflater;
		private Context applicationContext;

		public HomeListViewAdapter(Context context) {
			String methodName = "HomeListViewAdapter";
			String operation = "Creating: "+ methodName+" instance";
			ConstsDatabase.logINFO(CLASSNAME, methodName, operation);
			
			/*
			 * Creating instance
			 */
			inflater = LayoutInflater.from(context);
			this.applicationContext = context;
		}

		public HomeListViewAdapter(Context context, List<Object> objectList) {
			String methodName = "HomeListViewAdapter";
			String operation = "Creating: "+ methodName+" instance";
			ConstsDatabase.logINFO(CLASSNAME, methodName, operation);
			
			/*
			 * Creating instance
			 */
			inflater = LayoutInflater.from(context);
			this.mList = objectList;
		}

		
		public void addAll(ArrayList<Transaction> list) {
			for (int i = 0; i < list.size(); i++) {
				this.mList.add(list.get(i));
			}
			notifyDataSetChanged();
		}

		private ArrayList<Transaction> getTransactionList(List<Object> list,
				int position) {
			ArrayList<Transaction> returnList = new ArrayList<Transaction>();
			for (int i = position + 1; i < list.size(); i++) {
				int itemType = getItemViewType(i);
				if (itemType == TYPE_ITEM) {
					try {
						returnList.add((Transaction) list.get(i));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					break;
				}

			}
			return returnList;
		}

		// clear adapter
		public void clearAdapter() {
			this.mList.clear();
			this.separatorPos.clear();
			notifyDataSetChanged();
		}

		@SuppressWarnings("unchecked")
		public void addSeparatorItem(String label) {
			this.mList.add(label);
			separatorPos.add(this.mList.size() - 1);
			notifyDataSetChanged();
		}

		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			return separatorPos.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return TYPE_MAX_COUNT;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return this.mList.size();
		}

	
		public void updateObjectList(Transaction transaction) {
			this.mList.add(transaction);
			notifyDataSetChanged();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return this.mList.get(position);
		}

		@Override
		public boolean isEnabled(int position) {
			if (getItemViewType(position) == TYPE_SEPARATOR) {
				return false;
			} else {
				return true;
			}
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			String methodName = "getView";
			String operation = "Get view for the listView";
			ConstsDatabase.logINFO(CLASSNAME, methodName, operation);
			
			// TODO Auto-generated method stub
			ViewHolder holder = null;
			int itemType = getItemViewType(position);
			if (convertView == null) {
				switch (itemType) {
				case TYPE_ITEM:
					convertView = inflater
							.inflate(R.layout.listview_home, null);
					holder = new ViewHolder();
					holder.tvAmount = (TextView) convertView
							.findViewById(R.id.tvAmount);
					holder.tvCurrency = (TextView) convertView
							.findViewById(R.id.tvCurrency);
					holder.tvDate = (TextView) convertView
							.findViewById(R.id.tvTime);
					holder.tvDes = (TextView) convertView
							.findViewById(R.id.tvDescription);
					holder.ivCategory = (ImageView) convertView
							.findViewById(R.id.ivCategory);
					break;
				case TYPE_SEPARATOR:
					convertView = inflater.inflate(
							R.layout.listview_home_separator, null);
					holder = new ViewHolder();

					holder.tvDate = (TextView) convertView
							.findViewById(R.id.tvSeparatorHomeGTime);
					holder.tvAmount = (TextView) convertView
							.findViewById(R.id.tvSeparatorTotalExpenses);
					holder.tvSeparatorTotIncome = (TextView) convertView
							.findViewById(R.id.tvSeparatorTotalIncome);
					break;
				}

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			// bind data to the convertView
			switch (itemType) {
			case TYPE_ITEM:
				/*
				 * Get transaction from the list
				 */
				Transaction transaction = (Transaction) mList.get(position);
				
				/*
				 * Fetch the transaction parameters
				 */
				int amount = (int)Math.round(transaction.getAmount(applicationContext));
				String date = transaction.getDate();
				String time = transaction.getTime();
				String currency = " "+ Settings.getDefaultCurrency(applicationContext);
				String description = transaction.getDescriptionName();
				int displayIconResId = transaction.getImageId();
				/*
				 * Set the transaction value to listView display
				 */
				holder.tvAmount.setText(""+new DecimalFormat().format(amount));
				holder.tvCurrency.setText(currency);
				holder.tvDate.setText(date + Consts.STRTXT_ON + time);
				holder.tvDes.setText(description);
				holder.ivCategory.setImageResource(displayIconResId);
				
				/*
				 * Set up the backgroud
				 */
				convertView
						.setBackgroundColor(isSelected(position) ? 0x9934B5E4
								: Color.TRANSPARENT);
				break;

			case TYPE_SEPARATOR:
				/*
				 * Configure separator items
				 */
				String labelDate = null;
				try {
					labelDate = (String) mList.get(position);
				
				
				/*
				 * Get default currency
				 */
				String defaultCurrency = Settings
						.getDefaultCurrency(applicationContext);
				/*
				 * Get transaction list from the range to calculate the sum
				 */
				ArrayList<Transaction> transListRange = getTransactionList(
						mList, position);
				/*
				 * Get sum of the expenses transactions
				 */
				DecimalFormat dFormat = new DecimalFormat();
				int expenseSum = (int)Math.round(getSumCategory(transListRange,
						ConstsDatabase.CATEGORY_EXPENSES, applicationContext));
				/*
				 * Get sum of the income expenses
				 */
				int incomeSum = (int) Math.round(getSumCategory(transListRange,
						ConstsDatabase.CATEGORY_INCOME, applicationContext));
				/*
				 * Set the values to the textview(s)
				 */
				holder.tvDate.setText(labelDate);
				holder.tvAmount.setText(dFormat.format(expenseSum) + " " + defaultCurrency);
				holder.tvSeparatorTotIncome.setText(dFormat.format(incomeSum) + " "
						+ defaultCurrency);
				} catch (ClassCastException e) {
					ConstsDatabase.logERROR(methodName, "Failed: Assigning values to separator textView");
				}
				break;
			}

			return convertView;
		}

		public boolean isSelected(int position) {
			return listSelectedTransactions.get(position) == null ? false
					: true;
		}
		
		public double getSumCategory(ArrayList<Transaction> list,
				String category, Context context) {
			double sum = 0;
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).getCategory().equals(category)) {
					sum = sum + list.get(i).getAmount(context);
				}
			}
			return sum;
		}


		public SparseArray<Transaction> listSelectedTransactions = new SparseArray<Transaction>();
		final String selectedCount = "Selected";

		public void removeTransactionToList(ActionMode mode, int position) {
			listSelectedTransactions.delete(position);
			int itemCount = listSelectedTransactions.size();
			setTextMenuText(mode, itemCount + " " + selectedCount);
			notifyDataSetChanged();
		}

		public void addTransactionToList(ActionMode mode, int position) {
			try {
				Transaction selectedTransaction = (Transaction) mList
						.get(position);
				listSelectedTransactions.put(position, selectedTransaction);
				int itemCount = listSelectedTransactions.size();
				setTextMenuText(mode, itemCount + " " + selectedCount);
				notifyDataSetChanged();
			} catch (ClassCastException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void setTextMenuText(ActionMode mode, final String selectedCount) {
			Menu menu = mode.getMenu();
			MenuItem menuTexItem = menu.findItem(R.id.action_text);
			View v = menuTexItem.getActionView();
			if (v instanceof TextView) {
				((TextView) v).setText(selectedCount);
			}
		}

		public int getSelectedTransactionCount() {
			return this.listSelectedTransactions.size();
		}

		public Transaction getTransactionToDelete(int index) {
			return listSelectedTransactions.valueAt(index);
		}

		static class ViewHolder {
			TextView tvAmount, tvDate, tvDes, tvCurrency,
					tvSeparatorTotExpenses, tvSeparatorTotIncome;
			ImageView ivCategory;
		}

	}

	private Calendar calendarNewTransaction;

	private Accounts accountsNewTransaction;

	private String newTransactionCategory;

	private void showDeleteDialog() {
		String methodName = "showDeleteDialog";
		String operation = "Show confirmation dialog on account deletions attempt";
		ConstsDatabase.logINFO(CLASSNAME, methodName, operation);
		
		/*
		 * Create dialog builder class
		 */
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		/*
		 * Set up the dialog preferances
		 */
		builder.setTitle(R.string.dialog_title_delete);
		builder.setMessage(R.string.dialog_message_delete);
		
		/*
		 * Set click listeners 
		 */
		builder.setPositiveButton(R.string.dialog_button_delete,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						int selectedCount = homeListViewAdapter
								.getSelectedTransactionCount();
						TransactionTable transactionTable = new TransactionTable(
								getApplicationContext());
						transactionTable.open();
						for (int j = 0; j < selectedCount; j++) {
							Transaction transaction = homeListViewAdapter
									.getTransactionToDelete(j);
							transactionTable.deleteTransactionSqliteDB(transaction);
						}
						transactionTable.close();
						homeListViewAdapter.listSelectedTransactions.clear();
						addTransactionsToAdapter(listNavigationIndex);
						Toast.makeText(getApplicationContext(), "Deleted",
								Toast.LENGTH_SHORT).show();
					}
				}).setNegativeButton(R.string.dialog_button_cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}
				});
		builder.create().show();
	}

	private Transaction selectedTransaction;

	private static final String TIME_KEY = "Time";

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			Bundle bundle = data.getExtras();
			long millis = bundle.getLong(TIME_KEY);
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(millis);
			int range = Calendar.getInstance().get(Calendar.DATE)
					- cal.get(Calendar.DATE);
			if (range == 0) {
				actionBar.setSelectedNavigationItem(VALUE_TODAY);
				addTransactionsToAdapter(VALUE_TODAY);
			} else if (range == 1) {
				actionBar.setSelectedNavigationItem(VALUE_YESTERDAY);
				addTransactionsToAdapter(VALUE_YESTERDAY);
			} else if (range >= 2 && range <= 7) {
				actionBar.setSelectedNavigationItem(VALUE_WEEK);
				addTransactionsToAdapter(VALUE_WEEK);
			} else if (range > 7 && range <= 30) {
				actionBar.setSelectedNavigationItem(VALUE_MONTH);
				addTransactionsToAdapter(VALUE_MONTH);
			} else {
				actionBar.setSelectedNavigationItem(VALUE_TODAY);
				addTransactionsToAdapter(VALUE_TODAY);
			}
		}
	}
	
	private String getCategoryChoice() {
		return ConstsDatabase.CATEGORY_ALL;
	}

}
