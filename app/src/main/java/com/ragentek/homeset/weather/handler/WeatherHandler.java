package com.ragentek.homeset.weather.handler;

import android.content.Context;

import com.amap.api.services.weather.LocalDayWeatherForecast;
import com.amap.api.services.weather.LocalWeatherForecast;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.ragentek.homeset.weather.data.WeatherLiveData;
import com.ragentek.homeset.weather.utils.LogUtils;

import java.util.List;


public class WeatherHandler {
    private static final String TAG = "WeatherHandler";

    private Context mContext;

    public interface WeatherHandlerListener {
        void onGetWeatherResponse(WeatherLiveData data);
    }

    WeatherHandlerListener mListener;

    public WeatherHandler(Context context, WeatherHandlerListener listener) {
        mContext = context;
        mListener = listener;
    }

    public void weather(String city, int type) {
        WeatherThread weatherThread = new WeatherThread(mContext, mListener, city, type);
        weatherThread.start();
    }

    private class WeatherThread extends Thread {
        Context context;
        WeatherHandlerListener listener;
        String city;
        int type;
        WeatherSearchQuery query;
        WeatherSearch search;

        WeatherSearch.OnWeatherSearchListener weatherSearchListener = new WeatherSearch.OnWeatherSearchListener() {
            @Override
            public void onWeatherLiveSearched(LocalWeatherLiveResult localWeatherLiveResult, int rCode) {
                WeatherLiveData weatherLiveData = null;

                if (rCode == 1000) {
                    if (localWeatherLiveResult != null && localWeatherLiveResult.getLiveResult() != null) {
                        LocalWeatherLive weatherLive = localWeatherLiveResult.getLiveResult();

                        weatherLiveData = new WeatherLiveData();
                        weatherLiveData.setAdCode(weatherLive.getAdCode());
                        weatherLiveData.setCity(weatherLive.getCity());
                        weatherLiveData.setHumidity(weatherLive.getHumidity());
                        weatherLiveData.setProvince(weatherLive.getProvince());
                        weatherLiveData.setReportTime(weatherLive.getReportTime());
                        weatherLiveData.setTemperature(weatherLive.getTemperature());
                        weatherLiveData.setWeather(weatherLive.getWeather());
                        weatherLiveData.setWindDirection(weatherLive.getWindDirection());
                        weatherLiveData.setWindPower(weatherLive.getWindPower());
                    } else {
                        LogUtils.e(TAG, "WeatherThread, onWeatherLiveSearched, get empty live result");
                    }
                } else {
                    LogUtils.e(TAG, "WeatherThread, onWeatherLiveSearched, rCode=" + rCode);
                }

                LogUtils.d(TAG, "WeatherThread, call listener=" + listener);
                if (listener != null) {
                    listener.onGetWeatherResponse(weatherLiveData);
                }
            }

            @Override
            public void onWeatherForecastSearched(LocalWeatherForecastResult localWeatherForecastResult, int rCode) {
                if (rCode == 1000) {
                    if (localWeatherForecastResult != null && localWeatherForecastResult.getForecastResult() != null) {
                        LocalWeatherForecast weatherForecast = localWeatherForecastResult.getForecastResult();

                        LogUtils.d(TAG, "WeatherThread, onWeatherForecastSearched, getAdCode=" + weatherForecast.getAdCode()
                                + " getCity=" + weatherForecast.getCity()
                                + " getProvince=" + weatherForecast.getProvince()
                                + " getReportTime=" + weatherForecast.getReportTime());


                        List<LocalDayWeatherForecast> dayWeatherForecastList = weatherForecast.getWeatherForecast();
                        for (int i = 0; i < dayWeatherForecastList.size(); i++) {
                            LogUtils.d(TAG, "Day-" + i
                                    + " getDate=" + dayWeatherForecastList.get(i).getDate()
                                    + " getDayTemp=" + dayWeatherForecastList.get(i).getDayTemp()
                                    + " getDayWeather=" + dayWeatherForecastList.get(i).getDayWeather()
                                    + " getDayWindDirection=" + dayWeatherForecastList.get(i).getDayWindDirection()
                                    + " getDayWindPower=" + dayWeatherForecastList.get(i).getDayWindPower()
                                    + " getNightTemp=" + dayWeatherForecastList.get(i).getNightTemp()
                                    + " getNightWeather=" + dayWeatherForecastList.get(i).getNightWeather()
                                    + " getNightWindDirection=" + dayWeatherForecastList.get(i).getNightWindDirection()
                                    + " getNightWindPower=" + dayWeatherForecastList.get(i).getNightWindPower()
                                    + " getWeek=" + dayWeatherForecastList.get(i).getWeek()
                            );
                        }
                    } else {
                        LogUtils.e(TAG, "WeatherThread, onWeatherForecastSearched, get empty live result");
                    }
                } else {
                    LogUtils.e(TAG, "WeatherThread, onWeatherForecastSearched, rCode=" + rCode);
                }
            }
        };

        public WeatherThread(Context context, WeatherHandlerListener listener, String city, int type) {
            this.context = context;
            this.listener = listener;
            this.city = city;
            this.type = type;
        }

        @Override
        public void run() {
            LogUtils.d(TAG, "WeatherThread, city=" + city + " type=" + type);

            query = new WeatherSearchQuery(city, type);

            search = new WeatherSearch(context);
            search.setOnWeatherSearchListener(weatherSearchListener);
            search.setQuery(query);
            search.searchWeatherAsyn();
        }
    }
}