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

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thefinestartist.finestwebview.FinestWebView;
import com.tomeokin.archsample.R;
import com.tomeokin.archsample.data.model.Collection;
import com.tomeokin.archsample.data.model.NewsData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 资讯页的 list 适配器
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder>
        implements View.OnClickListener {

    private final Activity mActivity;
    private List<NewsData> mNewsList = null;
    private HashMap<String, Collection> mCollectionMap = new HashMap<>();
    private OnNewsCollectedChangeListener mListener;

    // yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
    private SimpleDateFormat formatTZ = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CHINA);
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    public interface OnNewsCollectedChangeListener {
        void onNewsCollected(String id, NewsData newsData);

        void onNewsUncollected(String id, NewsData newsData);
    }

    public void setOnNewsCollectedChangeListener(OnNewsCollectedChangeListener listener) {
        this.mListener = listener;
    }

    public void setCollectionMap(HashMap<String, Collection> collectionMap) {
        this.mCollectionMap = collectionMap;
    }

    @Inject
    public NewsAdapter(Activity activity) {
        mActivity = activity;
        formatTZ.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.list_item_news, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(this);

        holder.favIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = (int) v.getTag();
                final NewsData newsData = mNewsList.get(position);

                if (mCollectionMap.containsKey(newsData._id)) {
                    holder.favIv.setImageResource(R.drawable.favour_outline_64);
                    if (mListener != null) {
                        mListener.onNewsUncollected(newsData._id, newsData);
                    }
                } else {
                    holder.favIv.setImageResource(R.drawable.favour_fill_64);
                    if (mListener != null) {
                        mListener.onNewsCollected(newsData._id, newsData);
                    }
                }
            }
        });
        return holder;
    }

    @Override
    public void onClick(View v) {
        final int position = (int) v.getTag();
        final NewsData newsData = mNewsList.get(position);

        new FinestWebView.Builder(mActivity).titleDefault(newsData.desc)
                .setCustomAnimations(R.anim.slide_left_in, R.anim.hold, R.anim.hold,
                        R.anim.slide_right_out)
                .show(newsData.url);
        //Log.i("take", "position: " + position + " type " + newsData.type);
        //Log.i("take", "position: " + position + " publishedAt " + newsData.publishedAt);
        //Log.i("take", "position: " + position + " desc " + newsData.desc);
        //Date date;
        //String dateTime;
        //try {
        //  date = formatTZ.parse(newsData.publishedAt.substring(0, 19));
        //  dateTime = format.format(date);
        //} catch (ParseException e) {
        //  e.printStackTrace();
        //}
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final NewsData newsData = mNewsList.get(position);
        holder.itemView.setTag(position);
        holder.favIv.setTag(position);

        final boolean isCollected = mCollectionMap.containsKey(newsData._id);
        holder.favIv.setImageResource(
                isCollected ? R.drawable.favour_fill_64 : R.drawable.favour_outline_64);

        holder.newsTitleTv.setText(newsData.desc);
        holder.newsTypeTv.setText(newsData.type);

        String dateTime = null;
        try {
            Date date = formatTZ.parse(newsData.publishedAt.substring(0, 19));
            dateTime = format.format(date);
        } catch (Exception e) {
            Log.i("take", e.toString());
        }
        holder.publishDateTv.setText(dateTime);
        holder.publishDateTv.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return mNewsList != null ? mNewsList.size() : 0;
    }

    public void setNewsList(List<NewsData> newsList) {
        this.mNewsList = newsList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.newsTitle) TextView newsTitleTv;
        @BindView(R.id.newsType) TextView newsTypeTv;
        @BindView(R.id.publishDate) TextView publishDateTv;
        @BindView(R.id.collect_Iv) ImageView favIv;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
