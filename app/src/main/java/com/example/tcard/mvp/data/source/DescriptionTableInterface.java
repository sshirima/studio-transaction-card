package com.example.tcard.mvp.data.source;

import com.example.tcard.mvp.data.Description;

import java.util.List;

/**
 * Created by sshirima on 9/7/16.
 */
public interface DescriptionTableInterface {

    interface LoadDescriptionsCallback{

        void onDescriptionsLoaded(List<Description> descriptions);

        void onDataNotAvailable();
    }

    interface GetDescriptionCallback{
        void onDescriptionLoaded (Description description);
        void onDataNotAvailable();
    }

    void getDescriptions(LoadDescriptionsCallback callback);

    void getDescription(String descriptionId, GetDescriptionCallback callback);

    void saveDescription(Description description);

    void refreshDescriptions();

    void deleteAllDescriptions();

    void deleteDescription(String descriptionId);
}
