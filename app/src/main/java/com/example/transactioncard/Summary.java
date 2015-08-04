package com.example.transactioncard;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import com.example.transactioncard.database.TransactionTable;
import com.example.transactioncard.object.ExpandableListAdapter;
import com.example.transactioncard.object.Transaction;
import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.LegendAlign;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.LineGraphView;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.LinearLayout;

public class Summary extends Activity implements OnCheckedChangeListener,
		OnChildClickListener, OnGroupExpandListener, OnGroupCollapseListener {

	private ActionBar actionBar;
	private int mDataRange;
	private Calendar mCurrentStart, mCurrentEnd;
	private CheckBox cbIncome, cbExpenses;
	private ExpandableListView expListView;
	private final int VALUE_TODAY = 0;
	private final int VALUE_YESTERDAY = 1;
	private final int VALUE_WEEK = 2;
	private final int VALUE_MONTH = 3;
	private final int VALUE_YEAR = 4;
	private ExpandableListAdapter listAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_summary);
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		mDataRange = VALUE_TODAY;
		mCurrentStart = getStartTime(VALUE_TODAY);
		mCurrentEnd = getEndTime(VALUE_TODAY);
		plotGraph(getStartTime(VALUE_TODAY), getEndTime(VALUE_TODAY));
		cbIncome = (CheckBox) findViewById(R.id.cbIncome);
		cbExpenses = (CheckBox) findViewById(R.id.cbExpenses);
		cbExpenses.setTextColor(Color.RED);
		cbIncome.setTextColor(Color.rgb(0, 128, 0));
		cbExpenses.setOnCheckedChangeListener(this);
		cbIncome.setOnCheckedChangeListener(this);
		mGraphLayout = (LinearLayout) findViewById(R.id.graphLayout);
		mGraphLayout.addView(graphView);
		expListView = (ExpandableListView) findViewById(R.id.elSummary);
		// preparing list data
		prepareListData();
		listAdapter = new ExpandableListAdapter(this, titles, itemList);
		// setting list adapter
		expListView.setAdapter(listAdapter);

		expListView.setOnChildClickListener(this);

		// Listview Group expanded listener
		expListView.setOnGroupExpandListener(this);

		// Listview Group collasped listener
		expListView.setOnGroupCollapseListener(this);
	}

	private List<String> titles;
	private HashMap<String, List<Transaction>> itemList;

	private void prepareListData() {
		titles = new ArrayList<String>();
		titles.add("Today");
		titles.add("Yesterday");
		titles.add("Week");
		titles.add("Month");
		titles.add("Year");
		itemList = new HashMap<String, List<Transaction>>();
		// adding items
		List<Transaction> today = new ArrayList<Transaction>();
		today = getCustomList(this, getStartTime(VALUE_TODAY),
				getEndTime(VALUE_TODAY));

		List<Transaction> yesterday = new ArrayList<Transaction>();
		yesterday = getCustomList(this, getStartTime(VALUE_YESTERDAY),
				getEndTime(VALUE_YESTERDAY));

		List<Transaction> week = new ArrayList<Transaction>();
		week = getCustomList(this, getStartTime(VALUE_WEEK),
				getEndTime(VALUE_WEEK));

		List<Transaction> month = new ArrayList<Transaction>();
		month = getCustomList(this, getStartTime(VALUE_MONTH),
				getEndTime(VALUE_MONTH));

		List<Transaction> year = new ArrayList<Transaction>();
		year = getCustomList(this, getStartTime(VALUE_YEAR),
				getEndTime(VALUE_YEAR));

		// adding items to the hashmap
		itemList.put(titles.get(0), today);
		itemList.put(titles.get(1), yesterday);
		itemList.put(titles.get(2), week);
		itemList.put(titles.get(3), month);
		itemList.put(titles.get(4), year);
	}

	public static ArrayList<Transaction> sortListAscendingByTime(
			ArrayList<Transaction> list) {
		// TODO Auto-generated method stub
		ArrayList<Long> mTimeList = new ArrayList<Long>();
		ArrayList<Transaction> mSortedList = new ArrayList<Transaction>();
		for (int i = 0; i < list.size(); i++) {
			mTimeList.add(list.get(i).getTimeInMillis());
		}
		Collections.sort(mTimeList);
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

	public static ArrayList<Transaction> getCustomList(Context context,
			Calendar start, Calendar end) {
		TransactionTable mDatabase = new TransactionTable(context);
		mDatabase.open();
		ArrayList<Transaction> list = mDatabase.getTransactionsByTimeRange(start,
				end);
		mDatabase.close();
		return list;
	}

	private LineGraphView graphView;
	private LinearLayout mGraphLayout;
	private GraphViewSeries incomeGraphViewSeries, expenseGraphViewSeries;

	private View plotGraph(final Calendar start, Calendar end) {
		ArrayList<Transaction> mList = getCustomList(getApplicationContext(),
				start, end);
		int eSize = 0;
		int iSize = 0;
		ArrayList<Transaction> incomeList = new ArrayList<Transaction>();
		ArrayList<Transaction> expenseList = new ArrayList<Transaction>();
		for (int i = 0; i < mList.size(); i++) {
			if (mList.get(i).getCategory().equals(HomeActivity.CATEGORY_LIST[1])) {
				eSize++;
				expenseList.add(mList.get(i));
			} else {
				iSize++;
				incomeList.add(mList.get(i));
			}
		}
		// sort Arraylist ascending
		expenseList = sortListAscendingByTime(expenseList);
		incomeList = sortListAscendingByTime(incomeList);
		// array of the data to be displayed in the graph
		GraphViewData[] expenseData = new GraphViewData[eSize + 1];
		GraphViewData[] incomeData = new GraphViewData[iSize + 1];
		// initial data set at 0,0 for x,y
		expenseData[0] = new GraphViewData(0, 0);
		incomeData[0] = new GraphViewData(0, 0);
		for (int i = 1; i <= eSize; i++) {
			expenseData[i] = new GraphViewData(getValueX(
					start.getTimeInMillis(), expenseList.get(i - 1)
							.getTimeInMillis()), expenseList.get(i - 1)
					.getAmountInDefaultCurrency(getApplicationContext()));
		}
		for (int i = 1; i <= iSize; i++) {
			incomeData[i] = new GraphViewData(getValueX(
					start.getTimeInMillis(), incomeList.get(i - 1)
							.getTimeInMillis()), incomeList.get(i - 1)
					.getAmountInDefaultCurrency(getApplicationContext()));
		}
		graphView = new LineGraphView(getApplicationContext(), "Graph");
		// styling the plot
		GraphViewSeriesStyle incomeStyle = new GraphViewSeriesStyle();
		GraphViewSeriesStyle expensesStyle = new GraphViewSeriesStyle();
		incomeStyle.color = Color.rgb(0, 128, 0);
		expensesStyle.color = Color.RED;
		incomeStyle.thickness = 2;
		expensesStyle.thickness = 2;
		// add data to the graph
		incomeGraphViewSeries = new GraphViewSeries("Income", incomeStyle,
				incomeData);
		expenseGraphViewSeries = new GraphViewSeries("Expenses", expensesStyle,
				expenseData);
		// commit the data to the plot, x, y plot
		graphView.addSeries(incomeGraphViewSeries);
		graphView.addSeries(expenseGraphViewSeries);
		// set the portion of the graph which will be visible
		graphView.setScrollable(true);
		// graphView.setScalable(true);
		// set static horizontal labels

		graphView.setCustomLabelFormatter(new CustomLabelFormatter() {

			@Override
			public String formatLabel(double value, boolean isValueX) {
				// TODO Auto-generated method stub
				if (isValueX) {
					switch (mDataRange) {
					case 0:
						SimpleDateFormat mDateFormat = new SimpleDateFormat(
								"HH:mm", Locale.ENGLISH);
						long mStart = start.getTimeInMillis();
						long mTimeMills = (long) value * 1000 + mStart;
						return value == 0 ? "0" : mDateFormat.format(new Date(
								mTimeMills));
					case 1:
						SimpleDateFormat mDateFormat1 = new SimpleDateFormat(
								"HH:mm", Locale.ENGLISH);
						long mYesterday = start.getTimeInMillis();
						long mTimeMillsYesterday = (long) value * 1000
								+ mYesterday;
						return value == 0 ? "0" : mDateFormat1.format(new Date(
								mTimeMillsYesterday));
					case 2:
						SimpleDateFormat mDateFormatWeek = new SimpleDateFormat(
								"EEE", Locale.ENGLISH);
						long mWeekStart = start.getTimeInMillis();
						long mTimeMillsWeek = (long) value * 1000 + mWeekStart;
						return value == 0 ? "0" : mDateFormatWeek
								.format(new Date(mTimeMillsWeek));
					case 3:
						SimpleDateFormat mDateFormatMonth = new SimpleDateFormat(
								"d", Locale.ENGLISH);
						long mMonthStart = start.getTimeInMillis();
						long mTimeMillsMonth = (long) value * 1000
								+ mMonthStart;
						return value == 0 ? "0" : mDateFormatMonth
								.format(new Date(mTimeMillsMonth));
					case 4:
						SimpleDateFormat mDateFormatYear = new SimpleDateFormat(
								"MMM", Locale.ENGLISH);
						long mYearStart = start.getTimeInMillis();
						long mTimeMillsYear = (long) value * 1000 + mYearStart;
						return value == 0 ? "0" : mDateFormatYear
								.format(new Date(mTimeMillsYear));
					default:

						return null;
					}

				}
				return null;
			}
		});
		// styling the graph
		graphView.getGraphViewStyle().setGridColor(Color.GRAY);
		graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.BLACK);
		graphView.getGraphViewStyle().setVerticalLabelsColor(Color.BLUE);
		graphView.getGraphViewStyle().setTextSize(7);
		graphView.getGraphViewStyle().setNumHorizontalLabels(
				getHorLabels(mDataRange));
		graphView.getGraphViewStyle().setNumVerticalLabels(10);
		// legend setup
		graphView.setShowLegend(true);
		graphView.setLegendAlign(LegendAlign.BOTTOM);
		graphView.getGraphViewStyle().setLegendBorder(5);
		graphView.getGraphViewStyle().setLegendSpacing(5);
		graphView.getGraphViewStyle().setLegendWidth(70);
		// if no data to be displayed, set maximum bounds
		if (mList.size() == 0) {
			graphView.setManualYAxisBounds(10000, 0);
		}
		return graphView;
	}

	public double getValueX(long start, long end) {
		return (double) (end - start) / 1000;
	}

	private int getHorLabels(int mTime) {
		switch (mTime) {
		case 0:
			return 5;

		case 1:
			return 5;
		case 2:
			return 8;
		case 3:
			return 15;
		case 4:
			return 12;
		default:
			return 5;
		}
	}

	public static Calendar getEndTime(int range) {
		Calendar mEnd;
		switch (range) {
		case 0:
			mEnd = Calendar.getInstance();
			break;
		case 1:
			mEnd = Calendar.getInstance();
			mEnd.set(Calendar.HOUR_OF_DAY, 0);
			mEnd.set(Calendar.MINUTE, 0);
			break;
		case 2:
			mEnd = Calendar.getInstance();
			break;
		case 3:
			mEnd = Calendar.getInstance();
			break;
		case 4:
			mEnd = Calendar.getInstance();
			break;
		default:
			mEnd = Calendar.getInstance();
			break;
		}
		return mEnd;
	}

	public static Calendar getStartTime(int day) {
		Calendar mStart = Calendar.getInstance();
		switch (day) {
		case 0:
			mStart.set(Calendar.HOUR_OF_DAY, 0);
			mStart.set(Calendar.MINUTE, 0);
			mStart.set(Calendar.SECOND, 0);
			break;
		case 1:
			mStart.set(Calendar.HOUR_OF_DAY, 0);
			mStart.set(Calendar.MINUTE, 0);
			mStart.add(Calendar.DATE, -1);
			break;
		case 2:
			mStart.add(Calendar.DATE, -7);
			mStart.set(Calendar.HOUR_OF_DAY, 0);
			mStart.set(Calendar.MINUTE, 0);
			mStart.set(Calendar.SECOND, 0);
			break;
		case 3:
			mStart.add(Calendar.DATE, -30);
			mStart.set(Calendar.HOUR_OF_DAY, 0);
			mStart.set(Calendar.MINUTE, 0);
			mStart.set(Calendar.SECOND, 0);
			break;
		case 4:
			mStart.add(Calendar.DATE, -366);
			mStart.set(Calendar.HOUR_OF_DAY, 0);
			mStart.set(Calendar.MINUTE, 0);
			mStart.set(Calendar.SECOND, 0);
			break;
		}
		return mStart;
	}

	@Override
	public void onCheckedChanged(CompoundButton checkbox, boolean arg1) {
		// TODO Auto-generated method stub
		switch (checkbox.getId()) {
		case R.id.cbExpenses:
			updateGraph(mCurrentStart, mCurrentEnd);
			break;
		case R.id.cbIncome:
			updateGraph(mCurrentStart, mCurrentEnd);
			break;
		}
	}

	private void updateGraph(Calendar start, Calendar end) {
		ArrayList<Transaction> mList = getCustomList(getApplicationContext(),
				start, end);
		int eSize = 0;
		int iSize = 0;
		ArrayList<Transaction> incomeList = new ArrayList<Transaction>();
		ArrayList<Transaction> expenseList = new ArrayList<Transaction>();
		for (int i = 0; i < mList.size(); i++) {
			if (mList.get(i).getCategory().equals(HomeActivity.CATEGORY_LIST[1])) {
				eSize++;
				expenseList.add(mList.get(i));
			} else {
				iSize++;
				incomeList.add(mList.get(i));
			}
		}
		// sort Arraylist ascending
		expenseList = sortListAscendingByTime(expenseList);
		incomeList = sortListAscendingByTime(incomeList);
		// array of the data to be displayed in the graph
		GraphViewData[] expenseData = new GraphViewData[eSize + 1];
		GraphViewData[] incomeData = new GraphViewData[iSize + 1];
		// initial data set at 0,0 for x,y
		expenseData[0] = new GraphViewData(0, 0);
		incomeData[0] = new GraphViewData(0, 0);
		for (int i = 1; i <= eSize; i++) {
			expenseData[i] = new GraphViewData(getValueX(
					start.getTimeInMillis(), expenseList.get(i - 1)
							.getTimeInMillis()), expenseList.get(i - 1)
					.getAmountInDefaultCurrency(getApplicationContext()));
		}
		for (int i = 1; i <= iSize; i++) {
			incomeData[i] = new GraphViewData(getValueX(
					start.getTimeInMillis(), incomeList.get(i - 1)
							.getTimeInMillis()), incomeList.get(i - 1)
					.getAmountInDefaultCurrency(getApplicationContext()));
		}
		if (cbExpenses.isChecked() && cbIncome.isChecked()) {
			expenseGraphViewSeries.resetData(expenseData);
			incomeGraphViewSeries.resetData(incomeData);
			graphView.setManualYAxisBounds(getMaxCount(mList), 0);
		} else if (cbExpenses.isChecked()) {
			expenseGraphViewSeries.resetData(expenseData);
			incomeGraphViewSeries.resetData(new GraphViewData[0]);
			graphView.setManualYAxisBounds(getMaxCount(expenseList), 0);
		} else if (cbIncome.isChecked()) {
			incomeGraphViewSeries.resetData(incomeData);
			expenseGraphViewSeries.resetData(new GraphViewData[0]);
			graphView.setManualYAxisBounds(getMaxCount(incomeList), 0);
		} else {
			incomeGraphViewSeries.resetData(new GraphViewData[0]);
			expenseGraphViewSeries.resetData(new GraphViewData[0]);
		}
		graphView.getGraphViewStyle().setNumHorizontalLabels(
				getHorLabels(mDataRange));
		graphView.getGraphViewStyle().setNumVerticalLabels(10);

	}

	private double getMaxCount(ArrayList<Transaction> mList) {
		ArrayList<Double> mNumbers = new ArrayList<Double>();
		for (int i = 0; i < mList.size(); i++) {
			mNumbers.add(mList.get(i).getAmountInDefaultCurrency(getApplicationContext()));
		}
		return mList.size() == 0 ? 1000.00 : Collections.max(mNumbers);
	}

	@Override
	public boolean onChildClick(ExpandableListView arg0, View arg1,
			int groupPosition, int childPosition, long arg4) {
		// TODO Auto-generated method stub
		Transaction selectedItem = itemList.get(titles.get(groupPosition)).get(
				childPosition);
		long _id = selectedItem.getTransactionId();
		Bundle bundle = new Bundle();
		bundle.putLong("id", _id);
		Intent intent = new Intent(Summary.this, TransactionDetails.class);
		intent.putExtras(bundle);
		startActivity(intent);
		return false;
	}

	@Override
	public void onGroupExpand(int groupPosition) {
		// TODO Auto-generated method stub
		switch (groupPosition) {
		case 0:
			mDataRange = VALUE_TODAY;
			mCurrentStart = getStartTime(VALUE_TODAY);
			mCurrentEnd = getEndTime(VALUE_TODAY);
			break;
		case 1:
			mDataRange = VALUE_YESTERDAY;
			mCurrentStart = getStartTime(VALUE_YESTERDAY);
			mCurrentEnd = getEndTime(VALUE_YESTERDAY);
			break;
		case 2:
			mDataRange = VALUE_WEEK;
			mCurrentStart = getStartTime(VALUE_WEEK);
			mCurrentEnd = getEndTime(VALUE_WEEK);
			break;
		case 3:
			mDataRange = VALUE_MONTH;
			mCurrentStart = getStartTime(VALUE_MONTH);
			mCurrentEnd = getEndTime(VALUE_MONTH);
			break;
		case 4:
			mDataRange = VALUE_YEAR;
			mCurrentStart = getStartTime(VALUE_YEAR);
			mCurrentEnd = getEndTime(VALUE_YEAR);
			break;
		}
		updateGraph(mCurrentStart, mCurrentEnd);
	}

	@Override
	public void onGroupCollapse(int arg0) {
		// TODO Auto-generated method stub
		mDataRange = VALUE_TODAY;
		mCurrentStart = getStartTime(VALUE_TODAY);
		mCurrentEnd = getEndTime(VALUE_TODAY);
		updateGraph(mCurrentStart, mCurrentEnd);
	}

}
