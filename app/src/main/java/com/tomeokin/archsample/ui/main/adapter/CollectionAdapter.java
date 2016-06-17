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
package com.tomeokin.archsample.ui.main.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tomeokin.archsample.R;
import com.tomeokin.archsample.data.model.Collection;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.gujun.android.taggroup.TagGroup;

/**
 * 收藏的 list 适配器
 */
public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.ViewHolder>
        implements View.OnClickListener {

    private List<Collection> collectionList = null;
    private OnTagClickListener mOnTagClickListener = null;

    public interface OnTagClickListener {
        void onTagClickListener(View view, String newsId);
    }

    public void setOnTagClickListener(OnTagClickListener onTagClickListener) {
        this.mOnTagClickListener = onTagClickListener;
    }

    @Inject
    public CollectionAdapter() {

    }

    public void setCollectionList(List<Collection> collectionList) {
        this.collectionList = collectionList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.list_item_collect, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(this);

        // TODO add search support
        holder.tagGroup.setOnTagClickListener(new TagGroup.OnTagClickListener() {
            @Override
            public void onTagClick(String tag) {

            }
        });
        holder.tagGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = (int) v.getTag();
                final String newsId = collectionList.get(position).newsId;

                if (mOnTagClickListener != null) {
                    mOnTagClickListener.onTagClickListener(v, newsId);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Collection collection = collectionList.get(position);
        holder.itemView.setTag(position);
        holder.tagGroup.setTag(position);

        holder.collectTitle.setText(collection.newsDesc);
        holder.collectUrl.setText(collection.newsUrl);
        if (collection.newsTag == null) {
            collection.newsTag = "";
        }
        holder.tagGroup.setTags(collection.newsTag.split(",")); // TODO
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public int getItemCount() {
        return collectionList != null ? collectionList.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.collectTitle) TextView collectTitle;
        @BindView(R.id.collectUrl) TextView collectUrl;
        @BindView(R.id.tag_group) TagGroup tagGroup;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
