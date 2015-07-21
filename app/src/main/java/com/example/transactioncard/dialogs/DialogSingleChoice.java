package com.example.transactioncard.dialogs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.example.transactioncard.object.Accounts;
import com.example.transactioncard.object.Description;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class DialogSingleChoice extends DialogFragment{

	private String dialogTitle = "Choose";
	private CharSequence[] choiceList;
	
	public void setDialogTitle(String dialogTitle){
		this.dialogTitle = dialogTitle;
	}
	
	public void setChoiceList(DialogFragment dialogFragments, CharSequence[] choiceList){
		this.dialogFragment = dialogFragments;
		this.choiceList = choiceList;
	}
	
	private DialogFragment dialogFragment;
	private ArrayList<Accounts> accountList = new ArrayList<Accounts>();
	public void setAccountList(DialogFragment dialogFragment, ArrayList<Accounts> accountsList){	
		this.accountList = accountsList;
		this.dialogFragment = dialogFragment;
		ArrayList<String> accountNameList = new ArrayList<String>();
		for (int i = 0; i < accountsList.size(); i++) {
			Accounts account = accountsList.get(i);
			accountNameList.add(account.getAccountName());
		}
		this.choiceList = accountNameList.toArray(new CharSequence[accountNameList.size()]);
	}
	
	public interface ChoiceSelectedListener {
		public void onChoiceSelected(DialogFragment dialogFragment, Accounts accounts);
		public void onChoiceSelected(DialogFragment dialogFragment, String selectedItem);
		public void onDescriptionSelected(Description description);
	}
	
	ChoiceSelectedListener choiceSelectedListener;
	
	private List<Description> descriptionList;
	public void setDescriptionList(List<Description> descriptionList){
		this.descriptionList = descriptionList;
		this.choiceList = getDescriptionNames(descriptionList);
	}
	
	private CharSequence[] getDescriptionNames(List<Description> list ){
		ArrayList<String> nameList = new ArrayList<String>();
		for (int i = 0; i < descriptionList.size(); i++) {
			Description description = descriptionList.get(i);
			nameList.add(description.getDescription());
		}
		return nameList.toArray(new CharSequence[nameList.size()]);
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		 AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		    builder.setTitle(dialogTitle);
		     builder.setItems(choiceList, new DialogInterface.OnClickListener() {
		               public void onClick(DialogInterface dialog, int selectedItemPosition) {
		            	 
		            	   if (!(accountList.size() == 0)){
		            		   choiceSelectedListener.onChoiceSelected(dialogFragment, accountList.get(selectedItemPosition)); 
		            	   } else if (!(descriptionList==null)){
		            		   Description description = descriptionList.get(selectedItemPosition);
		            		   choiceSelectedListener.onDescriptionSelected(description);
		            	   }else {
		            		   choiceSelectedListener.onChoiceSelected(dialogFragment, choiceList[selectedItemPosition].toString()); 
		            	   }
		            	   
		           }
		    });
		    return builder.create();
	}
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		try {
			choiceSelectedListener = (ChoiceSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement ChoiceSelectedListener");
		}
	}
}
