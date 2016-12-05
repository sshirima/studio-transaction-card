package com.example.tcard.mvp.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.tcard.mvp.data.source.DescriptionTableInterface;
import com.example.tcard.mvp.data.Description;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sshirima on 12/3/16.
 */
public class DescriptionTable implements DescriptionTableInterface{

    private static DescriptionTable mDescriptionTable;
    private SQLiteDatabase mDatabase;
    private TcardDB mTcardDB;

    //Prevent direct instantiation
    public DescriptionTable(Context  context){
        if (context != null){
            this.mTcardDB = new TcardDB(context);
        }else {
            throw new NullPointerException("instantiate DescriptionTable: AppContext cannot be null");
        }
    }

    public static DescriptionTable getInstance(Context context){
        if (mDescriptionTable == null){
            mDescriptionTable = new DescriptionTable(context);
            return mDescriptionTable;
        }else {
            return mDescriptionTable;
        }
    }
    public void open() throws SQLException {
        this.mDatabase = mTcardDB.getWritableDatabase();
    }

    public void close(){
        mTcardDB.close();
    }

    /**
     * @param callback
     * Note: {@link LoadDescriptionsCallback#onDataNotAvailable()} is fired if the database doesnot exist
     * or the table is just empty
     */
    @Override
    public void getDescriptions(LoadDescriptionsCallback callback) {
        List<Description> descriptions = new ArrayList<>();

        String[] returnColumns= {DBConsts.DESCRIPTION_ID, DBConsts.DESCRIPTION_NAME, DBConsts.DESCRIPTION_CATEGORY};

        Cursor cursor = mDatabase.query(DBConsts.DESCRIPTION_TABLE, returnColumns, null, null, null,null, null);

        if (cursor != null && cursor.getCount()>0){
            while(cursor.moveToNext()){
                String descId = cursor.getString(cursor.getColumnIndexOrThrow(DBConsts.DESCRIPTION_ID));
                String descCategory = cursor.getString(cursor.getColumnIndexOrThrow(DBConsts.DESCRIPTION_CATEGORY));
                String descName = cursor.getString(cursor.getColumnIndexOrThrow(DBConsts.DESCRIPTION_NAME));

                Description description = new Description(descId,descName, descCategory);
                descriptions.add(description);
            }
        }

        if (cursor != null){
            cursor.close();
        }

        if (descriptions.isEmpty()){
            //This will be called if the table is empty or just new
            callback.onDataNotAvailable();
        }else {
            callback.onDescriptionsLoaded(descriptions);
        }
    }

    /**
     *
     * @param descriptionId
     * @param callback
     *Note: {@link LoadDescriptionsCallback#onDataNotAvailable()} will be fired if {@link Description} isnot found
     *
     */
    @Override
    public void getDescription(String descriptionId, GetDescriptionCallback callback) {
        String[] returnColumns = {DBConsts.DESCRIPTION_ID, DBConsts.DESCRIPTION_NAME, DBConsts.DESCRIPTION_CATEGORY};

        String selection = DBConsts.DESCRIPTION_ID + " LIKE ?";
        String[] selectionsArgs = {descriptionId};

        Cursor cursor = mDatabase.query(DBConsts.DESCRIPTION_TABLE,returnColumns ,selection, selectionsArgs, null, null, null);

        Description description = null;
        if (cursor != null && cursor.getCount()>0){
            cursor.moveToFirst();
            String descId = cursor.getString(cursor.getColumnIndexOrThrow(DBConsts.DESCRIPTION_ID));
            String descCategory = cursor.getString(cursor.getColumnIndexOrThrow(DBConsts.DESCRIPTION_CATEGORY));
            String descName = cursor.getString(cursor.getColumnIndexOrThrow(DBConsts.DESCRIPTION_NAME));

            description = new Description(descId,descName, descCategory);
        }

        if (cursor != null){
            cursor.close();
        }

        if (description != null){
            callback.onDescriptionLoaded(description);
        } else {
            callback.onDataNotAvailable();
        }

    }

    @Override
    public void saveDescription(Description description) {
        ContentValues cv = new ContentValues();

        cv.put(DBConsts.DESCRIPTION_NAME, description.getName());
        cv.put(DBConsts.DESCRIPTION_CATEGORY, description.getCategory());

        mDatabase.insert(DBConsts.DESCRIPTION_TABLE, null, cv);

    }

    @Override
    public void refreshDescriptions() {
        //Not required for the local data source the descriptions cache handles all the logic
    }

    @Override
    public void deleteAllDescriptions() {
        mDatabase.delete(DBConsts.DESCRIPTION_TABLE, null, null);
    }

    @Override
    public void deleteDescription(String descriptionId) {
        String selection = DBConsts.DESCRIPTION_ID + " LIKE ?";
        String[] selectionArgs = {descriptionId};

        mDatabase.delete(DBConsts.DESCRIPTION_TABLE, selection, selectionArgs);
    }
}
