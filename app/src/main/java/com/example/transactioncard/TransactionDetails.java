 package com.example.transactioncard;

import java.util.ArrayList;
import java.util.Calendar;
import com.example.transactioncard.database.AccountTable;
import com.example.transactioncard.database.TransactionTable;
import com.example.transactioncard.dialogs.DialogConfirmation;
import com.example.transactioncard.dialogs.DialogConfirmation.DialogConfirmationListener;
import com.example.transactioncard.dialogs.DialogTransactionNew;
import com.example.transactioncard.dialogs.DialogSingleChoice;
import com.example.transactioncard.dialogs.DialogTimeChange;
import com.example.transactioncard.dialogs.DialogSingleChoice.ChoiceSelectedListener;
import com.example.transactioncard.dialogs.DialogTimeChange.DateSetListener;
import com.example.transactioncard.object.Accounts;
import com.example.transactioncard.object.CacheDescription;
import com.example.transactioncard.object.Currencies;
import com.example.transactioncard.object.Description;
import com.example.transactioncard.object.Transaction;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class TransactionDetails extends Activity implements DateSetListener,
		OnClickListener, ChoiceSelectedListener,
		DialogConfirmationListener {

	CacheDescription cacheDescription;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transaction_details);
		cacheDescription = new CacheDescription(getApplicationContext());
		initializesComponentsVar();
		getTransaction();
		updateUserInterface(originalTransaction);
		descriptionList = cacheDescription.getDescriptionNames();
		setAutoSuggestEditText();
		ivTransactionDetailEditAccount.setOnClickListener(this);
		ivTransactionDetailEditAmount.setOnClickListener(this);
		ivTransactionDetailEditCategory.setOnClickListener(this);
		ivTransactionDetailEditDate.setOnClickListener(this);
		ivTransactionDetailEditDescription.setOnClickListener(this);
		ivTransactionDetailEditTime.setOnClickListener(this);
	}

	private String[] descriptionList;

	private void setAutoSuggestEditText() {
		ArrayAdapter<String> autoSuggestAdapter = new ArrayAdapter<String>(
				getApplicationContext(), R.layout.list_decription_dropdown,
				descriptionList);
		actvTransactionDetailsDetails.setAdapter(autoSuggestAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_transaction_details, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.changesSave:
			if (isEdited) {
				DialogConfirmation confirmation = new DialogConfirmation();
				String message = getResources().getString(R.string.dialog_confirm_changes_message);
				String negativeButton = getResources().getString(R.string.dialog_confirm_changes_button_neg);
				String positiveButton = getResources().getString(R.string.dialog_confirm_changes_button_pos);
				confirmation.setMassage(message);
				confirmation.setMassageNegativeButton(negativeButton);
				confirmation.setMassagePositiveButton(positiveButton);
				confirmation.show(getFragmentManager(), "Confirmation");
			} else {
				onDialogCancel();
			}
			break;
		case R.id.changesCancel:
			onDialogCancel();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	

	private void updateUserInterface(Transaction transaction) {
		// TODO Auto-generated method stub
		String amount = Double.toString(transaction.getAmount(getApplicationContext()));
		String accountName = transaction.getAccountName();
		String category = transaction.getCategory();
		String details = transaction.getDescriptionName();
		String date = transaction.getDate();
		String time = transaction.getTime();
		etTransactionDetailsAccount.setText(accountName);
		etTransactionDetailsAmount.setText(amount);
		etTransactionDetailsCategory.setText(category);
		actvTransactionDetailsDetails.setText(details);
		etTransactionDetailsTime.setText(date + " at " + time);

	}

	static public final String KEY_DEFAULT_ACCOUNT = "default_account";
	static public final String DEFAULT_ACCOUNT_ID = "1";

	private long transactionId;
	private Transaction originalTransaction;
	private Transaction editedTransaction;

	private void getTransaction() {
		Bundle bundle = getIntent().getExtras();
		transactionId = bundle.getLong("id");
		TransactionTable transactionTable = new TransactionTable(
				getApplicationContext());
		transactionTable.open();
		originalTransaction = transactionTable
				.getTransactionById(transactionId);
		editedTransaction = transactionTable
				.getTransactionById(transactionId);
		transactionTable.close();
	}

	private EditText etTransactionDetailsAmount;
	private AutoCompleteTextView actvTransactionDetailsDetails;
	private EditText etTransactionDetailsAccount;
	private EditText etTransactionDetailsCategory;
	private EditText etTransactionDetailsTime;
	private ImageView ivTransactionDetailEditAmount;
	private ImageView ivTransactionDetailEditDescription;
	private ImageView ivTransactionDetailEditAccount;
	private ImageView ivTransactionDetailEditCategory;
	private ImageView ivTransactionDetailEditTime;
	private ImageView ivTransactionDetailEditDate;

	private void initializesComponentsVar() {
		// TODO Auto-generated method stub
		etTransactionDetailsAmount = (EditText) findViewById(R.id.etTransactionDetailValueAmount);
		actvTransactionDetailsDetails = (AutoCompleteTextView) findViewById(R.id.actvTransactionDetailValueDetail);
		etTransactionDetailsAccount = (EditText) findViewById(R.id.etTransactionDetailAccountValue);
		etTransactionDetailsCategory = (EditText) findViewById(R.id.etTransactionDetailValueCategory);
		etTransactionDetailsTime = (EditText) findViewById(R.id.etTransactionDetailValueTime);
		ivTransactionDetailEditAccount = (ImageView) findViewById(R.id.ivTransactionDetailEditAccount);
		ivTransactionDetailEditAmount = (ImageView) findViewById(R.id.ivTransactionDetailEditAmount);
		ivTransactionDetailEditCategory = (ImageView) findViewById(R.id.ivTransactionDetailEditCategory);
		ivTransactionDetailEditDescription = (ImageView) findViewById(R.id.ivTransactionDetailEditDescription);
		ivTransactionDetailEditTime = (ImageView) findViewById(R.id.ivTransactionDetailEditTime);
		ivTransactionDetailEditDate = (ImageView) findViewById(R.id.ivTransactionDetailEditDate);
	}

	private static final int TIME_DIALOG_ID = 1;
	private static final int DATE_DIALOG_ID = 0;

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		DialogTimeChange dialogTimeChange = new DialogTimeChange();
		switch (view.getId()) {
		case R.id.ivTransactionDetailEditAccount:
			String titleAccount = getApplicationContext().getResources()
					.getString(R.string.dialog_title_change_account);
			String dialogTagAccount = "ChangeAccount";
			setSingleChoiceDialogAccount(titleAccount, dialogTagAccount);
			break;
		case R.id.ivTransactionDetailEditAmount:
			etTransactionDetailsAmount.setEnabled(true);
			markEdited();
			break;
		case R.id.ivTransactionDetailEditCategory:
			String titleCategory = getApplicationContext().getResources()
					.getString(R.string.dialog_title_change_category);
			String dialogTagCategory = "ChangeCategory";
			setSingleChoiceDialog(titleCategory, dialogTagCategory);
			break;
		case R.id.ivTransactionDetailEditDate:
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(editedTransaction.getTimeInMillis());
			dialogTimeChange.setInitialDate(calendar.get(Calendar.YEAR),
					calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH), DATE_DIALOG_ID);
			dialogTimeChange.show(getFragmentManager(), "DateChangeDialog");
			break;
		case R.id.ivTransactionDetailEditDescription:
			markEdited();
			actvTransactionDetailsDetails.setEnabled(true);
			break;

		case R.id.ivTransactionDetailEditTime:
			Calendar calendar1 = Calendar.getInstance();
			calendar1.setTimeInMillis(editedTransaction.getTimeInMillis());
			dialogTimeChange.setInitialTime(
					calendar1.get(Calendar.HOUR_OF_DAY),
					calendar1.get(Calendar.MINUTE), true, TIME_DIALOG_ID);
			dialogTimeChange.show(getFragmentManager(), "TimeChangeDialog");
			break;
		default:
			break;
		}
	}

	private boolean isEdited;

	private void setSingleChoiceDialog(String title, String dialogTag) {
		DialogSingleChoice singleChoiceDialog = new DialogSingleChoice();
		singleChoiceDialog.setDialogTitle(title);
		CharSequence[] choiceList = DialogTransactionNew.getCategories();
		singleChoiceDialog.setChoiceList(null, choiceList);
		singleChoiceDialog.show(getFragmentManager(), dialogTag);
	}

	private void setSingleChoiceDialogAccount(String title, String dialogTag) {
		ArrayList<Accounts> accountList = getAccountList();
		DialogSingleChoice singleChoiceDialog = new DialogSingleChoice();
		singleChoiceDialog.setDialogTitle(title);
		singleChoiceDialog.setAccountList(null, accountList);
		singleChoiceDialog.show(getFragmentManager(), dialogTag);
	}

	private ArrayList<Accounts> getAccountList() {
		AccountTable accountTable = new AccountTable(getApplicationContext());
		accountTable.open();
		ArrayList<Accounts> accountList = accountTable.getAccountAll();
		accountTable.close();
		return accountList;
	}

	@Override
	public void onChoiceSelected(DialogFragment dialogFragment,
			Accounts accounts) {
		// TODO Auto-generated method stub
		String oldAccountName = originalTransaction.getAccountName();
		String editedString = accounts.getAccountName();
		if (!oldAccountName.equals(editedString)) {
			editedTransaction.setAccountId(accounts.getId());
			editedTransaction.setAccountName(accounts.getAccountName());
			updateUserInterface(editedTransaction);
			etTransactionDetailsAccount.setEnabled(true);
			etTransactionDetailsAccount.setClickable(false);
			markEdited();
		}else{
			editedTransaction.setAccountId(accounts.getId());
			editedTransaction.setAccountName(accounts.getAccountName());
			updateUserInterface(editedTransaction);
			etTransactionDetailsAccount.setEnabled(true);
			etTransactionDetailsAccount.setClickable(false);
		}
	}

	@Override
	public void onChoiceSelected(DialogFragment dialogFragment,
			String selectedItem) {
		// TODO Auto-generated method stub
		String oldCategory = originalTransaction.getCategory();
		if (!oldCategory.equals(selectedItem)) {
			editedTransaction.setCategory(selectedItem);
			updateUserInterface(editedTransaction);
			etTransactionDetailsCategory.setEnabled(true);
			etTransactionDetailsCategory.setClickable(false);
			markEdited();
		}else {
			editedTransaction.setCategory(selectedItem);
			updateUserInterface(editedTransaction);
			etTransactionDetailsCategory.setEnabled(true);
			etTransactionDetailsCategory.setClickable(false);
		}
	}

	@Override
	public void onDescriptionSelected(Description description) {
		// TODO Auto-generated method stub
		long oldDescriptionId = originalTransaction.getDescription().getId();
		long newDescriptionId = description.getId();
		if (!(oldDescriptionId == newDescriptionId)) {
			editedTransaction.setDescription(description);
			actvTransactionDetailsDetails.setEnabled(true);
			actvTransactionDetailsDetails.setClickable(false);
			markEdited();
		}
	}

	@Override
	public void onDateSet(DialogFragment dialogFrag, int year, int month,
			int day, boolean isStart) {
		// TODO Auto-generated method stub

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(editedTransaction.getTimeInMillis());
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		editedTransaction.setTime(calendar.getTimeInMillis());
		updateUserInterface(editedTransaction);
		etTransactionDetailsTime.setEnabled(true);
		etTransactionDetailsTime.setClickable(false);
		markEdited();

	}

	@Override
	public void onTimeChange(DialogFragment dialogFrag, int hour, int minute,
			boolean isStart) {
		// TODO Auto-generated method stub

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(editedTransaction.getTimeInMillis());
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		editedTransaction.setTime(calendar.getTimeInMillis());
		updateUserInterface(editedTransaction);
		etTransactionDetailsTime.setEnabled(true);
		etTransactionDetailsTime.setClickable(false);
		markEdited();

	}

	private void markEdited() {
		if (!isEdited) {
			isEdited = true;
		}
	}

	private static final String TIME_KEY = "Time";

	@Override
	public void onDialogConfirm() {
		// TODO Auto-generated method stub
		TransactionTable mTableTransactionTable = new TransactionTable(
				getApplicationContext());
		mTableTransactionTable.open();
		setAmountEdited();
		String description = actvTransactionDetailsDetails.getText().toString();
		editedTransaction.setDescription(cacheDescription
				.getDescription(description));
		mTableTransactionTable.updateTransactioSqliteDB(editedTransaction);
		mTableTransactionTable.close();
		Toast.makeText(getApplicationContext(), "Changes saved",
				Toast.LENGTH_SHORT).show();
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putLong(TIME_KEY, editedTransaction.getTimeInMillis());
		intent.putExtras(bundle);
		setResult(RESULT_OK, intent);
		finish();
	}

	private void setAmountEdited() {
		Double amount = Double.parseDouble(etTransactionDetailsAmount.getText()
				.toString());
		String currencyCode = Currencies.getDefaultCurrency(getApplicationContext());
		editedTransaction.setTransactionAmount(amount, currencyCode);
	}

	@Override
	public void onDialogCancel() {
		// TODO Auto-generated method stub

		finish();
	}

}
