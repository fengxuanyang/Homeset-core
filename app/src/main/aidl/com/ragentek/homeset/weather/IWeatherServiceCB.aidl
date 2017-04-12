// IWeatherServiceCB.aidl
package com.ragentek.homeset.weather;

// Declare any non-default types here with import statements
import com.ragentek.homeset.weather.data.AlmanacData;
import com.ragentek.homeset.weather.data.LocationData;
import com.ragentek.homeset.weather.data.LunarData;
import com.ragentek.homeset.weather.data.WeatherLiveData;

interface IWeatherServiceCB {

    void onGetLocation(in LocationData data);

    void onGetWeather(in WeatherLiveData data);

    void onGetAlmanac(in AlmanacData data);

    void onGetLunar(in LunarData data);

    void onSetCycleUpdateTime(int result);

    void onGetCycleUpdateTime(int hours);
}