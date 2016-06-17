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
package com.tomeokin.archsample.ui.main.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.tomeokin.archsample.R;
import com.tomeokin.archsample.data.model.Collection;
import com.tomeokin.archsample.data.model.NewsData;
import com.tomeokin.archsample.ui.base.BaseFragment;
import com.tomeokin.archsample.ui.main.NewsListMvpView;
import com.tomeokin.archsample.ui.main.activity.MainActivity;
import com.tomeokin.archsample.ui.main.adapter.NewsAdapter;
import com.tomeokin.archsample.ui.main.decorator.DividerItemDecoration;
import com.tomeokin.archsample.ui.main.presenter.NewsListPresenter;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * News 页面
 */
public class NewsListFragment extends BaseFragment
        implements NewsAdapter.OnNewsCollectedChangeListener, NewsListMvpView {
    @BindView(R.id.newsRv) RecyclerView newsRv;
    @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;

    @Inject NewsAdapter mNewsAdapter;
    @Inject NewsListPresenter mNewsListPresenter;

    private LinearLayoutManager layoutManager;
    private Activity mActivity;

    private int page = 1;
    private int count = 20;
    private int loading = 0; // 0 idle, 1 loading, 2 error

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mActivity = (Activity) context;
        ((MainActivity) getActivity()).component()
                .inject(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
        mNewsListPresenter.attachView(this);

        layoutManager = new LinearLayoutManager(getContext());
        newsRv.setLayoutManager(layoutManager);
        newsRv.addItemDecoration(
                new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        newsRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private float mLastVisibleItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && mLastVisibleItem + 1 == recyclerView.getLayoutManager()
                        .getItemCount()) {

                    if (loading != 1) { // 没有正在加载的任务
                        mNewsListPresenter.loadMoreNewsDatas(page, count);
                    } else {
                        Toast.makeText(mActivity, "waiting for loading", Toast.LENGTH_SHORT)
                                .show();
                    }
                    //if (loading == 2) { // 上一次请求失败
                    //    loading = 1;
                    //    Toast.makeText(getContext(), "加载更多", Toast.LENGTH_SHORT)
                    //            .show();
                    //    load();
                    //} else if (loading == 0) { // 空闲中，可以发送请求
                    //    int p = mNewsAdapter.getItemCount() / count + 1;
                    //    if (p == page) { // 没有更多的数据
                    //        Toast.makeText(getContext(), "已全部加载", Toast.LENGTH_SHORT)
                    //                .show();
                    //    } else {
                    //        page = p;
                    //        loading = 1;
                    //        Toast.makeText(getContext(), "加载更多", Toast.LENGTH_SHORT)
                    //                .show();
                    //        load();
                    //    }
                    //}
                    //if (loading != 1) { // 没有请求中的线程
                    //
                    //
                    //    //updateData();
                    //}
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                mLastVisibleItem = layoutManager.findLastVisibleItemPosition();
            }
        });
        mNewsAdapter.setOnNewsCollectedChangeListener(this);
        newsRv.setAdapter(mNewsAdapter);

        swipeRefreshLayout.setColorSchemeResources(R.color.swipe_color_1, R.color.swipe_color_2,
                R.color.swipe_color_3, R.color.swipe_color_4);
        swipeRefreshLayout.setEnabled(true);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                if (mNewsListPresenter != null) {
                    mNewsListPresenter.loadMoreNewsDatas(page, count);
                }
                //load();
                //updateData();
            }
        });

        mNewsListPresenter.loadCollections();
        mNewsListPresenter.loadNewsDatas(page, count);
        swipeRefreshLayout.setRefreshing(true);
        loading = 1;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void updateData() {
        //NewsApiSource.getInstance()
        //    .getNewsByType("Android", count, page);
        //Log.i("take", "start update");
    }

    //public void load() {
    //    //if (!NetworkUtils.isNetworkAvailable()) {
    //    //    Toast.makeText(getContext(), "please check your network", Toast.LENGTH_SHORT).show();
    //    //    return;
    //    //}
    //
    //    if (page == 1) {
    //        swipeRefreshLayout.setRefreshing(true);
    //    }
    //
    //    unsubscribe();
    //    subscription = Data.getInstance()
    //            .subscribeData(new Observer<List<NewsData>>() {
    //                @Override
    //                public void onCompleted() {
    //                    Log.i("take", "onCompleted");
    //                }
    //
    //                @Override
    //                public void onError(Throwable e) {
    //                    e.printStackTrace();
    //                    swipeRefreshLayout.setRefreshing(false);
    //                    Toast.makeText(getActivity(), R.string.loading_failed, Toast.LENGTH_SHORT)
    //                            .show();
    //                    loading = 2;
    //                }
    //
    //                @Override
    //                public void onNext(List<NewsData> newsDatas) {
    //                    swipeRefreshLayout.setRefreshing(false);
    //                    mNewsAdapter.setNewsList(newsDatas);
    //                    loading = 0;
    //                    //if (newsDatas.size() > mNewsAdapter.getItemCount() || page == 1) {
    //                    //    loading = 0;
    //                    //}
    //                }
    //            }, page, count);
    //}

    @Override
    public void onNewsCollected(String id, NewsData newsData) {
        mNewsListPresenter.addCollection(id, newsData);
    }

    @Override
    public void onNewsUncollected(String id, NewsData newsData) {
        mNewsListPresenter.deleteCollection(id, newsData);
    }

    @Override
    public void updateCollectionMap(HashMap<String, Collection> collectionMap) {
        mNewsAdapter.setCollectionMap(collectionMap);
        //mNewsAdapter.notifyDataSetChanged();
    }

    @Override
    public void loadCollectionMapError(Throwable throwable) {
    }

    @Override
    public void updateNewsDataList(List<NewsData> newsDataList) {
        Logger.t("loading").i("update newsDatas");
        mNewsAdapter.setNewsList(newsDataList);
        mNewsAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
        loading = 0;
        page = newsDataList.size() / count + 1;
    }

    @Override
    public void updateNewsDataListError(Throwable throwable) {
        swipeRefreshLayout.setRefreshing(false);
        Toast.makeText(getActivity(), R.string.loading_failed, Toast.LENGTH_SHORT)
                .show();
        loading = 2;
    }
}
