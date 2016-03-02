package com.tryp.support.data;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLngBounds;

import java.util.Collection;

/**
 * Created by cliffroot on 01.03.16.
 */
public interface StationRepository {

    interface Callback<T> {
        void onDone (T result);
        void onFail (String reason);
    }

    void getAllStations     (Callback<Collection<Station>> callback);
    void getVisibleStations (@NonNull LatLngBounds bounds, Callback<Collection<Station>> callback);
    void getStationInfo (Integer id, Callback<Station> callback);


}
