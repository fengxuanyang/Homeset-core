package com.ragentek.homeset.weather.data;

import android.os.Parcel;
import android.os.Parcelable;


public class WeatherLiveData implements Parcelable {
    private String adCode;
    private String city;
    private String humidity;
    private String province;
    private String reportTime;
    private String temperature;
    private String weather;
    private String windDirection;
    private String windPower;

    public String getAdCode() {
        return adCode;
    }

    public void setAdCode(String adCode) {
        this.adCode = adCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getReportTime() {
        return reportTime;
    }

    public void setReportTime(String reportTime) {
        this.reportTime = reportTime;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public String getWindPower() {
        return windPower;
    }

    public void setWindPower(String windPower) {
        this.windPower = windPower;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(adCode);
        dest.writeString(city);
        dest.writeString(humidity);
        dest.writeString(province);
        dest.writeString(reportTime);
        dest.writeString(temperature);
        dest.writeString(weather);
        dest.writeString(windDirection);
        dest.writeString(windPower);
    }

    public static final Parcelable.Creator<WeatherLiveData> CREATOR = new Creator<WeatherLiveData>() {
        @Override
        public WeatherLiveData[] newArray(int size) {
            return new WeatherLiveData[size];
        }

        @Override
        public WeatherLiveData createFromParcel(Parcel source) {
            WeatherLiveData weatherLiveData = new WeatherLiveData();

            String adCode = source.readString();
            weatherLiveData.adCode = adCode;

            String city = source.readString();
            weatherLiveData.city = city;

            String humidity = source.readString();
            weatherLiveData.humidity = humidity;

            String province = source.readString();
            weatherLiveData.province = province;

            String reportTime = source.readString();
            weatherLiveData.reportTime = reportTime;

            String temperature = source.readString();
            weatherLiveData.temperature = temperature;

            String weather = source.readString();
            weatherLiveData.weather = weather;

            String windDirection = source.readString();
            weatherLiveData.windDirection = windDirection;

            String windPower = source.readString();
            weatherLiveData.windPower = windPower;

            return weatherLiveData;
        }
    };

    @Override
    public String toString() {
        return "WeatherLiveData{" +
                "adCode='" + adCode + '\'' +
                ", city='" + city + '\'' +
                ", humidity='" + humidity + '\'' +
                ", province='" + province + '\'' +
                ", reportTime='" + reportTime + '\'' +
                ", temperature='" + temperature + '\'' +
                ", weather='" + weather + '\'' +
                ", windDirection='" + windDirection + '\'' +
                ", windPower='" + windPower + '\'' +
                '}';
    }
}
