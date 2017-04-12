package com.ragentek.homeset.ui.login;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.ragentek.homeset.core.R;
import com.ragentek.homeset.core.net.http.HttpManager;
import com.ragentek.homeset.core.net.http.api.DeviceApi;
import com.ragentek.homeset.core.utils.DeviceUtils;
import com.ragentek.homeset.ui.utils.LogUtils;
import com.ragentek.protocol.messages.http.device.DeviceLoginRequestVO;
import com.ragentek.protocol.messages.http.device.DeviceLoginResultVO;
import com.ragentek.protocol.messages.http.device.DeviceRegisterRequestVO;
import com.ragentek.protocol.messages.http.device.DeviceRegisterResultVO;


import rx.Subscriber;
import rx.schedulers.Schedulers;

public class LoginActivity extends Activity {
    private static final String TAG = "LoginActivity";

    private static final int MSG_SHOW_HINT = 1000;

    DeviceApi mDeviceApi;
    DeviceUtils mDeviceUtils;

    private TextView mHintView;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            LogUtils.d(TAG, "handleMessage, msg.what=" + msg.what);
            switch (msg.what) {
                case MSG_SHOW_HINT: {
                    Bundle bundle = msg.getData();
                    String hint = (String) bundle.get("hint");
                    mHintView.setText(hint);

                    if ("登录成功".equals(hint)) {
                        finish();
                    }
                    break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mHintView = (TextView) findViewById(R.id.hint);

        mDeviceApi = HttpManager.createService(DeviceApi.class);
        mDeviceUtils = new DeviceUtils(getApplicationContext());
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mDeviceUtils != null) {
            String isRegistered = mDeviceUtils.getRegisterFlag();
            String isLogined = mDeviceUtils.getLoginFlag();
            LogUtils.d(TAG, "isRegistered=" + isRegistered + " isLogined=" + isLogined);
            if (isRegistered.equals("0")) {
                register();
            } else if (isLogined.equals("0")) {
                login();
            }
        }
    }

    private void register() {
        LogUtils.d(TAG, "register");
        if (mDeviceApi != null) {
            DeviceRegisterRequestVO deviceRegisterRequestVO = new DeviceRegisterRequestVO();
            deviceRegisterRequestVO.setSn(mDeviceUtils.getSN());
            deviceRegisterRequestVO.setWifiMac(mDeviceUtils.getWifiMac());
            deviceRegisterRequestVO.setBtMac(mDeviceUtils.getBtMac());
            deviceRegisterRequestVO.setHwVer(mDeviceUtils.getHwVer());
            deviceRegisterRequestVO.setFwVer(mDeviceUtils.getFwVer());
            deviceRegisterRequestVO.setSwVer(mDeviceUtils.getSwVer());
            deviceRegisterRequestVO.setHwPfVer(mDeviceUtils.getHwPfVer());
            deviceRegisterRequestVO.setSwPfVer(mDeviceUtils.getSwPfVer());
            deviceRegisterRequestVO.setImei(mDeviceUtils.getImei());
            deviceRegisterRequestVO.setImsi(mDeviceUtils.getImsi());
            deviceRegisterRequestVO.setTimeStamp(mDeviceUtils.getTimeStamp());
            deviceRegisterRequestVO.setSign(mDeviceUtils.getSign());

            mDeviceApi.register(deviceRegisterRequestVO)
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Subscriber<DeviceRegisterResultVO>() {
                        @Override
                        public void onCompleted() {
                            LogUtils.d(TAG, "register, onCompleted");
                        }

                        @Override
                        public void onError(Throwable e) {
                            LogUtils.d(TAG, "register, onError");
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(DeviceRegisterResultVO deviceRegisterResultVO) {
                            LogUtils.d(TAG, "register, onNext, deviceRegisterResultVO=" + deviceRegisterResultVO);
                            registerResult(deviceRegisterResultVO);
                        }
                    });
        }
    }

    private void registerResult(DeviceRegisterResultVO deviceRegisterResultVO) {
        LogUtils.d(TAG, "registerResult, deviceRegisterResultVO=" + deviceRegisterResultVO);
        if (deviceRegisterResultVO != null) {
            if (deviceRegisterResultVO.getRes_code() == 0) {
                LogUtils.d(TAG, "registerResult, register success");

                long did = deviceRegisterResultVO.getDid();
                String qrticket = deviceRegisterResultVO.getQrticket();

                mDeviceUtils.setDid(Long.toString(did));
                mDeviceUtils.setQrticket(qrticket);
                mDeviceUtils.setRegisterFlag("1");

                Message msg = new Message();
                msg.what = MSG_SHOW_HINT;
                Bundle bundle = new Bundle();
                bundle.putString("hint", "注册成功");
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            } else {
                LogUtils.d(TAG, "registerResult, register fail");

                Message msg = new Message();
                msg.what = MSG_SHOW_HINT;
                Bundle bundle = new Bundle();
                bundle.putString("hint", "注册失败");
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
        }
        login();
    }

    private void login() {
        LogUtils.d(TAG, "login");
        if (mDeviceApi != null) {
            DeviceLoginRequestVO deviceLoginRequestVO = new DeviceLoginRequestVO();
            deviceLoginRequestVO.setSn(mDeviceUtils.getSN());
            deviceLoginRequestVO.setFwVer(mDeviceUtils.getFwVer());
            deviceLoginRequestVO.setSwVer(mDeviceUtils.getSwVer());
            deviceLoginRequestVO.setImei(mDeviceUtils.getImei());
            deviceLoginRequestVO.setImsi(mDeviceUtils.getImsi());
            deviceLoginRequestVO.setTimeStamp(mDeviceUtils.getTimeStamp());
            deviceLoginRequestVO.setSign(mDeviceUtils.getSign());

            mDeviceApi.login(deviceLoginRequestVO)
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Subscriber<DeviceLoginResultVO>() {
                        @Override
                        public void onCompleted() {
                            LogUtils.d(TAG, "login, onCompleted");
                        }

                        @Override
                        public void onError(Throwable e) {
                            LogUtils.d(TAG, "login, onError");
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(DeviceLoginResultVO deviceLoginResultVO) {
                            LogUtils.d(TAG, "login, onNext, deviceLoginResultVO=" + deviceLoginResultVO);
                            loginResult(deviceLoginResultVO);
                        }
                    });
        }
    }

    private void loginResult(DeviceLoginResultVO deviceLoginResultVO) {
        LogUtils.d(TAG, "loginResult, deviceLoginResultVO=" + deviceLoginResultVO);
        if (deviceLoginResultVO != null) {
            if (deviceLoginResultVO.getRes_code() == 0) {
                LogUtils.d(TAG, "loginResult, login success");

                mDeviceUtils.setLoginFlag("1");
                mDeviceUtils.setQrticket(deviceLoginResultVO.getQrticket());
                mDeviceUtils.setAccessToken(deviceLoginResultVO.getAccess_token());
                mDeviceUtils.setRefreshToken(deviceLoginResultVO.getRefresh_token());

                Message msg = new Message();
                msg.what = MSG_SHOW_HINT;
                Bundle bundle = new Bundle();
                bundle.putString("hint", "登录成功");
                msg.setData(bundle);
                mHandler.sendMessageDelayed(msg, 1000);
            } else {
                LogUtils.d(TAG, "loginResult, login fail");

                Message msg = new Message();
                msg.what = MSG_SHOW_HINT;
                Bundle bundle = new Bundle();
                bundle.putString("hint", "登录失败");
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
        }
    }
}