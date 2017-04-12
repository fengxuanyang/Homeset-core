package com.ragentek.homeset.weather.data;

import android.os.Parcel;
import android.os.Parcelable;


public class LunarData implements Parcelable {
    private String zodiac;
    private String ganzhi;
    private String year;
    private String month;
    private String day;

    public String getZodiac() {
        return zodiac;
    }

    public void setZodiac(String zodiac) {
        this.zodiac = zodiac;
    }

    public String getGanzhi() {
        return ganzhi;
    }

    public void setGanzhi(String ganzhi) {
        this.ganzhi = ganzhi;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(zodiac);
        dest.writeString(ganzhi);
        dest.writeString(year);
        dest.writeString(month);
        dest.writeString(day);
    }

    public static final Parcelable.Creator<LunarData> CREATOR = new Creator<LunarData>() {
        @Override
        public LunarData[] newArray(int size) {
            return new LunarData[size];
        }

        @Override
        public LunarData createFromParcel(Parcel source) {
            LunarData lunarData = new LunarData();

            String zodiac = source.readString();
            lunarData.zodiac = zodiac;

            String ganzhi = source.readString();
            lunarData.ganzhi = ganzhi;

            String year = source.readString();
            lunarData.year = year;

            String month = source.readString();
            lunarData.month = month;

            String day = source.readString();
            lunarData.day = day;

            return lunarData;
        }
    };

    @Override
    public String toString() {
        return "LunarData{" +
                "zodiac='" + zodiac + '\'' +
                ", ganzhi='" + ganzhi + '\'' +
                ", year='" + year + '\'' +
                ", month='" + month + '\'' +
                ", day='" + day + '\'' +
                '}';
    }
}