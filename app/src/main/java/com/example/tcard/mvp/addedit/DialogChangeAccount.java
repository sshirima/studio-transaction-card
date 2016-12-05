package com.example.tcard.mvp.addedit;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.example.tcard.mvp.data.Account;

import java.util.ArrayList;

/**
 * Created by sshirima on 10/13/16.
 */
public class DialogChangeAccount extends DialogFragment {

    private static final String TXT_TITLE = "Choose account:";
    private CharSequence[] mChoiceList;
    private ArrayList<Account> mAccounts;

    public interface ChangeAccountListerner{
        void onAccountChanged(Account account);
    }

    ChangeAccountListerner mChangeAccountListerner;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Get accounts
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(TXT_TITLE);
        builder.setItems(mChoiceList, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int selectedItemPosition) {
                mChangeAccountListerner.onAccountChanged(mAccounts.get(selectedItemPosition));
            }
        });
        return builder.create();
    }

    public void setAccountList(ArrayList<Account> accounts){
        this.mAccounts = accounts;
        mChoiceList = new CharSequence[]{};
        for (int i = 0; i < accounts.size(); i++) {
            mChoiceList[i] = accounts.get(i).getName();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mChangeAccountListerner = (ChangeAccountListerner) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement ChangeAccountListerner");
        }
    }
}
