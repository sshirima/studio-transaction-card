package com.example.tcard.mvp.display;

import android.app.Activity;

import com.example.tcard.mvp.data.Transaction;
import com.example.tcard.mvp.data.source.TransactionsCache;
import com.example.tcard.mvp.data.source.TransactionsTableInterface;

import java.util.ArrayList;
import java.util.List;

import static com.example.tcard.mvp.Preconditions.checkNotNull;


/**
 * Created by sshirima on 9/19/16.
 */
public class DisplayTransPresenter implements DisplayTransContract.Presenter{

    private final TransactionsCache mTransactionsCache;

    private final DisplayTransContract.View mDisplayView;

    private boolean mFirstLoad = true;

    private TransactionFilterType mCurrentFiltering = TransactionFilterType.TODAY;

    public DisplayTransPresenter(TransactionsCache transactionsCache, DisplayTransContract.View displayView) {
        this.mTransactionsCache = checkNotNull(transactionsCache, "TransactionPresenter#TransactionPresenter:" +
                "TransactionCache cannot be null");
        this.mDisplayView = checkNotNull(displayView, "TransactionPresenter#TransactionPresenter:" +
                "DisplayView cannot be null");
        displayView.setPresenter(this);
    }

    @Override
    public void start() {
        loadTransactions(false);
    }

    @Override
    public void result(int requestCode, int resultCode) {
        //If transaction was successful added show the indicator on the UI
        if (requestCode == 1 && Activity.RESULT_OK == resultCode){
            mDisplayView.showSuccessfullySavedMessage();
        }
    }

    @Override
    public void loadTransactions(boolean forceUpdate) {
        //Simplification for sample, network reload will be forced for first load
        loadTransactions(forceUpdate || mFirstLoad, true);
    }

    private void loadTransactions(boolean forceUpdate, final boolean showLoadingUI) {
        if (showLoadingUI){
            mDisplayView.setLoadingIndicator(true);
        }

        if (forceUpdate){
            mTransactionsCache.refreshTransactions();
        }

        mTransactionsCache.getTransactions(new TransactionsTableInterface.LoadTransactionsCallback() {
            @Override
            public void onTrasactionsLoaded(List<Transaction> transactions) {
                List<Transaction> transactionToShow = new ArrayList<Transaction>();

                //We filter the tasks based on the request type
                for (Transaction transaction: transactions){
                    switch (mCurrentFiltering){
                        case TODAY:
                            //Filter logic for today
                            transactionToShow.add(transaction);
                            break;
                        case YESTERDAY:
                            transactionToShow.add(transaction);
                            break;
                        case WEEK:
                            transactionToShow.add(transaction);
                            break;
                        case MONTH:
                            transactionToShow.add(transaction);
                            break;
                        case CUSTOM:
                            transactionToShow.add(transaction);
                            break;
                    }
                }

                //The view might not be able to handle UI updates anymore
                if (mDisplayView.isActive()){
                    return;
                }
                if (showLoadingUI){
                    mDisplayView.setLoadingIndicator(false);
                }

                processTransactions(transactionToShow);
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
    }

    private void processTransactions(List<Transaction> transactionToShow) {
        if (transactionToShow.isEmpty()){
            //Show message indicating there are no transactions for that filter type
            processEmptyTransactions();
        } else {
            //Show list of transactions
            mDisplayView.showTransactions(transactionToShow);
            //Show filter label
        }
    }

    private void processEmptyTransactions() {
        mDisplayView.showNoTasks();
    }

    @Override
    public void addNewTransaction() {
        mDisplayView.showAddTransaction();
    }

    /**
     * Set the current display transactions type
     *
     * @param requestType
     */
    @Override
    public void setFiltering(TransactionFilterType requestType) {
        mCurrentFiltering = requestType;
    }

    @Override
    public TransactionFilterType getFiltering() {
        return mCurrentFiltering;
    }


}
