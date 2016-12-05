package com.example.tcard.mvp.display;

/**
 * Created by sshirima on 9/19/16.
 *
 * Used with the filter spinner in the transaction list
 */
public enum TransactionFilterType {
    /**
     * Filter today transactions, from 00:00hrs of the subject day
     */
    TODAY,
    /**
     * Filter Yesterday transactions
     */
    YESTERDAY,
    /**
     * Filter transaction, from the beginning of the week
     */
    WEEK,
    /**
     * Filter transactions, from the begining of the month
     */
    MONTH,
    /**
     * Custom filtering, Prompt to enter start and end date/time
     */
    CUSTOM
}
