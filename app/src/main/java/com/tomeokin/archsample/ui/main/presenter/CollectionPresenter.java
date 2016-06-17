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

import com.orhanobut.logger.Logger;
import com.tomeokin.archsample.data.CollectManager;
import com.tomeokin.archsample.data.model.Collection;
import com.tomeokin.archsample.ui.base.BasePresenter;
import com.tomeokin.archsample.ui.main.CollectionMvpView;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * 收藏页面的表现层
 */
public class CollectionPresenter extends BasePresenter<CollectionMvpView> {

    private final CollectManager mCollectManager;

    private Subscription mCollectionSubscription;

    @Override
    public void attachView(CollectionMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        unsubscribe(mCollectionSubscription);
    }

    protected void unsubscribe(Subscription subscription) {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
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
                        Logger.t("loading").e(e, "load collection map failure!");
                    }

                    @Override
                    public void onNext(HashMap<String, Collection> collectionHashMap) {
                        checkViewAttached();
                        getMvpView().updateCollectionMap(collectionHashMap);
                    }
                });
    }

    @Inject
    public CollectionPresenter(CollectManager collectManager) {
        mCollectManager = collectManager;
    }

    public List<Collection> getCollectionList() {
        return mCollectManager.getCollectionList();
    }
}
