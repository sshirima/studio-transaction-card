package com.example.tcard.mvp.data.source;

import com.example.tcard.mvp.data.source.local.AccountTable;
import com.example.tcard.mvp.data.Account;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.example.tcard.mvp.Preconditions.checkNotNull;

/**
 * Created by sshirima on 12/5/16.
 */
public class AccountsCache implements AccountTableInterface{

    private static AccountsCache INSTANCE = null;
    private AccountTable mAccountTable;

    /**
     *This variable has package local visibility so that it can be accessed from the tests
     */
    Map<String, Account> mCachedAccounts;

    /**
     *Mark the cache as invalid , to force an update the next time data is requested. This variable
     * has local package visibility so that it can be accessed from tests
     */
    boolean mCacheIsDirty = false;

    //Prevent direct instantiation
    private AccountsCache (AccountTable accountTable){
        if (accountTable != null){
            mAccountTable = accountTable;
        } else {
            throw new NullPointerException("Instantiating accountcache:AccountTable can not be null");
        }
    }

    /**
     * Return the single instance of this class, creating it if necessary
     * @param accountTable
     * @return the {@link AccountsCache}
     */
    public static AccountsCache getInstance(AccountTable accountTable){
        if (INSTANCE == null){
            INSTANCE = new AccountsCache(accountTable);
        }
        return INSTANCE;
    }

    /**
     * Used to force {@link #getInstance(AccountTable)} to create new instance
     * next time it is called
     */
    public static void destroyInstance(){INSTANCE = null;}

    /**
     * Get accounts from cache, local datasource (SQLite) or remote data source, whichever is
     * available first
     * <p>
     *     Note: {@link LoadAccountCallback#onDataNotAvailable()} is fired if all dat sources fails
     *     to get the data
     * </p>
     * @param callback
     */

    @Override
    public void getAccounts(final LoadAccountCallback callback) {
        checkNotNull(callback,"AccountsCache#getAccounts: Callback can not be null");

        //Respond immediately with cache if available and not dirty
        if (mCachedAccounts != null && !mCacheIsDirty){
            callback.onAccountsLoaded(new ArrayList<>(mCachedAccounts.values()));
        }

        if (mCacheIsDirty){
            //If cache is dirty we need to fetch new data from the network
            mAccountTable.getAccounts(new LoadAccountCallback() {
                @Override
                public void onAccountsLoaded(List<Account> accounts) {
                    refreshCache(accounts);
                    callback.onAccountsLoaded(new ArrayList<>(mCachedAccounts.values()));
                }

                @Override
                public void onDataNotAvailable() {

                }
            });
        } else {
            //Qeury the local storage if available, if not query the network.

        }
    }

    private void refreshCache(List<Account> accounts) {
        if (mCachedAccounts == null) {
            mCachedAccounts = new LinkedHashMap<>();
        }
        mCachedAccounts.clear();
        for (Account account: accounts){
            mCachedAccounts.put(account.getId(), account);
        }
        mCacheIsDirty = false;
    }

    /**
     * Get accounts from the local datasource (SQLite) unless the table is new or empty. In that case
     * it uses network datasource. This is done to simplify the sample
     *<p>
     *     Note:{@link LoadAccountCallback#onDataNotAvailable()} is fired if both datasources fails
     *     to get the data
     *</p>
     * @param accountId
     * @param callback
     */
    @Override
    public void getAccount(String accountId, final GetAccountCallback callback) {
        checkNotNull(callback,"");
        checkNotNull(accountId,"");

        Account account = getAccountWithId(accountId);
        //Respond immediately with cache if available
        if (account  != null) {
            callback.onAccountLoaded(account);
            return;
        }

        //Load from the server persistence if needed

        //Is the account in the local datasource? If not query the network
        mAccountTable.getAccount(accountId, new GetAccountCallback() {
            @Override
            public void onAccountLoaded(Account account) {
                callback.onAccountLoaded(account);
            }

            @Override
            public void onDataNotAvailable() {
                //Query remote data source
            }
        });
    }

    private Account getAccountWithId(String accountId) {
        if (mCachedAccounts == null || mCachedAccounts.isEmpty()){
            return null;
        } else{
            return mCachedAccounts.get(accountId);
        }
    }

    @Override
    public void saveAccount(Account account) {
        checkNotNull(account,"");

        mAccountTable.saveAccount(account);

        //Do in memory cache update to keep the app UI up to date
        if (mCachedAccounts == null){
            mCachedAccounts = new LinkedHashMap<>();
        }

        mCachedAccounts.put(account.getId(), account);
    }

    @Override
    public void refreshAccount() {

    }

    @Override
    public void deleteAllAccounts() {
        mAccountTable.deleteAllAccounts();

        if (mCachedAccounts == null){
            mCachedAccounts = new LinkedHashMap<>();
        }
        mCachedAccounts.clear();
    }

    @Override
    public void deleteAccount(String accountId) {
        mAccountTable.deleteAccount(accountId);

        mCachedAccounts.remove(accountId);

    }
}
