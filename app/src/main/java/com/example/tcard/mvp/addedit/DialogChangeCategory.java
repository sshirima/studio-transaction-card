package com.example.tcard.mvp.addedit;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by sshirima on 10/13/16.
 */
public class DialogChangeCategory extends DialogFragment{

    private final static String TXT_TITLE = "Choose Category";
    private CharSequence[] mChoiceList;

    public interface ChangeCategoryListerner{
        void onCategoryChanged(String category);
    }

    ChangeCategoryListerner mChangeCategoryListerner;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(TXT_TITLE);
        builder.setItems(mChoiceList, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int selectedItemPosition) {
                mChangeCategoryListerner.onCategoryChanged(mChoiceList[selectedItemPosition].toString());
            }
        });
        return builder.create();
    }

    public void setCategoryList(CharSequence[] categoryList){
        this.mChoiceList = categoryList;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}
