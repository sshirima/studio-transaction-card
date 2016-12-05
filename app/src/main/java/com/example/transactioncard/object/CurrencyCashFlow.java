package com.example.transactioncard.object;

import android.content.Context;

public class CurrencyCashFlow {
	
	private int currencyId;
	private String currencyName;
	private String currencyCode;
	private double rateFromUSD;
	private int flagId;
	private long updateTime;
	
	public CurrencyCashFlow(Context context, String currencyCode){
		this.currencyCode = currencyCode;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
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
