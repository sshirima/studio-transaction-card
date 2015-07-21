package com.example.transactioncard.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class DialogConfirmation extends DialogFragment {
	
	public interface DialogConfirmationListener {
		public void onDialogConfirm();
		public void onDialogCancel();
	}
	private String message;
	private String messagePositiveButton;
	private String messageNegativeButton;
	
	public void setMassage(String message){
		this.message = message;
	}
	
	public void setMassagePositiveButton(String message){
		this.messagePositiveButton = message;
	}
	
	public void setMassageNegativeButton(String message){
		this.messageNegativeButton = message;
	}
	
	DialogConfirmationListener dialogConfirmationListener;
	
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
               .setPositiveButton(messagePositiveButton, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       dialogConfirmationListener.onDialogConfirm();
                   }
               })
               .setNegativeButton(messageNegativeButton, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                      dialogConfirmationListener.onDialogCancel();
                      dismiss();
                   }
               });
        return builder.create();
    }
    
    @Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		try {
			dialogConfirmationListener = (DialogConfirmationListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement DialogConfirmationListener");
		}
	}
}
