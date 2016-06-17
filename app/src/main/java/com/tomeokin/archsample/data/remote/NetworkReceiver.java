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
package com.tomeokin.archsample.data.remote;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationCompat;

import com.orhanobut.logger.Logger;
import com.tomeokin.archsample.ArchApplication;
import com.tomeokin.archsample.R;
import com.tomeokin.archsample.data.CollectManager;
import com.tomeokin.archsample.ui.main.activity.MainActivity;

/**
 * 网络状态监听广播
 */
public class NetworkReceiver extends BroadcastReceiver {
    public static final int ACTION_SHOW_NOTIFICATION_REQUEST_CODE = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences preferences = ArchApplication.getApp()
                .getSharedPreferences(CollectManager.PREF_COLLECTION, Context.MODE_PRIVATE);
        final boolean haveCollection =
                preferences.getBoolean(CollectManager.HAVE_NEW_COLLECTIONS, false);
        final int count = preferences.getInt(CollectManager.NEW_COLLECTION_COUNT, 0);
        if (!haveCollection || count <= 0) {
            return;
        }

        Logger.t("NetworkReceiver").i("connection state change");
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            ConnectivityManager manager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (wifi.isConnected()) {
                Logger.t("NetworkReceiver").i("wifi is connected");
                //List<Collection> collections = CollectManager.getInstance()
                //    .getCollectionsFromPref();

                PendingIntent pi = PendingIntent.getActivity(context, 0,
                        new Intent(context, MainActivity.class), 0);
                Notification notification =
                        new NotificationCompat.Builder(context).setTicker("have new collections")
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle(count + " new collections")
                                //.setContentText("have new collections")
                                .setContentIntent(pi)
                                .setAutoCancel(true)
                                .build();
                NotificationManager notificationManager =
                        (NotificationManager) context.getSystemService(
                                Context.NOTIFICATION_SERVICE);
                notificationManager.notify(ACTION_SHOW_NOTIFICATION_REQUEST_CODE, notification);
                preferences.edit()
                        .putBoolean(CollectManager.HAVE_NEW_COLLECTIONS, false)
                        .apply();
            }
        }
    }
}
