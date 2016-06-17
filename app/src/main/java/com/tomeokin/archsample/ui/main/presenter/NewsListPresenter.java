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
package com.tomeokin.archsample.ui.main.presenter;

import android.app.Application;

import com.orhanobut.logger.Logger;
import com.tomeokin.archsample.data.CollectManager;
import com.tomeokin.archsample.data.NewsManager;
import com.tomeokin.archsample.data.model.Collection;
import com.tomeokin.archsample.data.model.NewsData;
import com.tomeokin.archsample.data.remote.utils.NetworkUtils;
import com.tomeokin.archsample.ui.base.BasePresenter;
import com.tomeokin.archsample.ui.main.NewsListMvpView;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 资讯页面的表现层
 */
public class NewsListPresenter extends BasePresenter<NewsListMvpView> {
    private final NewsManager mNewsManager;
    private final CollectManager mCollectManager;
    private final Application mApp;
    private Subscription mCollectionSubscription;
    private Subscription mNewsDataSubscription;

    @Override
    public void attachView(NewsListMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        unsubscribe(mCollectionSubscription);
        unsubscribe(mNewsDataSubscription);
    }

    protected void unsubscribe(Subscription subscription) {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    @Inject
    public NewsListPresenter(Application app, NewsManager newsManager,
            CollectManager collectManager) {
        mApp = app;
        mNewsManager = newsManager;
        mCollectManager = collectManager;
    }

    public void loadCollections() {
        unsubscribe(mCollectionSubscription);
        mCollectionSubscription = mCollectManager.getCollectionMap()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<HashMap<String, Collection>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        checkViewAttached();
                        getMvpView().loadCollectionMapError(e);
                        Logger.t("loading")
                                .e(e, "load collection map failure!");
                    }

                    @Override
                    public void onNext(HashMap<String, Collection> collectionHashMap) {
                        checkViewAttached();
                        getMvpView().updateCollectionMap(collectionHashMap);
                    }
                });
    }

    public void addCollection(String id, NewsData newsData) {
        Collection collection = new Collection();
        collection.newsId = newsData._id;
        collection.newsDesc = newsData.desc;
        collection.newsTag = newsData.type;
        collection.newsUrl = newsData.url;

        mCollectManager.addCollection(collection)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<HashMap<String, Collection>>() {
                    @Override
                    public void call(HashMap<String, Collection> collectionMap) {
                        checkViewAttached();
                        getMvpView().updateCollectionMap(collectionMap);
                    }
                });
    }

    public void deleteCollection(String id, NewsData newsData) {
        Collection collection = new Collection();
        collection.newsId = newsData._id;
        collection.newsDesc = newsData.desc;
        collection.newsTag = newsData.type;
        collection.newsUrl = newsData.url;

        mCollectManager.removeCollection(collection)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<HashMap<String, Collection>>() {
                    @Override
                    public void call(HashMap<String, Collection> collectionHashMap) {
                        checkViewAttached();
                        getMvpView().updateCollectionMap(collectionHashMap);
                    }
                });
    }

    public void loadNewsDatas(int page, int count) {
        unsubscribe(mNewsDataSubscription);
        mNewsDataSubscription = mNewsManager.subscribeData(new Observer<List<NewsData>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                checkViewAttached();
                getMvpView().updateNewsDataListError(e);
                Logger.t("loading")
                        .e(e, "load news datas failure!");
            }

            @Override
            public void onNext(List<NewsData> newsDatas) {
                checkViewAttached();
                getMvpView().updateNewsDataList(newsDatas);
            }
        }, page, count);
    }

    public void loadMoreNewsDatas(int page, int count) {
        if (NetworkUtils.isNetworkAvailable(mApp)) {
            mNewsManager.loadFromNetwork(page, count);
        } else {
            getMvpView().updateNewsDataListError(null);
        }
    }
}
