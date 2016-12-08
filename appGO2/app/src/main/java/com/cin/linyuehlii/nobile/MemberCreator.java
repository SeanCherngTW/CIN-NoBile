package com.cin.linyuehlii.nobile;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Shark on 2016/7/28.
 */
public class MemberCreator implements Parcelable.Creator<Member> {
    @Override
    public Member createFromParcel(Parcel source) {
        return new Member(source);
    }

    @Override
    public Member[] newArray(int size) {
        return new Member[0];
    }
}
