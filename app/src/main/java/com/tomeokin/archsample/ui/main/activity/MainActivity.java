package com.tomeokin.archsample.ui.main.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Menu;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.tomeokin.archsample.R;
import com.tomeokin.archsample.ui.base.BaseActivity;
import com.tomeokin.archsample.ui.main.MainMvpView;
import com.tomeokin.archsample.ui.main.adapter.MainPageAdapter;
import com.tomeokin.archsample.ui.main.adapter.TabEntity;
import com.tomeokin.archsample.ui.main.fragment.CollectFragment;
import com.tomeokin.archsample.ui.main.fragment.NewsListFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 首页
 */
public class MainActivity extends BaseActivity implements MainMvpView {

    @BindView(R.id.tabLayout) CommonTabLayout tabLayout;
    @BindView(R.id.viewPager) ViewPager viewPager;

    private MainPageAdapter pageAdapter;
    private List<Fragment> fragments;
    private List<String> titles;
    private ArrayList<CustomTabEntity> tabEntities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        component().inject(this);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        fragments = new ArrayList<>(2);
        fragments.add(new NewsListFragment());
        fragments.add(new CollectFragment());
        titles = new ArrayList<>(2);
        titles.add("News");
        titles.add("Collect");
        tabEntities = new ArrayList<>();
        tabEntities.add(new TabEntity(titles.get(0), R.drawable.home_fill, R.drawable.home));
        tabEntities.add(new TabEntity(titles.get(1), R.drawable.fav_fill, R.drawable.fav));

        pageAdapter = new MainPageAdapter(getSupportFragmentManager(), fragments, titles);
        viewPager.setAdapter(pageAdapter);
        tabLayout.setTabData(tabEntities);
        tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                viewPager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                    int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tabLayout.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
}
