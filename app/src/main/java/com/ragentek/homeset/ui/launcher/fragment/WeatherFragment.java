package com.ragentek.homeset.ui.launcher.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ragentek.homeset.core.R;
import com.ragentek.homeset.ui.utils.LogUtils;
import com.ragentek.homeset.weather.data.AlmanacData;
import com.ragentek.homeset.weather.data.LunarData;
import com.ragentek.homeset.weather.data.WeatherLiveData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class WeatherFragment extends Fragment {
    private static final String TAG = "WeatherFragment";

    private static final int MAX_ALMANAC_ITEM_SHOWN = 4;

    private TimeChangeReceiver mReceiver;

    TextView mTemperatureView, mWeatherTextView, mUpdateTimeView, mHumidityView, mWindView;
    TextView mLunarView, mAlmanacYiView, mAlmanacJiView;
    TextView mHour, mMinute, mDate;
    ImageView mWeatherImgView;

    private class TimeChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.d(TAG, "TimeChangeReceiver");
            updateTime();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);

        mTemperatureView = (TextView) view.findViewById(R.id.temperature);
        mWeatherTextView = (TextView) view.findViewById(R.id.weather_text);
        mUpdateTimeView = (TextView) view.findViewById(R.id.update_time);
        mHumidityView = (TextView) view.findViewById(R.id.humidity_text);
        mWindView = (TextView) view.findViewById(R.id.wind_text);

        mLunarView = (TextView) view.findViewById(R.id.lunar);
        mAlmanacYiView = (TextView) view.findViewById(R.id.almanac_yi);
        mAlmanacJiView = (TextView) view.findViewById(R.id.almanac_ji);

        mHour = (TextView) view.findViewById(R.id.hour);
        mMinute = (TextView) view.findViewById(R.id.minute);
        mDate = (TextView) view.findViewById(R.id.date);

        mWeatherImgView = (ImageView) view.findViewById(R.id.weather_img);

        mReceiver = new TimeChangeReceiver();

        LogUtils.d(TAG, "onCreateView");

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.d(TAG, "onCreate");
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtils.d(TAG, "onStart");
        EventBus.getDefault().register(this);

    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtils.d(TAG, "onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.d(TAG, "onResume");

        doRegisterReceiver();


        updateDate();
        updateTime();
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtils.d(TAG, "onStop");

        doUnRegisterReceiver();

        EventBus.getDefault().unregister(this);
    }

    private void updateDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
        Date curDate = new Date(System.currentTimeMillis());
        String date = formatter.format(curDate);

        String dayOfWeek;
        Calendar c = Calendar.getInstance();
        int i = c.get(Calendar.DAY_OF_WEEK);

        LogUtils.e(TAG, "updateDate, i=" + i);

        switch (i) {
            case Calendar.SUNDAY:
                dayOfWeek = "星期日";
                break;
            case Calendar.MONDAY:
                dayOfWeek = "星期一";
                break;
            case Calendar.TUESDAY:
                dayOfWeek = "星期二";
                break;
            case Calendar.WEDNESDAY:
                dayOfWeek = "星期三";
                break;
            case Calendar.THURSDAY:
                dayOfWeek = "星期四";
                break;
            case Calendar.FRIDAY:
                dayOfWeek = "星期五";
                break;
            case Calendar.SATURDAY:
                dayOfWeek = "星期六";
                break;
            default:
                dayOfWeek = "";
        }

        date += " " + dayOfWeek;

        LogUtils.d(TAG, "updateDate, date=" + date);
        mDate.setText(date);
    }

    private void updateTime() {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        String sMinute;
        if (minute < 10) {
            sMinute = "0" + minute;
        } else {
            sMinute = String.valueOf(minute);
        }

        LogUtils.d(TAG, "updateTime, hour=" + hour + " minute=" + minute);
        mHour.setText(String.valueOf(hour));
        mMinute.setText(sMinute);
    }


    private void updateWeather(WeatherLiveData data) {
        mTemperatureView.setText(data.getTemperature());

        mWeatherTextView.setText(data.getWeather());

        String updateTime = data.getReportTime() + "更新";
        mUpdateTimeView.setText(updateTime);

        String humidity = "湿度 " + data.getHumidity() + "%";
        mHumidityView.setText(humidity);

        String wind = data.getWindDirection() + "风" + " " + data.getWindPower() + "级";
        mWindView.setText(wind);

        int id = getWeatherImgResId(data.getWeather());
        mWeatherImgView.setImageResource(id);
    }

    private void updateLunar(LunarData data) {
        String lunar = data.getGanzhi() + "年[" + data.getZodiac() + "年]" + "  农历 " + data.getMonth() + "月" + data.getDay();
        mLunarView.setText(lunar);
    }

    private void updateAlmanac(AlmanacData data) {
        String yi = data.getYi(), ji = data.getJi();
        String newYi, newJi;
        int i, j;

        for (i = 0, j = 0; i < yi.length(); i++) {
            if (yi.charAt(i) == ' ') {
                j++;
            }
            if (j == MAX_ALMANAC_ITEM_SHOWN) {
                break;
            }
        }
        if (j == MAX_ALMANAC_ITEM_SHOWN) {
            yi = yi.substring(0, i);
        }
        newYi = yi.replaceAll(" ", "\n");
        mAlmanacYiView.setText(newYi);

        for (i = 0, j = 0; i < ji.length(); i++) {
            if (ji.charAt(i) == ' ') {
                j++;
            }
            if (j == MAX_ALMANAC_ITEM_SHOWN) {
                break;
            }
        }
        if (j == MAX_ALMANAC_ITEM_SHOWN) {
            ji = ji.substring(0, i);
        }
        newJi = ji.replaceAll(" ", "\n");
        mAlmanacJiView.setText(newJi);
    }

    private int getWeatherImgResId(String weather) {
        int id = R.drawable.w_0;
        if (weather.equals("晴")) {
            id = R.drawable.w_qing;
        } else if (weather.equals("多云")) {
            id = R.drawable.w_duoyun;
        } else if (weather.equals("阴")) {
            id = R.drawable.w_yin;
        }else if (weather.equals("阵雨")) {
            id = R.drawable.w_zhengyu;
        }else if (weather.equals("雷阵雨")) {
            id = R.drawable.w_lzy;
        }else if (weather.equals("雷阵雨并伴有冰雹")) {
            id = R.drawable.w_lzy;
        }else if (weather.equals("雨夹雪")) {
            id = R.drawable.w_yjx;
        }else if (weather.equals("小雨")) {
            id = R.drawable.w_xiaoyu;
        }else if (weather.equals("中雨")) {
            id = R.drawable.w_zhongyu;
        }else if (weather.equals("大雨")) {
            id = R.drawable.w_dayu;
        }else if (weather.equals("暴雨")) {
            id = R.drawable.w_baoyu;
        }else if (weather.equals("大暴雨")) {
            id = R.drawable.w_dby;
        }else if (weather.equals("特大暴雨")) {
            id = R.drawable.w_tdby;
        }else if (weather.equals("阵雪")) {
            id = R.drawable.w_zhengxue;
        }else if (weather.equals("小雪")) {
            id = R.drawable.w_xiaoxue;
        }else if (weather.equals("中雪")) {
            id = R.drawable.w_daxue;
        }else if (weather.equals("大雪")) {
            id = R.drawable.w_daxue;
        }else if (weather.equals("暴雪")) {
            id = R.drawable.w_baoxue;
        }else if (weather.equals("雾")) {
            id = R.drawable.w_wu;
        }else if (weather.equals("冻雨")) {
            id = R.drawable.w_zhongyu;
        }else if (weather.equals("沙尘暴")) {
            id = R.drawable.w_scb;
        }else if (weather.equals("小雨-中雨")) {
            id = R.drawable.w_xyzy;
        }else if (weather.equals("中雨-大雨")) {
            id = R.drawable.w_zydy;
        }else if (weather.equals("大雨-暴雨")) {
            id = R.drawable.w_dyby;
        }else if (weather.equals("暴雨-大暴雨")) {
            id = R.drawable.w_bydby;
        }else if (weather.equals("大暴雨-特大暴雨")) {
            id = R.drawable.w_dbytdby;
        }else if (weather.equals("小雪-中雪")) {
            id = R.drawable.w_xxzx;
        }else if (weather.equals("中雪-大雪")) {
            id = R.drawable.w_zxdx;
        }else if (weather.equals("大雪-暴雪")) {
            id = R.drawable.w_dxbx;
        }else if (weather.equals("浮尘")) {
            id = R.drawable.w_fuchen;
        }else if (weather.equals("扬沙")) {
            id = R.drawable.w_yangchen;
        }else if (weather.equals("强沙尘暴")) {
            id = R.drawable.w_qscb;
        }else if (weather.equals("飑")) {
            id = R.drawable.w_biao;
        }else if (weather.equals("龙卷风")) {
            id = R.drawable.w_ljf;
        }else if (weather.equals("弱高吹雪")) {
            id = R.drawable.w_lgcx;
        }else if (weather.equals("轻霾")) {
            id = R.drawable.w_qingmai;
        }else if (weather.equals("霾")) {
            id = R.drawable.w_mai;
        }

        return id;
    }

    private void doRegisterReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        getActivity().registerReceiver(mReceiver, filter);
    }

    private void doUnRegisterReceiver() {
        try {
            getActivity().unregisterReceiver(mReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onEventWeatherLiveData(WeatherLiveData data) {
        LogUtils.d(TAG, "onEventWeatherLiveData, data=" + data.toString());
        updateWeather(data);
    }

    @Subscribe
    public void onEventLunarData(LunarData data) {
        updateLunar(data);
    }

    @Subscribe
    public void onEventAlmanacData(AlmanacData data) {
        LogUtils.d(TAG, "onEventAlmanacData, data=" + data.toString());
        updateAlmanac(data);
    }
}