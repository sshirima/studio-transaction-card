package com.example.transactioncard.object;

import java.util.Calendar;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.transactioncard.database.ConstsDatabase;
import com.example.transactioncard.database.CurrencyTable;

public class CurrencyConvertor {


	public CurrencyConvertor() {

	}

	public double convertUSDtoCODE(Context context, String toCode, double amount) {
		double rate = getSavedCurrencyRates(context, toCode);
		return amount * rate;
	}

	public double convertCODEtoUSD(Context context, String fromCodes, double amount) {
		double rate = getSavedCurrencyRates(context, fromCodes);
		return amount / rate;
	}

	public double convertCODEtoCODE(Context context, String fromCodes, String toCodes,
			double amount) {
		return convertUSDtoCODE(context, toCodes, convertCODEtoUSD(context, fromCodes, amount));
	}
	
	private static SharedPreferences conversionRates, updateDate;
	public static final String CURRENCY_RATES = "CurrencyRates";
	public static final String CURRENCY_UPDATE_DATE = "UpdateDate";
	
	public void setRateUSDtoCODE(Context context,String currencyCode, double rate){
		/*
		Save the currency rate to the DB
		 */

		CurrencyTable currencyTable = new CurrencyTable(context);
		CurrencyCashFlow currency = new CurrencyCashFlow(context, currencyCode);
		currency.setCurrencyRate(rate);

		/*
		Update the currency value to the sqlite DB
		 */
		currencyTable.open();
		boolean isUpdated = currencyTable.updateRateByCurrencyCode(currency);
		currencyTable.close();
		if (isUpdated){
			ConstsDatabase.logINFO("CurrencyConvertor", "setRateUSDtoCODE", "UpdateCurrencyRate");
		}else {
			ConstsDatabase.logERROR("setRateUSDtoCODE", "UpdateCurrencyRate");
		}
	}
	
	public long getDateUpdated(Context context, String currencyCode){
		updateDate = context.getSharedPreferences(CURRENCY_UPDATE_DATE, 0);
		return updateDate.getLong(currencyCode, 0);
	}
	
	public double getSavedCurrencyRates(Context context, String currencyCode){
		CurrencyTable currencyTable = new CurrencyTable(context);
		currencyTable.open();
		double rate = currencyTable.getRateByCurrencyCode(currencyCode);
		currencyTable.close();
		return rate;
	}

	public CurrencyCashFlow getSavedCurrency(Context context, String currencyCode){
		CurrencyTable currencyTable = new CurrencyTable(context);
		currencyTable.open();
		CurrencyCashFlow currency = currencyTable.getCurrencyByCode(currencyCode);
		currencyTable.close();
		return currency;
	}
	
}
