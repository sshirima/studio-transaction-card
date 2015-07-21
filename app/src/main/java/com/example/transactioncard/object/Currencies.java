package com.example.transactioncard.object;

import java.util.Currency;
import java.util.Locale;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.example.transactioncard.R;
public class Currencies {
	public static final String CURRENCY_CODE_TANZANIA = "TZS";
	public static final String CURRENCY_CODE_UGANDA = "UGX";
	public static final String CURRENCY_CODE_KENYA = "KES";
	public static final String CURRENCY_CODE_EURO = "EUR";
	public static final String CURRENCY_CODE_US = Currency.getInstance(
			Locale.US).getCurrencyCode();
	public static final String CURRENCY_CODE_POUND = Currency.getInstance(
			Locale.UK).getCurrencyCode();
	
	public static int RESOURCE_ID_FLAG_USA = R.drawable.ic_usa;
	public static int RESOURCE_ID_FLAG_UK = R.drawable.ic_uk;
	public static int RESOURCE_ID_FLAG_TANZANIA = R.drawable.ic_tanzania;
	public static int RESOURCE_ID_FLAG_KENYA = R.drawable.ic_kenya;
	public static int RESOURCE_ID_FLAG_UGANDA = R.drawable.ic_uganda;
	public static int RESOURCE_ID_FLAG_EURO = R.drawable.ic_euro;
	
	public static double DEFAULT_USD_TSH = 1602.55;
	public static double DEFAULT_USD_KEN = 88.00;
	public static double DEFAULT_USD_UGX = 2020.44;
	public static double DEFAULT_USD_GBP = 0.72;
	public static double DEFAULT_USD_EUR = 0.65;
	public static double DEFAULT_USD_USD = 1.00;
	
	
	public static String[] getDetailedCurrencyList() {
		String pound = "United Kingdon Pound";
		String kenya = "Kenya Shilling";;
		String tanzania = "Tanzania Shilling";
		String uganda = "Uganda Shilling";
		String us = "United States Dollar";
		String euro = "Euro";
		return new String[] {euro, pound, kenya, tanzania, uganda, us };
	}

	public static String[] getAllCurrencyCode() {
		return new String[] { CURRENCY_CODE_EURO,CURRENCY_CODE_POUND, CURRENCY_CODE_KENYA,
				CURRENCY_CODE_TANZANIA, CURRENCY_CODE_UGANDA, CURRENCY_CODE_US
				 };
	}

	public static String getDefaultCurrency(Context context){
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sharedPreferences.getString(KEY_CURRENCY,
				Currencies.CURRENCY_CODE_TANZANIA);
}
	
	public static double getDefaultRates(String currencyCode){
		double rate = 0;
		if (currencyCode.equals(CURRENCY_CODE_EURO)){
			rate = DEFAULT_USD_EUR;
		} else if (currencyCode.equals(CURRENCY_CODE_KENYA)){
			rate = DEFAULT_USD_KEN;
		} else if (currencyCode.equals(CURRENCY_CODE_POUND)){
			rate = DEFAULT_USD_GBP;
		} else if (currencyCode.equals(CURRENCY_CODE_TANZANIA)){
			rate = DEFAULT_USD_TSH;
		} else if (currencyCode.equals(CURRENCY_CODE_UGANDA)){
			rate = DEFAULT_USD_UGX;
		} else if (currencyCode.equals(CURRENCY_CODE_US)){
			rate = DEFAULT_USD_USD;
		}
		
		return rate;
	}
	
	public static int getCurrecnyFlag(String currencyCode){
		if(currencyCode.equals(CURRENCY_CODE_EURO)){
			return RESOURCE_ID_FLAG_EURO;
		}else if(currencyCode.equals(CURRENCY_CODE_KENYA)){
			return RESOURCE_ID_FLAG_KENYA;
		}else if(currencyCode.equals(CURRENCY_CODE_POUND)){
			return RESOURCE_ID_FLAG_UK;
		}else if(currencyCode.equals(CURRENCY_CODE_TANZANIA)){
			return RESOURCE_ID_FLAG_TANZANIA;
		}else if(currencyCode.equals(CURRENCY_CODE_UGANDA)){
			return RESOURCE_ID_FLAG_UGANDA;
		}else if(currencyCode.equals(CURRENCY_CODE_US)){
			return RESOURCE_ID_FLAG_USA;
		}else return 0;
	}
	static public final String KEY_CURRENCY = "currency";
	
}
