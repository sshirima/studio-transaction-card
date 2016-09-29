/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.transactioncard;

import com.example.transactioncard.dialogs.DialogSendAttachment;
import com.example.transactioncard.dialogs.DialogSendAttachment.DialogSendAttachmentListerner;
import com.example.transactioncard.object.DataTransfer;

import android.app.FragmentManager;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

public class ImportAndExport extends ListActivity implements
		OnItemClickListener, DialogSendAttachmentListerner {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Use an existing ListAdapter that will map an array
		// of strings to TextViews
		setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, listviewItems));
		getListView().setTextFilterEnabled(true);
		getListView().setOnItemClickListener(this);
	}

	private String[] listviewItems = { "Export data to SD card",
			"Import from SD card", "Send data to email address" };
	public static final String fileName = "Transactions.csv";
	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position,
			long arg3) {
		// TODO Auto-generated method stub
		switch (position) {
		case 0:
			DataTransfer dataTransfer = new DataTransfer(getApplicationContext());
			
			try {
				boolean isSaved = dataTransfer.exportDataToCSV(fileName);
				if (isSaved){
					Toast.makeText(getApplicationContext(), "File exported successfully", Toast.LENGTH_SHORT).show();
				}else {
					Toast.makeText(getApplicationContext(), "File export failed...", Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			break;
		case 1:

			break;
		case 2:
			FragmentManager fragmentManager = getFragmentManager();
			DialogSendAttachment dialogSendAttachment = new DialogSendAttachment();
			dialogSendAttachment.show(fragmentManager, "SendEmail");
			break;

		default:
			break;
		}
	}
	@Override
	public void onSendButtonClick() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onCancelButtonClick() {
		// TODO Auto-generated method stub
		
	}
}
