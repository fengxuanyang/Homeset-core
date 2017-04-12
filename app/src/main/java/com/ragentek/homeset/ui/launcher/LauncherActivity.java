package com.ragentek.homeset.ui.launcher;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Window;

import com.ragentek.homeset.core.HomesetService;
import com.ragentek.homeset.core.R;
import com.ragentek.homeset.core.utils.DeviceUtils;
import com.ragentek.homeset.ui.launcher.adapter.PageAdapter;
import com.ragentek.homeset.ui.launcher.fragment.KanCategoryFragment;
import com.ragentek.homeset.ui.launcher.fragment.TingCategoryFragment;
import com.ragentek.homeset.ui.launcher.fragment.WeChatFragment;
import com.ragentek.homeset.ui.launcher.fragment.WeatherFragment;
import com.ragentek.homeset.ui.login.LoginActivity;
import com.ragentek.homeset.ui.utils.LogUtils;
import com.ragentek.homeset.weather.IWeatherService;
import com.ragentek.homeset.weather.IWeatherServiceCB;
import com.ragentek.homeset.weather.WeatherService;
import com.ragentek.homeset.weather.data.AlmanacData;
import com.ragentek.homeset.weather.data.LocationData;
import com.ragentek.homeset.weather.data.LunarData;
import com.ragentek.homeset.weather.data.WeatherLiveData;
import com.ragentek.homeset.weather.utils.Contants;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;


public class LauncherActivity extends FragmentActivity {
    private static final String TAG = "LauncherActivity";

    private static final int MSG_BASE = 1000;
    private static final int MSG_FIRST_GET_ALL_DATA = MSG_BASE;
    private static final int MSG_UPDATE_LOCATION = MSG_BASE + 1;
    private static final int MSG_UPDATE_LIVE_WEATHER = MSG_BASE + 2;
    private static final int MSG_UPDATE_ALMANAC = MSG_BASE + 3;
    private static final int MSG_UPDATE_LUNAR = MSG_BASE + 4;

    private static final int PAGE_WEATHER = 0;
    private static final int PAGE_TING_CATEGORY = 1;
    private static final int PAGE_KANG_CATEGORY = 2;
    private static final int PAGE_CONTACT = 3;

    private IWeatherService mService;
    private TingCategoryFragment tingCategoryFragment;
    private KanCategoryFragment kanCategoryFragment;
    private WeatherFragment mWeatherFragment;
    private WeChatFragment mWeChatFragment;

    private ViewPager mViewPager;
    private PageIndicator mIndicator;

    WeatherLiveData mWeatherLiveData = null;
    LunarData mLunarData = null;
    AlmanacData mAlmanacData = null;

