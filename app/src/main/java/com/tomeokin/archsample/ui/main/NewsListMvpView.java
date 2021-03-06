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
package com.tomeokin.archsample.ui.main;

import com.tomeokin.archsample.data.model.Collection;
import com.tomeokin.archsample.data.model.NewsData;
import com.tomeokin.archsample.ui.base.MvpView;

import java.util.HashMap;
import java.util.List;

/**
 * 资讯页面的 view 接口
 */
public interface NewsListMvpView extends MvpView {
    void updateCollectionMap(HashMap<String, Collection> collectionMap);

    void loadCollectionMapError(Throwable throwable);

    void updateNewsDataList(List<NewsData> newsDataList);

    void updateNewsDataListError(Throwable throwable);
}
