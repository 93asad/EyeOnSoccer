package com.soccerapp.eyeonsoccer.Model;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by Asad on 1/11/2015. Represents news object
 */
public class News implements Serializable {
    private String mTitle;
    private String mImageLink;
    private String mDate;
    private String mLink;

    /**
     * Constructor to construct news object
     *
     * @param title
     * @param imageLink
     * @param date
     * @param link
     */
    public News(String title, String imageLink, String date, String link) {
        this.mTitle = title;
        this.mImageLink = imageLink;
        this.mDate = date;
        this.mLink = link;
    }

    /**
     * Get link
     *
     * @return
     */
    public String getLink() {
        return mLink;
    }

    /**
     * Get title
     *
     * @return
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Set title
     *
     * @param title
     */
    public void setTitle(String title) {
        this.mTitle = title;
    }

    /**
     * Get image link
     *
     * @return
     */
    public String getImageLink() {
        return mImageLink;
    }

    /**
     * Get date
     *
     * @return
     */
    public String getDate() {
        return mDate;
    }
}
