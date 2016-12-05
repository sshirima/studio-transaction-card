package com.example.tcard.mvp.data.source;

import com.example.tcard.mvp.data.Account;

import java.util.List;

/**
 * Created by sshirima on 9/7/16.
 */
public interface AccountTableInterface {

    interface LoadAccountCallback{
        void onAccountsLoaded(List<Account> accounts);

        void onDataNotAvailable();
    }

    interface GetAccountCallback{
        void onAccountLoaded(Account account);

        void onDataNotAvailable();
    }

    void getAccounts(LoadAccountCallback callback);

    void getAccount(String accountId, GetAccountCallback callback);

    void saveAccount(Account account);

    void refreshAccount();

    void deleteAllAccounts();

    void deleteAccount(String accountId);

}
