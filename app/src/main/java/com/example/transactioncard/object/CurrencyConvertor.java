package com.example.transactioncard.object;

import java.util.Calendar;

import android.content.Context;
import android.content.SharedPreferences;

public class CurrencyConvertor {

	private double rateUSDtoTSH = 1626.39;
	private double rateUSDtoKES = 87.2549;
	private double rateUSDtoUGX = 2574.46;
	private double rateUSDtoEUR = 0.77323;
	private double rateUSDtoGBP = 0.77323;
	private final String USD_CODE = Currencies.CURRENCY_CODE_US;
	private final String TSH_CODE = Currencies.CURRENCY_CODE_TANZANIA;
	private final String UGX_CODE = Currencies.CURRENCY_CODE_UGANDA;
	private final String KES_CODE = Currencies.CURRENCY_CODE_KENYA;
	private final String GBP_CODE = Currencies.CURRENCY_CODE_POUND;
	private final String EUR_CODE = Currencies.CURRENCY_CODE_EURO;
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
		conversionRates = context.getSharedPreferences(CURRENCY_RATES, 0);
		updateDate = context.getSharedPreferences(CURRENCY_UPDATE_DATE, 0);
		SharedPreferences.Editor rateEditor = conversionRates.edit();
		rateEditor.putString(currencyCode, Double.toString(rate));
		rateEditor.commit();
		SharedPreferences.Editor timeEditor = updateDate.edit();
		timeEditor.putLong(currencyCode, Calendar.getInstance().getTimeInMillis());
		timeEditor.commit();
	}
	
	public long getDateUpdated(Context context, String currencyCode){
		updateDate = context.getSharedPreferences(CURRENCY_UPDATE_DATE, 0);
		return updateDate.getLong(currencyCode, 0);
	}
	
	private double getDefaultRatings(String code){
		double returnRate = 0.0;
		if (code.equals(EUR_CODE)) {
			returnRate = rateUSDtoEUR;
		} else if (code.equals(GBP_CODE)) {
			returnRate = rateUSDtoGBP;
		} else if (code.equals(KES_CODE)) {
			returnRate = rateUSDtoKES;
		} else if (code.equals(TSH_CODE)) {
			returnRate = rateUSDtoTSH;
		} else if (code.equals(UGX_CODE)) {
			returnRate = rateUSDtoUGX;
		} else if (code.equals(USD_CODE)) {
			returnRate = 1.00;
		} else {
			returnRate = 1.00;
		}
		
		return returnRate;
	}
	
	public double getSavedCurrencyRates(Context context, String currencyCode){
		conversionRates = context.getSharedPreferences(CURRENCY_RATES, 0);
		String rate = Double.toString(getDefaultRatings(currencyCode));
		return Double.parseDouble(conversionRates.getString(currencyCode, rate));
	}
	
}
