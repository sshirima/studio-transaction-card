package com.example.transactioncard.dialogs;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TimePicker;

public class DialogTimeChange extends DialogFragment {
	private static int mYear;
	private static int mMonth;
	private static int mDay;
	private static int mHour;
	private static int mMinute;
	private static int newHour;
	private static int newMinute;
	private static boolean is24hours;
	private static int newYear;
	private static int newMonth;
	private static int newDay;
	private int dialogId = 0;
	private boolean isStartTime;
	// date listener
	DateSetListener onDateSetListener;
	private DialogFragment dialog;
	
	public interface DateSetListener{
		 void onDateSet(DialogFragment dialogFrag, int year, int month, int day, boolean isStart);
		 void onTimeChange(DialogFragment dialogFrag, int hour, int minute, boolean isStart);
	}
	
	public void setCallingDialog(DialogFragment dialogFragment){
		this.dialog = dialogFragment;
	}



	private int callingDialogID = 0;

	public void setCallingDialog(DialogFragment dialogFragment, int callingDialogID){
		this.dialog = dialogFragment;
		this.callingDialogID = callingDialogID;
	}

	public int getCallingDialogID(){
		return this.callingDialogID;
	}

	private DatePickerDialog.OnDateSetListener datePickListener = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker arg0, int year, int month, int day) {
			// TODO Auto-generated method stub
			newYear = year;
			newMonth = month;
			newDay = day;
			try{
				onDateSetListener.onDateSet(dialog, newYear, newMonth, newDay, isStartTime);
			}catch(Exception e){
				System.out.println("Failed to set date:" + e.toString());
			}

		}
	};

	private TimePickerDialog.OnTimeSetListener timePickerListerner = new TimePickerDialog.OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker arg0, int hour, int minute) {
			// TODO Auto-generated method stub
			newHour = hour;
			newMinute = minute;
			try{
			onDateSetListener.onTimeChange(dialog, newHour, newMinute, isStartTime);
			}catch(Exception e){
				System.out.println("Failed to set time:" + e.toString());
			}
		}
	};

	public void setInitialTime(int h, int m, boolean _is24Hours, int dialogID) {
		mHour = h;
		mMinute = m;
		is24hours = _is24Hours;
		dialogId = dialogID;
		
	}
	
	public boolean isStartTime(boolean startTime){
		this.isStartTime = startTime;
		return isStartTime;
	}
	
	public void setInitialDate(int y, int m, int d, int dialogID) {
		mYear = y;
		mMonth = m;
		mDay = d;
		dialogId = dialogID;
		
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		switch (dialogId) {
		case 0:
			return new DatePickerDialog(getActivity(), datePickListener, mYear,
					mMonth, mDay);
		case 1:
			return new TimePickerDialog(getActivity(), timePickerListerner,
					mHour, mMinute, is24hours);
		}
		return null;
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		try {
			onDateSetListener = (DateSetListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement DateSetListener");
		}
	}
}
