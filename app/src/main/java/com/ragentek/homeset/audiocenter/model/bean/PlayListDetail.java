package com.ragentek.homeset.audiocenter.model.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuanyang.feng on 2017/3/18.
 */

public class PlayListDetail {

    private int listType;
    private static int currnIndex;


    private static List<PlayListItem> list = new ArrayList<>();

    public int getListType() {
        return listType;
    }


    public PlayListDetail(int listType, List<PlayListItem> list) {
        this.listType = listType;
        this.list = list;
    }

    public static int getPlayItemCount() {
        return list.size();
    }

    public static List<PlayListItem> getAll() {
        return list;
    }

    public static int getCurrnIndex() {
        return currnIndex;
    }

    public PlayListItem getPlayItem(int position) {
        return list.get(position);
    }


    public void setPlayItem(int position, PlayListItem item) {
        list.set(position, item);
    }

    public void insertPlayItem(int position, PlayListItem item) {
        list.add(position, item);
    }

    public void addtoList(PlayListItem item) {
        list.add(item);
    }

    public void addtoList(List<PlayListItem> item) {
        list.addAll(item);
    }

    public void removeFromList(int location) {
        list.remove(location);
    }

    public void setCurrnIndex(int currnIndex) {
        this.currnIndex = currnIndex;
    }

}
