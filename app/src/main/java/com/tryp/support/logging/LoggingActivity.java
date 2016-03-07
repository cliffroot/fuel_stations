package com.tryp.support.logging;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by cliffroot on 04.03.16.
 */
public class LoggingActivity extends AppCompatActivity {

    final String TAG = "Log from " + getClass().getName() + hashCode();

    @Override
    public void onCreate (Bundle bundle) {
        super.onCreate(bundle);
        Log.w(TAG, "onCreate");
    }

    @Override
    public void onDestroy () {
        super.onDestroy();
        Log.w(TAG, "onDestroy");
    }

    @Override
    public void onSaveInstanceState (Bundle b) {
        super.onSaveInstanceState(b);
        Log.w(TAG, "onSaveInstanceState");
    }

    @Override
    public void onStop () {
        super.onStop();
        Log.w(TAG, "onStop");
    }

    @Override
    public void onStart () {
        super.onStart();
        Log.w(TAG, "onStart");
    }

    @Override
    public void onPause () {
        super.onPause();
        Log.w(TAG, "onPause");
    }

    @Override
    public void onResume () {
        super.onResume();
        Log.w(TAG, "onResume");
    }
}
