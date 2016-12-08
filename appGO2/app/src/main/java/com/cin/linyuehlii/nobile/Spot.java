package com.cin.linyuehlii.nobile;

public class Spot {// 格視為使用者使用時間
    private int id;
    private String day;
    private int fb;
    private int line;
    private int message;

    public Spot(String day, int fb, int line, int message) {
        this(0, day,fb, line, message);
    }

    public Spot(int id, String day, int fb, int line, int message) {
        this.id = id;
        this.day= day;
        this.fb= fb;
        this.line = line;
        this.message = message;

    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDay() {
        return day;
    }

    public void setFb(int fb) {
        this.fb = fb;
    }

    public int getFb() {
        return fb;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getLine() {
        return line;
    }

   // public byte[] getImage() {return image; }

    //public void setImage(byte[] image) {this.image = image;}

    public int getMessage() {
        return message;
    }

    public void setMessage(int message) {
        this.message = message;
    }
}
