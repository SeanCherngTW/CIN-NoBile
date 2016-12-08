package com.cin.linyuehlii.nobile;

import java.io.Serializable;
public class Member_isi implements Serializable { // 格視為圖片-字串-圖片
    private int id;
    private int image;
    private String name;
    private int image2;

    public Member_isi(int id,int image, String name,int image2) {
        super();
        this.id = id;
        this.image = image;
        this.name = name;
        this.image2=image2;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getImage2() {
        return image2;
    }

    public void setImage2(int image2) {
        this.image2 = image2;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
