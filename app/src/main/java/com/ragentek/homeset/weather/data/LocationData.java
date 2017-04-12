package com.ragentek.homeset.weather.data;

import android.os.Parcel;
import android.os.Parcelable;


public class LocationData implements Parcelable {
    private int locationType;
    private double latitude;
    private double longitude;
    private double altitude;
    private float accuracy;
    private String time;
    private String address;
    private String country;
    private String province;
    private String city;
    private String cityCode;
    private String district;
    private String street;
    private String streetNum;
    private String adCode;
    private String aoiName;
    private int errorCode;
    private long lTime;

    public int getLocationType() {
        return locationType;
    }

    public void setLocationType(int locationType) {
        this.locationType = locationType;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreetNum() {
        return streetNum;
    }

    public void setStreetNum(String streetNum) {
        this.streetNum = streetNum;
    }

    public String getAdCode() {
        return adCode;
    }

    public void setAdCode(String adCode) {
        this.adCode = adCode;
    }

    public String getAoiName() {
        return aoiName;
    }

    public void setAoiName(String aoiName) {
        this.aoiName = aoiName;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public long getlTime() {
        return lTime;
    }

    public void setlTime(long lTime) {
        this.lTime = lTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(locationType);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeDouble(altitude);
        dest.writeFloat(accuracy);
        dest.writeString(time);
        dest.writeString(address);
        dest.writeString(country);
        dest.writeString(province);
        dest.writeString(city);
        dest.writeString(cityCode);
        dest.writeString(district);
        dest.writeString(street);
        dest.writeString(streetNum);
        dest.writeString(adCode);
        dest.writeString(aoiName);
        dest.writeInt(errorCode);
        dest.writeLong(lTime);
    }

    public static final Parcelable.Creator<LocationData> CREATOR = new Creator<LocationData>() {
        @Override
        public LocationData[] newArray(int size) {
            return new LocationData[size];
        }

        @Override
        public LocationData createFromParcel(Parcel source) {
            LocationData locationData = new LocationData();

            int locationType = source.readInt();
            locationData.locationType = locationType;

            double latitude = source.readDouble();
            locationData.latitude = latitude;

            double longitude = source.readDouble();
            locationData.longitude = longitude;

            double altitude = source.readDouble();
            locationData.altitude = altitude;

            float accuracy = source.readFloat();
            locationData.accuracy = accuracy;

            String time = source.readString();
            locationData.time = time;

            String address = source.readString();
            locationData.address = address;

            String country = source.readString();
            locationData.country = country;

            String province = source.readString();
            locationData.province = province;

            String city = source.readString();
            locationData.city = city;

            String cityCode = source.readString();
            locationData.cityCode = cityCode;

            String district = source.readString();
            locationData.district = district;

            String street = source.readString();
            locationData.street = street;

            String streetNum = source.readString();
            locationData.streetNum = streetNum;

            String adCode = source.readString();
            locationData.adCode = adCode;

            String aoiName = source.readString();
            locationData.aoiName = aoiName;

            int errorCode = source.readInt();
            locationData.errorCode = errorCode;

            long lTime = source.readLong();
            locationData.lTime = lTime;

            return locationData;
        }
    };

    @Override
    public String toString() {
        return "LocationData{" +
                "locationType=" + locationType +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", altitude=" + altitude +
                ", accuracy=" + accuracy +
                ", time='" + time + '\'' +
                ", address='" + address + '\'' +
                ", country='" + country + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", cityCode='" + cityCode + '\'' +
                ", district='" + district + '\'' +
                ", street='" + street + '\'' +
                ", streetNum='" + streetNum + '\'' +
                ", adCode='" + adCode + '\'' +
                ", aoiName='" + aoiName + '\'' +
                ", errorCode=" + errorCode +
                ", lTime=" + lTime +
                '}';
    }
}