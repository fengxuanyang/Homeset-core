package com.ragentek.homeset.audiocenter.model.bean;

/**
 * item  of playlist UI
 * Created by xuanyang.feng on 2017/3/18.
 */

public class PlayListItem<T> {
    T t;
    // PAUSE audio type radio album track
    private int audioType;
    private int categoryType;
    //audio id
    private Long id;
    //0 is not fav ,1 is fav
    private int fav;
    private int group;

    public boolean isNewAdded() {
        return isNewAdded;
    }

    public void setNewAdded(boolean newAdded) {
        isNewAdded = newAdded;
    }

    private boolean isNewAdded;

    public PlayListItem(int audioType, int categoryType, Long id) {
        this.audioType = audioType;
        this.categoryType = categoryType;
        this.id = id;
    }

    public int getFav() {
        return fav;
    }

    public void updateFav() {
        fav = (fav + 1) % 2;
    }

    public void setFav(int fav) {
        this.fav = fav;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }


    public Long getId() {
        return id;
    }


    public int getCategoryType() {
        return categoryType;
    }


    public int getAudioType() {
        return audioType;
    }


    public T getAudio() {
        return t;
    }

    public void setAudio(T t) {
        this.t = t;
    }
}
