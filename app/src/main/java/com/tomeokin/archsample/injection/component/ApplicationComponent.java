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
package com.tomeokin.archsample.injection.component;

import android.app.Application;
import android.content.Context;

import com.tomeokin.archsample.ArchApplication;
import com.tomeokin.archsample.data.CollectManager;
import com.tomeokin.archsample.data.NewsManager;
import com.tomeokin.archsample.data.local.CollectionDatabaseHelper;
import com.tomeokin.archsample.data.remote.NewsApi;
import com.tomeokin.archsample.data.remote.NewsCache;
import com.tomeokin.archsample.injection.ApplicationContext;
import com.tomeokin.archsample.injection.module.ApplicationModule;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Application 级别的注入管理
 */
@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(ArchApplication application);

    Application application();

    @ApplicationContext
    Context context();

    NewsApi newsApi();

    CollectionDatabaseHelper collectionDatabaseHelper();

    CollectManager collectManager();

    NewsCache newsCache();

    NewsManager newsManager();

    EventBus eventBus();
}
