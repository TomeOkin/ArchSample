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
package com.tomeokin.archsample;

import android.app.Application;

import com.orhanobut.logger.Logger;
import com.tomeokin.archsample.injection.component.ApplicationComponent;
import com.tomeokin.archsample.injection.component.DaggerApplicationComponent;
import com.tomeokin.archsample.injection.module.ApplicationModule;

/**
 * Application
 */
public class ArchApplication extends Application {
    private static ArchApplication mApp;
    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        Logger.init();

        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    public ApplicationComponent component() {
        return applicationComponent;
    }

    public static ArchApplication getApp() {
        return mApp;
    }
}
