package com.ragentek.homeset.core.net.http.api;

import com.ragentek.protocol.messages.http.APIResultVO;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;


public interface AudioApi {
    @GET("audio/category")
    Observable<APIResultVO> queryAudioCategory(@Query("uid") long uid, @Query("did") long did, @Query("atoken") String atoken);
}