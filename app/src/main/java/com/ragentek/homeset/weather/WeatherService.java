package com.ragentek.homeset.weather;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;

import com.amap.api.services.weather.WeatherSearchQuery;
import com.ragentek.homeset.core.net.http.HttpManager;
import com.ragentek.homeset.core.net.http.api.LocationApi;
import com.ragentek.homeset.core.utils.DeviceUtils;
import com.ragentek.homeset.weather.data.AlmanacData;
import com.ragentek.homeset.weather.data.LocationData;
import com.ragentek.homeset.weather.data.LunarData;
import com.ragentek.homeset.weather.data.WeatherLiveData;
import com.ragentek.homeset.weather.handler.AlmanacHandler;
import com.ragentek.homeset.weather.handler.LocationHandler;
import com.ragentek.homeset.weather.handler.LunarHandler;
import com.ragentek.homeset.weather.handler.WeatherHandler;
import com.ragentek.homeset.weather.utils.Contants;
import com.ragentek.homeset.weather.utils.LogUtils;
import com.ragentek.protocol.commons.lbs.LocationVO;
import com.ragentek.protocol.messages.http.APIResultVO;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import rx.Subscriber;
import rx.schedulers.Schedulers;


public class WeatherService extends Service
        implements AlmanacHandler.AlmanacHandlerListener, LocationHandler.LocationHandlerListener, WeatherHandler.WeatherHandlerListener {
    private static final String TAG = "WeatherService";

    private static final int MSG_BASE = 1000;
    private static final int MSG_GET_DATA = MSG_BASE + 1;
    private static final int MSG_GET_IMMEDIATE_DATA = MSG_BASE + 2;
    private static final int MSG_SET_CYCLE_UPDATE_TIME = MSG_BASE + 3;
    private static final int MSG_GET_CYCLE_UPDATE_TIME = MSG_BASE + 4;

    private Context mContext;
    private MyHandler mHandler = null;
    private static SharedPreferences mPreferences;

    private WeatherServiceStub mWeatherServiceStub;

    private LocationApi mLocationApi;
    private DeviceUtils mDeviceUtils;

    private int mOption;
    private IWeatherServiceCB mIWeatherServiceCB;

    private AlmanacData mLatestAlmanacData = null;
    private LocationData mLatestLocationData = null;
    private WeatherLiveData mLatestWeatherLiveData = null;

    private int mCycleUpdateTime;

    private MyTimerTask mMyTimerTask;
    private Timer mTimer;

    private IWeatherServiceCB mNotifyCallback;

/*
    private final HashMap<String, Integer> mOptionMap = new HashMap<String, Integer>();
    private final HashMap<String, SoftReference<IWeatherServiceCB>> mWeatherServiceCBMap = new HashMap<String, SoftReference<IWeatherServiceCB>>();
    */

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            LogUtils.d(TAG, "TimerTask, cycle update data");
            String id = Binder.getCallingPid() + "_" + System.currentTimeMillis();
            processGetImmediateData(id, Contants.OPTION_ALL, true, null);
        }
    }

    @Override
    public void onGetAlmanacResponse(AlmanacData data) {
        if (data != null) {
            LogUtils.d(TAG, "onGetAlmanacResponse, data=" + data.toString());
        }
        mLatestAlmanacData = data;
        if (mIWeatherServiceCB != null) {
            try {
                LogUtils.d(TAG, "onGetAlmanacResponse, callback->onGetAlmanac");
                mIWeatherServiceCB.onGetAlmanac(data);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        if (mNotifyCallback != null) {
            try {
                LogUtils.d(TAG, "onGetAlmanacResponse, notify->onGetAlmanac");
                mNotifyCallback.onGetAlmanac(data);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onGetLocationResponse(LocationData data) {
        if (data != null) {
            LogUtils.d(TAG, "onGetLocationResponse, data=" + data.toString());
        }
        mLatestLocationData = data;
        if ((mOption & Contants.OPTION_LOCATION) != 0) {
            if (mIWeatherServiceCB != null) {
                try {
                    LogUtils.d(TAG, "onGetLocationResponse, callback->onGetLocation");
                    mIWeatherServiceCB.onGetLocation(data);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            if (mNotifyCallback != null) {
                try {
                    LogUtils.d(TAG, "onGetLocationResponse, notify->onGetLocation");
                    mNotifyCallback.onGetLocation(data);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        if ((mOption & Contants.OPTION_WEATHER) != 0) {
            LogUtils.d(TAG, "onGetLocationResponse, OPTION_WEATHER, continue get weather");
            WeatherHandler weatherHandler = new WeatherHandler(mContext, WeatherService.this);
            weatherHandler.weather(data.getCity(), WeatherSearchQuery.WEATHER_TYPE_LIVE);
        }

        reportLocation(data);
    }

    @Override
    public void onGetWeatherResponse(WeatherLiveData data) {
        if (data != null) {
            LogUtils.d(TAG, "onGetWeatherResponse, data=" + data.toString());
        }
        mLatestWeatherLiveData = data;
        if (mIWeatherServiceCB != null) {
            try {
                LogUtils.d(TAG, "onGetWeatherResponse, callback->onGetWeather");
                mIWeatherServiceCB.onGetWeather(data);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        if (mNotifyCallback != null) {
            try {
                LogUtils.d(TAG, "onGetWeatherResponse, notify->onGetWeather");
                mNotifyCallback.onGetWeather(data);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        LogUtils.d(TAG, "onCreate");

        mContext = getApplicationContext();
        mHandler = new MyHandler();

        mPreferences = mContext.getSharedPreferences(Contants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        mCycleUpdateTime = getSettingValue(Contants.SP_CYCLE_UPDATE_TIME, Contants.DEFAULT_CYCLE_UPDATE_TIME);

        mTimer = new Timer();
        mMyTimerTask = new MyTimerTask();
        mTimer.schedule(mMyTimerTask, 0, mCycleUpdateTime * 60 * 1000 * 1000);

        mLocationApi = HttpManager.createService(LocationApi.class);

        mDeviceUtils = new DeviceUtils(getApplicationContext());

        mWeatherServiceStub = new WeatherServiceStub(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mMyTimerTask != null) {
            mMyTimerTask.cancel();
        }

        if (mTimer != null) {
            mTimer.cancel();
        }

        LogUtils.d(TAG, "onDestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.d(TAG, "onStartCommand, intent=" + intent + " flags=" + flags + " startId=" + startId);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        LogUtils.d(TAG, "onBind, intent=" + intent);
        return mWeatherServiceStub;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtils.d(TAG, "onUnbind, intent=" + intent);
        return super.onUnbind(intent);
    }

    private static class WeatherServiceStub extends IWeatherService.Stub {
        WeatherService mService;

        WeatherServiceStub(WeatherService mService) {
            this.mService = mService;
        }

        @Override
        public void getData(int option, IWeatherServiceCB callBack) throws RemoteException {
            String id = Binder.getCallingPid() + "_" + System.currentTimeMillis();
            mService.getData(id, option, callBack);
        }

        @Override
        public void getImmediateData(int option, IWeatherServiceCB callBack) throws RemoteException {
            String id = Binder.getCallingPid() + "_" + System.currentTimeMillis();
            mService.getImmediateData(id, option, callBack);
        }

        @Override
        public void setCycleUpdateTime(int hours, IWeatherServiceCB callBack) throws RemoteException {
            String id = Binder.getCallingPid() + "_" + System.currentTimeMillis();
            mService.setCycleUpdateTime(id, hours, callBack);
        }

        @Override
        public void getCycleUpdateTime(IWeatherServiceCB callBack) throws RemoteException {
            String id = Binder.getCallingPid() + "_" + System.currentTimeMillis();
            mService.getCycleUpdateTime(id, callBack);
        }

        @Override
        public void registerNotifyCallback(IWeatherServiceCB callBack) throws RemoteException {
            mService.registerNotifyCallback(callBack);
        }

        @Override
        public void unRegisterNotifyCallback() throws RemoteException {
            mService.unRegisterNotifyCallback();
        }
    }

    private void getData(String id, int option, IWeatherServiceCB callBack) {
        Message msg = mHandler.obtainMessage(MSG_GET_DATA, callBack);
        Bundle data = new Bundle();
        data.putString("id", id);
        data.putInt("option", option);
        msg.setData(data);
        mHandler.sendMessage(msg);
    }

    private void getImmediateData(String id, int option, IWeatherServiceCB callBack) {
        Message msg = mHandler.obtainMessage(MSG_GET_IMMEDIATE_DATA, callBack);
        Bundle data = new Bundle();
        data.putString("id", id);
        data.putInt("option", option);
        msg.setData(data);
        mHandler.sendMessage(msg);
    }

    private void setCycleUpdateTime(String id, int hours, IWeatherServiceCB callBack) {
        Message msg = mHandler.obtainMessage(MSG_SET_CYCLE_UPDATE_TIME, callBack);
        Bundle data = new Bundle();
        data.putString("id", id);
        data.putInt("hours", hours);
        msg.setData(data);
        mHandler.sendMessage(msg);
    }

    private void getCycleUpdateTime(String id, IWeatherServiceCB callBack) {
        Message msg = mHandler.obtainMessage(MSG_GET_CYCLE_UPDATE_TIME, callBack);
        Bundle data = new Bundle();
        data.putString("id", id);
        msg.setData(data);
        mHandler.sendMessage(msg);
    }

    private void registerNotifyCallback(IWeatherServiceCB callBack) {
        mNotifyCallback = callBack;
    }

    private void unRegisterNotifyCallback() {
        mNotifyCallback = null;
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            LogUtils.d(TAG, "handleMessage, msg.what=" + msg.what);

            switch (msg.what) {
                case MSG_GET_DATA: {
                    Bundle data = msg.getData();
                    String id = data.getString("id");
                    int option = data.getInt("option");
                    processGetData(id, option, (IWeatherServiceCB) msg.obj);
                    break;
                }
                case MSG_GET_IMMEDIATE_DATA: {
                    Bundle data = msg.getData();
                    String id = data.getString("id");
                    int option = data.getInt("option");
                    processGetImmediateData(id, option, true, (IWeatherServiceCB) msg.obj);
                    break;
                }
                case MSG_SET_CYCLE_UPDATE_TIME: {
                    Bundle data = msg.getData();
                    String id = data.getString("id");
                    int hours = data.getInt("hours");
                    processSetCycleUpdateTime(id, hours, (IWeatherServiceCB) msg.obj);
                    break;
                }
                case MSG_GET_CYCLE_UPDATE_TIME: {
                    Bundle data = msg.getData();
                    String id = data.getString("id");
                    processGetCycleUpdateTime(id, (IWeatherServiceCB) msg.obj);
                    break;
                }
                default:
                    break;
            }
        }
    }

    private void processGetData(String id, int option, IWeatherServiceCB callback) {
        LogUtils.d(TAG, "processGetData, id=" + id + " option=" + option + " callback=" + callback);

        mOption = option;
        mIWeatherServiceCB = callback;

        if ((option & Contants.OPTION_LOCATION) != 0 && (option & Contants.OPTION_WEATHER) == 0) {
            LogUtils.d(TAG, "processGetData, OPTION_LOCATION (without OPTION_WEATHER)");

            if (mLatestLocationData == null) {
                processGetImmediateData(id, Contants.OPTION_LOCATION, false, callback);
            } else {
                if (callback != null) {
                    try {
                        callback.onGetLocation(mLatestLocationData);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if ((option & Contants.OPTION_WEATHER) != 0) {
            LogUtils.d(TAG, "processGetData, OPTION_WEATHER, get location first");

            if (mLatestWeatherLiveData == null) {
                processGetImmediateData(id, Contants.OPTION_WEATHER, false, callback);
            } else {
                if (callback != null) {
                    try {
                        callback.onGetWeather(mLatestWeatherLiveData);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                if ((option & Contants.OPTION_LOCATION) != 0) {
                    if (mLatestLocationData == null) {
                        processGetImmediateData(id, Contants.OPTION_LOCATION, false, callback);
                    } else {
                        if (callback != null) {
                            try {
                                callback.onGetLocation(mLatestLocationData);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }

        if ((option & Contants.OPTION_ALMANAC) != 0) {
            LogUtils.d(TAG, "processGetData, OPTION_ALMANAC");

            if (mLatestAlmanacData == null) {
                processGetImmediateData(id, Contants.OPTION_ALMANAC, false, callback);
            } else {
                if (callback != null) {
                    try {
                        callback.onGetAlmanac(mLatestAlmanacData);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if ((option & Contants.OPTION_LUNAR) != 0) {
            LogUtils.d(TAG, "processGetData, OPTION_LUNAR");

            Calendar calendar = Calendar.getInstance();
            LunarHandler lunarHandler = new LunarHandler(calendar);
            LunarData lunarData = lunarHandler.lunar();

            if (callback != null) {
                try {
                    callback.onGetLunar(lunarData);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void processGetImmediateData(String id, int option, boolean saveOption, IWeatherServiceCB callback) {
        LogUtils.d(TAG, "processGetImmediateData, id=" + id + " option=" + option + " saveOption=" + saveOption + " callback=" + callback);

        if (saveOption) {
            mOption = option;
        }
        mIWeatherServiceCB = callback;

        if ((option & Contants.OPTION_LOCATION) != 0 && (option & Contants.OPTION_WEATHER) == 0) {
            LogUtils.d(TAG, "processGetImmediateData, OPTION_LOCATION (without OPTION_WEATHER)");

            LocationHandler locationHandler = new LocationHandler(mContext, WeatherService.this);
            locationHandler.location();
        }

        if ((option & Contants.OPTION_WEATHER) != 0) {
            LogUtils.d(TAG, "processGetImmediateData, OPTION_WEATHER, get location first");

            LocationHandler locationHandler = new LocationHandler(mContext, WeatherService.this);
            locationHandler.location();
        }

        if ((option & Contants.OPTION_ALMANAC) != 0) {
            LogUtils.d(TAG, "processGetImmediateData, OPTION_ALMANAC");

            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            String date = Integer.toString(year);

            if (month <= 9) {
                date += "0" + month;
            } else {
                date += month;
            }

            if (day <= 9) {
                date += "0" + day;
            } else {
                date += day;
            }

            AlmanacHandler almanacHandler = new AlmanacHandler(mContext, WeatherService.this);
            almanacHandler.almanac(date);
        }

        if ((option & Contants.OPTION_LUNAR) != 0) {
            LogUtils.d(TAG, "processGetImmediateData, OPTION_LUNAR");

            Calendar calendar = Calendar.getInstance();
            LunarHandler lunarHandler = new LunarHandler(calendar);
            LunarData lunarData = lunarHandler.lunar();

            if (callback != null) {
                try {
                    callback.onGetLunar(lunarData);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void processSetCycleUpdateTime(String id, int hours, IWeatherServiceCB callback) {
        LogUtils.d(TAG, "processSetCycleUpdateTime, id=" + id + " hours=" + hours + " callback=" + callback);

        mCycleUpdateTime = hours;

        if (mTimer != null) {
            if (mMyTimerTask != null) {
                mMyTimerTask.cancel();
            }

            mMyTimerTask = new MyTimerTask();
            mTimer.schedule(mMyTimerTask, 0, hours * 60 * 1000 * 1000);
        }

        setSettingValue(Contants.SP_CYCLE_UPDATE_TIME, hours);
    }

    private void processGetCycleUpdateTime(String id, IWeatherServiceCB callback) {
        LogUtils.d(TAG, "processGetCycleUpdateTime, id=" + id + " callback=" + callback);

        int hours = getSettingValue(Contants.SP_CYCLE_UPDATE_TIME, Contants.DEFAULT_CYCLE_UPDATE_TIME);
        LogUtils.d(TAG, "processGetCycleUpdateTime, hours=" + hours);

        if (callback != null) {
            try {
                callback.onGetCycleUpdateTime(hours);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public static int getSettingValue(String key, int defaultVal) {
        return mPreferences.getInt(key, defaultVal);
    }

    public static void setSettingValue(String key, int val) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(key, val);
        editor.apply();
    }

    private void reportLocation(LocationData data) {
        if (data != null) {
            if (mLocationApi != null) {
                LogUtils.d(TAG, "reportLocation, report location data to server");

                LocationVO locationVO = new LocationVO(data.getLatitude(), data.getLongitude(), data.getAltitude(),
                        data.getProvince(), data.getCity(), data.getStreet(), data.getAccuracy(), data.getAddress(),
                        data.getCityCode(), data.getAdCode(), data.getlTime());

                long uid = mDeviceUtils.getLUid();
                long did = mDeviceUtils.getLDid();
                String atoken = mDeviceUtils.getAccessToken();

                mLocationApi.reportLocation(uid, did, atoken, locationVO)
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Subscriber<APIResultVO>() {
                            @Override
                            public void onCompleted() {
                                LogUtils.d(TAG, "reportLocation, onCompleted");
                            }

                            @Override
                            public void onError(Throwable e) {
                                LogUtils.d(TAG, "reportLocation, onError");
                                e.printStackTrace();
                            }

                            @Override
                            public void onNext(APIResultVO apiResultVO) {
                                LogUtils.d(TAG, "reportLocation, onNext, apiResultVO=" + apiResultVO);
                            }
                        });
            }
        }
    }


/*
    private void registerWeatherServiceCallback(String id, IWeatherServiceCB callBack) {
        LogUtils.d(TAG, "registerWeatherServiceCallback, id=" + id);
        SoftReference<IWeatherServiceCB> cb = mWeatherServiceCBMap.get(id);
        if (cb != null) {
            cb = null;
        } else {
            mWeatherServiceCBMap.put(id, new SoftReference<IWeatherServiceCB>(callBack));
        }
    }

    private void unRegisterWeatherServiceCallback(String id) {
        LogUtils.d(TAG, "unRegisterWeatherServiceCallback, id=" + id);
        SoftReference<IWeatherServiceCB> cb = mWeatherServiceCBMap.get(id);
        cb = null;
        mWeatherServiceCBMap.remove(id);
    }

    private IWeatherServiceCB getWeatherServiceCallbackById(String id){
        LogUtils.d(TAG, "getWeatherServiceCallbackById, id="+id);

        if(mWeatherServiceCBMap.size() <= 0){
            return null;
        }

        SoftReference<IWeatherServiceCB> callBackRef = mWeatherServiceCBMap.get(id);
        LogUtils.d(TAG, "getWeatherServiceCallbackById, callBackRef="+callBackRef);
        if(callBackRef == null){
            return null;
        }

        IWeatherServiceCB callBack = callBackRef.get();
        LogUtils.d(TAG, "getWeatherServiceCallbackById, callBack="+callBack);

        return callBack;
    }
    */
}