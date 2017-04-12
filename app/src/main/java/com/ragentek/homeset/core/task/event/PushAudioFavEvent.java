package com.ragentek.homeset.core.task.event;

import com.ragentek.protocol.commons.audio.FavoriteVO;

public class PushAudioFavEvent {
    //action: 1-Add favorite  0-Delete favorite
    private int action;
    private FavoriteVO favoriteVO;

    public PushAudioFavEvent() {
    }

    public PushAudioFavEvent(int action, FavoriteVO favoriteVO) {
        this.action = action;
        this.favoriteVO = favoriteVO;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public FavoriteVO getFavoriteVO() {
        return favoriteVO;
    }

    public void setFavoriteVO(FavoriteVO favoriteVO) {
        this.favoriteVO = favoriteVO;
    }

    @Override
    public String toString() {
        return "PushAudioFavEvent{" +
                "action=" + action +
                ", favoriteVO=" + favoriteVO +
                '}';
    }
}
