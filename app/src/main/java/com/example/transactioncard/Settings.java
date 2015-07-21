package com.example.transactioncard;

import java.util.ArrayList;
import java.util.List;

import com.example.transactioncard.database.AccountTable;
import com.example.transactioncard.object.Accounts;
import com.example.transactioncard.object.Currencies;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class Settings extends PreferenceActivity {
	
	
	public static Accounts getDefaultAccount(Context context) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		String defaultAccountId = sharedPreferences.getString(
				KEY_DEFAULT_ACCOUNT, DEFAULT_ACCOUNT_ID);
		AccountTable accountTable = new AccountTable(context);
		accountTable.open();
		Accounts defaultAccount = accountTable.getAccountById(Long
				.parseLong(defaultAccountId));
		accountTable.close();
		return defaultAccount;
	}

	@Override
	public void onBuildHeaders(List<Header> target) {
		loadHeadersFromResource(R.xml.settings_1, target);
	}

	static private final String PREFERENCES = "preferences";
	static public final String KEY_PASSWORD = "set_password";
	static public final String KEY_ENABLE_PASSWORD = "enable_password";
	static public final String KEY_DATE_FORMAT = "date_format";
	static public final String KEY_DEFAULT_ACCOUNT = "default_account";
	static public final String KEY_CURRENCY = "currency";
	static public final String DEFAULT_ACCOUNT_ID = "1";
	static public final String DEFAULT_DATE_FORMAT = "MMMM dd, yyyy";
	
	public static String getDefaultCurrency(Context context){
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sharedPreferences.getString(KEY_CURRENCY,
				Currencies.CURRENCY_CODE_TANZANIA);
}

	public static class SettingsFragment extends PreferenceFragment implements
			OnSharedPreferenceChangeListener {

		@Override
		public void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			String key = getArguments().getString("header_key");
			if (key.equals(PREFERENCES)) {
				addPreferencesFromResource(R.xml.preferences);
				EditTextPreference setPassword = (EditTextPreference) findPreference(KEY_PASSWORD);
				SharedPreferences sharedPreferences = PreferenceManager
						.getDefaultSharedPreferences(getActivity());
				boolean isChecked = (boolean) sharedPreferences.getBoolean(
						KEY_ENABLE_PASSWORD, false);
				setPassword.setEnabled(isChecked);

				ArrayList<Accounts> accountList = getAccountList();
				CharSequence[] accountNameList = new String[accountList.size()];
				CharSequence[] accountIdList = new String[accountList.size()];
				for (int i = 0; i < accountList.size(); i++) {
					accountNameList[i] = accountList.get(i).getAccountName();
					accountIdList[i] = Long
							.toString(accountList.get(i).getId());
				}
				ListPreference accountPrefs = (ListPreference) findPreference(KEY_DEFAULT_ACCOUNT);
				accountPrefs.setEntries(accountNameList);
				accountPrefs.setEntryValues(accountIdList);
				AccountTable accountTable = new AccountTable(getActivity());
				accountTable.open();
				String defaultAccountId = sharedPreferences.getString(KEY_DEFAULT_ACCOUNT,
						DEFAULT_ACCOUNT_ID);
				Accounts defaultAccount = accountTable.getAccountById(Long.parseLong(defaultAccountId));
				accountTable.close();
				accountPrefs.setSummary(defaultAccount.getAccountName());

				ListPreference currencyList = (ListPreference) findPreference(KEY_CURRENCY);
				currencyList.setEntries(Currencies.getDetailedCurrencyList());
				currencyList.setEntryValues(Currencies.getAllCurrencyCode());
				currencyList.setSummary(sharedPreferences.getString(
						KEY_CURRENCY, "TZS"));
				
			} else if (key.equals(" ")) {
				addPreferencesFromResource(R.layout.about);
			} else if (key.equals(" ")) {
				addPreferencesFromResource(R.layout.about);
			}
		}

		private ArrayList<Accounts> getAccountList() {
			ArrayList<Accounts> accountList = new ArrayList<Accounts>();
			AccountTable accountTable = new AccountTable(getActivity());
			accountTable.open();
			accountList = accountTable.getAccountAll();
			accountTable.close();
			return accountList;
		}

		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
				String key) {
			// TODO Auto-generated method stub
			if (key.equals(KEY_ENABLE_PASSWORD)) {
				Preference enablePassword = findPreference(key);
				boolean isChecked = (boolean) sharedPreferences.getBoolean(key,
						false);
				if (isChecked) {
					Preference setPassword = findPreference(KEY_PASSWORD);
					setPassword.setEnabled(true);
					sharedPreferences.edit().putString(KEY_PASSWORD, "");
					sharedPreferences.edit().commit();
					
					enablePassword.setSummary(R.string.password_enabled);
				} else {
					Preference setPassword = findPreference(KEY_PASSWORD);
					setPassword.setEnabled(false);
					enablePassword.setSummary(R.string.password_disabled);
					sharedPreferences.edit().putString(KEY_PASSWORD, "");
					sharedPreferences.edit().commit();
				}
			} else if (key.equals(KEY_DEFAULT_ACCOUNT)){
				Preference defaultAccountPreference = findPreference(key);
				String defaultAccountId = sharedPreferences.getString(key,
						DEFAULT_ACCOUNT_ID);
				AccountTable accountTable = new AccountTable(getActivity());
				accountTable.open();
				Accounts defaultAccount = accountTable.getAccountById(Long.parseLong(defaultAccountId));
				accountTable.close();
				defaultAccountPreference.setSummary(defaultAccount.getAccountName());
			}else if (key.equals(KEY_CURRENCY)){
				Preference curreny = findPreference(key);
				curreny.setSummary(sharedPreferences.getString(key, Currencies.CURRENCY_CODE_US));
			}
		}
		@Override
		public void onResume() {
		    super.onResume();
		    getPreferenceScreen().getSharedPreferences()
		            .registerOnSharedPreferenceChangeListener(this);
		}

		@Override
		public void onPause() {
		    super.onPause();
		    getPreferenceScreen().getSharedPreferences()
		            .unregisterOnSharedPreferenceChangeListener(this);
		}
	}
	
	
}
