package com.example.tcard.mvp.addedit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.tcard.mvp.Injection;
import com.example.tcard.mvp.utils.ActivityUtils;
import com.example.transactioncard.R;

/**
 * Created by sshirima on 10/18/16.
 */
public class AddTransActivity extends AppCompatActivity {
    public static final int REQUEST_ADD_TRANSACTION = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtransaction);

        //Set up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        //Set up fragment
        AddTransFragment addTransFragment =
                (AddTransFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        String transactionId = null;

        if (addTransFragment == null) {
            addTransFragment = AddTransFragment.newInstance();
            actionBar.setTitle("New transaction");

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), addTransFragment, R.id.contentFrame);
        }

        //Create presenter
        new AddEditTransPresenter(transactionId, Injection.provideTransactionCache(getApplicationContext()),addTransFragment);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
