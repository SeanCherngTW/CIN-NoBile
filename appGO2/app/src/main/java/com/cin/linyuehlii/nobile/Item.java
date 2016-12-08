package com.cin.linyuehlii.nobile;

import java.io.Serializable;

//用在listview的呈現。暫時沒用到!!!本來有，不過MainActivity的方法不太容易參考過來，就直接在主程式用get
public class Item implements Serializable {
    private String ID;
    private String address;
    private String sub;
    private String snip;

    public Item(String address, String sub, String snip) {
        this("0", address, sub, snip);
    }

    public Item(String ID, String address, String sub, String snip) {
        this.ID = ID;
        this.address = address;
        this.sub = sub;
        this.snip = snip;
    }

    public void setId(String ID) {
        this.ID = ID;
    }

    public String getId() {
        return ID;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getSub() {
        return sub;
    }

    public String getSnip() {
        return snip;
    }

    public void setSnip(String snip) {
        this.snip = snip;
    }
}

