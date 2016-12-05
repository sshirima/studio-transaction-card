package com.example.transactioncard.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.view.View.OnClickListener;

import com.example.transactioncard.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by samson on 2/23/16.
 */
public class DialogTimeRangeSelector extends DialogFragment implements OnClickListener/*,DialogTimeChange.DateSetListener*/ {

    private EditText etFrom;
    private EditText etTo;
    private ImageView ivPickFromCalender;
    private ImageView ivPickFromTime;
    private ImageView ivPickToCalender;
    private ImageView ivPickToTime;
    private Button btOk;
    private Button btCancel;
    private Calendar calendarFrom;
    private Calendar calendarTo;
    private Calendar calendar;
    private boolean isClickedCalendarTo = false;
    private boolean isClickedCalendarFrom = false;
    private boolean isClickedTimeFrom = false;
    private boolean isClickedTimeTo = false;

    public interface DialogTimeRangeListerner {
        void onDialogTimeRangeButtonOk(Calendar calendarFrom, Calendar calendarTo);

        void onDialogTimeRangeButtonCancel();
    }

    DialogTimeRangeListerner dialogTimeRangeListerner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String methodName = "";
        String operation = "";

        /*
		 * Create dialog interface
		 */
        View dialog = inflater.inflate(R.layout.dialog_timerange_selector,
                container, false);

        /*
        Initializes components variables
         */
        etFrom = (EditText)dialog.findViewById(R.id.etTimeRangeFrom);
        etTo = (EditText)dialog.findViewById(R.id.etTimeRangeTo);
        ivPickFromCalender = (ImageView)dialog.findViewById(R.id.ivTimeRangeFromPickCalender);
        ivPickFromTime = (ImageView)dialog.findViewById(R.id.ivTimeRangeFromPickTime);
        ivPickToCalender = (ImageView)dialog.findViewById(R.id.ivTimeRangeToPickCalender);
        ivPickToTime = (ImageView)dialog.findViewById(R.id.ivTimeRangeToPickTime);
        btOk = (Button)dialog.findViewById(R.id.btTimeRangeOk);
        btCancel = (Button) dialog.findViewById(R.id.btTimeRangeCancel);

        /*
        Set onClick listerner for the callback buttons
         */
        btCancel.setOnClickListener(this);
        btOk.setOnClickListener(this);
        ivPickFromCalender.setOnClickListener(this);
        ivPickFromTime.setOnClickListener(this);
        ivPickToCalender.setOnClickListener(this);;
        ivPickToTime.setOnClickListener(this);

        return dialog;
    }

    public Calendar getCalendarFrom() {
        return calendarFrom;
    }

    public void setCalendarFrom(Calendar calendarFrom) {
        this.calendarFrom = calendarFrom;
    }

    public Calendar getCalendarTo() {
        return calendarTo;
    }

    public void setCalendarTo(Calendar calendarTo) {
        this.calendarTo = calendarTo;
    }

    public boolean isClickedCalendarTo() {
        return isClickedCalendarTo;
    }

    public void setIsClickedCalendarTo(boolean isClickedCalendarTo) {
        this.isClickedCalendarTo = isClickedCalendarTo;
    }

    public boolean isClickedCalendarFrom() {
        return isClickedCalendarFrom;
    }

    public void setIsClickedCalendarFrom(boolean isClickedCalendarFrom) {
        this.isClickedCalendarFrom = isClickedCalendarFrom;
    }

    public boolean isClickedTimeFrom() {
        return isClickedTimeFrom;
    }

    public void setIsClickedTimeFrom(boolean isClickedTimeFrom) {
        this.isClickedTimeFrom = isClickedTimeFrom;
    }

    public boolean isClickedTimeTo() {
        return isClickedTimeTo;
    }

    public void setIsClickedTimeTo(boolean isClickedTimeTo) {
        this.isClickedTimeTo = isClickedTimeTo;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_timerange_selector);
        dialog.show();
        dialog.setTitle("Select time range");
        return dialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            dialogTimeRangeListerner = (DialogTimeRangeListerner) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement DialogTimeRangeListerner");
        }
    }

    private static final int TIME_DIALOG_ID = 1;
    private static final int DATE_DIALOG_ID = 0;

    @Override
    public void onClick(View v) {
        DialogTimeChange dialogTimeChange = new DialogTimeChange();
        if (calendar == null){
            calendar = Calendar.getInstance();
        }
        switch (v.getId()){
            case R.id.ivTimeRangeFromPickCalender:
                isClickedCalendarFrom = true;
                if (calendarFrom == null){
                    calendarFrom = Calendar.getInstance();
                }
                showDialogCalender(dialogTimeChange, calendarFrom);
                calendarFrom.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
                calendarFrom.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
                calendarFrom.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));
                /*
                Update text on the editText field
                 */
