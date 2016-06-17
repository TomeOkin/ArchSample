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
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;
import com.tomeokin.archsample.data.model.NewsData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * 资讯缓存
 */
@Singleton
public class NewsCache {
    private static String DATA_FILE_NAME = "news.cache";

    private final Application mApp;
    private final File dataFile;
    private final Gson gson = new Gson();

    @Inject
    public NewsCache(Application app) {
        this.mApp = app;
        dataFile = new File(mApp.getFilesDir(), DATA_FILE_NAME);
    }

    public List<NewsData> readItems() {
        try {
            Reader reader = new FileReader(dataFile);
            return gson.fromJson(reader, new TypeToken<List<NewsData>>() { }.getType());
        } catch (FileNotFoundException e) {
            Logger.t("loading").e("ReadingCache: Read %s false", DATA_FILE_NAME);
            return null;
        }
    }

    public void writeItems(List<NewsData> items) {
        String json = gson.toJson(items);
        try {
            if (!dataFile.exists()) {
                try {
                    dataFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Writer writer = new FileWriter(dataFile);
            writer.write(json);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void delete() {
        dataFile.delete();
    }
}
