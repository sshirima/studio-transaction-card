package com.example.tcard.mvp.data;

/**
 * Created by sshirima on 9/6/16.
 */
public class Description {

    private String mId;
    private String name;
    private String category;

    public Description (String name, String category){
        this.name = name;
        this.category = category;
    }

    public Description (String id, String name, String category){
        this.mId = id;
        this.name = name;
        this.category = category;
    }

    public void setId(String id){
        this.mId=id;
    }

    public String getId(){return mId;}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
