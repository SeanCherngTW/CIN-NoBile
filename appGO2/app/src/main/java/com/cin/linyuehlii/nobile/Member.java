package com.cin.linyuehlii.nobile;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;

public class Member implements Serializable, Parcelable { // // 格視為圖片-字串-字串
    private int id;
    private int image;
    private String packageName;
    private int seekBarProgress = 60;
    private String name;
    private String time;


    public Member(Parcel source) {
        id = source.readInt();
        image = source.readInt();
        packageName = source.readString();
        name = source.readString();
        time = source.readString();
    }

    public Member(String name, String score) {
        this(0, 0, name, score, "NoName");
    }

    public Member(int id, int image, String name, String time) {
        this(id, image, name, time, "NoName");
    }

    public Member(int id, int image, String name, String time, String packageName) {
        super();
        this.id = id;
        this.image = image;
        this.name = name;
        this.time = time;
        this.packageName = packageName;
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

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setSeekBarProgress(int progress) {
        seekBarProgress = progress;
    }

    public int getSeekBarProgress() {
        return seekBarProgress;
    }

    public long getTimeInMilliSecond() {
        return (long) seekBarProgress * 60 * 1000;
    }


    /*******************************
     * 實作Parcelabel自動產生的方法
     *********************/
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Log.d("writeToParcel", "writeToParcel");
        dest.writeInt(id);
        dest.writeInt(image);
        dest.writeString(name);
        dest.writeString(time);
        dest.writeString(packageName);
    }

    /*******************************
     * 實作Parcelabel自動產生的方法
     *********************/


    public static final Parcelable.Creator<Member> CREATOR = new Parcelable.Creator<Member>() {

        /*******************************
         * 實作Parcelabel的CREATOR
         *********************/

        @Override
        public Member createFromParcel(Parcel source) {
            return new Member(source);
        }

        @Override
        public Member[] newArray(int size) {
            return new Member[size];
        }
    };

    /*******************************實作Parcelabel的CREATOR*********************/
}

