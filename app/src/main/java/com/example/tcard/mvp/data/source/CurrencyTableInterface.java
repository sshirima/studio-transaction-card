package com.example.tcard.mvp.data.source;

import com.example.tcard.mvp.data.Currency;

import java.util.List;

/**
 * Created by sshirima on 12/3/16.
 */
public interface CurrencyTableInterface {

    interface LoadCurrencyCallBack{
        void onCurrenciesLoaded(List<Currency> currencies);

        void onDataNotAvailable();
    }

    interface GetCurrencyCallback{
        void onCurrencyLoaded(Currency currency);

        void onDataNotLoaded();
    }

    void getCurrencies(LoadCurrencyCallBack callBack);

    void getCurrency(String currencyCode, GetCurrencyCallback callback);

    void saveCurrency(Currency currency);

    void deleteAllCurrencies();

    void deleteCurrency(String currencyCode);

    void refreshCurrencies();

}
