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
package com.tomeokin.archsample.data;

import android.app.Application;
import android.support.annotation.NonNull;

import com.orhanobut.logger.Logger;
import com.tomeokin.archsample.data.model.News;
import com.tomeokin.archsample.data.model.NewsData;
import com.tomeokin.archsample.data.remote.NewsApi;
import com.tomeokin.archsample.data.remote.NewsCache;
import com.tomeokin.archsample.data.remote.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

/**
 * 对外的资讯管理类
 */
@Singleton
public class NewsManager {
    private final Application mApp;
    private final NewsApi mNewsApi;
    private final NewsCache mNewsCache;
    private BehaviorSubject<List<NewsData>> mCache;

    @Inject
    public NewsManager(Application app, NewsApi newsApi, NewsCache newsCache) {
        mApp = app;
        mNewsApi = newsApi;
        mNewsCache = newsCache;
    }

    // 刷新或者加载更多
    public void loadFromNetwork(final int page, int count) {
        if (mCache == null) {
            Logger.t("loading").w("LoadingNews: Must be use subscribeData first!");
            return;
        }

        if (!NetworkUtils.isNetworkAvailable(mApp)) {
            Logger.t("loading").w("LoadingNews: Network Unavailable!");
            //return;
        }

        Logger.t("loading").i("LoadingNews: Start loading");

        mNewsApi.getNewsByType("Android", count, page)
                .subscribeOn(Schedulers.io())
                .map(new Func1<News, List<NewsData>>() {
                    @Override
                    public List<NewsData> call(News news) {
                        // 连接成功但拿不到数据
                        if (news == null || news.results == null || news.results.isEmpty()) {
                            Logger.t("loading").w("LoadingNews: Get NewsData from network but received empty");
                            if (news == null) {
                                Logger.t("loading").w("LoadingNews: -- news object is empty");
                            } else {
                                Logger.t("loading").w("LoadingNews: -- error message: %s", news.error);
                            }

                            return mNewsCache.readItems();
                        }

                        Logger.t("loading").i("LoadingNews: received with data, size = %d", news.results.size());

                        List<NewsData> newsDatas = mCache.getValue();
                        if (newsDatas == null) {
                            newsDatas = new ArrayList<>();
                        }

                        // 如果是刷新，清空缓存
                        if (page == 0) {
                            newsDatas.clear();
                        }
                        newsDatas.addAll(news.results);
                        return newsDatas;
                    }
                })
                .doOnNext(new Action1<List<NewsData>>() {
                    @Override
                    public void call(List<NewsData> items) {
                        // 写缓存
                        mNewsCache.writeItems(items);
                    }
                })
                .subscribe(new Action1<List<NewsData>>() {
                    @Override
                    public void call(List<NewsData> items) {
                        // 发射数据
                        mCache.onNext(items);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Logger.t("loading").e("LoadingNews: get NewsData from network false");
                        mCache.onError(throwable);
                    }
                });
    }

    // 第一次启动时加载数据
    public Subscription subscribeData(@NonNull Observer<List<NewsData>> observer, final int page,
            final int count) {
        if (mCache == null) {
            mCache = BehaviorSubject.create();

            Observable.create(new Observable.OnSubscribe<List<NewsData>>() {
                @Override
                public void call(Subscriber<? super List<NewsData>> subscriber) {

                    // FIXME: 2016/5/30 由于加载时的参数是页码和条数，没网加载缓存，之后有网且有新数据时就会出现重复的问题
                    // FIXME: 2016/5/30 不过可以通过刷新解决，后续添加个标志
                    // FIXME: 2016/6/9 注意到如果服务器失去响应，界面会显示为空
                    if (!NetworkUtils.isNetworkAvailable(mApp)) {
                        List<NewsData> items = mNewsCache.readItems();
                        if (items == null) {
                            subscriber.onNext(null);
                        } else {
                            subscriber.onNext(items);
                        }
                    } else {
                        loadFromNetwork(page, count);
                    }

                    //List<NewsData> items = mNewsCache.readItems();
                    //if (items == null) {
                    //    subscriber.onNext(null);
                    //    Log.i("loading", "subscribeData: load from network");
                    //    if (NetworkUtils.isNetworkAvailable(mApp)) {
                    //        loadFromNetwork(page, count);
                    //    }
                    //} else {
                    //    subscriber.onNext(items);
                    //}
                }
            })
                    .subscribeOn(Schedulers.io())
                    .subscribe(mCache);
        }

        return mCache.observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
}