//                etFrom.setText(getFormatedTime(calendarFrom));
                break;
            case R.id.ivTimeRangeFromPickTime:
                isClickedTimeFrom = true;
                if (calendarFrom == null){
                    calendarFrom = Calendar.getInstance();
                }
                showDialogTime(dialogTimeChange, calendarFrom);
                calendarFrom.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
                calendarFrom.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
                /*
                Update text on the editText field
                 */
//                etFrom.setText(getFormatedTime(calendarFrom));
                break;
            case R.id.ivTimeRangeToPickCalender:
                isClickedCalendarTo = true;
                showDialogCalender(dialogTimeChange, calendarTo);
                if (calendarTo == null){
                    calendarTo = Calendar.getInstance();
                }
                calendarTo.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
                calendarTo.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
                calendarTo.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));
                 /*
                Update text on the editText field
                 */
//                etTo.setText(getFormatedTime(calendarTo));
                break;
            case R.id.ivTimeRangeToPickTime:
                isClickedTimeTo = true;
                if (calendarTo == null){
                    calendarTo = Calendar.getInstance();
                }
                showDialogTime(dialogTimeChange, calendarTo);
                calendarTo.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
                calendarTo.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
                 /*
                Update text on the editText field
                 */
//                etTo.setText(getFormatedTime(calendarTo));
                break;
            case R.id.btTimeRangeCancel:
                dialogTimeRangeListerner.onDialogTimeRangeButtonCancel();
                dismiss();
                break;
            case R.id.btTimeRangeOk:
                dialogTimeRangeListerner.onDialogTimeRangeButtonOk(calendarFrom,calendarTo);
                dismiss();
                break;

        }

    }

    private String getFormatedTime(Calendar cal) {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyy/MM/d",
            Locale.ENGLISH);
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm",
                Locale.ENGLISH);
        return sdfDate.format(new Date(cal.getTimeInMillis())) + ", " + sdfTime.format(new Date(cal.getTimeInMillis()));
    }

    private void showDialogTime(DialogTimeChange dialogTimeChange, Calendar cal) {
        if (cal == null){
            cal = Calendar.getInstance();
        }
        dialogTimeChange.setInitialTime(
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE), true, TIME_DIALOG_ID);
        dialogTimeChange.setCallingDialog(DialogTimeRangeSelector.this, 1);
        dialogTimeChange.show(getFragmentManager(), "TimeChangeDialog");
        /*cal.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));*/
//        return cal;
    }

    private void showDialogCalender(DialogTimeChange dialogTimeChange, Calendar cal) {
        if (cal == null){
            cal = Calendar.getInstance();
        }
        dialogTimeChange.setInitialDate(cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH), DATE_DIALOG_ID);
        dialogTimeChange.setCallingDialog(DialogTimeRangeSelector.this, 1);
        dialogTimeChange.show(getFragmentManager(), "DateChangeDialog");
        /*cal.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
        cal.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
        cal.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));*/
//        return cal;
    }

   /* @Override
    public void onDateSet(DialogFragment dialogFrag, int year, int month, int day, boolean isStart) {
        if (calendar == null){
            calendar = Calendar.getInstance();
        }

    }*/

   /* @Override
    public void onTimeChange(DialogFragment dialogFrag, int hour, int minute, boolean isStart) {
        if (calendar == null){
            calendar = Calendar.getInstance();
        }
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
    }*/
}
