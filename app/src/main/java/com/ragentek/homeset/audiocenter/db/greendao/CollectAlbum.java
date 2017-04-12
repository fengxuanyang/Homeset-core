package com.ragentek.homeset.audiocenter.db.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by xuanyang.feng on 2017/3/1.
 */
@Entity
public class CollectAlbum {
    @Id
    private Long id;
    private int albumId;
    private String name;
    private String tag;


    @Generated(hash = 182167066)
    public CollectAlbum(Long id, int albumId, String name, String tag) {
        this.id = id;
        this.albumId = albumId;
        this.name = name;
        this.tag = tag;
    }

    @Generated(hash = 1074311969)
    public CollectAlbum() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
