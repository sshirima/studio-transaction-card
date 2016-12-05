package com.example.tcard.mvp.data;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by sshirima on 9/6/16.
 */
public class Transaction {

    private String mId;
    private double mAmount;
    private long mTimeInMills;
    private int mAccountId;
    private int mDescriptionId;
    private String mCurrencyCode;
    private Account mAccount;
    private Description mDescription;

    public Transaction(){}

    /**
     * For creating new transaction, for the first time to save in DB
     * @param amount
     * @param timeInMills
     * @param accountId
     * @param descId
     * @param currencyCode
     */
    public Transaction(double amount, long timeInMills, int accountId,
                       int descId, String currencyCode){
        this.mId = UUID.randomUUID().toString();
        this.mAmount = amount;
        this.mTimeInMills = timeInMills;
        this.mAccountId = accountId;
        this.mDescriptionId = descId;
        this.mCurrencyCode = currencyCode;
    }

    /**
     * For creating transaction instance by re-triving parameter from DB
     * @param id
     * @param amount
     * @param timeInMills
     * @param accountId
     * @param descId
     * @param currencyCode
     */
    public Transaction(String id, double amount, long timeInMills, int accountId,
                       int descId, String currencyCode){
        this.mId = id;
        this.mAmount = amount;
        this.mTimeInMills = timeInMills;
        this.mAccountId = accountId;
        this.mDescriptionId = descId;
        this.mCurrencyCode = currencyCode;
    }

    public String getId() {
        return mId;
    }

    public String getAmount(){return Double.toString(mAmount);}

    public long getTimeInMills() {
        return mTimeInMills;
    }

    public String getDate(){
        return new SimpleDateFormat("yyy/MM/d",
                Locale.ENGLISH).format(new Date(mTimeInMills));
    }

    public String getTime(){
        return new SimpleDateFormat("HH:mm",
                Locale.ENGLISH).format(new Date(mTimeInMills));
    }

    public Calendar getCalender(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mTimeInMills);
        return calendar;
    }

    public void setDescription(Description description){
        this.mDescription = description;
    }

    public  void setAccount(Account account){
        this.mAccount = account;
    }

    public int getAccountId() {
        return mAccountId;
    }

    public int getDescriptionId() {
        return mDescriptionId;
    }

    public String getCurrencyCode() {
        return mCurrencyCode;
    }

    public Account getAccount() {
        return mAccount;
    }

    public Description getDescription() {
        return mDescription;
    }

    public boolean isEmpty(){
        return (mId == null || "".equals(mId));
    }
}
