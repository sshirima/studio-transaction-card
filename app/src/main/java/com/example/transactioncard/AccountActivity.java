package com.example.transactioncard;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.example.transactioncard.HomeActivity.HomeListViewAdapter;
import com.example.transactioncard.database.AccountTable;
import com.example.transactioncard.database.ConstsDatabase;
import com.example.transactioncard.dialogs.DialogAccountEdit;
import com.example.transactioncard.dialogs.DialogAccountEdit.DialogEditAccountListerner;
import com.example.transactioncard.dialogs.DialogAccountNew;
import com.example.transactioncard.dialogs.DialogAccountNew.DialogNewAccountListerner;
import com.example.transactioncard.object.Accounts;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.MultiChoiceModeListener;

public class AccountActivity extends Activity implements
		DialogNewAccountListerner, DialogEditAccountListerner, OnItemClickListener {

	private static final String CLASSNAME = AccountActivity.class.getName();

	private static ArrayList<Accounts> accountsList;
	private static ListViewAdapter accountActivityAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		String methodName = "onCreate";
		String operation = "Initializes activity view";
		ConstsDatabase.logINFO(CLASSNAME, methodName, operation);

		/*
		 * Set the content view of the AccountActivity from the resources
		 */
		setContentView(R.layout.activity_accounts);

		/*
		 * Initialize view component variables
		 */
		initializesViewComponentVariables();

		/*
		 * Get list of the all accounts
		 */
		getAccountList();
		accountActivityAdapter = new ListViewAdapter(this);
		listviewAccountActivity.setAdapter(accountActivityAdapter);

		/*
		 * Clicks listerners
		 */
		listviewAccountActivity
				.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		listviewAccountActivity
				.setMultiChoiceModeListener(multichoiceListener());
		listviewAccountActivity.setOnItemClickListener(this);
	}

	private MultiChoiceModeListener multichoiceListener() {
		return new MultiChoiceModeListener() {

			@Override
			public void onItemCheckedStateChanged(ActionMode mode,
					int position, long id, boolean checked) {
				if (checked) {
					accountActivityAdapter.addAccountToList(mode, position);
				} else {
					accountActivityAdapter.removeAccountToList(mode, position);
				}
			}

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				// Respond to clicks on the actions in the CAB
				switch (item.getItemId()) {
				case R.id.act_mode_acc_activity_delete:
					/*
					 * Delete account and all of its associated transactions
					 */
					showDeleteDialog();
					/*
					 * Close the action mode context
					 */
					mode.finish();
					return true;
				case R.id.act_mode_acc_activity_edit:
					/*
					 * Edit the selected transaction
					 */
					displayEditAccountDialog();
					accountActivityAdapter.listSelectedAccounts.clear();
					/*
					 * Close the action mode context
					 */
					mode.finish();
					return true;
				default:
					return false;
				}
			}

			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				MenuInflater inflater = mode.getMenuInflater();

				inflater.inflate(R.menu.action_mode_account_activity, menu);
				int itemCount = accountActivityAdapter.listSelectedAccounts
						.size();
				accountActivityAdapter.setTextMenuText(mode, itemCount + " "
						+ accountActivityAdapter.selectedCount);
				return true;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {

			}

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				// Here you can perform updates to the CAB due to
				// an invalidate() request
				return false;
			}
		};
	}

	private void displayEditAccountDialog() {
		// TODO Auto-generated method stub
		String methodName = "editSelectedAccount";
		String operation = "Edit selected account parameters";
		ConstsDatabase.logINFO(CLASSNAME, methodName, operation);

		/*
		 * Load new account dialog with the account details
		 */
		FragmentManager fragmentManager = getFragmentManager();

		DialogAccountEdit dialogAccountEdit = new DialogAccountEdit();
		/*
		 * Get edit account dialog, and set the account to be edited
		 */
		Accounts editedAccounts = accountActivityAdapter.getAccountToEdit(0);

		if (!(editedAccounts == null)) {
			dialogAccountEdit.setEditedAccount(editedAccounts);
			/*
			 * Show the dialog
			 */
			dialogAccountEdit.show(fragmentManager, Consts.STRTAG_NEW_ACCOUNT);
		} else {
			Toast.makeText(getApplicationContext(), "No account selected",
					Toast.LENGTH_SHORT).show();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	private void showDeleteDialog() {
		String methodName = "showDeleteDialog";
		String operation = "Show confirmation dialog on account deletions attempt";
		ConstsDatabase.logINFO(CLASSNAME, methodName, operation);

		/*
		 * Create dialog builder class
		 */
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		/*
		 * Set up the dialog preferances
		 */
		builder.setTitle(R.string.dialog_delete_account_title);
		builder.setMessage(R.string.dialog_delete_account_message);

		/*
		 * Set click listeners
		 */
		builder.setPositiveButton(R.string.dialog_button_delete,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						/*
						 * Get the size of the selected account from the adapter
						 */
						int selectedCount = accountActivityAdapter
								.getSelectedAccountCount();
						/*
						 * Create account table instance and open the account
						 * table
						 */
						AccountTable accountTable = new AccountTable(
								getApplicationContext());
						accountTable.open();

						/*
						 * Loop through the account to delete
						 */
						int deletedAccountCount = 0;

						for (int j = 0; j < selectedCount; j++) {
							Accounts account = accountActivityAdapter
									.getAccountToDelete(j);
							if (account.getId() == 1) {
								Toast.makeText(getApplicationContext(),
										"Can't delete default account",
										Toast.LENGTH_SHORT).show();
							} else {

								accountTable.deleteAccountSqliteDB(account);
								deletedAccountCount++;
							}

						}

						/*
						 * Close the account table
						 */
						accountTable.close();

						/*
						 * Clear the selection list
						 */
						accountActivityAdapter.listSelectedAccounts.clear();

						/*
						 * Check number of the deleted accounts
						 */
						if (deletedAccountCount != 0) {

							Toast.makeText(getApplicationContext(),
									deletedAccountCount + " Account(s): Deleted",
									Toast.LENGTH_SHORT).show();
						}

						/*
						 * Update the account list
						 */
						getAccountList();
						accountActivityAdapter.notifyDataSetChanged();
					}
				}).setNegativeButton(R.string.dialog_button_cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.menu_account, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.action_account_newaccount:
			displayNewAccountDialog();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 
	 */
	private void displayNewAccountDialog() {
		String methodName = "displayNewAccountDialog";
		String operation = "Present new account dialog for user input";
		ConstsDatabase.logINFO(CLASSNAME, methodName, operation);

		/*
		 * Load and display new Account dialog
		 */
		FragmentManager fragmentManager = getFragmentManager();
		DialogAccountNew dialogNewAccount = new DialogAccountNew();
		dialogNewAccount.show(fragmentManager, Consts.STRTAG_NEW_ACCOUNT);
	}

	private void getAccountList() {
		String methodName = "getAccountList";
		String operation = "Query list of all accounts";
		ConstsDatabase.logINFO(CLASSNAME, methodName, operation);

		/*
		 * Open the database and query for the accounts
		 */
		AccountTable accountTable = new AccountTable(getApplicationContext());
		accountTable.open();
		accountsList = accountTable.getAccountAll();
		accountTable.close();
	}

	private ListView listviewAccountActivity;

	private void initializesViewComponentVariables() {
		// TODO Auto-generated method stub
		listviewAccountActivity = (ListView) findViewById(R.id.lvAccounts);
	}

	@Override
	public void onDialogNewAccountCreateClick(DialogFragment dialog,
			Accounts account) {
		// TODO Auto-generated method stub
		String methodName = "onDialogNewAccountCreateClick";
		String operation = "Handling create new account click button";
		ConstsDatabase.logINFO(CLASSNAME, methodName, operation);

		/*
		 * Get new account info
		 */
		dialog.dismiss();

		/*
		 * Update the account list display
		 */

		getAccountList();
		accountActivityAdapter.notifyDataSetChanged();
	}

	@Override
	public void onDialogNewAccountCancelClick(DialogFragment dialog) {
		// TODO Auto-generated method stub
		String methodName = "onDialogNewAccountCancelClick";
		String operation = "Handle newAccount dialog cancel operation";
		ConstsDatabase.logINFO(CLASSNAME, methodName, operation);

		/*
		 * * Toast notification on the cancelation process
		 */

		Toast.makeText(getApplicationContext(), "Canceled", Toast.LENGTH_SHORT)
				.show();
		dialog.dismiss();
	}

	@Override
	public void onDialogEditAccountSaveClick(DialogFragment dialog,
			Accounts account) {
		// TODO Auto-generated method stub
		String methodName = "onDialogEditAccountSaveClick";
		String operation = "Handle edit account dialog save operation";
		ConstsDatabase.logINFO(CLASSNAME, methodName, operation);
		/*
		 * Save changes of the new account to the sqlite DB
		 */
		AccountTable accountTable = new AccountTable(getApplicationContext());
		accountTable.open();
		/*
		 * Update account to the DB
		 */
		if (accountTable.editAccountsSqliteDB(account) != null) {

			dialog.dismiss();
			/*
			 * Update account activity view
			 */
			getAccountList();
			accountActivityAdapter.notifyDataSetChanged();
		} else {
			dialog.dismiss();
			Toast.makeText(getApplicationContext(),
					Consts.STRTOAST_MSG_NOTEDIT, Toast.LENGTH_SHORT).show();
		}
		;
		/*
		 * Close the table
		 */
		accountTable.close();
	}

	@Override
	public void onDialogEditAccountCancelClick(DialogFragment dialog) {
		// TODO Auto-generated method stub
		String methodName = "onDialogEditAccountCancelClick";
		String operation = "Handle edit account dialog cancel operation";
		ConstsDatabase.logINFO(CLASSNAME, methodName, operation);

		/*
		 * Toast notification on the cancelation process
		 */
		Toast.makeText(getApplicationContext(), Consts.STRTOAST_MSG_CANCELED,
				Toast.LENGTH_SHORT).show();
		dialog.dismiss();
	}

	private static class ListViewAdapter extends HomeListViewAdapter {

		LayoutInflater inflater;
		Context context;
		private final String STRTXTV_BALANCE = "Balance %s %s"; 
		private final String STRTXTV_INCOME = "Income %s %s";
		private final String STRTXTV_EXPENSES = "Expenses %s %s";
		
 		public ListViewAdapter(Context context) {
			super(context);
			inflater = LayoutInflater.from(context);
			this.context = context;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return accountsList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return accountsList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;

			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater
						.inflate(R.layout.listview_accounts, null);
				holder.tvBalance = (TextView) convertView
						.findViewById(R.id.tvBalance);
				holder.tvName = (TextView) convertView
						.findViewById(R.id.tvName);
				holder.tvIncome = (TextView) convertView
						.findViewById(R.id.tvIncome);
				holder.tvExpenses = (TextView) convertView
						.findViewById(R.id.tvExpenses);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			/*
			 * Get account information
			 */
			Accounts account = (Accounts) accountsList.get(position);
			String currency = Settings.getDefaultCurrency(context);
			double balance = account.getIncome(context)
					- account.getExpenses(context);
			/*
			 * Set the account information
			 */
			String balanceFormated = getFormateStringFromDouble((int)Math.round(balance));
			String expenseFormated = getFormateStringFromDouble((int)Math.round(account.getExpenses(context)));
			String incomeFormated = getFormateStringFromDouble((int)Math.round(account.getIncome(context)));
			
			holder.tvName.setText(account.getAccountName());
			holder.tvBalance.setText(String.format(STRTXTV_BALANCE, balanceFormated, currency));
			holder.tvExpenses.setText(String.format(STRTXTV_EXPENSES, expenseFormated, currency));
			holder.tvIncome.setText(String.format(STRTXTV_INCOME, incomeFormated, currency));

			return convertView;
		}
		
		private String getFormateStringFromDouble(double value){
			return new DecimalFormat().format(value);
		} 

		public void setTextMenuText(ActionMode mode, final String selectedCount) {
			Menu menu = mode.getMenu();
			MenuItem menuTexItem = menu
					.findItem(R.id.actmode_acc_activity_text);
			View v = menuTexItem.getActionView();
			if (v instanceof TextView) {
				((TextView) v).setText(selectedCount);
			}
		}

		public SparseArray<Accounts> listSelectedAccounts = new SparseArray<Accounts>();

		public int getSelectedAccountCount() {
			return this.listSelectedAccounts.size();
		}

		public void addAccountToList(ActionMode mode, int position) {
			try {
				Accounts selectedAccounts = (Accounts) accountsList
						.get(position);
				listSelectedAccounts.put(position, selectedAccounts);
				int itemCount = listSelectedAccounts.size();
				setTextMenuText(mode, itemCount + " " + selectedCount);
				notifyDataSetChanged();
			} catch (ClassCastException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void removeAccountToList(ActionMode mode, int position) {
			listSelectedAccounts.delete(position);
			int itemCount = listSelectedAccounts.size();
			setTextMenuText(mode, itemCount + " " + selectedCount);
			notifyDataSetChanged();
		}

		public Accounts getAccountToDelete(int index) {
			return listSelectedAccounts.valueAt(index);
		}

		public Accounts getAccountToEdit(int index) {
			return listSelectedAccounts.valueAt(index);
		}

		private class ViewHolder {
			TextView tvName, tvBalance, tvIncome, tvExpenses;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		/*
		 * Get the selected account
		 */
		long accountId = accountsList.get(position).getId();
		/*
		 * Prepare a bundle and put an account id
		 */
		Bundle bundle = new Bundle();
		bundle.putLong(Consts.STRBUNDLE_ACCOUNT_ID, accountId);
		/*
		 * Create an intent to start another activity
		 */
		Intent intent = new Intent(AccountActivity.this, AccountDetailActivity.class);
		/*
		 * Put bundle on the activity intent
		 */
		intent.putExtras(bundle);
		/*
		 * Start another activity
		 */
		startActivityForResult(intent, 0);
	}

}
