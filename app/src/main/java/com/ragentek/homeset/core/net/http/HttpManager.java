package com.ragentek.homeset.core.net.http;

import android.content.Context;

import com.ragentek.homeset.core.BuildConfig;
import com.ragentek.homeset.core.utils.DeviceUtils;

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
import retrofit2.converter.fastjson.FastJsonConverterFactory;

/**
 * 1. provide a service client that will be used to launch a http request defined in ***Api.java
 * 2. process most http requests by adding latest access token to them
 * <p>
 * TODO: Notes: Read the following articles can help you better use retrofit(3thd HTTP utils jar):
 * 1. Getting Started and Create an Android Client: https://futurestud.io/blog/retrofit-getting-started-and-android-client
 * <p>
 * 2. Optional Query Parameters: https://futurestud.io/blog/retrofit-optional-query-parameters
 * <p>
 * 3. How to Download Files from Server: https://futurestud.io/blog/retrofit-2-how-to-download-files-from-server
 * <p>
 * 4. How to Upload Files to Server: https://futurestud.io/blog/retrofit-2-how-to-upload-files-to-server
 */
public class HttpManager {
    private static final String TAG = "HttpManager";

    private static final boolean DEBUG = true;

    //    private static final String API_BASE_URL = "http://192.168.12.18:28080";
    private static final String API_BASE_URL = "http://www.robyun.com/" + BuildConfig.WEBAPI_PATH + "/";

    private static final String HEADER_KEY_CONTENT_TYPE = "Content-type";
    private static final String HEADER_KEY_ACCEPT = "Accept";
    private static final String HEADER_KEY_DID = "did";
    private static final String HEADER_KEY_UID = "uid";
    private static final String HEADER_KEY_ATOKEN = "atoken";

    private static final String HEADER_VALUE_CONTENT_TYPE = "application/x-www-form-urlencoded";
    private static final String HEADER_VALUE_ACCEPT = "application/json";

    protected static Retrofit.Builder mBuilder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(FastJsonConverterFactory.create())
//                    GsonConverterFactory
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create());

    //    private static AccountDaoUtils mAccountUtils;
    private static DeviceUtils mDeviceUtils;

    /**
     * provide a service client without processing the request
     *
     * @param serviceClass a java interface defined in ***Api.java ,which descirbes some functionally-similar http requests
     * @param <S>          the type of serviceClass
     * @return
     */
    public static <S> S createService(Class<S> serviceClass) {
        OkHttpClient.Builder mHttpClient = new OkHttpClient.Builder();
        mHttpClient.addInterceptor(new CommonHeaderInterceptor());
        mHttpClient.addInterceptor(getLoggingInterceptor());

        trustAllCerti(mHttpClient);
        OkHttpClient client = mHttpClient.build();

        Retrofit retrofit = mBuilder.client(client).build();
        return retrofit.create(serviceClass);
    }

    /**
     * provide a service client which will process the request by adding access token using interceptor
     *
     * @param serviceClass a java interface defined in ***Api.java ,which describes some functionally-similar http requests
     * @param <S>          the type of serviceClass
     * @return a service client
     */
    public static <S> S createAuthenticationService(Class<S> serviceClass, Context context) {
//        mAccountUtils = AccountDaoUtils.getInstance(context);
        mDeviceUtils = DeviceUtils.getInstance(context);

        OkHttpClient.Builder mHttpClient = new OkHttpClient.Builder();
        mHttpClient.addInterceptor(new CommonHeaderInterceptor());
        mHttpClient.addInterceptor(new AuthenticationInterceptor());
        mHttpClient.addInterceptor(getLoggingInterceptor());

        trustAllCerti(mHttpClient);
        OkHttpClient client = mHttpClient.build();

        Retrofit retrofit = mBuilder.client(client).build();
        return retrofit.create(serviceClass);
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