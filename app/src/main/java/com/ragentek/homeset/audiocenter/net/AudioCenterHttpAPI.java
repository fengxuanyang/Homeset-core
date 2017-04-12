package com.ragentek.homeset.audiocenter.net;

import com.ragentek.homeset.audiocenter.model.bean.AudioResult;
import com.ragentek.protocol.messages.http.audio.AlbumResultVO;
import com.ragentek.protocol.messages.http.audio.CategoryResultVO;
import com.ragentek.protocol.messages.http.audio.FavoriteResultVO;
import com.ragentek.protocol.messages.http.audio.MusicResultVO;
import com.ragentek.protocol.messages.http.audio.RadioResultVO;
import com.ragentek.protocol.messages.http.audio.TagResultVO;
import com.ragentek.protocol.messages.http.audio.TrackResultVO;

import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by xuanyang.feng on 2017/2/16.
 */

public interface AudioCenterHttpAPI {

    //http://{server:port}/atlas/audio/category?uid=100&did=100&atoken=15sdf487sdf1s587df
    @GET("audio/category")
    Observable<AudioResult<CategoryResultVO>> getCategory
    (@Query("uid") int uid, @Query("did") int did, @Query("atoken") String atoken);


    @GET("audio/tag")
    Observable<AudioResult<TagResultVO>> getTag
            (@Query("uid") int uid, @Query("did") int did, @Query("category_id") long category_id, @Query("atoken") String atoken);

    @GET("album/tag")
    Observable<AudioResult<AlbumResultVO>> getAlbums
            (@Query("uid") int uid, @Query("did") int did, @Query("category_id") long category_id, @Query("tag") String tag
                    , @Query("page") int page, @Query("count") int count, @Query("atoken") String atoken);


    @GET("album/track/{id}")
    Observable<AudioResult<TrackResultVO>> getTracks
            (@Path("id") long id, @Query("uid") int uid, @Query("did") int did, @Query("page") int page, @Query("count") int count, @Query("atoken") String atoken);

    //    http://{server:port}/${path}/music/tag?uid=100&did=100&tag=红歌&page=0&count=20&atoken=15sdf487sdf1s587df
    @GET("music/tag")
    Observable<AudioResult<MusicResultVO>> getMusics
    (@Query("uid") int uid, @Query("did") int did, @Query("tag") String tag
            , @Query("page") int page, @Query("count") int count, @Query("atoken") String atoken);


    @POST("audio/favorite/{id}")
    Observable<AudioResult<String>> addFavorite
            (@Path("id") long id,   @Query("did") int did, @Query("category_id") int category_id, @Query("group") int group, @Query("atoken") String atoken);

    //    DELETE	http://{server:port}/${path}/audio/favorite/{id}
    @DELETE("audio/favorite/{id}")
    Observable<AudioResult<String>> removeFavorite
    (@Path("id") long id, @Query("uid") int uid, @Query("did") int did, @Query("category_id") int category_id, @Query("group") int group, @Query("atoken") String atoken);

    //http://{server:port}/${path}/music/favorite/100?uid=100&did=100&category_id=3&group=1&atoken=15sdf487sdf1s587df
    @GET("audio/favorite")
    Observable<AudioResult<FavoriteResultVO>> getFavorites
    (@Query("uid") int uid, @Query("did") int did, @Query("page") int page, @Query("count") int count, @Query("atoken") String atoken);

    //    http://{server:port}/${path}/radio/province
//http://{server:port}/${path}/radio/province?uid=100&did=100&atoken=15sdf487sdf1s587df
    @GET("radio/province")
    Observable<AudioResult<RadioResultVO>> getRadiosByProvince
    (@Query("uid") int uid, @Query("did") int did, @Query("atoken") String atoken);

    //    http://{server:port}/${path}/radio/tag
//    http://{server:port}/${path}/radio/tag?uid=100&did=100&type=3&province=5&page=0&count=20&atoken=15sdf487sdf1s587df
    @GET("radio/tag")
    Observable<AudioResult<RadioResultVO>> getRadiosByTag
    (@Query("uid") int uid, @Query("did") int did, @Query("radio_type") int type, @Query("province") int province, @Query("page") int page, @Query("count") int count, @Query("atoken") String atoken);
}


