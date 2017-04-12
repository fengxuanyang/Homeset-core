package com.ragentek.homeset.weather.utils;

public class Contants {
    public static final String TAG = "WeatherService";

    public static final boolean ENABLE_DEBUG = true;

    public static final int OPTION_LOCATION = 0x0001;
    public static final int OPTION_WEATHER = 0x0002;
    public static final int OPTION_ALMANAC = 0x0004;
    public static final int OPTION_LUNAR = 0x0008;
    public static final int OPTION_ALL = 0x000F;

    public static final String SHARED_PREFERENCES_NAME = "weather_service_pref";
    public static final String SP_CYCLE_UPDATE_TIME = "cycle_update_time";

    public static final int DEFAULT_CYCLE_UPDATE_TIME = 1;
}