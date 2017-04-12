package com.ragentek.homeset.audiocenter.model.bean;

import java.io.Serializable;

/**
 * Created by xuanyang.feng on 2017/3/16.
 */

public class CategoryDetail implements Serializable {
    private int id = -1; //default is fav
    private String action;
    private String name;
    private int icon;
    private int size = 1;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public void setSize(int size) {
        this.size = size;
    }


    public String getAction() {
        return action;
    }

    public String getName() {
        return name;
    }

    public int getIcon() {
        return icon;
    }

    public int getSize() {
        return size;
    }

    public String toString() {
        return "name:" + name + ",id" + id;
    }

}
