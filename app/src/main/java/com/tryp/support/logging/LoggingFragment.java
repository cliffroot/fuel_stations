package com.tryp.support.logging;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by cliffroot on 04.03.16.
 */
public class LoggingFragment extends Fragment {

    final String TAG = "Log from " + getClass().getName() + hashCode();

    @Override
    public void onAttach (Context context) {
        super.onAttach(context);
        Log.w(TAG, "onAttach");
    }

    @Override
    public void onCreate (Bundle b) {
        super.onCreate(b);
        Log.w(TAG, "onCreate");
    }

    @Override
    public View onCreateView (LayoutInflater li, ViewGroup vg, Bundle b) {
        View v = super.onCreateView(li, vg, b);
        Log.w(TAG, "onCreateView");
        return v;
    }

    @Override
    public void onActivityCreated (Bundle b) {
        super.onActivityCreated(b);
        Log.w(TAG, "onActivityCreated");
    }

    @Override
    public void onStart () {
        super.onStart();
        Log.w(TAG, "onStart");
    }

    @Override
    public void onResume () {
        super.onResume();
        Log.w(TAG, "onResume");
    }

    @Override
    public void onPause () {
        super.onPause();
        Log.w(TAG, "onPause");
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
    public void onDestroyView () {
        super.onDestroyView();
        Log.w(TAG, "onDestroyView");
    }

    @Override
    public void onDestroy () {
        super.onDestroy();
        Log.w(TAG, "onDestroy");
    }

    @Override
    public void onDetach () {
        super.onDetach();
        Log.w(TAG, "onDetach");
    }


}
