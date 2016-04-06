package com.example.transactioncard.manager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.example.transactioncard.Consts;
import com.example.transactioncard.HomeActivity;
import com.example.transactioncard.R;
import com.example.transactioncard.Settings;
import com.example.transactioncard.database.ConstsDatabase;
import com.example.transactioncard.database.TransactionTable;
import com.example.transactioncard.object.Transaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class HomeActivityManager {
	
	private static final String CLASSNAME = HomeActivityManager.class.getName();
	
	private Context context;
	
	public HomeActivityManager(Context context){
		String methodName = "HomeActivityManager";
		String operation = "Create "+methodName+" instance";
		ConstsDatabase.logINFO(CLASSNAME, methodName, operation);
		this.context = context;
	}
	
	public String getDateOfMonthFromCalendar(Calendar calendar) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		SimpleDateFormat sdf = new SimpleDateFormat(
				sharedPreferences.getString(Settings.KEY_DATE_FORMAT, "MMMM dd, yyyy"),
				Locale.ENGLISH);
		return sdf.format(new Date(calendar.getTimeInMillis()));
	}
	
	public String getDayOfWeekFromCalendar (Calendar calendar){
		String methodName = "getDayOfWeekFromCalendar";
		String operation = "Get day of the week from the calendar";
		ConstsDatabase.logINFO(CLASSNAME, methodName, operation);
		
		/*
		 * Get day of the week
		 */
		String dayOfWeek = null;
		
		switch (calendar.get(Calendar.DAY_OF_WEEK)) {
		case 1:
			dayOfWeek = Consts.STR_SUNDAY;
			break;
		case 2:
			dayOfWeek = Consts.STR_MONDAY;
			break;
		case 3:
			dayOfWeek = Consts.STR_TUESDAY;
			break;
		case 4:
			dayOfWeek = Consts.STR_WEDNESDAY;
			break;
		case 5:
			dayOfWeek = Consts.STR_THURSDAY;
			break;
		case 6:
			dayOfWeek = Consts.STR_FRIDAY;
			break;
		case 7:
			dayOfWeek = Consts.STR_SATURDAY;
			break;
		}
		return dayOfWeek;
		
	}
	
	public static Calendar getStartTimeFromSelectIndex(int index) {
		Calendar mStart = Calendar.getInstance();
		switch (index) {
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
			mStart.set(Calendar.DATE, -365);
			mStart.set(Calendar.HOUR_OF_DAY, 0);
			mStart.set(Calendar.HOUR_OF_DAY, 0);
			mStart.set(Calendar.MINUTE, 0);
			mStart.set(Calendar.SECOND, 0);
			break;
		}
		return mStart;
	}
	
	public static Calendar getEndTimeFromSelectIndex(int index) {
		Calendar mEnd;
		switch (index) {
		case 0:
		case 2:
		case 3:
		case 4:
			mEnd = Calendar.getInstance();
			break;
		case 1:
			mEnd = Calendar.getInstance();
			mEnd.set(Calendar.HOUR_OF_DAY, 0);
			mEnd.set(Calendar.MINUTE, 0);
			break;
		
		default:
			mEnd = Calendar.getInstance();
			break;
		}
		return mEnd;
	}

	/**
	 * @param homeActivity TODO
	 * @param methodName
	 * @param selectionArgs
	 * @param selection
	 * @return
	 */
	public ArrayList<Transaction> queryTransactionsBySelection(
			HomeActivity homeActivity, String methodName, String[] selectionArgs, String selection) {
		ConstsDatabase.logINFO(HomeActivity.CLASSNAME, methodName, "Query with selection: "+selection);
		TransactionTable transactionTable = new TransactionTable(
				homeActivity.getApplicationContext());
		transactionTable.open();
		
		ArrayList<Transaction> returnList = new ArrayList<Transaction>();
		returnList = transactionTable.getTransactionsByCondition(selection,
				selectionArgs);
		transactionTable.close();
		ArrayList<Transaction> sortedList = HomeActivity.sortListDescendingByTime(returnList);
		return sortedList;
	}

	public String getNameFromListNavIndex(Context context, int index) {
		String methodName = "getNameFromListNavIndex";
		String operation = "Get name of the selection from list navigation index";
		ConstsDatabase.logINFO(HomeActivity.CLASSNAME, methodName, operation);
		
		String returnString = null;
		String[] array = context.getResources().getStringArray(R.array.time_filter_list);
		returnString = array[index];
		return returnString;
	}

}
