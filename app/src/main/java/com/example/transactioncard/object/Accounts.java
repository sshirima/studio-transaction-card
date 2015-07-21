package com.example.transactioncard.object;

import android.content.Context;

public class Accounts {

	private String accountName;
	private long accountID;
	private double income =0;
	private double expenditure=0;
	private boolean mIsDefault = false;
	
	public Accounts(Context context, String _accountName) {
		this.accountName = _accountName;
		
	}
	
	public void setAccountName(String accName){
		this.accountName = accName;
	}
	
	public void setDefault(boolean isDefault){
		this.mIsDefault = isDefault;
	}
	
	public void setId(long id){
		this.accountID = id;
	}
	

	
	public void setIncome(double income){
		this.income = income;
	}
	
	public void setExpenditure(double expenditure){
		this.expenditure = expenditure;
	}
	
	public double getExpenses(Context context){
		String currencyCode = Currencies.getDefaultCurrency(context);
		CurrencyConvertor convertor = new CurrencyConvertor();
		double returnAmount = convertor.convertUSDtoCODE(context, currencyCode,
				this.expenditure);
		return Math.round(returnAmount);
	}
	
	public double getIncome(Context context){
		String currencyCode = Currencies.getDefaultCurrency(context);
		CurrencyConvertor convertor = new CurrencyConvertor();
		double returnAmount = convertor.convertUSDtoCODE(context, currencyCode,
				this.income);
		return Math.round(returnAmount);
	}
	
	public double getBalance(Context context){
		String currencyCode = Currencies.getDefaultCurrency(context);
		CurrencyConvertor convertor = new CurrencyConvertor();
		double returnAmount = convertor.convertUSDtoCODE(context, currencyCode,
				this.income - this.expenditure);
		return Math.round(returnAmount);
	}
	
	public String getAccountName(){
		return this.accountName;
	}
	
	public boolean isDefault(){
		return this.mIsDefault;
	}
	
	public long getId(){
		return this.accountID;
	}
}
