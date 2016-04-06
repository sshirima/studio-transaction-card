package com.example.transactioncard.object;


import android.content.Context;

import com.example.transactioncard.Settings;

public class Description {

	private long descriptionId;
	private String mDescription;
	private double amountTotal;
	private String category;

	public Description() {

	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Description(String descriptionName) {

	}

	public double getAmountTotalInUSD() {
		return amountTotal;
	}

	public double getAmountTotalInDefaultCurrency(Context context){
		CurrencyConvertor currencyConvertor = new CurrencyConvertor();
		String defaultCurrency = ""+ Settings.getDefaultCurrency(context);
		return currencyConvertor.convertUSDtoCODE(context, defaultCurrency, this.amountTotal);
	}

	public void setAmountTotalInUSD(double amountTotal) {
		this.amountTotal = amountTotal;
	}

	public void setDescription(String description) {
		this.mDescription = description;
	}

	public void setId(long id) {
		this.descriptionId = id;
	}

	public String getDescription() {
		return this.mDescription;
	}

	public long getId() {
		return this.descriptionId;
	}
}
