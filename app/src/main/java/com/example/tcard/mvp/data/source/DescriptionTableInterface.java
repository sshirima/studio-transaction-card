package com.example.tcard.mvp.data.source;

import com.example.tcard.mvp.utils.Description;

import java.util.List;

/**
 * Created by sshirima on 9/7/16.
 */
public interface DescriptionTableInterface {

    interface LoadDescriptionsCallback{
        void onDescriptionLoaded(List<Description> descriptions);
        void onDataNotAvailable();
    }

    interface GetDescriptionCallback{
        void onDescriptionLoaded (Description description);
        void onDataNotAvailable();
    }


}
