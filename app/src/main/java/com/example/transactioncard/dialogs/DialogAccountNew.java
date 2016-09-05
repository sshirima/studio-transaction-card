/**
 * 
 */
package com.example.transactioncard.dialogs;

import com.example.transactioncard.database.AccountTable;
import com.example.transactioncard.database.ConstsDatabase;
import com.example.transactioncard.object.Accounts;

import com.example.transactioncard.R;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * @author samson
 *
 */
public class DialogAccountNew extends DialogFragment implements OnClickListener{
	
	public static final String CLASSNAME = DialogAccountNew.class.getName();
	
	private EditText etNewAccount;
	private TextView tvNewAccountLabel;
	private Button bNewAccountCreate;
	private Button bNewAccountCancel;
	
	public interface DialogNewAccountListerner {
		public void onDialogNewAccountCreateClick(DialogFragment dialog,
				Accounts account);

		public void onDialogNewAccountCancelClick(DialogFragment dialog);
	}
	
	DialogNewAccountListerner dialogNewAccountListerner;
	
	public static DialogAccountNew newInstance() {
		return new DialogAccountNew();
	}
	
	public DialogAccountNew (){
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		String methodName = "onCreateView";
		String operation = "Creating view for the new account dialog";
		ConstsDatabase.logINFO(CLASSNAME, methodName, operation);
		
		/*
		 * Create dialog interface
		 */
		View dialog = inflater.inflate(R.layout.dialog_new_account,
				container, false);
		
		/*
		 * Initi
		 */
		etNewAccount = (EditText)dialog.findViewById(R.id.etNewAccountName);
		bNewAccountCancel = (Button)dialog.findViewById(R.id.bNewAccountCancel);
		bNewAccountCreate = (Button)dialog.findViewById(R.id.bNewAccountCreate);
		tvNewAccountLabel = (TextView)dialog.findViewById(R.id.tvLabelNewAccountName);
		
		/*
		 * Set onClick listers
		 */
		bNewAccountCancel.setOnClickListener(this);
		bNewAccountCreate.setOnClickListener(this);
		return dialog;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Dialog dialog = new Dialog(getActivity());
		dialog.setContentView(R.layout.dialog_new_account);
		dialog.show();
		dialog.setTitle(R.string.new_acc_name_dialog_title);
		return dialog;
	}
	
	
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		try {
			dialogNewAccountListerner = (DialogNewAccountListerner) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement DialogNewAccountListerner");
		}
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.bNewAccountCancel:
			/*
			 * Removes new account dialog 
			 */
			dialogNewAccountListerner.onDialogNewAccountCancelClick(DialogAccountNew.this);
			break;

		case R.id.bNewAccountCreate:
			String accountName = etNewAccount.getText().toString();
			/*
			 * Check if  the account name field is empty
			 */
			if (!accountName.equals("")){
				/*
				 * Save the new account from  the given name
				 */
				Accounts account = new Accounts(getActivity(), accountName);
				
				/*
				 * Open the account table for adding new account
				 */
				AccountTable accountTable = new AccountTable(getActivity());
				accountTable.open();
				Accounts newAccount = accountTable.insertAccountSqliteDB(account);
				accountTable.close();
				/*
				 * Return the information to the main fragment
				 */
				dialogNewAccountListerner.onDialogNewAccountCreateClick(DialogAccountNew.this, newAccount);
			} else {
				tvNewAccountLabel.setTextColor(Color.RED);
			}
			break;
		}
	}

}
