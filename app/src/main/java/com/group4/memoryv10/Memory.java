package com.group4.memoryv10;

import android.net.Uri;

import com.google.firebase.database.Exclude;

public class Memory {

    private String userid;
    private String memoURL;
    private String people;
    private String date;
    private String place;
    private String mKey;


    public Memory(){

    }

    public Memory(String userid, String memoURL, String people, String date, String place) {
        this.userid = userid;
        this.memoURL = memoURL;
        this.people = people;
        this.date = date;
        this.place = place;
    }


    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getMemoURL() {
        return memoURL;
    }

    public void setMemoURL(String memoURL) {
        this.memoURL = memoURL;
    }

    public String getPeople() {
        return people;
    }

    public void setPeople(String people) {
        this.people = people;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    @Exclude
    public String getKey() {
        return mKey;
    }

    @Exclude
    public void setKey(String key) {
        mKey = key;
    }

}
