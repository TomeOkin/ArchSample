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
package com.tomeokin.archsample.data.local;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;
import com.tomeokin.archsample.data.model.Collection;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 点击收藏时有关的保存
 */
@Singleton
public class CollectionDatabaseHelper {
    private final BriteDatabase mBriteDatabase;

    @Inject
    public CollectionDatabaseHelper(DbOpenHelper dbHelper) {
        mBriteDatabase = SqlBrite.create()
                .wrapDatabaseHelper(dbHelper, Schedulers.io());
    }

    public BriteDatabase getBriteDatabase() {
        return mBriteDatabase;
    }

    public Observable<Collection> setCollections(final List<Collection> collectionList) {
        return Observable.create(new Observable.OnSubscribe<Collection>() {
            @Override
            public void call(Subscriber<? super Collection> subscriber) {
                if (subscriber.isUnsubscribed()) return;
                BriteDatabase.Transaction transaction = mBriteDatabase.newTransaction();
                try {
                    mBriteDatabase.delete(Db.CollectionTable.TABLE_NAME, null);
                    for (Collection collection : collectionList) {
                        long result = mBriteDatabase.insert(Db.CollectionTable.TABLE_NAME,
                                Db.CollectionTable.toContentValues(collection),
                                SQLiteDatabase.CONFLICT_REPLACE);
                        if (result >= 0) subscriber.onNext(collection);
                    }
                    transaction.markSuccessful();
                    subscriber.onCompleted();
                } finally {
                    transaction.end();
                }
            }
        });
    }

    public Observable<List<Collection>> getCollections() {
        return mBriteDatabase.createQuery(Db.CollectionTable.TABLE_NAME, "SELECT * FROM "
                + Db.CollectionTable.TABLE_NAME
                + " ORDER BY "
                + Db.CollectionTable.NEW_ID
                + " DESC")
                .mapToList(new Func1<Cursor, Collection>() {
                    @Override
                    public Collection call(Cursor cursor) {
                        return Db.CollectionTable.parseCursor(cursor);
                    }
                });
    }

    public void addCollection(final Collection collection) {
        mBriteDatabase.insert(Db.CollectionTable.TABLE_NAME,
                Db.CollectionTable.toContentValues(collection));
    }

    public void deleteCollection(final Collection collection) {
        mBriteDatabase.delete(Db.CollectionTable.TABLE_NAME, Db.CollectionTable.NEW_ID + " = ?",
                collection.newsId);
    }

    public void updateCollection(final Collection collection) {
        mBriteDatabase.update(Db.CollectionTable.TABLE_NAME,
                Db.CollectionTable.toContentValues(collection), Db.CollectionTable.NEW_ID + " = ?",
                collection.newsId + "");
    }
}
