package com.ragentek.homeset.core.net.http.api;

import com.ragentek.protocol.messages.http.device.DeviceLoginRequestVO;
import com.ragentek.protocol.messages.http.device.DeviceLoginResultVO;
import com.ragentek.protocol.messages.http.device.DeviceRegisterRequestVO;
import com.ragentek.protocol.messages.http.device.DeviceRegisterResultVO;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;


public interface DeviceApi {
    @POST("device/register")
    Observable<DeviceRegisterResultVO> register(@Body DeviceRegisterRequestVO deviceRegisterRequestVO);

    @POST("device/login")
    Observable<DeviceLoginResultVO> login(@Body DeviceLoginRequestVO deviceLoginRequestVO);
}