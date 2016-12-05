package com.example.tcard.mvp.data.source;

import com.example.tcard.mvp.data.source.local.CurrencyTable;
import com.example.tcard.mvp.data.Currency;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.example.tcard.mvp.Preconditions.checkNotNull;

/**
 * Created by sshirima on 12/5/16.
 */
public class CurrencyCache implements CurrencyTableInterface{
    private static CurrencyCache INSTANCE = null;
    private CurrencyTable mCurrencyTable;

    /**
     *This variable has package local visibility so that it can be accessed from the tests
     */
    Map<String, Currency> mCachedCurrencies;

    /**
     *Mark the cache as invalid , to force an update the next time data is requested. This variable
     * has local package visibility so that it can be accessed from tests
     */
    boolean mCacheIsDirty = false;

    //Prevent direct instantiation
    private CurrencyCache (CurrencyTable descriptionTable){
        if (descriptionTable != null){
            mCurrencyTable = descriptionTable;
        } else {
            throw new NullPointerException("Instantiating descriptioncache:CurrencyTable can not be null");
        }
    }

    /**
     * Return the single instance of this class, creating it if necessary
     * @param CurrencyTable
     * @return the {@link CurrencyCache}
     */
    public static CurrencyCache getInstance(CurrencyTable CurrencyTable){
        if (INSTANCE == null){
            INSTANCE = new CurrencyCache(CurrencyTable);
        }
        return INSTANCE;
    }

    /**
     * Used to force {@link #getInstance(CurrencyTable)} to create new instance
     * next time it is called
     */
    public static void destroyInstance(){INSTANCE = null;}

    /**
     * Get descriptions from cache, local datasource (SQLite) or remote data source, whichever is
     * available first
     * <p>
     *     Note: {@link LoadCurrencyCallBack#onDataNotAvailable()} is fired if all dat sources fails
     *     to get the data
     * </p>
     * @param callback
     */

    @Override
    public void getCurrencies(final LoadCurrencyCallBack callback) {
        checkNotNull(callback,"CurrencyCache#getCurrencys: Callback can not be null");

        //Respond immediately with cache if available and not dirty
        if (mCachedCurrencies != null && !mCacheIsDirty){
            callback.onCurrenciesLoaded(new ArrayList<>(mCachedCurrencies.values()));
        }

        if (mCacheIsDirty){
            //If cache is dirty we need to fetch new data from the network
            mCurrencyTable.getCurrencies(new LoadCurrencyCallBack() {
                @Override
                public void onCurrenciesLoaded(List<Currency> descriptions) {
                    refreshCache(descriptions);
                    callback.onCurrenciesLoaded(new ArrayList<>(mCachedCurrencies.values()));
                }

                @Override
                public void onDataNotAvailable() {

                }
            });
        } else {
            //Qeury the local storage if available, if not query the network.

        }
    }

    private void refreshCache(List<Currency> descriptions) {
        if (mCachedCurrencies == null) {
            mCachedCurrencies = new LinkedHashMap<>();
        }
        mCachedCurrencies.clear();
        for (Currency description: descriptions){
            mCachedCurrencies.put(description.getCurrencyCode(), description);
        }
        mCacheIsDirty = false;
    }

    /**
     * Get descriptions from the local datasource (SQLite) unless the table is new or empty. In that case
     * it uses network datasource. This is done to simplify the sample
     *<p>
     *     Note:{@link GetCurrencyCallback#onDataNotLoaded()} is fired if both datasources fails
     *     to get the data
     *</p>
     * @param currencyCode
     * @param callback
     */
    @Override
    public void getCurrency(String currencyCode, final GetCurrencyCallback callback) {
        checkNotNull(callback,"");
        checkNotNull(currencyCode,"");

        Currency description = getCurrencyWithId(currencyCode);
        //Respond immediately with cache if available
        if (description  != null) {
            callback.onCurrencyLoaded(description);
            return;
        }

        //Load from the server persistence if needed

        //Is the description in the local datasource? If not query the network
        mCurrencyTable.getCurrency(currencyCode, new GetCurrencyCallback() {
            @Override
            public void onCurrencyLoaded(Currency description) {
                callback.onCurrencyLoaded(description);
            }

            @Override
            public void onDataNotLoaded() {

            }
        });
    }

    private Currency getCurrencyWithId(String currencyCode) {
        if (mCachedCurrencies == null || mCachedCurrencies.isEmpty()){
            return null;
        } else{
            return mCachedCurrencies.get(currencyCode);
        }
    }

    @Override
    public void saveCurrency(Currency description) {
        checkNotNull(description,"");

        mCurrencyTable.saveCurrency(description);

        //Do in memory cache update to keep the app UI up to date
        if (mCachedCurrencies == null){
            mCachedCurrencies = new LinkedHashMap<>();
        }

        mCachedCurrencies.put(description.getCurrencyCode(), description);
    }



    @Override
    public void deleteAllCurrencies() {
        mCurrencyTable.deleteAllCurrencies();

        if (mCachedCurrencies == null){
            mCachedCurrencies = new LinkedHashMap<>();
        }
        mCachedCurrencies.clear();
    }

    @Override
    public void deleteCurrency(String currencyCode) {
        mCurrencyTable.deleteCurrency(currencyCode);

        mCachedCurrencies.remove(currencyCode);

    }

    @Override
    public void refreshCurrencies() {

    }
}