    private DeviceUtils mDeviceUtils;
    private boolean mIsBindServiceOK = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_launcher);

        boolean speechOperationCall = false;

        Intent intent = getIntent();
        LogUtils.d(TAG, "onCreate, intent=" + intent);
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String operation = bundle.getString("operation");
                if ("call".equals(operation)) {
                    LogUtils.d(TAG, "onCreate, speech operation: call");
                    speechOperationCall = true;
                }
            }
        }

        mViewPager = (ViewPager) findViewById(R.id.viewpage);
        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        tingCategoryFragment = new TingCategoryFragment();
        kanCategoryFragment = new KanCategoryFragment();
        mWeatherFragment = new WeatherFragment();
        mWeChatFragment = new WeChatFragment();

        fragments.add(mWeatherFragment);
        fragments.add(tingCategoryFragment);
        fragments.add(kanCategoryFragment);
        fragments.add(mWeChatFragment);

        mViewPager.setAdapter(new PageAdapter(getSupportFragmentManager(), fragments));
        if (speechOperationCall) {
            mViewPager.setCurrentItem(PAGE_CONTACT);
        } else {
            mViewPager.setCurrentItem(PAGE_WEATHER);
        }
        mViewPager.addOnPageChangeListener(new PageChange());

        mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(mViewPager);

        mDeviceUtils = new DeviceUtils(getApplicationContext());

        startCoreService();
    }

    @Override
    protected void onStart() {
        super.onStart();

        LogUtils.d(TAG, "onStart");

        if (!mIsBindServiceOK) {
            mIsBindServiceOK = bindService(new Intent(LauncherActivity.this, WeatherService.class), mConnection, Context.BIND_AUTO_CREATE);
            LogUtils.d(TAG, "onStart, mIsBindServiceOK=" + mIsBindServiceOK);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        LogUtils.d(TAG, "onResume");

        if (mDeviceUtils != null) {
            String isRegistered = mDeviceUtils.getRegisterFlag();
            String isLogined = mDeviceUtils.getLoginFlag();
            LogUtils.d(TAG, "onResume, isRegistered=" + isRegistered + " isLogined=" + isLogined);
            if (isRegistered.equals("0") || isLogined.equals("0")) {
                Intent intent = new Intent(LauncherActivity.this, LoginActivity.class);
                startActivity(intent);
                return;
            }
        }

        if (!mIsBindServiceOK) {
            mIsBindServiceOK = bindService(new Intent(LauncherActivity.this, WeatherService.class), mConnection, Context.BIND_AUTO_CREATE);
            LogUtils.d(TAG, "onResume, mIsBindServiceOK=" + mIsBindServiceOK);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        LogUtils.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();

        LogUtils.d(TAG, "onStop, mIsBindServiceOK=" + mIsBindServiceOK);
        if (mIsBindServiceOK) {
            try {
                if (mService != null) {
                    mService.unRegisterNotifyCallback();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            unbindService(mConnection);

            mIsBindServiceOK = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LogUtils.d(TAG, "onDestroy");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        LogUtils.d(TAG, "onNewIntent, intent=" + intent);

        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String operation = bundle.getString("operation");
                if ("call".equals(operation)) {
                    LogUtils.d(TAG, "onNewIntent, speech operation: call");
                    mViewPager.setCurrentItem(PAGE_CONTACT);
                }
            }
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected, name=" + name + " service=" + service);
            if ("com.ragentek.homeset.weather.WeatherService".equals(name.getShortClassName())) {
                mService = IWeatherService.Stub.asInterface(service);

                try {
                    mHandler.sendEmptyMessageDelayed(MSG_FIRST_GET_ALL_DATA, 3000);
                    mService.registerNotifyCallback(mWeatherServiceCB);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected, name=" + name);
        }
    };

    private IWeatherServiceCB mWeatherServiceCB = new IWeatherServiceCB.Stub() {
        @Override
        public void onGetLocation(LocationData data) throws RemoteException {
            if (data != null) {
                LogUtils.d(TAG, "onGetLocation, data=" + data.toString());
                Message m = mHandler.obtainMessage(MSG_UPDATE_LOCATION, data);
                mHandler.sendMessage(m);
            }
        }

        @Override
        public void onGetWeather(WeatherLiveData data) throws RemoteException {
            if (data != null) {
                LogUtils.d(TAG, "onGetWeather, data=" + data.toString());
                Message m = mHandler.obtainMessage(MSG_UPDATE_LIVE_WEATHER, data);
                mHandler.sendMessage(m);

                mWeatherLiveData = data;
            }
        }

        @Override
        public void onGetAlmanac(AlmanacData data) throws RemoteException {
            if (data != null) {
                LogUtils.d(TAG, "onGetAlmanac, data=" + data.toString());
                Message m = mHandler.obtainMessage(MSG_UPDATE_ALMANAC, data);
                mHandler.sendMessage(m);

                mAlmanacData = data;
            }
        }

        @Override
        public void onGetLunar(LunarData data) throws RemoteException {
            if (data != null) {
                LogUtils.d(TAG, "onGetLunar, data=" + data.toString());
                Message m = mHandler.obtainMessage(MSG_UPDATE_LUNAR, data);
                mHandler.sendMessage(m);

                mLunarData = data;
            }
        }

        @Override
        public void onSetCycleUpdateTime(int result) throws RemoteException {
            LogUtils.d(TAG, "onSetCycleUpdateTime, result=" + result);
        }

        @Override
        public void onGetCycleUpdateTime(int hours) throws RemoteException {
            LogUtils.d(TAG, "onGetCycleUpdateTime, hours=" + hours);
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            LogUtils.d(TAG, "handleMessage, msg.what=" + msg.what);
            switch (msg.what) {
                case MSG_FIRST_GET_ALL_DATA: {
                    try {
                        mService.getData(Contants.OPTION_ALL, mWeatherServiceCB);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case MSG_UPDATE_LOCATION: {
                    break;
                }
                case MSG_UPDATE_LIVE_WEATHER: {
                    WeatherLiveData data = (WeatherLiveData) msg.obj;
                    EventBus.getDefault().post(data);
                    break;
                }
                case MSG_UPDATE_ALMANAC: {
                    AlmanacData data = (AlmanacData) msg.obj;
                    EventBus.getDefault().post(data);
                    break;
                }
                case MSG_UPDATE_LUNAR: {
                    LunarData data = (LunarData) msg.obj;
                    EventBus.getDefault().post(data);
                    break;
                }
            }
        }
    };

    private class PageChange implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            LogUtils.d(TAG, "onPageSelected, position=" + position);
            if (position == PAGE_WEATHER) {
                if (mWeatherLiveData != null) {
                    EventBus.getDefault().post(mWeatherLiveData);
                }

                if (mLunarData != null) {
                    EventBus.getDefault().post(mLunarData);
                }

                if (mAlmanacData != null) {
                    EventBus.getDefault().post(mAlmanacData);
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    private void startCoreService() {
        Intent intent = new Intent(LauncherActivity.this, HomesetService.class);
        intent.setAction(HomesetService.ACTION_START);
        startService(intent);
    }
}