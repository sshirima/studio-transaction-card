package com.example.tcard.mvp.data;

/**
 * Created by sshirima on 12/3/16.
 */
public class Currency {

    String mCurrencyCode;
    String mCurrencyName;
    double mRatetousd;
    long mModifiedDate;

    public Currency (){}

    public Currency (String currencyCode, String currencyName){
        this.mCurrencyCode = currencyCode;
        this.mCurrencyName = currencyName;
    }

    public String getCurrencyCode(){
        return this.mCurrencyCode;
    }

    public String getCurrencyName(){
        return this.mCurrencyName;
    }

    public void setCurrencyCode(String currencyCode){
        this.mCurrencyCode = currencyCode;
    }

    public void setCurrencyName(String currencyName){
        this.mCurrencyName = currencyName;
    }

    public void setRatetousd(double ratetousd){
        this.mRatetousd = ratetousd;
    }

    public void setModifiedDate (long timeInMills){
        this.mModifiedDate = timeInMills;
    }

    public double getRatetoUSD(){
        return this.mRatetousd;
    }

    public long getModifiedDate(){
        return this.mModifiedDate;
    }
}
