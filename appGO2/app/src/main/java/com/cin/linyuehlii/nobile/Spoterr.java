package com.cin.linyuehlii.nobile;

import java.io.Serializable;

public class Spoterr implements Serializable{//我是專門每NewSQL用的型態
    private int id;
    private int image;
    private String appname;
    private String time;


    public Spoterr(int image, String appname, String time) {
        this(0, image,appname, time);
    }

    public Spoterr(int id, int image, String appname, String time) {
        this.id = id;
        this.appname = appname;
        this.time = time;
        this.image = image;

    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getAppname() {
        return appname;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getImage() {
        return image;
    }

}
