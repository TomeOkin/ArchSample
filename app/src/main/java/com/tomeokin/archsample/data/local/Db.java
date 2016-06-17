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

import android.content.ContentValues;
import android.database.Cursor;

import com.tomeokin.archsample.data.model.Collection;

/**
 * 数据库相关数据模型处理
 */
public class Db {
    public Db() {
    }

    public abstract static class CollectionTable {
        public static final String TABLE_NAME = "collection_table";

        public static final String ID = "_id";
        public static final String NEW_ID = "news_id";
        public static final String NEW_DESC = "news_desc";
        public static final String NEW_TAG = "news_tag";
        public static final String NEW_URL = "news_url";

        // @formatter:off
        public static final String CREATE = ""
            + "CREATE TABLE " + TABLE_NAME + "("
            + ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
            + NEW_ID + " TEXT NOT NULL, "
            + NEW_DESC + " TEXT NOT NULL, "
            + NEW_URL + " TEXT NOT NULL, "
            + NEW_TAG + " TEXT NOT NULL"
            + ")";
        // @formatter:on

        public static ContentValues toContentValues(Collection collection) {
            ContentValues values = new ContentValues();
            //values.put(ID, collection.id);
            values.put(NEW_ID, collection.newsId);
            values.put(NEW_DESC, collection.newsDesc);
            values.put(NEW_TAG, collection.newsTag);
            values.put(NEW_URL, collection.newsUrl);
            return values;
        }

        public static Collection parseCursor(Cursor cursor) {
            Collection collection = new Collection();
            collection.id = cursor.getLong(cursor.getColumnIndex(ID));
            collection.newsId = cursor.getString(cursor.getColumnIndex(NEW_ID));
            collection.newsDesc = cursor.getString(cursor.getColumnIndex(NEW_DESC));
            collection.newsTag = cursor.getString(cursor.getColumnIndex(NEW_TAG));
            collection.newsUrl = cursor.getString(cursor.getColumnIndex(NEW_URL));
            return collection;
        }
    }
}
