package com.ragentek.homeset.audiocenter.model.bean;

import com.ragentek.protocol.commons.audio.AlbumVO;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xuanyang.feng on 2017/3/16.
 */

public class AlbumsResultMsg implements Serializable {
    //{"res_code":0,"res_msg":"total_count":100, "total_page":5, "current_page":0, "albums":[AlbumVO, AlbumVO]}

    private List<AlbumVO> albums;
    private int total_count;
    private int total_page;
    private int current_page;


    public List<AlbumVO> getAlbums() {
        return albums;
    }

    public void setAlbums(List<AlbumVO> albums) {
        this.albums = albums;
    }

    public int getTotal_count() {
        return total_count;
    }

    public void setTotal_count(int total_count) {
        this.total_count = total_count;
    }

    public int getTotal_page() {
        return total_page;
    }

    public void setTotal_page(int total_page) {
        this.total_page = total_page;
    }

    public int getCurrent_page() {
        return current_page;
    }

    public void setCurrent_page(int current_page) {
        this.current_page = current_page;
    }
}
