package com.ragentek.homeset.core.net.http.api;

import com.ragentek.protocol.commons.lbs.LocationVO;
import com.ragentek.protocol.messages.http.APIResultVO;

import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;


public interface LocationApi {
    @POST("location")
    Observable<APIResultVO> reportLocation(@Query("uid") long uid, @Query("did") long did, @Query("atoken") String atoken, @Body LocationVO locationVO);
}