package com.example.tcard.mvp.addedit;


import com.example.tcard.mvp.BasePresenter;
import com.example.tcard.mvp.BaseView;
import com.example.tcard.mvp.data.Account;
import com.example.tcard.mvp.data.Description;

/**
 * Created by sshirima on 10/10/16.
 *
 * This specifies the contract between the view and the presenter
 */
public interface AddEditTransContract {

    interface  View extends BaseView<Presenter>{
        void showEmptyTransactionError();
        void showTransactionList();
        void setAmount(String amount);
        void setTime(long timeInMillSecond);
        void setAccount(Account account);
        void setDescription(Description description);
        void setCurrency(String currencyCode);
        boolean isActive();
    }

    interface Presenter extends BasePresenter{
        void saveTransaction(double amount, long timeInMillSecond, int accountId,
                             int descriptionId, String currency);
        void populateTransaction();

        void loadPreferenceSettings();
    }
}

