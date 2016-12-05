package com.example.tcard.mvp.display;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.tcard.mvp.Injection;
import com.example.tcard.mvp.utils.ActivityUtils;
import com.example.transactioncard.R;

/**
 * Created by sshirima on 9/21/16.
 */
public class DisplayTransActivity extends AppCompatActivity{

    private static final String CURRENT_FILTER_KEY = "CURRENT_FILTER_KEY";
    private DisplayTransPresenter mTransactionPresenter;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //Set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Set up navigation drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setStatusBarBackground(R.color.colorPrimary);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null){
            setupDrawerContent(navigationView);
        }

        //Setup and start TransactionFragment (View)
        DisplayTransFragment transactionsFragment = (DisplayTransFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (transactionsFragment == null){
            //Create Fragment
            transactionsFragment = DisplayTransFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), transactionsFragment, R.id.contentFrame);
        }

        //Create transaction presenter
        mTransactionPresenter = new DisplayTransPresenter(
                Injection.provideTransactionCache(getApplicationContext()), transactionsFragment);

        //Load previously saved state, if available
        if (savedInstanceState != null){
            TransactionFilterType currentFiltering =
                    (TransactionFilterType) savedInstanceState.getSerializable(CURRENT_FILTER_KEY);
            mTransactionPresenter.setFiltering(currentFiltering);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(CURRENT_FILTER_KEY, mTransactionPresenter.getFiltering());

        super.onSaveInstanceState(outState);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.list_navigation_menu_item:
                        //Do nothing here we are already on that screen
                        break;
                    case R.id.statistics_navigation_menu_item:
                        //Start Summary activity
                        break;
                    default:
                        break;
                }

                //Close navigation drawer when an item is selected
                item.setChecked(true);
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }
}
