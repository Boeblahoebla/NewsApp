package com.example.android.newsapp;

import android.graphics.Bitmap;

/**
 * Created by DTPAdmin on 30/04/2018.
 */

public class NewsStory {

    // Private global variables
    private String webTitle, sectionName, webUrl, webPublicationDate, storyByline;
    private Bitmap thumbnailImage;

    // Constructor
    public NewsStory(String title, String nameOfSection, String urlWeb, String dateOfPublication, String bylineStory, Bitmap thumb){
        webTitle = title;
        sectionName = nameOfSection;
        webUrl = urlWeb;
        webPublicationDate = dateOfPublication;
        storyByline = bylineStory;
        thumbnailImage = thumb;
    }

    // getters are listed here
    public String getSectionName() {
        return sectionName;
    }

    public String getWebTitle() {
        return webTitle;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public String getWebPublicationDate(){
        return webPublicationDate;
    }

    public String getStoryByline(){
        return storyByline;
    }

    public Bitmap getThumbnailImage() {
        return thumbnailImage;
    }
}
