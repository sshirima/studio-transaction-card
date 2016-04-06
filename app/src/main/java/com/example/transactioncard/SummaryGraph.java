package com.example.transactioncard;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.example.transactioncard.database.ConstsDatabase;
import com.example.transactioncard.database.DescriptionTable;
import com.example.transactioncard.dialogs.DialogTimeChange;
import com.example.transactioncard.dialogs.DialogTimeRangeSelector;
import com.example.transactioncard.manager.HomeActivityManager;
import com.example.transactioncard.object.Description;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.PercentFormatter;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by samson on 8/28/15.
 */
public class SummaryGraph extends Activity implements ActionBar.OnNavigationListener,
        OnChartValueSelectedListener, AdapterView.OnItemSelectedListener,
        CheckBox.OnCheckedChangeListener, DialogTimeRangeSelector.DialogTimeRangeListerner, DialogTimeChange.DateSetListener {

    public static final String CLASSNAME = SummaryGraph.class.getName();
    private String graphTitle;
    private PieChart rlPieChartView;
    private float[] yData ;//= {5, 10, 15, 20, 30};
    private String[] xData ;//{"Sony", "Huawei", "Techno", "Iphone", "Samsung"};

    private Spinner spDuration;
    private Spinner spGraphType;
    private ActionBar actionBar;
    private RadioButton rdExpense;
    private RadioButton rdIncome;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String methodName = "onCreate";
        String operation = "Creating activity interface";
        /*
        Get data from  the DB for expenses
         */

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        /*
		 * Set up the action bar
		 */
//        ConstsDatabase.logINFO(CLASSNAME, methodName, "Set up: Home actionbar");
        actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionBar.setDisplayShowTitleEnabled(false);

        /*
		 * Creating  action bar dropdown menu
		 */
//        ConstsDatabase.logINFO(CLASSNAME, methodName, "Set up: Action bar dropdown spinner");

        ArrayAdapter<CharSequence> actionbarDropdownSpinner = getDropDownSpinner();
        actionBar.setListNavigationCallbacks(actionbarDropdownSpinner, this);

        setContentView(R.layout.activity_summarygraph);

        rlPieChartView = (PieChart) findViewById(R.id.rlPieChartView);

        spGraphType = (Spinner) findViewById(R.id.spGraphType);
        spDuration = (Spinner) findViewById(R.id.spDuration);

        rdExpense = (RadioButton)findViewById(R.id.rdExpense);
        rdIncome = (RadioButton)findViewById(R.id.rdIncome);

        if (rdExpense.isChecked()){
            getDataFromDB(ConstsDatabase.CATEGORY_EXPENSES);
        }

        rdExpense.setOnCheckedChangeListener(this);
        rdIncome.setOnCheckedChangeListener(this);

        ArrayAdapter<CharSequence> spGraphTypeAdapter = ArrayAdapter.createFromResource(this, R.array.summary_graphtype,android.R.layout.simple_spinner_item);
        spGraphTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGraphType.setAdapter(spGraphTypeAdapter);

        ArrayAdapter<CharSequence> spDurationAdapter = ArrayAdapter.createFromResource(this, R.array.time_filter_list,android.R.layout.simple_spinner_item);
        spDurationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDuration.setAdapter(spDurationAdapter);

        //Set on graphType spinner selected
        spGraphType.setOnItemSelectedListener(this);

        //Set on duration selection selected
        spDuration.setOnItemSelectedListener(this);

        rlPieChartView.setUsePercentValues(true);
        rlPieChartView.setDescription("");

       /* rlPieChartView.setDragDecelerationFrictionCoef(0.95f);

        typeface = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        rlPieChartView.setCenterTextTypeface(Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf"));
        */
        rlPieChartView.setDrawHoleEnabled(true);
        rlPieChartView.setHoleColorTransparent(true);

        rlPieChartView.setTransparentCircleColor(Color.WHITE);
        rlPieChartView.setTransparentCircleAlpha(110);

        rlPieChartView.setHoleRadius(58f);
        rlPieChartView.setTransparentCircleRadius(61f);

        rlPieChartView.setDrawCenterText(true);

        rlPieChartView.setRotationAngle(0);

        // enable rotation of the chart by touch
        rlPieChartView.setRotationEnabled(true);

        // add a selection listener
        rlPieChartView.setOnChartValueSelectedListener(this);

        //Set legend
        Legend legend = rlPieChartView.getLegend();
        legend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        legend.setXEntrySpace(7);
        legend.setYEntrySpace(5);

        //set data
        ArrayList<Entry> yVals = new ArrayList<Entry>();
        ArrayList<String> xVals = new ArrayList<String>();

        populateDataToTheGraph(yVals, xVals);


    }

    private void populateDataToTheGraph(ArrayList<Entry> yVals, ArrayList<String> xVals) {

        try {
            for (int i =0;i<yData.length;i++)
                yVals.add(new Entry(yData[i], i));

            for (int j=0;j<xData.length;j++)
                xVals.add(xData[j]);
        } catch (Exception e){
            e.printStackTrace();

        }
        //create pie dataset
        PieDataSet pieDataSet = new PieDataSet(yVals, "");
        pieDataSet.setSliceSpace(3);
        pieDataSet.setSelectionShift(5);

        //Add many colors
        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int k: ColorTemplate.VORDIPLOM_COLORS)
            colors.add(k);

        for (int k: ColorTemplate.JOYFUL_COLORS)
            colors.add(k);

        for (int k: ColorTemplate.COLORFUL_COLORS)
            colors.add(k);

        for (int k: ColorTemplate.LIBERTY_COLORS)
            colors.add(k);

        for (int k: ColorTemplate.PASTEL_COLORS)
            colors.add(k);

        colors.add(ColorTemplate.getHoloBlue());
        pieDataSet.setColors(colors);

        //instantiate pie data object
        PieData pieData = new PieData(xVals,pieDataSet);
        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueTextSize(11f);
        pieData.setValueTextColor(Color.GRAY);


        rlPieChartView.setCenterText(selectedCategory);

        rlPieChartView.setData(pieData);

        //Undo all highlights
        rlPieChartView.highlightValues(null);

        //update pie chart
        rlPieChartView.invalidate();
    }

    private ArrayAdapter<CharSequence> getDropDownSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.summary_graphtitles,
                R.layout.listview_actionbar_home);
        adapter.setDropDownViewResource(R.layout.listview_actionbar_home_dropdownview);
        return adapter;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_summarygraph, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.actionToggleValues: {
                for (DataSet<?> set : rlPieChartView.getData().getDataSets())
                    set.setDrawValues(!set.isDrawValuesEnabled());

                rlPieChartView.invalidate();
                break;
            }
            case R.id.actionToggleHole: {
                if (rlPieChartView.isDrawHoleEnabled())
                    rlPieChartView.setDrawHoleEnabled(false);
                else
                    rlPieChartView.setDrawHoleEnabled(true);
                rlPieChartView.invalidate();
                break;
            }
            case R.id.actionDrawCenter: {
                if (rlPieChartView.isDrawCenterTextEnabled())
                    rlPieChartView.setDrawCenterText(false);
                else
                    rlPieChartView.setDrawCenterText(true);
                rlPieChartView.invalidate();
                break;
            }
            case R.id.actionToggleXVals: {

                rlPieChartView.setDrawSliceText(!rlPieChartView.isDrawSliceTextEnabled());
                rlPieChartView.invalidate();
                break;
            }
            case R.id.actionSave: {
                // rlPieChartView.saveToGallery("title"+System.currentTimeMillis());
                rlPieChartView.saveToPath("title" + System.currentTimeMillis(), "");
                break;
            }
            case R.id.actionTogglePercent:
                rlPieChartView.setUsePercentValues(!rlPieChartView.isUsePercentValuesEnabled());
                rlPieChartView.invalidate();
                break;
            case R.id.animateX: {
                rlPieChartView.animateX(1800);
                break;
            }
            case R.id.animateY: {
                rlPieChartView.animateY(1800);
                break;
            }
            case R.id.animateXY: {
                rlPieChartView.animateXY(1800, 1800);
                break;
            }
        }
        return true;
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

        if (e == null)
            return;
        Log.i("VAL SELECTED",
                "Value: " + e.getVal() + ", xIndex: " + e.getXIndex()
                        + ", DataSet index: " + dataSetIndex);
    }

    @Override
    public void onNothingSelected() {
        Log.i("PieChart", "nothing selected");
    }

    private int spDurationCurrentIndex=0;

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.spDuration:
                //Toast.makeText(getApplicationContext(), "Duration "+position, Toast.LENGTH_SHORT).show();
                spDurationCurrentIndex = position;

                switch (spDurationCurrentIndex){
                    case 4:
                        dialogTimeRangeSelector = new DialogTimeRangeSelector();
                        dialogTimeRangeSelector.show(getFragmentManager(), "TimeChangeDialog");
                    break;

                    default:
                        updateGraph();
                        break;
                }
                break;
            case R.id.spGraphType:
                switch (position){
                    case 0:
                        //Toast.makeText(getApplicationContext(), "Pie chart", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Intent intent = new Intent(this, DualLineGraphActivity.class);
                        startActivity(intent);
                        //Toast.makeText(getApplicationContext(), "Line Graph "+position, Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Intent intent2 = new Intent(this, DualBarGraph.class);
                        startActivity(intent2);
                        //Toast.makeText(getApplicationContext(), "Bar graph ", Toast.LENGTH_SHORT).show();
                        break;
                }


                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        switch(itemPosition){
            case 0:
//                Toast.makeText(getApplicationContext(), "Amount vs time", Toast.LENGTH_SHORT).show();
                break;

            case 1:
//                Toast.makeText(getApplicationContext(), "Amount vs description", Toast.LENGTH_SHORT).show();
                break;

        }
        return false;
    }

    private String selectedCategory = ConstsDatabase.CATEGORY_EXPENSES;

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.rdExpense:
                if (isChecked){

                    boolean isCheckedrdIncome = rdIncome.isChecked();

                    if (isCheckedrdIncome){
                        rdIncome.setChecked(false);
                    }

                    selectedCategory = ConstsDatabase.CATEGORY_EXPENSES;

                    updateGraph();


                }
                break;
            case R.id.rdIncome:
                if (isChecked){

                    boolean isCheckedrdIncome = rdExpense.isChecked();

                    if (isCheckedrdIncome){
                        rdExpense.setChecked(false);
                    }
                    

                    selectedCategory = ConstsDatabase.CATEGORY_INCOME;

                    updateGraph();

                }
                break;
        }
    }

    private void updateGraph() {
        getDataFromDB(selectedCategory);

        ArrayList<Entry> yVals = new ArrayList<Entry>();
        ArrayList<String> xVals = new ArrayList<String>();

        populateDataToTheGraph(yVals, xVals);
    }

    DialogTimeRangeSelector dialogTimeRangeSelector;
    Calendar calendarFrom;
    Calendar calendarTo;

    private void getDataFromDB(String category){
        String methodName = "getDataFromDB";
        String operation = "Get data from DB by category, start and endTime";

        ConstsDatabase.logINFO(CLASSNAME, methodName, operation);

        DescriptionTable descriptionTable = new DescriptionTable(getApplicationContext());
        descriptionTable.open();

        Calendar startTime = HomeActivityManager.getStartTimeFromSelectIndex(spDurationCurrentIndex);
        Calendar endTime = HomeActivityManager.getEndTimeFromSelectIndex(spDurationCurrentIndex);

        ArrayList<Description> descList;
        if (spDurationCurrentIndex == 4){
            /*
            Start custom date selector
             */

            descList = descriptionTable.getDescriptionAmounts(category, calendarFrom, calendarTo);

//            descList = descriptionTable.getTotalByDescriptionAll(category);

        } else{
            descList = descriptionTable.getDescriptionAmounts(category, startTime, endTime);
        }

        if (descList != null){
            yData = new float[descList.size()];
            xData = new String[descList.size()];
            for (int i = 0; i<descList.size();i++){
                Description description = descList.get(i);
                xData[i] = description.getDescription();
                yData[i] = (float)description.getAmountTotalInDefaultCurrency(getApplicationContext());
            }
        } else {
            //Set dataset equal to zero
            yData = new float[0];
            xData = new String[0];
        }

        descriptionTable.close();
    }

    @Override
    public void onDialogTimeRangeButtonOk(Calendar calFrom, Calendar calTo) {
        calendarFrom = calFrom;
        calendarTo = calTo;
        dialogTimeRangeSelector = null;
        /*
        Update graph
         */
        updateGraph();
    }

    @Override
    public void onDialogTimeRangeButtonCancel() {
        dialogTimeRangeSelector = null;
    }

    @Override
    public void onDateSet(DialogFragment dialogFrag, int year, int month, int day, boolean isStart) {

        if (dialogTimeRangeSelector != null){
            Dialog dialog = dialogFrag.getDialog();
            Calendar calendar;

            if (dialogTimeRangeSelector.isClickedCalendarFrom()){

                 if (dialogTimeRangeSelector.getCalendarFrom() != null){

                     calendar =  dialogTimeRangeSelector.getCalendarFrom();
                     calendar.set(Calendar.YEAR, year);
                     calendar.set(Calendar.MONTH, month);
                     calendar.set(Calendar.DAY_OF_MONTH, day);
                     dialogTimeRangeSelector.setCalendarFrom(calendar);
                 } else {
                     calendar = Calendar.getInstance();
                     calendar.set(Calendar.YEAR, year);
                     calendar.set(Calendar.MONTH, month);
                     calendar.set(Calendar.DAY_OF_MONTH, day);
                     dialogTimeRangeSelector.setCalendarFrom(calendar);
                 }
                /*
				    Update editText
				 */
                EditText editText = (EditText)dialog.findViewById(R.id.etTimeRangeFrom);
                editText.setText(HomeActivity.getFormatedTime(calendar));

                dialogTimeRangeSelector.setIsClickedCalendarFrom(false);

            } else if (dialogTimeRangeSelector.isClickedCalendarTo()){
                if (dialogTimeRangeSelector.getCalendarTo() != null){
                    calendar = dialogTimeRangeSelector.getCalendarTo();
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, day);
                    dialogTimeRangeSelector.setCalendarTo(calendar);
                } else {
                    calendar = Calendar.getInstance();
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, day);
                    dialogTimeRangeSelector.setCalendarTo(calendar);
                }
                /*
				Update editText
		        */
                EditText editText = (EditText)dialog.findViewById(R.id.etTimeRangeTo);
                editText.setText(HomeActivity.getFormatedTime(calendar));

                dialogTimeRangeSelector.setIsClickedCalendarTo(false);
            }
        }
    }

    @Override
    public void onTimeChange(DialogFragment dialogFrag, int hour, int minute, boolean isStart) {
        if (dialogTimeRangeSelector != null){
            Dialog dialog = dialogFrag.getDialog();
            Calendar calendar;

            if (dialogTimeRangeSelector.isClickedTimeFrom()){

                if (dialogTimeRangeSelector.getCalendarFrom() != null){
                    calendar = dialogTimeRangeSelector.getCalendarFrom();
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, minute);
                    dialogTimeRangeSelector.setCalendarFrom(calendar);
                } else {
                    calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY,hour);
                    calendar.set(Calendar.MINUTE, hour);
                    dialogTimeRangeSelector.setCalendarFrom(calendar);
                }
                /*
				Update editText
				 */
                EditText editText = (EditText)dialog.findViewById(R.id.etTimeRangeFrom);
                editText.setText(HomeActivity.getFormatedTime(calendar));

                dialogTimeRangeSelector.setIsClickedTimeFrom(false);

            } else if (dialogTimeRangeSelector.isClickedTimeTo()){

                if (dialogTimeRangeSelector.getCalendarTo() != null){

                    calendar = dialogTimeRangeSelector.getCalendarTo();
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, minute);
                    dialogTimeRangeSelector.setCalendarTo(calendar);
                } else {
                    calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, minute);
                    dialogTimeRangeSelector.setCalendarTo(calendar);
                }

                /*
				Update editText
				 */
                EditText editText = (EditText)dialog.findViewById(R.id.etTimeRangeTo);
                editText.setText(HomeActivity.getFormatedTime(calendar));

                dialogTimeRangeSelector.setIsClickedTimeTo(false);

            }
        }
    }
}
