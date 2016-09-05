package com.example.transactioncard.object;

import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.transactioncard.database.CurrencyTable;

public class Currencies {
	public static final String CURRENCY_CODE_TANZANIA = "TZS";
	public static final String CURRENCY_CODE_UGANDA = "UGX";
	public static final String CURRENCY_CODE_KENYA = "KES";
	public static final String CURRENCY_CODE_EURO = "EUR";
	public static final String CURRENCY_CODE_US = Currency.getInstance(
			Locale.US).getCurrencyCode();
	public static final String CURRENCY_CODE_POUND = Currency.getInstance(
			Locale.UK).getCurrencyCode();
	static public final String KEY_CURRENCY = "currency";
	private List<CurrencyCashFlow> currencyList;
	private Map<String, String> codeToFlagID;

	public Currencies (Context context){
		CurrencyTable currencyTable = new CurrencyTable(context);
		currencyTable.open();
		currencyList = currencyTable.getCurrenciesAll();
		codeToFlagID = new TreeMap<String, String>();
		for (int i = 0; i < currencyList.size(); i++) {
			CurrencyCashFlow currency = currencyList.get(i);
			codeToFlagID.put(currency.getCurrencyCode(), ""+currency.getFlagId());
		}
		currencyTable.close();
	}

	public String[] getDetailedCurrencyList() {
		String[] returnList = new String[currencyList.size()];
		for (int i = 0; i < currencyList.size(); i++) {
			returnList[i] = currencyList.get(i).getCurrencyName();
		}
		return returnList;
	}

	public String[] getAllCurrencyCode() {

		String[] returnList = new String[currencyList.size()];
		for (int i = 0; i < currencyList.size(); i++) {
			returnList[i] = currencyList.get(i).getCurrencyCode();
		}
		return returnList;
	}

	public String getDefaultCurrency(Context context){
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sharedPreferences.getString(KEY_CURRENCY,
				Currencies.CURRENCY_CODE_TANZANIA);
	}
	
	public static double getDefaultRates(String currencyCode){
		return 1.0;
	}
	
	public int getCurrencyFlag(String currencyCode){
		int flagId = Integer.parseInt(codeToFlagID.get(currencyCode));
		return flagId;
	}

	
}
