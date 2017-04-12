package com.ragentek.homeset.audiocenter.net;

import android.content.Context;
import android.util.Log;

import com.ragentek.homeset.audiocenter.model.bean.AudioResult;
import com.ragentek.homeset.audiocenter.utils.LogUtil;
import com.ragentek.homeset.core.BuildConfig;
import com.ragentek.homeset.core.utils.DeviceUtils;
import com.ragentek.protocol.messages.http.audio.AlbumResultVO;
import com.ragentek.protocol.messages.http.audio.CategoryResultVO;
import com.ragentek.protocol.messages.http.audio.FavoriteResultVO;
import com.ragentek.protocol.messages.http.audio.MusicResultVO;
import com.ragentek.protocol.messages.http.audio.RadioResultVO;
import com.ragentek.protocol.messages.http.audio.TagResultVO;
import com.ragentek.protocol.messages.http.audio.TrackResultVO;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by xuanyang.feng on 2017/2/16.
 */

public class AudioCenterHttpManager {
    private static final int DEFAULT_TIMEOUT = 5;
    //                public static final String BASE_URL = "http://192.168.12.18:28080/openapi/";
    //    public static final String BASE_URL = "http://192.168.12.10:8080/atlasyun.webapi/";
    public static final String BASE_URL = "http://www.robyun.com/" + BuildConfig.WEBAPI_PATH + "/";
    private Retrofit retrofitAudio;
    private static final String TAG = "AudioCenterHttpManager";
    private AudioCenterHttpAPI mAudioCenterHttpAPI;
    private String token = "fengxuanyang";
    private int uid = 0;
    private int did = 0;

    private static final String HEADER_KEY_CONTENT_TYPE = "Content-type";
    private static final String HEADER_KEY_ACCEPT = "Accept";
    private static final String HEADER_KEY_DID = "did";
    private static final String HEADER_KEY_UID = "uid";
    private static final String HEADER_KEY_ATOKEN = "atoken";

    private static final String HEADER_VALUE_CONTENT_TYPE = "application/x-www-form-urlencoded";
    private static final String HEADER_VALUE_ACCEPT = "application/json";
    private static DeviceUtils mDeviceUtils;
    private static AudioCenterHttpManager mAudioCenterHttpManager;
    private static boolean DEBUG = true;


    public static final AudioCenterHttpManager getInstance(Context context) {
        if (mAudioCenterHttpManager == null) {
            synchronized (AudioCenterHttpManager.class) {
                if (mAudioCenterHttpManager == null) {
                    mAudioCenterHttpManager = new AudioCenterHttpManager(context);
                }
            }
        }
        return mAudioCenterHttpManager;
    }

    private AudioCenterHttpManager(Context context) {
        mDeviceUtils = DeviceUtils.getInstance(context.getApplicationContext());
        uid = Integer.parseInt(mDeviceUtils.getUid());
        did = Integer.parseInt(mDeviceUtils.getDid());
        token = mDeviceUtils.getAccessToken();
        OkHttpClient.Builder mBuilder = new OkHttpClient.Builder()
                .addInterceptor(getLoggingInterceptor())
                .addInterceptor(new CommonHeaderInterceptor())
                .addInterceptor(new AuthenticationInterceptor());
        trustAllCerti(mBuilder);
        OkHttpClient okHttpClient = mBuilder.build();
        retrofitAudio = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .build();
        mAudioCenterHttpAPI = retrofitAudio.create(AudioCenterHttpAPI.class);
    }


