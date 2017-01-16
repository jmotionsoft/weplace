package com.jmotionsoft.towntalk.http;

import android.content.Context;
import android.os.Build;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.jmotionsoft.towntalk.BuildConfig;
import com.jmotionsoft.towntalk.util.AppPreferences;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpApi {
    private static final String API_URL = "";

    public static final String HTTP_HEADER_TOKEN = "X-Auth-Token";
    public static final String HTTP_HEADER_TOKEN_EXP = "X-Auth-Token-Exp";
    public static final String HTTP_HEADER_USER_AGENT = "User-Agent";

    private static final HttpLoggingInterceptor.Level DEBUG_LEVEL = HttpLoggingInterceptor.Level.BODY;

    public static WePlaceService getWePlaceService(Context context){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .client(getClient(context))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(WePlaceService.class);
    }

    public static DrawableTypeRequest<GlideUrl> loadImage(Context context, String imageNo){
        String url = API_URL+"board/image/"+imageNo;

        final String userAgent = getUserAgent();
        final String token = new AppPreferences(context).getLoginToken();

        LazyHeaders.Builder lazyHeadersBuilder = new LazyHeaders.Builder();
        lazyHeadersBuilder.addHeader(HTTP_HEADER_USER_AGENT, userAgent);
        if(token != null)
            lazyHeadersBuilder.addHeader(HTTP_HEADER_TOKEN, token);

        GlideUrl glideUrl = new GlideUrl(url, lazyHeadersBuilder.build());

        return Glide.with(context).load(glideUrl);
    }

    private static OkHttpClient getClient(Context context){
        final String userAgent = getUserAgent();
        final String token = new AppPreferences(context).getLoginToken();

        Interceptor defaultInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request orgReq = chain.request();

                Request.Builder builder = orgReq.newBuilder();
                builder.header(HTTP_HEADER_USER_AGENT, userAgent);

                if(token != null){
                    builder.header(HTTP_HEADER_TOKEN, token);
                }

                builder.method(orgReq.method(), orgReq.body());
                return chain.proceed(builder.build());
            }
        };

        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
        logInterceptor.setLevel(DEBUG_LEVEL);

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(defaultInterceptor)
                .addInterceptor(logInterceptor)
                .build();

        return httpClient;
    }

    private static String getUserAgent(){
        String userAgent = "Android/"+ Build.VERSION.RELEASE+"("+Build.VERSION.SDK_INT+")" +
                " Model/"+Build.MODEL+"("+ Build.BRAND+")"+
                " WePlace/"+BuildConfig.VERSION_NAME+"("+ BuildConfig.VERSION_CODE+")";

        return userAgent;
    }
}
