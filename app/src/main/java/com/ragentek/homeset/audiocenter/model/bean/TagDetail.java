package com.ragentek.homeset.audiocenter.model.bean;

import java.io.Serializable;

/**
 * Created by xuanyang.feng on 2017/3/16.
 */

public class TagDetail implements Serializable {
    private String name;
    private int icon;
    private int categoryID;

    public int getRadioType() {
        return radioType;
    }

    public void setRadioType(int radioType) {
        this.radioType = radioType;
    }

    private int radioType;//for radio


    public int getProvince() {
        return province;
    }

    public void setProvince(int province) {
        this.province = province;
    }

    private int province;//for radio

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }


    public String getName() {

        return name;
    }

    public int getIcon() {
        return icon;
    }


}
