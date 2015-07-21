package com.example.transactioncard;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import transactioncard.currency.XmlUtilities;
import transactioncard.currency.XmlUtilitiesException;
import transactioncard.currency.XmlYahooConsts;
import transactioncard.currency.YahooCurrencyConvertor;

import com.example.transactioncard.object.Currencies;
import com.example.transactioncard.object.CurrencyConvertor;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CurrencyActivity extends Activity implements
		OnItemSelectedListener {

	private String[] CURRENCY_LIST = { Currencies.CURRENCY_CODE_TANZANIA,
			Currencies.CURRENCY_CODE_UGANDA, Currencies.CURRENCY_CODE_KENYA,
			Currencies.CURRENCY_CODE_POUND, Currencies.CURRENCY_CODE_EURO };
	/*
	 * View component variable
	 */
	private ListView lvCurrencyExchange;
	private ListViewAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_currency_exhange);
		LoadRate load = new LoadRate();
		load.execute(new String[] { null });
		initializesComponentVariables();
		adapter = new ListViewAdapter(getApplicationContext());
		lvCurrencyExchange.setAdapter(adapter);
	}

	private class LoadRate extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			setRefreshActionButtonState(true);
			/*
			 * Create url for the query
			 */

		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			/*
			 * Get the currency code list
			 */

			String result = null;
			YahooCurrencyConvertor yahooCurrencyConvertor = new YahooCurrencyConvertor();
			publishProgress(new String[] {});
			try {

				result = yahooCurrencyConvertor.getCurrencyRates(
						Currencies.CURRENCY_CODE_US, CURRENCY_LIST);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}
			return result;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			/*
			 * Cancel the progress bar
			 */
			setRefreshActionButtonState(false);
			/*
			 * Extract xml values
			 */
			if (result != null){
				readXmlStringAndUpdate(result);
				/*
				 * Update the view
				 */
				adapter.notifyDataSetChanged();
				
				Toast.makeText(getApplicationContext(), "Updated!",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(), "Couldn't update the rates!",
						Toast.LENGTH_SHORT).show();
			}

		}

	}
	
	public void readXmlStringAndUpdate(String data) {

        try {
            /*
             * Check if data is ready
             */
            if (data != null) {
                Document document = XmlUtilities.GetDocumentFromString(data);
                /*
                 * Get root node (query)
                 */
                Node nodeRootQuery = XmlUtilities.GetRootNode(document, XmlYahooConsts.STRXML_YAHOO_RES_Rootnode);
                /*
                 * Get child node (results)
                 */
                Node xmlNodeResults = XmlUtilities.GetChildNode(nodeRootQuery, XmlYahooConsts.STRXML_YAHOO_RES_Results);
                /*
                 * Get child node (rate)
                 */
                ArrayList<Node> nodelist = XmlUtilities.GetChildNodeList(xmlNodeResults, XmlYahooConsts.STRXML_YAHOO_RES_rate);
                for (int i = 0; i < nodelist.size(); i++) {
                    extractDataFromNodeRate(nodelist.get(i));
                }

            }

        } catch (XmlUtilitiesException e) {
            e.printStackTrace();
        }
    }
	
	private void extractDataFromNodeRate(Node xmlNoderate) throws NullPointerException{
        try {
            if (xmlNoderate != null) {
				/*
				 * Get rate attribute (id)
				 */
				String rateId = XmlUtilities.GetAttributeValue(xmlNoderate,
						XmlYahooConsts.STRXML_YAHOO_ATTR_ID);
				/*
				 * Get code from rate id
				 */
				String currencyCode = rateId.substring(rateId.length() - 3);
				/*
				 * Get xmlNoderate values (Name, Rate, Date, Time, Ask, Bid)
				 */
				String name = XmlUtilities.GetChildValue(xmlNoderate,
						XmlYahooConsts.STRXML_YAHOO_RES_Name);
				String rate = XmlUtilities.GetChildValue(xmlNoderate,
						XmlYahooConsts.STRXML_YAHOO_RES_Rate);
				String date = XmlUtilities.GetChildValue(xmlNoderate,
						XmlYahooConsts.STRXML_YAHOO_RES_Date);
				String time = XmlUtilities.GetChildValue(xmlNoderate,
						XmlYahooConsts.STRXML_YAHOO_RES_Time);
				String ask = XmlUtilities.GetChildValue(xmlNoderate,
						XmlYahooConsts.STRXML_YAHOO_RES_Ask);
				String bid = XmlUtilities.GetChildValue(xmlNoderate,
						XmlYahooConsts.STRXML_YAHOO_RES_Bid);
				/*
				 * Save the results
				 */
				CurrencyConvertor currencyConvertor = new CurrencyConvertor();
				/*
				 * Save result into system preference
				 */
				currencyConvertor.setRateUSDtoCODE(getApplicationContext(),
						currencyCode, Double.parseDouble(rate));
				
				System.out.println("Rate id: " + rateId);
				System.out.println("Name: " + name);
				System.out.println("Rate: " + rate);
				System.out.println("Date: " + date);
				System.out.println("Time: " + time);
				System.out.println("Ask: " + ask);
				System.out.println("Bid: " + bid);
				
			} else {
				throw new NullPointerException();
			}
        } catch (XmlUtilitiesException ex) {
            Logger.getLogger(CurrencyActivity.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

	public String[] getCurrenciesWithDetails() {
		return Currencies.getDetailedCurrencyList();
	}

	private void initializesComponentVariables() {
		lvCurrencyExchange = (ListView) findViewById(R.id.lvCurrenciesExchange);
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub
	}

	private Menu optionsMenu;

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		this.optionsMenu = menu;
		getMenuInflater().inflate(R.menu.menu_currency_exchange, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.menuitemCurrencyxRefresh:
			LoadRate load = new LoadRate();
			load.execute(new String[] { null });
			;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void setRefreshActionButtonState(final boolean refreshing) {
		if (optionsMenu != null) {
			final MenuItem refreshItem = optionsMenu
					.findItem(R.id.menuitemCurrencyxRefresh);
			if (refreshItem != null) {
				if (refreshing) {
					refreshItem.setActionView(R.layout.progress_bar_circular);
				} else {
					refreshItem.setActionView(null);
				}
			}
		}
	}

	private class ListViewAdapter extends BaseAdapter {

		private LayoutInflater inflater;
		private Context context;

		public ListViewAdapter(Context context) {
			this.context = context;
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return Currencies.getAllCurrencyCode().length;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			ViewHolder viewHolder = null;
			if (convertView == null) {
				/*
				 * Get the convertView
				 */
				convertView = inflater.inflate(
						R.layout.listview_currency_exchange_rates, null);
				/*
				 * Create instance for view holder and assing view resource id
				 */
				viewHolder = new ViewHolder();
				viewHolder.ivFlag = (ImageView) convertView
						.findViewById(R.id.ivCurrencyFlag);
				viewHolder.tvCurrencyCodes = (TextView) convertView
						.findViewById(R.id.tvCurrencyCodes);
				viewHolder.tvExchangeDetails = (TextView) convertView
						.findViewById(R.id.tvCurrencyExchangeDetails);
				viewHolder.tvUpdateDetails = (TextView) convertView
						.findViewById(R.id.tvCurrencyUpdate);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			/*
			 * Get currency codes
			 */
			String currencyCode = Currencies.getAllCurrencyCode()[position];
			CurrencyConvertor currencyConvertor = new CurrencyConvertor();
			double converted = currencyConvertor.getSavedCurrencyRates(
					getApplicationContext(), currencyCode);
			String conversionExpression = "1 (USD) = " + converted + "("
					+ currencyCode + ")";
			viewHolder.tvExchangeDetails.setText(conversionExpression);
			viewHolder.tvCurrencyCodes
					.setText(getCurrenciesWithDetails()[position]);
			viewHolder.ivFlag.setImageResource(Currencies
					.getCurrecnyFlag(currencyCode));
			long updatedTime = currencyConvertor.getDateUpdated(
					getApplicationContext(), currencyCode);
			;
			if (!(updatedTime == 0)) {
				viewHolder.tvUpdateDetails.setTextColor(Color.BLUE);
				viewHolder.tvUpdateDetails.setText(getFormatedDate(context,
						updatedTime));
			} else {
				viewHolder.tvUpdateDetails.setTextColor(Color.BLUE);
				viewHolder.tvUpdateDetails.setText("Never");
			}

			return convertView;
		}

	}

	private class ViewHolder {
		ImageView ivFlag;
		TextView tvCurrencyCodes, tvExchangeDetails, tvUpdateDetails;

	}

	static public final String DEFAULT_DATE_FORMAT = "MMM, dd";
	static public final String KEY_DATE_FORMAT = "date_format";

	public static String getFormatedDate(Context context, long timeInmillis) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		String savedFormat = sharedPreferences.getString(KEY_DATE_FORMAT,
				DEFAULT_DATE_FORMAT);
		SimpleDateFormat dateFormat = new SimpleDateFormat(savedFormat
				+ "' at ' HH:mm", Locale.US);
		return dateFormat.format(new Date(timeInmillis));
	}
}
