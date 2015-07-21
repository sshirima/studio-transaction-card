package com.example.transactioncard.object;

import android.content.Context;

public class Currency {
	
	private int currencyId;
	private String currencyName;
	private String currencyCode;
	private double rateFromUSD;
	private int flagId;
	private Context applicationContext;
	
	public Currency (Context context, String currencyCode){
		this.applicationContext = context;
		this.currencyCode = currencyCode;
	}
	
	
	
	public int getFlagId() {
		return flagId;
	}



	public void setFlagId(int flagId) {
		this.flagId = flagId;
	}



	public String getCurrencyName (){
		return this.currencyName;
	}
	
	public String getCurrencyCode (){
		return this.currencyCode;
	}
	
	public double getRateFromUSD (){
		return this.rateFromUSD;
	}
	
	public int getCurrencyId(){
		return this.currencyId;
	}

	
	public void setCurrencyName(String currencyName){
		this.currencyName = currencyName;
	}
	
	public void setCurrencyId(int currencyId){
		this.currencyId = currencyId;
	}
	
	public void setCurrencyRate(double currencyRate){
		this.rateFromUSD = currencyRate;
	}

}
