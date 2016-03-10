package com.tryp.support;

import android.app.Application;

import io.realm.RealmConfiguration;

/**
 * Created by cliffroot on 10.03.16.
 */
public class CustomApplication extends Application {

    private RealmConfiguration config;

    @Override
    public void onCreate () {
        super.onCreate();
        config = new RealmConfiguration.Builder(this).build();
    }

    public RealmConfiguration getRealmConfiguration () {
        return config;
    }
}
