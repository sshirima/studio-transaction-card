package com.example.transactioncard.dialogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.example.transactioncard.R;
import com.example.transactioncard.Settings;
import com.example.transactioncard.database.AccountTable;
import com.example.transactioncard.database.ConstsDatabase;
import com.example.transactioncard.object.Accounts;
import com.example.transactioncard.object.CacheDescription;
import com.example.transactioncard.object.Currencies;
import com.example.transactioncard.object.Description;
import com.example.transactioncard.object.Transaction;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class DialogTransactionNew extends DialogFragment implements OnClickListener,
		OnItemSelectedListener {
	private static final String CLASSNAME = DialogTransactionNew.class.getName();
	private static final int TIME_DIALOG_ID = 1;
	private static final int DATE_DIALOG_ID = 0;

	public interface NoticeDialogListerner {
		public void onDialogPositiveClick(DialogFragment dialog,
				Transaction transaction);

		public void onDialogNegativeClick(DialogFragment dialog);
	}

	public static DialogTransactionNew newInstance() {
		return new DialogTransactionNew();
	}

	// instance of the interface to deliver the action events
	NoticeDialogListerner dialogListerner;

	private long transactionAmount = 0;
	private Description transactionDescription;
	private String transactionAccount;
	private long transactionAccountId;
	private Calendar calendar;
	private long transactionTimeInMillis;
	private String transactionCategory;

	private Transaction newTransaction;
	private CacheDescription cacheDescription;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		String methodName = "onCreateView";
		String operation = "Creating view for the new account dialog";
		ConstsDatabase.logINFO(CLASSNAME, methodName, operation);
		
		/*
		 * Initialize dialog view for the user interface
		 */
		View dialog = inflater.inflate(R.layout.dialog_new_transaction_layout,
				container, false);
		initializesComponentVariables(dialog);
		
		/*
		 * Set up edittext auto suggestion option
		 */
		cacheDescription = new CacheDescription(getActivity());
		descriptionList = cacheDescription.getDescriptionNames();
		ArrayAdapter<String> adapterSpinnerCurrency = setSpinnerCurrency();
		setAutoSuggestEditText();
		spNewTransactionCurrenncy.setAdapter(adapterSpinnerCurrency);
		setDefaultCurrency(spNewTransactionCurrenncy);
		/*
		 * Get default transaction parameters
		 */
		defaultTransactionValues();
		/*
		 * Create new transaction with the default recommended values
		 */
		newTransaction = createNewTransaction();
		/*
		 * Set transaction values to the display screen
		 */
		setValuesToComponents();
		/*
		 * Set click listener(s)
		 */
		ivNewTransactionEditCategory.setOnClickListener(this);
		ivNewTransactionEditAccount.setOnClickListener(this);
		ivNewTransactionEditDate.setOnClickListener(this);
		ivNewTransactionEditTime.setOnClickListener(this);
		bNewTransactionCancel.setOnClickListener(this);
		bNewTransactionSave.setOnClickListener(this);
		spNewTransactionCurrenncy.setOnItemSelectedListener(this);
		return dialog;
	}
	
	private Transaction createNewTransaction() {
		String defaultCurrency = Settings.getDefaultCurrency(getActivity());
		Transaction transaction = new Transaction(getActivity(),
				transactionAmount, transactionTimeInMillis, defaultCurrency);
		
		transaction.setTransactionAmount(transactionAmount, defaultCurrency);
		transaction.setCategory(transactionCategory);
		transaction.setAccountName(transactionAccount);
		transaction.setAccountId(transactionAccountId);
		transaction.setDescription(transactionDescription);
		return transaction;
	}

	private void setDefaultCurrency(Spinner spinner) {
		String[] currencyList = getResources().getStringArray(
				R.array.currency_list);
		ArrayList<String> arrayList = new ArrayList<String>(
				Arrays.asList(currencyList));
		String defaultCurrency = Transaction.getDefaultCurrency(getActivity());
		spinner.setSelection(arrayList.indexOf(defaultCurrency));
	}

	private void defaultTransactionValues() {
		transactionDescription = cacheDescription.getDescriptionList().get(0);
		transactionAccount = getAccountList().get(0).getAccountName();
		transactionAccountId = getAccountList().get(0).getId();
		calendar = Calendar.getInstance();
		transactionTimeInMillis = calendar.getTimeInMillis();
		transactionCategory = getCategories()[0].toString();
	}

	private String[] descriptionList;

	private void setAutoSuggestEditText() {
		ArrayAdapter<String> autoSuggestAdapter = new ArrayAdapter<String>(
				getActivity(), android.R.layout.simple_list_item_1,
				descriptionList);
		actvNewTransactionDesc.setAdapter(autoSuggestAdapter);
	}

	private ArrayAdapter<String> setSpinnerCurrency() {
		String[] currencyList = getResources().getStringArray(
				R.array.currency_list);
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(
				getActivity(), android.R.layout.simple_spinner_item,
				currencyList);
		spinnerAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		return spinnerAdapter;
	}

	private void setSingleChoiceDialogAccount(String title, String dialogTag) {
		ArrayList<Accounts> accountList = getAccountList();
		DialogSingleChoice singleChoiceDialog = new DialogSingleChoice();
		singleChoiceDialog.setDialogTitle(title);
		singleChoiceDialog.setAccountList(DialogTransactionNew.this, accountList);
		singleChoiceDialog.show(getFragmentManager(), dialogTag);
	}

	private ArrayList<Accounts> getAccountList() {
		AccountTable accountTable = new AccountTable(getActivity());
		accountTable.open();
		ArrayList<Accounts> accountList = accountTable.getAccountAll();
		accountTable.close();
		return accountList;
	}

	private void setSingleChoiceDialog(String title, String dialogTag) {
		DialogSingleChoice singleChoiceDialog = new DialogSingleChoice();
		singleChoiceDialog.setDialogTitle(title);
		CharSequence[] choiceList = getCategories();
		singleChoiceDialog.setChoiceList(DialogTransactionNew.this, choiceList);
		singleChoiceDialog.show(getFragmentManager(), dialogTag);
	}

	private EditText etNewTransactionAmount;
	private Spinner spNewTransactionCurrenncy;
	private TextView tvNewTransactionValueCategory;
	private TextView tvNewTransactionLabelAmount;
	private TextView tvNewTransactionLabelDescription;
	private TextView tvNewTransactionValueTime;
	private TextView tvNewTransactionValueDate;
	private TextView tvNewTransactionValueAccount;
	private ImageView ivNewTransactionEditCategory;
	private ImageView ivNewTransactionEditAccount;
	private ImageView ivNewTransactionEditTime;
	private ImageView ivNewTransactionEditDate;
	private Button bNewTransactionSave;
	private Button bNewTransactionCancel;
	private AutoCompleteTextView actvNewTransactionDesc;

	private void initializesComponentVariables(View dialog) {
		etNewTransactionAmount = (EditText) dialog
				.findViewById(R.id.etNewTransactionAmount);
		spNewTransactionCurrenncy = (Spinner) dialog
				.findViewById(R.id.spNewTransactionCurrency);
		tvNewTransactionValueCategory = (TextView) dialog
				.findViewById(R.id.tvNewTransactionValueCategory);
		tvNewTransactionValueTime = (TextView) dialog
				.findViewById(R.id.tvNewTransactionValueTime);
		tvNewTransactionValueDate = (TextView) dialog
				.findViewById(R.id.tvNewTransactionValueDate);
		tvNewTransactionValueAccount = (TextView) dialog
				.findViewById(R.id.tvNewTransactionValueAccount);
		ivNewTransactionEditCategory = (ImageView) dialog
				.findViewById(R.id.ivNewTransactionEditCategory);
		ivNewTransactionEditAccount = (ImageView) dialog
				.findViewById(R.id.ivNewTransactionEditAccount);
		ivNewTransactionEditTime = (ImageView) dialog
				.findViewById(R.id.ivNewTransactionEditTime);
		ivNewTransactionEditDate = (ImageView) dialog
				.findViewById(R.id.ivNewTransactionEditDate);
		tvNewTransactionLabelAmount = (TextView) dialog
				.findViewById(R.id.tvNewTransactionLabelAmount);
		tvNewTransactionLabelDescription = (TextView) dialog
				.findViewById(R.id.tvNewTransactionLabelDescription);
		bNewTransactionSave = (Button) dialog
				.findViewById(R.id.bNewTransactionSave);
		bNewTransactionCancel = (Button) dialog
				.findViewById(R.id.bNewTransactionCancel);
		actvNewTransactionDesc = (AutoCompleteTextView) dialog
				.findViewById(R.id.actvNewTransactionDesc);

	}

	private void setValuesToComponents() {
		String accountName = newTransaction.getAccountName();
		String category = newTransaction.getCategory();
		String date = newTransaction.getDate();
		String time = newTransaction.getTime();
		tvNewTransactionValueAccount.setText(accountName);
		tvNewTransactionValueCategory.setText(category);
		tvNewTransactionValueDate.setText(date);
		tvNewTransactionValueTime.setText(time);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Dialog dialog = new Dialog(getActivity());
		dialog.setContentView(R.layout.dialog_new_transaction_layout);
		dialog.show();
		dialog.setTitle(R.string.dialog_title_new_record);
		return dialog;
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		try {
			dialogListerner = (NoticeDialogListerner) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement NoticeDialogListerner");
		}
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		DialogTimeChange dialogTimeChange = new DialogTimeChange();
		switch (view.getId()) {
		case R.id.ivNewTransactionEditAccount:
			String titleAccount = getActivity().getResources().getString(
					R.string.dialog_title_change_account);
			String dialogTagAccount = "ChangeAccount";
			setSingleChoiceDialogAccount(titleAccount, dialogTagAccount);
			break;

		case R.id.ivNewTransactionEditCategory:
			String titleCategory = getActivity().getResources().getString(
					R.string.dialog_title_change_category);
			String dialogTagCategory = "ChangeCategory";
			setSingleChoiceDialog(titleCategory, dialogTagCategory);
			break;
		case R.id.ivNewTransactionEditDate:
			dialogTimeChange.setInitialDate(calendar.get(Calendar.YEAR),
					calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH), DATE_DIALOG_ID);
			dialogTimeChange.setCallingDialog(DialogTransactionNew.this);
			dialogTimeChange.show(getFragmentManager(), "DateChangeDialog");
			break;

		case R.id.ivNewTransactionEditTime:
			dialogTimeChange.setInitialTime(calendar.get(Calendar.HOUR_OF_DAY),
					calendar.get(Calendar.MINUTE), true, TIME_DIALOG_ID);
			dialogTimeChange.setCallingDialog(DialogTransactionNew.this);
			dialogTimeChange.show(getFragmentManager(), "TimeChangeDialog");
			break;
		case R.id.bNewTransactionSave:
			boolean amountNotEmpty = !(etNewTransactionAmount.getText()
					.toString().equals(""));
			boolean descriptionNotEmpty = !(actvNewTransactionDesc.getText()
					.toString().equals(""));
			if (amountNotEmpty && descriptionNotEmpty) {
				Transaction transaction = createNewTransaction();
				double amount = Double.parseDouble(etNewTransactionAmount
						.getText().toString());
				String defaultCurrency = (String) spNewTransactionCurrenncy
						.getSelectedItem();
				transaction.setTransactionAmount(amount, defaultCurrency);
				String description = actvNewTransactionDesc.getText()
						.toString();
				transaction.setDescription(cacheDescription
						.getDescription(description));
				dialogListerner.onDialogPositiveClick(DialogTransactionNew.this,
						transaction);
			} else {
				if (!amountNotEmpty) {
					tvNewTransactionLabelAmount.setText(R.string.empty_field);
					tvNewTransactionLabelAmount.setTextColor(Color.RED);
				} else {
					tvNewTransactionLabelAmount.setText(R.string.amount);
					tvNewTransactionLabelAmount.setTextColor(Color.BLACK);
				}
				if (!descriptionNotEmpty) {
					tvNewTransactionLabelDescription
							.setText(R.string.empty_field);
					tvNewTransactionLabelDescription.setTextColor(Color.RED);
				} else {
					tvNewTransactionLabelDescription
							.setText(R.string.description);
					tvNewTransactionLabelDescription.setTextColor(Color.BLACK);
				}

			}
			break;
		case R.id.bNewTransactionCancel:
			dialogListerner.onDialogNegativeClick(DialogTransactionNew.this);
			dismiss();
			break;

		}
	}

	public static CharSequence[] getCategories() {
		return new CharSequence[] { "Expenses", "Income" };
	}

	@Override
	public void onItemSelected(AdapterView<?> adapter, View view, int position,
			long arg3) {
		// TODO Auto-generated method stub
		switch (adapter.getId()) {
		case R.id.spNewTransactionCurrency:
			break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}
}
