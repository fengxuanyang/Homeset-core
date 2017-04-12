package com.ragentek.homeset.weather.data;

import android.os.Parcel;
import android.os.Parcelable;


public class AlmanacData implements Parcelable {
    private String yi;
    private String ji;

    public String getYi() {
        return yi;
    }

    public void setYi(String yi) {
        this.yi = yi;
    }

    public String getJi() {
        return ji;
    }

    public void setJi(String ji) {
        this.ji = ji;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(yi);
        dest.writeString(ji);
    }

    public static final Parcelable.Creator<AlmanacData> CREATOR = new Creator<AlmanacData>() {
        @Override
        public AlmanacData[] newArray(int size) {
            return new AlmanacData[size];
        }

        @Override
        public AlmanacData createFromParcel(Parcel source) {
            AlmanacData almanacData = new AlmanacData();

            String yi = source.readString();
            almanacData.yi = yi;

            String ji = source.readString();
            almanacData.ji = ji;

            return almanacData;
        }
    };

    @Override
    public String toString() {
        return "AlmanacData{" +
                "yi='" + yi + '\'' +
                ", ji='" + ji + '\'' +
                '}';
    }
}