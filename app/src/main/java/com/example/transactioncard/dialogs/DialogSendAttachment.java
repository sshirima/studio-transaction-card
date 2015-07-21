package com.example.transactioncard.dialogs;

import com.example.transactioncard.ImportAndExport;
import com.example.transactioncard.R;
import com.example.transactioncard.object.DataTransfer;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DialogSendAttachment extends DialogFragment implements
		OnClickListener {

	public static DialogSendAttachment newInstance() {
		return new DialogSendAttachment();
	}
	
	public interface DialogSendAttachmentListerner {
		public void onSendButtonClick();
		public void onCancelButtonClick();
	}
	
	DialogSendAttachmentListerner dialogSendAttachmentListerner;

	private EditText etEmailAddress;
	private EditText etEmailSubject;
	private EditText etEmailBody;
	private Button bEmailCancel;
	private Button bEmailSend;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Dialog dialog = new Dialog(getActivity());
		dialog.setContentView(R.layout.dialog_send_email);
		dialog.show();
		dialog.setTitle(R.string.email_dialog_title);
		return dialog;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.dialog_send_email,
				container, false);
		initializesComponentInfos(view);
		setURI();
		bEmailCancel.setOnClickListener(this);
		bEmailSend.setOnClickListener(this);
		return view;
	}

	private void initializesComponentInfos(View dialog) {
		etEmailBody = (EditText) dialog.findViewById(R.id.etEmailBody);
		etEmailAddress = (EditText) dialog.findViewById(R.id.etEmailAddress);
		etEmailSubject = (EditText) dialog.findViewById(R.id.etEmailSubject);
		bEmailCancel = (Button) dialog.findViewById(R.id.bEmailCancel);
		bEmailSend = (Button) dialog.findViewById(R.id.bEmailSend);
	}

	private void setURI(){
		DataTransfer dataTransfer = new DataTransfer(getActivity());
		boolean isSaved = dataTransfer.exportDataToCSV(ImportAndExport.fileName);
		if (isSaved){
			String directory = DataTransfer.getFileDirectory();
			URI = Uri.parse("file://" + directory);
		}else {
			Toast.makeText(getActivity(), "Attachment not found", Toast.LENGTH_SHORT).show();
		}
	}
	
	Uri URI = null;
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.bEmailCancel:

			break;
		case R.id.bEmailSend:
			String email = etEmailAddress.getText().toString();
			String subject = etEmailSubject.getText().toString();
			String body = etEmailBody.getText().toString();
			
			String emailAddresses[] = { email };
			
			Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
					emailAddresses);
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
			emailIntent.setType("plain/text");
			emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, body);
			
			if (URI != null){
				emailIntent.putExtra(Intent.EXTRA_STREAM, URI);
				}
			startActivity(emailIntent);
			dismiss();
			break;

		default:
			break;
		}
	}
	
	  @Override
		public void onAttach(Activity activity) {
			// TODO Auto-generated method stub
			super.onAttach(activity);
			try {
				dialogSendAttachmentListerner = (DialogSendAttachmentListerner) activity;
			} catch (ClassCastException e) {
				throw new ClassCastException(activity.toString()
						+ " must implement DialogSendAttachmentListerner");
			}
		}

}
