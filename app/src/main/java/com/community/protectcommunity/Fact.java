package com.community.protectcommunity;

public class Fact {
    private String name;
    //resource id, in drawable
    private int imageId;

    public Fact(String name, int imageId){
        this.name = name;
        this.imageId = imageId;
    }

    // getter for name
    public String getName() {
        return name;
    }

    // getter for image
    public int getImageId() {
        return imageId;
    }
}
