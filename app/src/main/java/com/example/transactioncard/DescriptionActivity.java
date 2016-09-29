package com.example.transactioncard;

import java.util.ArrayList;
import java.util.List;

import com.example.transactioncard.database.DescriptionTable;
import com.example.transactioncard.dialogs.DialogConfirmation;
import com.example.transactioncard.dialogs.DialogConfirmation.DialogConfirmationListener;
import com.example.transactioncard.object.CacheDescription;
import com.example.transactioncard.object.Description;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class DescriptionActivity extends Activity implements
		OnEditorActionListener, DialogConfirmationListener,
		OnItemLongClickListener, OnItemClickListener {

	private ListView lvNewDescription;
	private EditText etDescriptionNew;
	private List<Description> descriptionList;
	private ArrayAdapter<String> adapter;
	LinearLayout linearLayout;

	private List<String> arrayList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_description);
		initializesComponentVars();
		arrayList = setDescriptionList();
		setUpListviewAdapter();
		lvNewDescription.setAdapter(adapter);
		lvNewDescription.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		lvNewDescription.setOnItemLongClickListener(this);
		lvNewDescription.setOnItemClickListener(this);
		etDescriptionNew.setOnEditorActionListener(this);
	}

	private void setUpListviewAdapter() {
		adapter = new ArrayAdapter<String>(getApplicationContext(),
				R.layout.listview_description, arrayList);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_description, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.action_new_description:
			boolean notEmpty = !etDescriptionNew.getText().toString()
					.equals("");
			if (notEmpty) {
				DialogConfirmation confirmation = new DialogConfirmation();
				String descriptionName = etDescriptionNew.getText().toString();
				confirmation.setMassage("Save description as "
						+ descriptionName + " ?");
				confirmation.setMassageNegativeButton("Don't save");
				confirmation.setMassagePositiveButton("Save");
				isNewRecord = true;
				confirmation.show(getFragmentManager(), "Confirmation");
			}
			linearLayout.setVisibility(View.VISIBLE);
			etDescriptionNew.requestFocus();
			item.setIcon(R.drawable.ic_action_save);
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private List<String> setDescriptionList() {
		DescriptionTable descriptionTable = new DescriptionTable(
				getApplicationContext());
		descriptionTable.open();
		descriptionList = descriptionTable.getDescriptionsAll();
		descriptionTable.close();
		List<String> returnList = new ArrayList<String>();
		for (int i = 0; i < descriptionList.size(); i++) {
			returnList.add(descriptionList.get(i).getDescription());
		}
		return returnList;
	}

	private void initializesComponentVars() {
		lvNewDescription = (ListView) findViewById(R.id.lvDescription);
		etDescriptionNew = (EditText) findViewById(R.id.etDescriptionNew);
		linearLayout = (LinearLayout) findViewById(R.id.llDescriptionNewEdittext);
		linearLayout.setVisibility(View.GONE);
	}

	@Override
	public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
		// TODO Auto-generated method stub

		return false;
	}

	@Override
	public void onDialogConfirm() {
		// TODO Auto-generated method stub
		if (isNewRecord) {
			CacheDescription cacheDescription = new CacheDescription(
					getApplicationContext());
			String descriptionName = etDescriptionNew.getText().toString();
			Description description = cacheDescription
					.getDescription(descriptionName);
			if (description != null) {
				Toast.makeText(getApplicationContext(), "Description cached",
						Toast.LENGTH_SHORT).show();
				etDescriptionNew.setText("");
				linearLayout.setVisibility(View.GONE);
				descriptionList.add(description);
				arrayList.add(description.getDescription());
				adapter.notifyDataSetChanged();

			} else {
				Toast.makeText(getApplicationContext(), "Not cached!!!",
						Toast.LENGTH_SHORT).show();
				etDescriptionNew.setText("");
				linearLayout.setVisibility(View.GONE);
			}
		} else {
			CacheDescription cacheDescription = new CacheDescription(
					getApplicationContext());
			Description description = descriptionList.get(selectedPosition);
			cacheDescription.deleteDescription(description);
			descriptionList.remove(selectedPosition);
			arrayList.remove(selectedPosition);
			adapter.notifyDataSetChanged();
			Toast.makeText(getApplicationContext(), "Deleted",
					Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public void onDialogCancel() {
		// TODO Auto-generated method stub
		etDescriptionNew.setVisibility(View.GONE);
	}

	private boolean isNewRecord;

	private int selectedPosition;

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View view,
			int position, long arg3) {
		// TODO Auto-generated method stub
		selectedPosition = position;
		startActionMode(new ActionMode.Callback() {

			@Override
			public boolean onPrepareActionMode(ActionMode arg0, Menu arg1) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onDestroyActionMode(ActionMode arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
				// TODO Auto-generated method stub
				actionMode.getMenuInflater().inflate(
						R.menu.action_mode_menu_description, menu);
				return true;
			}

			@Override
			public boolean onActionItemClicked(ActionMode actionMode,
					MenuItem menuItem) {
				// TODO Auto-generated method stub
				DialogConfirmation confirmation = new DialogConfirmation();
				Description description = descriptionList.get(selectedPosition);
				String descriptionName = description.getDescription();
				confirmation.setMassage("Do you want to delete "
						+ descriptionName + " ?");
				confirmation.setMassageNegativeButton("Cancel");
				confirmation.setMassagePositiveButton("Delete");
				isNewRecord = false;
				confirmation.show(getFragmentManager(), "Confirmation");
				actionMode.finish();
				return false;
			}
		});
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
		// TODO Auto-generated method stub
		long id = descriptionList.get(position).getId();
		Bundle bundle = new Bundle();
		bundle.putLong(Consts.STRBUNDLE_DESC_ID, id);
		Intent intent = new Intent(DescriptionActivity.this, ViewbyDescription.class);
		intent.putExtras(bundle);
		startActivityForResult(intent, 0);
	}
}
