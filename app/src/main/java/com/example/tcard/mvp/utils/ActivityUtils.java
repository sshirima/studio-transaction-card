package com.example.tcard.mvp.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import static com.example.tcard.mvp.Preconditions.checkNotNull;

/**
 * Created by sshirima on 9/23/16.
 *
 * This provide methods to help Activities laod their UI
 */
public class ActivityUtils {

    public static void addFragmentToActivity (FragmentManager fragmentManager, Fragment fragment, int frameId){
        checkNotNull(fragmentManager, "ActivityUtils#addFragmentToActivity:FragmantManager cannot be null");
        checkNotNull(fragment,"ActivityUtils#addFragmentToActivity:Fragment cannot be null");
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(frameId, fragment);
        fragmentTransaction.commit();
    }
}
