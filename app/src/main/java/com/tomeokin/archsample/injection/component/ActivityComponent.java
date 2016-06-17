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

import android.app.Activity;

import com.tomeokin.archsample.injection.PerActivity;
import com.tomeokin.archsample.injection.module.ActivityModule;
import com.tomeokin.archsample.ui.main.activity.MainActivity;
import com.tomeokin.archsample.ui.main.activity.TagEditorActivity;
import com.tomeokin.archsample.ui.main.fragment.CollectFragment;
import com.tomeokin.archsample.ui.main.fragment.NewsListFragment;

import dagger.Component;

/**
 * Activity 级别的注入管理
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    Activity activity();

    void inject(MainActivity mainActivity);
    void inject(TagEditorActivity newsActivity);

    void inject(CollectFragment collectFragment);
    void inject(NewsListFragment newsListFragment);
}
