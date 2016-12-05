package com.example.tcard.mvp.data;

/**
 * Created by sshirima on 9/6/16.
 */
public class Account {

    private String mId;
    private String mName;

    public Account (String name){
        this.mName = name;
    }

    public Account (String id, String name){
        this.mId= id;
        this.mName = name;
    }

    public String getName(){return this.mName;}

    public void setName(String name){this.mName= name;}

    public String getId() {
        return mId;
    }

    public void setId(String mId) {
        this.mId = mId;
    }
}
