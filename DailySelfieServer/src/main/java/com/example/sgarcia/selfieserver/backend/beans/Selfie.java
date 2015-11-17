package com.example.sgarcia.selfieserver.backend.beans;

import org.springframework.context.annotation.Bean;

/**
 * Created by sgarcia on 11/16/2015.
 */
public class Selfie {
    private String name;
    private int filterType;
    private String encodedImage;

    public Selfie(String name, int filterType, String encodedImage) {
        this.name = name;
        this.filterType = filterType;
        this.encodedImage = encodedImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFilterType() {
        return filterType;
    }

    public void setFilterType(int filterType) {
        this.filterType = filterType;
    }

    public String getEncodedImage() {
        return encodedImage;
    }

    public void setEncodedImage(String encodedImage) {
        this.encodedImage = encodedImage;
    }

    @Override
    public String toString() {
        return "Name:"+name+", filter type:"+filterType+", BODY: "+encodedImage;
    }
}
