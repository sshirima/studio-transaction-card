package com.example.transactioncard.object;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import com.example.transactioncard.Consts;
import com.example.transactioncard.HomeActivity;
import com.example.transactioncard.R;
import com.example.transactioncard.Settings;
import com.example.transactioncard.database.ConstsDatabase;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
	
	public static final String CLASSNAME = ExpandableListAdapter.class.getName();

	private Context context;
	private List<String> _listDataHeader; // header titles
	// child data in format of header title, child title
	private HashMap<String, List<Transaction>> _listDataChild;
	private static final String[] labels = { "Today", "Yesterday", "This Week",
			"This Month", "This Year" };
	private String EXPENSES_LABEL = "Expenses";
	private String INCOME_LABEL = "Income";

	public ExpandableListAdapter(Context context, List<String> listDataHeader,
			HashMap<String, List<Transaction>> listChildData) {
		this.context = context;
		this._listDataHeader = listDataHeader;
		this._listDataChild = listChildData;
	}

	@Override
	public Object getChild(int groupPosition, int childPosititon) {
		return this._listDataChild.get(this._listDataHeader.get(groupPosition))
				.get(childPosititon);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		String methodName = "getChildView";
		String operation = "Get the View of the child";
		ConstsDatabase.logINFO(CLASSNAME, methodName, operation);
		
		ViewHolder holder;
		Transaction transaction = this._listDataChild.get(
				this._listDataHeader.get(groupPosition)).get(childPosition);

		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this.context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.listview_home, null);
		}
		
		/*
		 * Initializes viewholder variables
		 */
		holder = new ViewHolder();
		holder.tvAmount = (TextView) convertView.findViewById(R.id.tvAmount);
		holder.tvCurrency = (TextView) convertView
				.findViewById(R.id.tvCurrency);
		holder.tvDate = (TextView) convertView.findViewById(R.id.tvTime);
		holder.tvDes = (TextView) convertView.findViewById(R.id.tvDescription);
		holder.ivCategory = (ImageView) convertView
				.findViewById(R.id.ivCategory);
		/*
		 * Get transaction values
		 */
		int amount = (int)Math.round(transaction.getAmountInDefaultCurrency(context));
		String currency = Settings.getDefaultCurrency(context);
		int imageId = transaction.getImageId();
		String date = transaction.getDate();
		String descriptionName = transaction.getDescriptionName();
		
		/*
		 * Setting the values to the texview
		 */
		holder.tvAmount.setText(new DecimalFormat().format(amount));
		holder.tvCurrency.setText(" "+currency);
		holder.tvDate.setText(date);
		holder.tvDes.setText(descriptionName);
		holder.ivCategory.setImageResource(imageId);
		return convertView;
	}

	static class ViewHolder {
		TextView tvAmount, tvDate, tvDes, tvCurrency;
		ImageView ivCategory;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return this._listDataChild.get(this._listDataHeader.get(groupPosition))
				.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return this._listDataHeader.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return this._listDataHeader.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}
	
	static public final String KEY_CURRENCY = "currency";
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		String methodName = "getGroupView";
		String operation = "Get view for the GroupView";
		ConstsDatabase.logINFO(CLASSNAME, methodName, operation);

		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this.context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(
					R.layout.listview_summary_title, null);
		}

		TextView tvLabel = (TextView) convertView
				.findViewById(R.id.tvSummaryTitles);
		TextView tvTotalIncome = (TextView) convertView
				.findViewById(R.id.tvTotalIncome);
		TextView tvTotalExpense = (TextView) convertView
				.findViewById(R.id.tvTotalExpense);
		double expenseSum = calculateSum(context,
				this._listDataChild.get(this.getGroup(groupPosition)), true);
		
		double incomeSum = calculateSum(context,
				this._listDataChild.get(this.getGroup(groupPosition)), false);
		int expenses = (int)Math.round(expenseSum);
		int income = (int)Math.round(incomeSum);
		
		tvLabel.setTypeface(null, Typeface.BOLD);
		tvLabel.setText(labels[groupPosition]);
		
		String currency = Settings.getDefaultCurrency(context);
		
		tvTotalExpense.setText(Consts.STRTXT_TOTAL+" " + EXPENSES_LABEL + ": " +  new DecimalFormat().format(expenses)
				+ " " + currency);
		tvTotalExpense.setTextColor(Color.RED);
		tvTotalIncome.setText(Consts.STRTXT_TOTAL+" " + INCOME_LABEL + ": " + new DecimalFormat().format(income) + " "
				+ currency);
		tvTotalIncome.setTextColor(Color.rgb(0, 128, 0));

		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public static double calculateSum(Context context, List<Transaction> list, boolean expenses) {
		double expense = 0;
		double income = 0;
		for (int i = 0; i < list.size(); i++) {
			boolean isExpenses = list.get(i).getCategory()
					.equals(HomeActivity.CATEGORY_LIST[1]);
			if (isExpenses) {
				expense = expense + list.get(i).getAmountInDefaultCurrency(context);
			} else {
				income = income + list.get(i).getAmountInDefaultCurrency(context);
			}
		}
		return expenses ? expense : income;
	}
}
