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
package com.tomeokin.archsample.ui.main.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

import com.orhanobut.logger.Logger;
import com.tomeokin.archsample.R;
import com.tomeokin.archsample.data.model.Collection;
import com.tomeokin.archsample.ui.base.BaseActivity;
import com.tomeokin.archsample.ui.main.TagEditorMvpView;
import com.tomeokin.archsample.ui.main.presenter.TagEditorPresenter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.gujun.android.taggroup.TagGroup;

/**
 * 编辑标签
 */
public class TagEditorActivity extends BaseActivity implements TagEditorMvpView {
    public static final String EXTRA_COLLECTION_ID = "collection_id";

    @BindView(R.id.tag_group) TagGroup tagGroup;

    @Inject TagEditorPresenter mPresenter;

    private Collection collection;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        component().inject(this);

        setContentView(R.layout.activity_tag_editor);
        ButterKnife.bind(this);
        mPresenter.attachView(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.arrow_left);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        final String collectionId = getIntent().getStringExtra(EXTRA_COLLECTION_ID);
        collection = mPresenter.getCollection(collectionId);
        if (collection.newsTag == null) {
            collection.newsTag = "";
        }
        tagGroup.setTags(collection.newsTag.split(",")); // TODO
    }

    @Override
    public void onBackPressed() {
        Logger.t("collection").i("on back pressed");
        String[] tagArray = tagGroup.getTags();
        String tags = "";
        for (String tag : tagArray) {
            tags += tag + ",";
        }
        collection.newsTag = tags;
        mPresenter.updateCollection(collection);
        setResult(Activity.RESULT_OK);
        //CollectManager.getInstance().addCollection(collection);
        //ContentValues values = new Collection.Builder()
        //    .id(collection.id)
        //    .newsDesc(collection.newsDesc)
        //    .newsTag(collection.newsTag)
        //    .newsUrl(collection.newsUrl)
        //    .build();
        //db.update(Collection.TABLE_NAME, values, Collection.ID + " = ?", collection.id + "");

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tag_editor_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_submit) {
            tagGroup.submitTag();
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return false;
    }
}
