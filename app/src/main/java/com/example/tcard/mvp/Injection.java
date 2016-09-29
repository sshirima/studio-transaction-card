package com.example.tcard.mvp;

import android.content.Context;

import com.example.tcard.mvp.data.source.TransactionsCache;
import com.example.tcard.mvp.data.source.local.TransactionTable;

import static com.example.tcard.mvp.Preconditions.checkNotNull;

/**
 * Created by sshirima on 9/20/16.
 */
public class Injection {

    public static TransactionsCache provideTransactionCache(Context context){
        checkNotNull(context, "Injection#provideTransactionCache>Context cannot be null");
        return TransactionsCache.getInstance(TransactionTable.getInstance(context));
    }
}
