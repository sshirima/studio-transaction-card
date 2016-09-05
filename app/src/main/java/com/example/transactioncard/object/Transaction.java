package com.example.transactioncard.object;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.Context;

import com.example.transactioncard.R;
import com.example.transactioncard.Settings;

public class Transaction {
	private long transactionId;
	private double transactionAmount;
	private Description description;
	private long accountId;
	private String accountName;
	private long timeInMillis;
	private String transactionCategory;
	private Context context;
	public static final String EXPENSES = "Expenses";
	public static final String INCOME = "Income";
	private String currencyCode = Currencies.CURRENCY_CODE_US;
	public Transaction() {

	}

	public Transaction(Context context, double transactionAmount, long timeInMillis,
			String currencyCode) {
		this.transactionAmount = transactionAmount;
		this.currencyCode = currencyCode;
		this.timeInMillis = timeInMillis;
		this.context = context;
	}

	public double getAmount(){
		return this.transactionAmount;
	}

	public double getAmountInDefaultCurrency(Context context) {
		String currencyCode = Settings.getDefaultCurrency(context);
		CurrencyConvertor currencyConvertor = new CurrencyConvertor();

		double amountInDefaultCurrency;

		if (currencyCode.equals(this.currencyCode)){
			amountInDefaultCurrency = this.transactionAmount;
		}else {
			double amountInUSD = currencyConvertor.convertCODEtoUSD(context,
					this.currencyCode, this.transactionAmount);
			amountInDefaultCurrency = currencyConvertor.convertUSDtoCODE(context, currencyCode,
					amountInUSD);
		}


		return amountInDefaultCurrency;
	}

	
	
	public String getCurrencyCode() {
		return this.currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getCategory() {
		return this.transactionCategory;
	}

	public long getAccountId() {
		return this.accountId;
	}

	public String getAccountName() {
		return this.accountName;
	}

	public long getTransactionId() {
		return this.transactionId;
	}

	public String getTime() {
		return getFormatedTime();
	}

	public long getTimeInMillis() {
		return timeInMillis;
	}

	public int getImageId() {
		return transactionCategory.equals(INCOME) ? R.drawable.ic_arrow_down
				: R.drawable.ic_arrow_up;
	}

	public String getDate() {
		return getFormatedDate();
	}

	public static String getFormatedDate(Calendar calendar) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyy/MM/d",
				Locale.ENGLISH);
		return simpleDateFormat.format(new Date(calendar.getTimeInMillis()));
	}

	public static String getFormatedTime(Calendar calendar) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm",
				Locale.ENGLISH);
		return simpleDateFormat.format(new Date(calendar.getTimeInMillis()));
	}

	private String getFormatedDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timeInMillis);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyy/MM/d",
				Locale.ENGLISH);
		return simpleDateFormat.format(new Date(calendar.getTimeInMillis()));
	}

	private String getFormatedTime() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timeInMillis);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm",
				Locale.ENGLISH);
		return simpleDateFormat.format(new Date(calendar.getTimeInMillis()));
	}

	public void setTransactionId(long transactionId) {
		this.transactionId = transactionId;
	}

	public void setTransactionAmount(double transactionAmount,
			String currencyCode) {
		this.transactionAmount = transactionAmount;
		this.currencyCode = currencyCode;
	}

	public void setTime(long timeInMillis) {
		this.timeInMillis = timeInMillis;
	}

	public void setCategory(String transactionCategory) {
		this.transactionCategory = transactionCategory;
	}

	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}

	public void setDescription(Description description) {
		this.description = description;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getDescriptionName() {
		return this.description.getDescription();
	}

	public Description getDescription() {
		return this.description;
	}

}
