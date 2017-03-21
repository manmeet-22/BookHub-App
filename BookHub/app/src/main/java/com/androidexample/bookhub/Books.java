package com.androidexample.bookhub;

/**
 * Created by MANI on 2/6/2017.
 */

public class Books {
    private String mAuthor;
    private String mTitle;
    private String mPublishDate;
    private String mUrl;
    private String mImage;

    public Books(String title, String author, String publishDate, String url, String image) {

        mTitle = title;
        mAuthor = author;
        mPublishDate = publishDate;
        mUrl = url;
        mImage = image;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getPublishDate() {
        return mPublishDate;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getImage() {
        return mImage;
    }
}


