/*
 * Copyright 2016 TomeOkin
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tomeokin.archsample.data.remote;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.orhanobut.logger.Logger;
import com.tomeokin.archsample.BuildConfig;
import com.tomeokin.archsample.data.remote.utils.NetworkUtils;

import java.io.File;
import java.io.IOException;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 初始化 NewsApi
 */
public class NewsSource {
    public static final String API_URL = "http://gank.io";
    public static final String NEWS_CACHE_DIR = "news_cache";

    private static NewsApi newsApi;
    private static NewsSource sInstance;

    private final Application mApp;

    private NewsSource(Application app) {
        mApp = app;
    }

    public static NewsSource getInstance(Application app) {
        if (sInstance == null) {
            sInstance = new NewsSource(app);
        }
        return sInstance;
    }

    // 更新策略
    private Interceptor cacheInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            boolean isNetworkAvailable = NetworkUtils.isNetworkAvailable(mApp);

            // 无网络
            if (!isNetworkAvailable) {
                Logger.t("network").i("Network Unavailable: force cache");
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
            }

            Response response = chain.proceed(request);
            String cacheControl = request.cacheControl()
                    .toString();
            if (!isNetworkAvailable) {
                // 断网情况下，缓存过期设置为 1 天
                cacheControl = "public, only-if-cached, max-stale=" + 60 * 60 * 24;
            } else {
                // 有网情况下，如果没有连接 wifi，缓存过期设置为 30s
                if (!NetworkUtils.isWifi(mApp)) {
                    cacheControl = "public, max-age=" + 30;
                } else {
                    cacheControl = "public, max-age=" + 0;
                }
            }
            Logger.t("network").i("cacheControl: %s", cacheControl);

            return response.newBuilder()
                    .header("Cache-Control", cacheControl)
                    .removeHeader("Pragma") // 清除头信息，因为服务器如果不支持，会返回一些干扰信息
                    .build();
        }
    };

    private OkHttpClient getClient() {
        // log用拦截器
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // 开发模式记录整个body，否则只记录基本信息如返回200，http协议版本等
        if (BuildConfig.DEBUG) {
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        }

        File httpCacheDirectory = new File(mApp.getCacheDir(), NEWS_CACHE_DIR);
        Cache cache = new Cache(httpCacheDirectory, 10 * 1024 * 1024);

        return new OkHttpClient.Builder().addInterceptor(cacheInterceptor)
                .addInterceptor(logging)
                .cache(cache)
                .build();
    }

    public NewsApi getNewsApi() {
        if (newsApi == null) {
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    .create();

            Retrofit retrofit = new Retrofit.Builder().baseUrl(API_URL)
                    .client(getClient())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            newsApi = retrofit.create(NewsApi.class);
        }

        return newsApi;
    }
}
