package com.community.protectcommunity;

public class Counselling {
    private String name;
    private String url;

    public Counselling(String name, String url){
        this.name = name;
        this.url = url;
    }

    // getter for name
    public String getName() {
        return name;
    }

    // getter for url
    public String getUrl() {
        return url;
    }
}
