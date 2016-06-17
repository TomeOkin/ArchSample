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
import android.content.Context;
import android.content.SharedPreferences;

import com.tomeokin.archsample.data.local.CollectionDatabaseHelper;
import com.tomeokin.archsample.data.model.Collection;
import com.tomeokin.archsample.ui.main.event.CollectionSetChangeEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * 对外的收藏管理类
 */
@Singleton
public class CollectManager {
    public static final String PREF_COLLECTION = "connection.pref";
    public static final String HAVE_NEW_COLLECTIONS = "have_new_collections";
    public static final String NEW_COLLECTION_COUNT = "new_collection_count";

    private final Application mApp;

    private HashMap<String, Collection> mCollectionArray;
    private HashMap<String, Collection> mNewCollectionArray;
    private final SharedPreferences preferences;
    private final CollectionDatabaseHelper mDatabaseHelper;
    private final EventBus mEventBus;

    @Inject
    public CollectManager(Application app, CollectionDatabaseHelper databaseHelper,
            EventBus eventBus) {
        mApp = app;
        preferences = mApp.getSharedPreferences(PREF_COLLECTION, Context.MODE_PRIVATE);
        mDatabaseHelper = databaseHelper;
        mEventBus = eventBus;

        mCollectionArray = new HashMap<>();
        mNewCollectionArray = new HashMap<>();
    }

    public Observable<HashMap<String, Collection>> removeCollection(final Collection collection) {
        //mCollectionArray.remove(id);
        //mNewCollectionArray.remove(id);
        //
        //// TODO
        //preferences.edit()
        //        .putBoolean(HAVE_NEW_COLLECTIONS, mNewCollectionArray.size() >= 0)
        //        .putInt(NEW_COLLECTION_COUNT, mNewCollectionArray.size())
        //        .apply();
        ////preferences.edit().putString(NEW_COLLECTION, gson.toJson(mCollectionArray.values())).apply();
        return Observable.create(new Observable.OnSubscribe<HashMap<String, Collection>>() {
            @Override
            public void call(Subscriber<? super HashMap<String, Collection>> subscriber) {
                mDatabaseHelper.deleteCollection(collection);
                mCollectionArray.remove(collection.newsId);
                mNewCollectionArray.remove(collection.newsId);
                mEventBus.post(new CollectionSetChangeEvent());
                subscriber.onNext(mCollectionArray);
                preferences.edit()
                        .putBoolean(HAVE_NEW_COLLECTIONS, true)
                        .putInt(NEW_COLLECTION_COUNT, mNewCollectionArray.size())
                        .apply();
            }
        });
    }

    public Observable<HashMap<String, Collection>> addCollection(final Collection collection) {
        //mCollectionArray.put(collection.newsId, collection);
        //
        //// TODO
        //preferences.edit()
        //        .putBoolean(HAVE_NEW_COLLECTIONS, mNewCollectionArray.size() >= 0)
        //        .putInt(NEW_COLLECTION_COUNT, mNewCollectionArray.size())
        //        .apply();
        ////preferences.edit().putString(NEW_COLLECTION, gson.toJson(mCollectionArray.values())).apply();
        return Observable.create(new Observable.OnSubscribe<HashMap<String, Collection>>() {
            @Override
            public void call(Subscriber<? super HashMap<String, Collection>> subscriber) {
                mDatabaseHelper.addCollection(collection);
                mCollectionArray.put(collection.newsId, collection);
                mNewCollectionArray.put(collection.newsId, collection);
                mEventBus.post(new CollectionSetChangeEvent());
                subscriber.onNext(mCollectionArray);
                preferences.edit()
                        .putBoolean(HAVE_NEW_COLLECTIONS, true)
                        .putInt(NEW_COLLECTION_COUNT, mNewCollectionArray.size())
                        .apply();
            }
        });
    }

    public Observable<HashMap<String, Collection>> getCollectionMap() {
        return mDatabaseHelper.getCollections()
                .map(new Func1<List<Collection>, HashMap<String, Collection>>() {
                    @Override
                    public HashMap<String, Collection> call(List<Collection> collectionList) {
                        HashMap<String, Collection> collectionArray = new HashMap<>();
                        for (Collection collection : collectionList) {
                            collectionArray.put(collection.newsId, collection);
                        }
                        mCollectionArray = collectionArray;
                        return collectionArray;
                    }
                });
    }

    public Collection getCollection(String id) {
        return mCollectionArray.get(id);
    }

    public void updateCollection(Collection collection) {
        mDatabaseHelper.updateCollection(collection);
        mCollectionArray.put(collection.newsId, collection);
    }

    public List<Collection> getCollectionList() {
        List<Collection> collectionList;
        if (mCollectionArray instanceof List) {
            collectionList = (List<Collection>) mCollectionArray.values();
        } else {
            collectionList = new ArrayList<>(mCollectionArray.values());
        }
        return collectionList;
    }
}
