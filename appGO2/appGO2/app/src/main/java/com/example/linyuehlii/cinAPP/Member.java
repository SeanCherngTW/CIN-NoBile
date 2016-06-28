package com.example.linyuehlii.cinAPP;

/**
 * Created by 大芳鎖定你 on 2016/5/31.
 */
public class Member { // VO- Value Object
    private int id;
    private int image;
    private String name;
    private String time;

    public Member() {
        super();
    }

    public Member(int id,int image, String name,String time) {
        super();
        this.id = id;
        this.image = image;
        this.name = name;
        this.time=time;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
