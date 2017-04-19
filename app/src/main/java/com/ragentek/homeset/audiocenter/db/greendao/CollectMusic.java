package com.ragentek.homeset.audiocenter.db.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by xuanyang.feng on 2017/3/1.
 */
@Entity
public class CollectMusic {
    @Id
    private Long id;
    private int songId;
    private String name;


    @Generated(hash = 1482944024)
    public CollectMusic(Long id, int songId, String name) {
        this.id = id;
        this.songId = songId;
        this.name = name;
    }

    @Generated(hash = 1084658417)
    public CollectMusic() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
