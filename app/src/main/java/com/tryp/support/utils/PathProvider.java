package com.tryp.support.utils;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by cliffroot on 21.11.15.
 */
public interface PathProvider {

    interface Callback<T> {
        void onCompleted(T result);
        void onFail();
    }

    void getSegments(LatLng from, LatLng to, Callback<List<LatLng>> callback);
    void getDistance(LatLng from, LatLng to, Callback<Float> callback);
    void getTime    (LatLng from, LatLng to, Callback<Integer> callback);

}
