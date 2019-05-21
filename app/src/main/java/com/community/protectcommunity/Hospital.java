package com.community.protectcommunity;

public class Hospital {
    private String name;
    //resource id, in drawable
    private String specialty;
    private String url;

    public Hospital(String name, String specialty, String url){
        this.name = name;
        this.specialty = specialty;
        this.url = url;
    }

    // getter for name
    public String getName() {
        return name;
    }

    // getter for specialty
    public String getSpecialty() {
        return specialty;
    }

    //getter for url
    public String getUrl() {
        return url;
    }
}
