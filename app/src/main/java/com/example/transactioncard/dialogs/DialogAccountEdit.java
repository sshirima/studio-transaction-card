/**
 * 
 */
package com.example.transactioncard.dialogs;

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
public class DialogAccountEdit extends DialogFragment implements OnClickListener{
	
	public static final String CLASSNAME = DialogAccountEdit.class.getName();
	
	private EditText etNewAccount;
	private TextView tvNewAccountLabel;
	private Button bNewAccountCreate;
	private Button bNewAccountCancel;
	
	private Accounts editedAccount;
	
	public interface DialogEditAccountListerner {
		public void onDialogEditAccountSaveClick(DialogFragment dialog,
				Accounts account);

		public void onDialogEditAccountCancelClick(DialogFragment dialog);
	}
	
	DialogEditAccountListerner dialogEditAccountListerner;
	
	public void setEditedAccount(Accounts editedAccounts){
		this.editedAccount = editedAccounts;
	} 
	
	public static DialogAccountEdit newInstance() {
		return new DialogAccountEdit();
	}
	
	public DialogAccountEdit (){
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		String methodName = "onCreateView";
		String operation = "Creating view for the edit account dialog";
		ConstsDatabase.logINFO(CLASSNAME, methodName, operation);
		
		/*
		 * Create dialog interface
		 */
		View dialog = inflater.inflate(R.layout.dialog_new_account,
				container, false);
		
		/*
		 * Inititialize activity component variable 
		 */
		etNewAccount = (EditText)dialog.findViewById(R.id.etNewAccountName);
		bNewAccountCancel = (Button)dialog.findViewById(R.id.bNewAccountCancel);
		bNewAccountCreate = (Button)dialog.findViewById(R.id.bNewAccountCreate);
		tvNewAccountLabel = (TextView)dialog.findViewById(R.id.tvLabelNewAccountName);
		
		/*
		 * Setup buttons display text
		 */
		bNewAccountCreate.setText(R.string.dialog_edit_button_save);
		etNewAccount.setText(editedAccount.getAccountName());
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
		dialog.setTitle(R.string.dialog_edit_acc_title);
		dialog.show();
		return dialog;
	}
	
	
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		try {
			dialogEditAccountListerner = (DialogEditAccountListerner) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement DialogEditAccountListerner");
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
			dialogEditAccountListerner.onDialogEditAccountCancelClick(DialogAccountEdit.this);
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
				editedAccount.setAccountName(accountName);
				/*
				 * Return the information to the main fragment
				 */
				dialogEditAccountListerner.onDialogEditAccountSaveClick(DialogAccountEdit.this, editedAccount);
			} else {
				tvNewAccountLabel.setTextColor(Color.RED);
			}
			break;
		}
	}

}
