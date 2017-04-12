package com.ragentek.homeset.audiocenter.db.greendao;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * Entity mapped to table "download".
 */
@Entity
public class DownloadDBEntity {
    @Id
    private Long id;
    private Long toolSize;
    private Long completedSize;
    @NotNull
    private String url;
    private String saveDirPath;
    private String fileName;
    private String type;
    private int downloadStatus;

    @Generated(hash = 1392479186)
    public DownloadDBEntity(Long id, Long toolSize, Long completedSize,
                            @NotNull String url, String saveDirPath, String fileName, String type,
                            int downloadStatus) {
        this.id = id;
        this.toolSize = toolSize;
        this.completedSize = completedSize;
        this.url = url;
        this.saveDirPath = saveDirPath;
        this.fileName = fileName;
        this.type = type;
        this.downloadStatus = downloadStatus;
    }

    @Generated(hash = 1143139915)
    public DownloadDBEntity() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getToolSize() {
        return this.toolSize;
    }

    public void setToolSize(Long toolSize) {
        this.toolSize = toolSize;
    }

    public Long getCompletedSize() {
        return this.completedSize;
    }

    public void setCompletedSize(Long completedSize) {
        this.completedSize = completedSize;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSaveDirPath() {
        return this.saveDirPath;
    }

    public void setSaveDirPath(String saveDirPath) {
        this.saveDirPath = saveDirPath;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getDownloadStatus() {
        return this.downloadStatus;
    }

    public void setDownloadStatus(int downloadStatus) {
        this.downloadStatus = downloadStatus;
    }


}
