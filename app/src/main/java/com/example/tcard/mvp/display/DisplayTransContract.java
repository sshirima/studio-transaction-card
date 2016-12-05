package com.example.tcard.mvp.display;

import com.example.tcard.mvp.BasePresenter;
import com.example.tcard.mvp.BaseView;
import com.example.tcard.mvp.data.Transaction;

import java.util.List;

/**
 * Created by sshirima on 9/19/16.
 */
public interface DisplayTransContract {
    interface View extends BaseView<Presenter>{

        void showTransactions(List<Transaction> transactions);

        void showNoTasks();

        void showAddTransaction();

        void showSuccessfullySavedMessage();

        void setLoadingIndicator(boolean active);

        boolean isActive();
    }
    interface Presenter extends BasePresenter{

        void result(int requestCode, int resultCode);

        void loadTransactions(boolean forceUpdate);

        void addNewTransaction();

        void setFiltering(TransactionFilterType requestType);

        TransactionFilterType getFiltering();

    }
}