    public void getCategory(Subscriber<CategoryResultVO> subscriber) {
        Log.d(TAG, "getCategory: ");
        mAudioCenterHttpAPI.getCategory(uid, did, token)
                .map(new HttpResultFunc<CategoryResultVO>())
                .onErrorReturn(new Func1<Throwable, CategoryResultVO>() {
                    @Override
                    public CategoryResultVO call(Throwable throwable) {
                        LogUtil.e("TAG", "getCategory --onErrorReturn:" + throwable.getMessage());
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    public void getTag(Subscriber<TagResultVO> subscriber, long category) {
        Log.d(TAG, "getTag: ");
        mAudioCenterHttpAPI.getTag(uid, did, category, token)
                .map(new HttpResultFunc<TagResultVO>())
                .onErrorReturn(new Func1<Throwable, TagResultVO>() {
                    @Override
                    public TagResultVO call(Throwable throwable) {
                        LogUtil.e("TAG", "getTag --onErrorReturn:" + throwable.getMessage());
                        return null;
                    }
                })

                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    public void getAlbums(Subscriber<AlbumResultVO> subscriber, long category, String tag, int page, int count) {
        Log.d(TAG, "getAlbums: ");
        mAudioCenterHttpAPI.getAlbums(uid, did, category, tag, page, count, token)
                .map(new HttpResultFunc<AlbumResultVO>())
                .onErrorReturn(new Func1<Throwable, AlbumResultVO>() {
                    @Override
                    public AlbumResultVO call(Throwable throwable) {
                        LogUtil.e("TAG", "getTag --onErrorReturn:" + throwable.getMessage());
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    public void getTracks(Subscriber<TrackResultVO> subscriber, long albumid, int page, int count) {
        Log.d(TAG, "getTracks  albumid:" + albumid);
        mAudioCenterHttpAPI.getTracks(albumid, uid, did, page, count, token)
                .map(new HttpResultFunc<TrackResultVO>())
                .onErrorReturn(new Func1<Throwable, TrackResultVO>() {
                    @Override
                    public TrackResultVO call(Throwable throwable) {
                        LogUtil.e("TAG", "getTag --onErrorReturn:" + throwable.getMessage());
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    public void getMusics(Subscriber<MusicResultVO> subscriber, String tag, int page, int count) {
        Log.d(TAG, "getTracks  getMusics:" + tag);
        mAudioCenterHttpAPI.getMusics(uid, did, tag, page, count, token)
                .map(new HttpResultFunc<MusicResultVO>())
                .onErrorReturn(new Func1<Throwable, MusicResultVO>() {
                    @Override
                    public MusicResultVO call(Throwable throwable) {
                        LogUtil.e("TAG", "getMusics --onErrorReturn:" + throwable.getMessage());
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
 

    public void addFavorite(Subscriber<String> subscriber, long id, int category, int group) {
        mAudioCenterHttpAPI.addFavorite(id, did, category, group, token)
                .map(new HttpResultFunc<String>())
                .onErrorReturn(new Func1<Throwable, String>() {
                    @Override
                    public String call(Throwable throwable) {
                        LogUtil.e("TAG", "getMusics --onErrorReturn:" + throwable.getMessage());
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    public void removeFavorite(Subscriber<String> subscriber, long id, int category, int group) {
        mAudioCenterHttpAPI.removeFavorite(id, uid, did, category, group, token)
                .map(new HttpResultFunc<String>())
                .onErrorReturn(new Func1<Throwable, String>() {
                    @Override
                    public String call(Throwable throwable) {
                        LogUtil.e("TAG", "getMusics --onErrorReturn:" + throwable.getMessage());
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    //    @GET("audio/favorite?")
//    Observable<AudioResult<FavoriteResultVO>> getFavorites
//            (@Query("uid") int uid, @Query("did") int did, @Query("page") int page, @Query("count") int count, @Query("token") String token);
//}
    public void getFavorites(Subscriber<FavoriteResultVO> subscriber, int page, int count) {
        mAudioCenterHttpAPI.getFavorites(uid, did, page, count, token)
                .map(new HttpResultFunc<FavoriteResultVO>())
                .onErrorReturn(new Func1<Throwable, FavoriteResultVO>() {
                    @Override
                    public FavoriteResultVO call(Throwable throwable) {
                        LogUtil.e("TAG", "getMusics --onErrorReturn:" + throwable.getMessage());
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    public void getRadiosByProvince(Subscriber<RadioResultVO> subscriber) {
        mAudioCenterHttpAPI.getRadiosByProvince(uid, did, token)
                .map(new HttpResultFunc<RadioResultVO>())
                .onErrorReturn(new Func1<Throwable, RadioResultVO>() {
                    @Override
                    public RadioResultVO call(Throwable throwable) {
                        LogUtil.e("TAG", "getRadiosByProvince --onErrorReturn:" + throwable.getMessage());
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    public void getRadiosByTAG(Subscriber<RadioResultVO> subscriber, int type, int province, int page, int count) {
        mAudioCenterHttpAPI.getRadiosByTag(uid, did, type, province, page, count, token)
                .map(new HttpResultFunc<RadioResultVO>())
                .onErrorReturn(new Func1<Throwable, RadioResultVO>() {
                    @Override
                    public RadioResultVO call(Throwable throwable) {
                        LogUtil.e("TAG", "getRadiosByTAG --onErrorReturn:" + throwable.getMessage());
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    private class HttpResultFunc<T> implements Func1<AudioResult<T>, T> {
        @Override
        public T call(AudioResult<T> httpResult) {
            if (httpResult != null) {
                return httpResult.getResultMessage();
            } else {
                throw new RuntimeException("error");
            }
        }
    }

    /**
     * TODO: need to write a service for upload file, because it's request heard type is "multipart/form-data", not "application/x-www-form-urlencoded"
     */


    private static class CommonHeaderInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originRequest = chain.request();
            Request.Builder builder = originRequest.newBuilder();
            builder.addHeader(HEADER_KEY_CONTENT_TYPE, HEADER_VALUE_CONTENT_TYPE);
            builder.addHeader(HEADER_KEY_ACCEPT, HEADER_VALUE_ACCEPT);

            Request request = builder.build();
            return chain.proceed(request);
        }
    }

    private static class AuthenticationInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originRequest = chain.request();

            Request request = setHeaderAuthentication(originRequest);
            Response response = chain.proceed(request);

            if (response.isSuccessful()) {
                if (isAccessTokenExpired(response)) {
                    boolean success = updateAccessToken();
                    if (!success) {
                        success = updateRefreshToken();
                    }

                    if (success) {
                        request = setHeaderAuthentication(originRequest);
                        response = chain.proceed(request);
                        return response;
                    }
                }
            }

            return response;
        }

        private Request setHeaderAuthentication(Request request) {
            Request.Builder builder = request.newBuilder();

            builder.addHeader(HEADER_KEY_UID, mDeviceUtils.getUid());
            builder.addHeader(HEADER_KEY_DID, mDeviceUtils.getDid());
            builder.addHeader(HEADER_KEY_ATOKEN, mDeviceUtils.getAccessToken());
            return builder.build();
        }

        private boolean isAccessTokenExpired(Response response) {
//            try {
//                ResponseBody responseBody = response.peekBody(response.body().contentLength());
//                ApiCallResultVO resultVo = JSON.parseObject(responseBody.string(), ApiCallResultVO.class);
//                // TODO: need to change
//                if (resultVo.getResCode() == ErrorCode.ACCESS_TOKEN_EXPIRED) {
//                    return true;
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            return false;
        }

        private boolean updateAccessToken() throws IOException {
//            AccountApi accountApi = createService(AccountApi.class);
//            TokenResultVO accessTokenDO = accountApi.updateAccessToken(String.valueOf(mAccountUtils.getUserId()), mAccountUtils.getRefreshToken()).execute().body();
//            if (isRefreshTokenExpired(accessTokenDO)) {
//                return false;
//            }
//
//            saveAccessToken(accessTokenDO.getToken());
            return true;
        }

//        private boolean isRefreshTokenExpired(TokenResultVO accessTokenDO) {
//            if (accessTokenDO == null) {
//                return false;
//            }
//
//            if (accessTokenDO.getResCode() == ErrorCode.REFRESHTOKEN_EXPIRED) {
//                return true;
//            }
//
//            return false;
//        }

        private void saveAccessToken(String accessToken) {
//            mAccountUtils.updateAccessToken(accessToken);
        }

        private boolean updateRefreshToken() throws IOException {
//            AccountApi accountApi = createService(AccountApi.class);
//            LoginResultVO loginResultVO = accountApi.loginSync(mDeviceUtils.getDeviceId(), mDeviceUtils.getFirmwareVersion(), mDeviceUtils.getSoftwareVersion(),
//                    RobotUtils.getTimeStampByLong(), mDeviceUtils.getWifiMacAddress(), "0").execute().body();
//
//            if (!isLoginSuccess(loginResultVO)) {
//                return false;
//            }
//
//            updateAccountInfo(loginResultVO);
            return true;
        }

//        private boolean isLoginSuccess(LoginResultVO loginResultVO) {
//            if (loginResultVO == null) {
//                return false;
//            }
//
//            if (loginResultVO.getResCode() == 0) {
//                return true;
//            }
//
//            return true;
//        }

//        private void updateAccountInfo(LoginResultVO loginResultVO) {
//            mAccountUtils.updateAll(loginResultVO.getUid(), loginResultVO.getBanded(), loginResultVO.getAccessToken(), loginResultVO.getRefreshToken());
//        }
    }

    private static HttpLoggingInterceptor getLoggingInterceptor() {
        if (DEBUG) {
            return new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
        }
        return new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE);
    }

    static void trustAllCerti(OkHttpClient.Builder sClient) {

        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    X509Certificate[] x509Certificates = new X509Certificate[0];
                    return x509Certificates;
                }
            }}, new SecureRandom());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {

        }

        sClient.sslSocketFactory(sc.getSocketFactory());
        sClient.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
    }
}

