package com.example.tcard.mvp.addedit;

import com.example.tcard.mvp.data.source.TransactionsCache;
import com.example.tcard.mvp.data.Transaction;

import static com.example.tcard.mvp.Preconditions.checkNotNull;

/**
 * Lister to user action from UI({@link }), retrieve the data and updates the UI as required
 *
 * Created by sshirima on 10/10/16.
 */
public class AddEditTransPresenter implements AddEditTransContract.Presenter,
        TransactionsCache.GetTransactionCallback{

    private TransactionsCache mTransactionCache;
    private AddEditTransContract.View mAddTransactionView;
    private String mTransactionId;

    /**
     * Create a presenter for the add/edit view
     *
     * @param transactionId
     * @param transactionsCache
     * @param addTransactionView
     */

    public AddEditTransPresenter(String transactionId, TransactionsCache transactionsCache,
                                 AddEditTransContract.View addTransactionView){
        mTransactionId = transactionId;
        mTransactionCache = transactionsCache;
        mAddTransactionView = checkNotNull(addTransactionView,"AddEditTransPresenter:View cannot be null");

        mAddTransactionView.setPresenter(this);
    }

    @Override
    public void saveTransaction(double amount, long timeInMillSecond, int accountId, int descriptionId, String currency) {
        if (isNewTransaction()){
            createTransaction(amount,timeInMillSecond,accountId,
                    descriptionId,currency);
        }else {
            updateTransaction(amount,timeInMillSecond,accountId,descriptionId,currency);
        }
    }

    @Override
    public void populateTransaction() {
        if (isNewTransaction()){
            throw new RuntimeException("populateTransaction was called but transaction is new");
        }
        mTransactionCache.getTransaction(mTransactionId, this);
    }

    @Override
    public void loadPreferenceSettings() {
        //Load default time

        //Load default account

        //Load Default description

        //Load descriptions for caching
    }

    @Override
    public void start() {
        if (!isNewTransaction()) {
            populateTransaction();
        }
    }

    @Override
    public void onTransactionLoaded(Transaction transaction) {
        // The view may not be able to hanlde UI updates anymore
        if (mAddTransactionView.isActive()){
            mAddTransactionView.setAccount(transaction.getAccount());
            mAddTransactionView.setDescription(transaction.getDescription());
            mAddTransactionView.setCurrency(transaction.getCurrencyCode());
            mAddTransactionView.setAmount(transaction.getAmount());
            mAddTransactionView.setTime(transaction.getTimeInMills());
        }
    }

    @Override
    public void onDataNotAvailable() {
        // The view may not be able to handle UI updates anymore
        if (mAddTransactionView.isActive()) {
            mAddTransactionView.showEmptyTransactionError();
        }
    }

    private void createTransaction(double amount, long timeInMillSecond, int accountId,
                                   int descriptionId, String currency) {
        Transaction transaction = new Transaction(amount,timeInMillSecond,accountId,
                descriptionId,currency);
        if (transaction.isEmpty()){
            mAddTransactionView.showEmptyTransactionError();
        } else {
            mTransactionCache.saveTransaction(transaction);
            mAddTransactionView.showTransactionList();
        }
    }

    private void updateTransaction(double amount, long timeInMillSecond, int accountId, int descriptionId, String currency) {
        if (isNewTransaction()){
            throw new RuntimeException("updateTransaction was called but transaction is new");
        }
        Transaction transaction = new Transaction(mTransactionId, amount,timeInMillSecond,
                accountId, descriptionId,currency);
        mTransactionCache.saveTransaction(transaction);
        mAddTransactionView.showTransactionList();//After an edit, go back to the list.
    }

    private boolean isNewTransaction(){return mTransactionId == null;}
}
