package com.ragentek.homeset.weather.handler;

import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.ragentek.homeset.weather.data.LocationData;
import com.ragentek.homeset.weather.utils.LogUtils;

import java.text.SimpleDateFormat;
import java.util.Date;


public class LocationHandler {
    private static final String TAG = "LocationHandler";

    private Context mContext;

    public interface LocationHandlerListener {
        void onGetLocationResponse(LocationData data);
    }

    LocationHandlerListener mListener;

    public LocationHandler(Context context, LocationHandlerListener listener) {
        mContext = context;
        mListener = listener;
    }

    public void location() {
        LocationThread locationThread = new LocationThread(mContext, mListener);
        locationThread.start();
    }

    private class LocationThread extends Thread {
        Context context;
        LocationHandlerListener listener;
        AMapLocationClient client = null;
        AMapLocationClientOption option = null;

        AMapLocationListener locationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation amapLocation) {
                LocationData locationData = null;

                if (amapLocation != null) {
                    locationData = new LocationData();

                    if (amapLocation.getErrorCode() == 0) {
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = new Date(amapLocation.getTime());
                        df.format(date);

                        locationData.setLocationType(amapLocation.getLocationType());
                        locationData.setLatitude(amapLocation.getLatitude());
                        locationData.setLongitude(amapLocation.getLongitude());
                        locationData.setAltitude(amapLocation.getAltitude());
                        locationData.setAccuracy(amapLocation.getAccuracy());
                        locationData.setTime(df.format(date));
                        locationData.setAddress(amapLocation.getAddress());
                        locationData.setCountry(amapLocation.getCountry());
                        locationData.setProvince(amapLocation.getProvince());
                        locationData.setCity(amapLocation.getCity());
                        locationData.setDistrict(amapLocation.getDistrict());
                        locationData.setStreet(amapLocation.getStreet());
                        locationData.setStreetNum(amapLocation.getStreetNum());
                        locationData.setCityCode(amapLocation.getCityCode());
                        locationData.setAdCode(amapLocation.getAdCode());
                        locationData.setAoiName(amapLocation.getAoiName());
                        locationData.setlTime(amapLocation.getTime());
                    } else {
                        LogUtils.e(TAG, "LocationThread, onLocationChanged, ErrorCode:" + amapLocation.getErrorCode()
                                + ", ErrorInfo:" + amapLocation.getErrorInfo());

                        locationData.setErrorCode(amapLocation.getErrorCode());
                    }
                }
                client.stopLocation();
                client.onDestroy();

                LogUtils.d(TAG, "LocationThread, call listener=" + listener);
                if (listener != null) {
                    listener.onGetLocationResponse(locationData);
                }
            }
        };

        public LocationThread(Context context, LocationHandlerListener listener) {
            this.context = context;
            this.listener = listener;
        }

        @Override
        public void run() {
            LogUtils.d(TAG, "LocationThread");

            client = new AMapLocationClient(context);
            client.setLocationListener(locationListener);

            option = new AMapLocationClientOption();
            option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            option.setOnceLocation(true);
            option.setNeedAddress(true);

            client.setLocationOption(option);

            LogUtils.d(TAG, "LocationThread, startLocation");
            client.startLocation();
        }
    }
}