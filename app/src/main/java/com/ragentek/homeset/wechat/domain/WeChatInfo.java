package com.ragentek.homeset.wechat.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wenjin.wang on 2017/3/29.
 */

public class WeChatInfo implements Parcelable {

    private String userName;
    private String alias;
    private String conRemark;
    private String nickName;
    private String iconUrl;

    public static final Creator<WeChatInfo> CREATOR = new Creator<WeChatInfo>() {
        @Override
        public WeChatInfo createFromParcel(Parcel source) {
            WeChatInfo weChatInfo = new WeChatInfo();

            weChatInfo.setUserName(source.readString());
            weChatInfo.setAlias(source.readString());
            weChatInfo.setConRemark(source.readString());
            weChatInfo.setNickName(source.readString());
            weChatInfo.setIconUrl(source.readString());

            return weChatInfo;
        }

        @Override
        public WeChatInfo[] newArray(int size) {

            return new WeChatInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userName);
        dest.writeString(alias);
        dest.writeString(conRemark);
        dest.writeString(nickName);
        dest.writeString(iconUrl);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getConRemark() {
        return conRemark;
    }

    public void setConRemark(String conRemark) {
        this.conRemark = conRemark;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    @Override
    public String toString() {
        return "WeChatInfo{" +
                "userName='" + userName + '\'' +
                ", alias='" + alias + '\'' +
                ", conRemark='" + conRemark + '\'' +
                ", nickName='" + nickName + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                '}';
    }
}
