package com.tomeokin.archsample.ui.base;

import android.support.v7.app.AppCompatActivity;

import com.tomeokin.archsample.ArchApplication;
import com.tomeokin.archsample.injection.component.ActivityComponent;
import com.tomeokin.archsample.injection.component.DaggerActivityComponent;
import com.tomeokin.archsample.injection.module.ActivityModule;

/**
 * BaseActivity，提供通用的 ActivityComponent
 */
public class BaseActivity extends AppCompatActivity {

    private ActivityComponent mActivityComponent;

    public ActivityComponent component() {
        if (mActivityComponent == null) {
            mActivityComponent = DaggerActivityComponent.builder()
                    .applicationComponent(((ArchApplication) getApplication()).component())
                    .activityModule(new ActivityModule(this))
                    .build();
        }
        return mActivityComponent;
    }
}
