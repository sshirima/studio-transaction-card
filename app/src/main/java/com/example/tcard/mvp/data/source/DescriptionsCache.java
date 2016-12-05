package com.example.tcard.mvp.data.source;

import com.example.tcard.mvp.data.source.local.DescriptionTable;
import com.example.tcard.mvp.data.Description;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.example.tcard.mvp.Preconditions.checkNotNull;

/**
 * Created by sshirima on 12/5/16.
 */
public class DescriptionsCache implements DescriptionTableInterface{
    private static DescriptionsCache INSTANCE = null;
    private DescriptionTable mDescriptionTable;

    /**
     *This variable has package local visibility so that it can be accessed from the tests
     */
    Map<String, Description> mCachedDescriptions;

    /**
     *Mark the cache as invalid , to force an update the next time data is requested. This variable
     * has local package visibility so that it can be accessed from tests
     */
    boolean mCacheIsDirty = false;

    //Prevent direct instantiation
    private DescriptionsCache (DescriptionTable descriptionTable){
        if (descriptionTable != null){
            mDescriptionTable = descriptionTable;
        } else {
            throw new NullPointerException("Instantiating descriptioncache:DescriptionTable can not be null");
        }
    }

    /**
     * Return the single instance of this class, creating it if necessary
     * @param descriptionTable
     * @return the {@link DescriptionsCache}
     */
    public static DescriptionsCache getInstance(DescriptionTable descriptionTable){
        if (INSTANCE == null){
            INSTANCE = new DescriptionsCache(descriptionTable);
        }
        return INSTANCE;
    }

    /**
     * Used to force {@link #getInstance(DescriptionTable)} to create new instance
     * next time it is called
     */
    public static void destroyInstance(){INSTANCE = null;}

    /**
     * Get descriptions from cache, local datasource (SQLite) or remote data source, whichever is
     * available first
     * <p>
     *     Note: {@link LoadDescriptionsCallback#onDataNotAvailable()} is fired if all dat sources fails
     *     to get the data
     * </p>
     * @param callback
     */

    @Override
    public void getDescriptions(final LoadDescriptionsCallback callback) {
        checkNotNull(callback,"DescriptionsCache#getDescriptions: Callback can not be null");

        //Respond immediately with cache if available and not dirty
        if (mCachedDescriptions != null && !mCacheIsDirty){
            callback.onDescriptionsLoaded(new ArrayList<>(mCachedDescriptions.values()));
        }

        if (mCacheIsDirty){
            //If cache is dirty we need to fetch new data from the network
            mDescriptionTable.getDescriptions(new LoadDescriptionsCallback() {

                @Override
                public void onDescriptionsLoaded(List<Description> descriptions) {
                    refreshCache(descriptions);
                    callback.onDescriptionsLoaded(new ArrayList<>(mCachedDescriptions.values()));
                }

                @Override
                public void onDataNotAvailable() {

                }
            });
        } else {
            //Qeury the local storage if available, if not query the network.

        }
    }

    private void refreshCache(List<Description> descriptions) {
        if (mCachedDescriptions == null) {
            mCachedDescriptions = new LinkedHashMap<>();
        }
        mCachedDescriptions.clear();
        for (Description description: descriptions){
            mCachedDescriptions.put(description.getId(), description);
        }
        mCacheIsDirty = false;
    }

    /**
     * Get descriptions from the local datasource (SQLite) unless the table is new or empty. In that case
     * it uses network datasource. This is done to simplify the sample
     *<p>
     *     Note:{@link GetDescriptionCallback#onDataNotAvailable()} is fired if both datasources fails
     *     to get the data
     *</p>
     * @param descriptionId
     * @param callback
     */
    @Override
    public void getDescription(String descriptionId, final GetDescriptionCallback callback) {
        checkNotNull(callback,"");
        checkNotNull(descriptionId,"");

        Description description = getDescriptionWithId(descriptionId);
        //Respond immediately with cache if available
        if (description  != null) {
            callback.onDescriptionLoaded(description);
            return;
        }

        //Load from the server persistence if needed

        //Is the description in the local datasource? If not query the network
        mDescriptionTable.getDescription(descriptionId, new GetDescriptionCallback() {
            @Override
            public void onDescriptionLoaded(Description description) {
                callback.onDescriptionLoaded(description);
            }

            @Override
            public void onDataNotAvailable() {
                //Query remote data source
            }
        });
    }

    private Description getDescriptionWithId(String descriptionId) {
        if (mCachedDescriptions == null || mCachedDescriptions.isEmpty()){
            return null;
        } else{
            return mCachedDescriptions.get(descriptionId);
        }
    }

    @Override
    public void saveDescription(Description description) {
        checkNotNull(description,"");

        mDescriptionTable.saveDescription(description);

        //Do in memory cache update to keep the app UI up to date
        if (mCachedDescriptions == null){
            mCachedDescriptions = new LinkedHashMap<>();
        }

        mCachedDescriptions.put(description.getId(), description);
    }

    @Override
    public void refreshDescriptions() {

    }

    @Override
    public void deleteAllDescriptions() {
        mDescriptionTable.deleteAllDescriptions();

        if (mCachedDescriptions == null){
            mCachedDescriptions = new LinkedHashMap<>();
        }
        mCachedDescriptions.clear();
    }

    @Override
    public void deleteDescription(String descriptionId) {
        mDescriptionTable.deleteDescription(descriptionId);

        mCachedDescriptions.remove(descriptionId);

    }
}
