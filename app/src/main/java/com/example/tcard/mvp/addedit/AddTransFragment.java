package com.example.tcard.mvp.addedit;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tcard.mvp.data.Account;
import com.example.tcard.mvp.data.Description;
import com.example.transactioncard.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.example.tcard.mvp.Preconditions.checkNotNull;

/**
 * Created by sshirima on 10/10/16.
 */
public class AddTransFragment extends Fragment implements AddEditTransContract.View, View.OnClickListener {

    public static final String ARGUMENT_EDIT_TRANS_ID = "EDIT_TRANS_ID";

    private AddEditTransContract.Presenter mPresenter;

    private double mAmount;
    private long mTimeInMills;
    private String mCurrencyCode;
    private Description mDescription;
    private Account mAccount;
    //View component variables
    private EditText etEditAmount;
    private AutoCompleteTextView actvEditDescription;
    private Spinner spEditCurrencies;
    private ImageView ivEditCategory;
    private ImageView ivEditAccount;
    private ImageView ivEditTime;
    private ImageView ivEditDate;
    private TextView tvShowCategory;
    private TextView tvShowAccount;
    private TextView tvShowTime;
    private TextView tvShowDate;

    public static AddTransFragment newInstance(){return new AddTransFragment();}

    public AddTransFragment(){
        //Get activity preferences
        //Initialize default component variable
        this.mTimeInMills = getTimeInMills();
        this.mAccount = getAccount();
        this.mDescription = getDescription();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fabSaveTransaction);
        fab.setImageResource(R.drawable.ic_done);
        //Set onClickListerners
        ivEditAccount.setOnClickListener(this);
        ivEditTime.setOnClickListener(this);
        ivEditDate.setOnClickListener(this);
        ivEditCategory.setOnClickListener(this);
        fab.setOnClickListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_addtrans, container, false);
        //initializeComponentVars(root);
        etEditAmount = (EditText) root.findViewById(R.id.etEditAmount);
        actvEditDescription = (AutoCompleteTextView)root.findViewById(R.id.actvEditDescription);
        ivEditAccount = (ImageView)root.findViewById(R.id.ivEditAccount);
        ivEditCategory = (ImageView)root.findViewById(R.id.ivEditCategory);
        ivEditDate = (ImageView)root.findViewById(R.id.ivEditDate);
        ivEditTime = (ImageView)root.findViewById(R.id.ivEditTime);
        tvShowDate = (TextView) root.findViewById(R.id.tvShowDate);
        tvShowTime = (TextView) root.findViewById(R.id.tvShowTime);
        tvShowCategory = (TextView) root.findViewById(R.id.tvShowCategory);
        tvShowAccount = (TextView) root.findViewById(R.id.tvShowAccount);

        tvShowDate.setText(getFormatedDate(mTimeInMills));
        tvShowTime.setText(getFormatedTime(mTimeInMills));
        tvShowAccount.setText(mAccount.getName());
        tvShowCategory.setText(mDescription.getCategory());

        setHasOptionsMenu(true);
        setRetainInstance(true);
        return root;
    }

    @Override
    public void showEmptyTransactionError() {
        Toast.makeText(getActivity(),"Empty fields", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showTransactionList() {
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    @Override
    public void setAmount(String amount) {
        try {
            mAmount = Double.parseDouble(amount);
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void setTime(long timeInMillSecond) {
        mTimeInMills = timeInMillSecond;
        tvShowDate.setText(getFormatedDate(mTimeInMills));
        tvShowTime.setText(getFormatedTime(mTimeInMills));
    }

    @Override
    public void setAccount(Account account){
        this.mAccount = account;
        tvShowAccount.setText(account.getName());
    }

    @Override
    public void setDescription(Description description) {
        this.mDescription = description;
        tvShowCategory.setText(mDescription.getCategory());
    }

    @Override
    public void setCurrency(String currencyCode) {
        mCurrencyCode = currencyCode;
    }

    public Account getAccount(){
        return new Account("Personal Account");
    }

    private long getTimeInMills(){
        if (mTimeInMills == 0){
            return Calendar.getInstance().getTimeInMillis();//Default time, current time
        } else {
            return mTimeInMills;
        }
    }

    public String getAmount(){
        if (!etEditAmount.getText().equals("")){
            return etEditAmount.getText().toString();
        } else {
            return Double.toString(mAmount);
        }
    }

    public Description getDescription(){return new Description("No description", "EXPENSES");}

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void setPresenter(AddEditTransContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter,"#setPresenter>presenter cannot be null");
    }

    private void initializeComponentVars(View view){
        /*etEditAmount = (EditText) view.findViewById(R.id.etEditAmount);
        actvEditDescription = (AutoCompleteTextView)view.findViewById(R.id.actvEditDescription);
        ivEditAccount = (ImageView)view.findViewById(R.id.ivEditAccount);
        ivEditCategory = (ImageView)view.findViewById(R.id.ivEditCategory);
        ivEditDate = (ImageView)view.findViewById(R.id.ivEditDate);
        ivEditTime = (ImageView)view.findViewById(R.id.ivEditTime);
        tvShowDate = (TextView) view.findViewById(R.id.tvShowDate);
        tvShowTime = (TextView) view.findViewById(R.id.tvShowTime);
        tvShowCategory = (TextView) view.findViewById(R.id.tvShowCategory);
        tvShowAccount = (TextView) view.findViewById(R.id.tvShowAccount);*/
    }

    private String getFormatedTime(long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm",
                Locale.ENGLISH);
        return simpleDateFormat.format(new Date(calendar.getTimeInMillis()));
    }

    private String getFormatedDate(long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyy/MM/d",
                Locale.ENGLISH);
        return simpleDateFormat.format(new Date(calendar.getTimeInMillis()));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fabSaveTransaction:
                mAmount = Double.parseDouble(getAmount());
                //Save new transaction to the database
                mPresenter.saveTransaction(mAmount,mTimeInMills,
                        Integer.parseInt(mAccount.getId()),
                        Integer.parseInt(mDescription.getId()), mCurrencyCode);
                break;
            case R.id.ivEditCategory:
                break;
            case R.id.ivEditAccount:
                break;
            case R.id.ivEditDate:
                break;
            case R.id.ivEditTime:
                break;
        }
    }
}
