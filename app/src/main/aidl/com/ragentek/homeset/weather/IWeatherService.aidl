// IWeatherService.aidl
package com.ragentek.homeset.weather;

// Declare any non-default types here with import statements
import com.ragentek.homeset.weather.IWeatherServiceCB;

interface IWeatherService {
    /*  option:
        OPTION_LOCATION: 0x0001
        OPTION_WEATHER: 0x0002
        OPTION_ALMANAC: 0x0004
        OPTION_LUNAR: 0x0008
        OPTION_ALL: 0X000F

        Example:
        OPTION_LOCATION: only get location data
        OPTION_LOCATION|DATA_WEATHER: get both location and weather data
     */
    void getData(int option, in IWeatherServiceCB callBack);

    void getImmediateData(int option, in IWeatherServiceCB callBack);

    void setCycleUpdateTime(int hours, in IWeatherServiceCB callBack);

    void getCycleUpdateTime(in IWeatherServiceCB callBack);

    void registerNotifyCallback(in IWeatherServiceCB callBack);

    void unRegisterNotifyCallback();
}