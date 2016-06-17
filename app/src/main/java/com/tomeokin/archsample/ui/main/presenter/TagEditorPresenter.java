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
import com.tomeokin.archsample.ui.main.TagEditorMvpView;

import javax.inject.Inject;

/**
 * 标签编辑页面的表现层
 */
public class TagEditorPresenter extends BasePresenter<TagEditorMvpView> {

    private final CollectManager mCollectManager;

    @Inject
    public TagEditorPresenter(CollectManager collectManager) {
        mCollectManager = collectManager;
    }

    public Collection getCollection(String collectionId) {
        return mCollectManager.getCollection(collectionId);
    }

    public void updateCollection(Collection collection) {
        Logger.t("collection").i("update collection tag");
        mCollectManager.updateCollection(collection);
    }
}
