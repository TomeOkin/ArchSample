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
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tomeokin.archsample.R;
import com.tomeokin.archsample.data.model.Collection;
import com.tomeokin.archsample.ui.base.BaseFragment;
import com.tomeokin.archsample.ui.main.CollectionMvpView;
import com.tomeokin.archsample.ui.main.activity.MainActivity;
import com.tomeokin.archsample.ui.main.activity.TagEditorActivity;
import com.tomeokin.archsample.ui.main.adapter.CollectionAdapter;
import com.tomeokin.archsample.ui.main.decorator.DividerItemDecoration;
import com.tomeokin.archsample.ui.main.event.CollectionSetChangeEvent;
import com.tomeokin.archsample.ui.main.presenter.CollectionPresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Collection 页面
 */
public class CollectFragment extends BaseFragment implements CollectionAdapter.OnTagClickListener,
        CollectionMvpView {
    private static final int REQUEST_TAG_EDITOR = 1;

    @BindView(R.id.collectionRv) RecyclerView collectionRv;

    @Inject EventBus mEventBus;
    @Inject CollectionPresenter mPresenter;
    @Inject CollectionAdapter mCollectionAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEventBus.register(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((MainActivity) getActivity()).component()
                .inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_collect, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
        mPresenter.attachView(this);

        collectionRv.setLayoutManager(new LinearLayoutManager(getContext()));
        collectionRv.addItemDecoration(
                new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        mCollectionAdapter.setOnTagClickListener(this);
        collectionRv.setAdapter(mCollectionAdapter);
        mPresenter.loadCollections();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_TAG_EDITOR) {
            mCollectionAdapter.setCollectionList(mPresenter.getCollectionList());
            mCollectionAdapter.notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onTagClickListener(View view, String collectionId) {
        Intent starter = new Intent(getActivity(), TagEditorActivity.class);
        starter.putExtra(TagEditorActivity.EXTRA_COLLECTION_ID, collectionId);
        getActivity().startActivityForResult(starter, REQUEST_TAG_EDITOR);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCollectionChange(CollectionSetChangeEvent event) {
        mCollectionAdapter.setCollectionList(mPresenter.getCollectionList());
        mCollectionAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateCollectionMap(HashMap<String, Collection> collectionMap) {
        mCollectionAdapter.setCollectionList(mPresenter.getCollectionList());
        mCollectionAdapter.notifyDataSetChanged();
    }

    @Override
    public void loadCollectionMapError(Throwable throwable) {

    }
}
