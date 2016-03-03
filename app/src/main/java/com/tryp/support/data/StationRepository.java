package com.tryp.support.data;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.tryp.support.stations_list.StationsListContract;

import java.util.Collection;
import java.util.List;

/**
 * Created by cliffroot on 01.03.16.
 */
public interface StationRepository {

    interface Callback<T> {
        void onDone (T result);
        void onFail (String reason);
    }

    void getAllStations         (Callback<Collection<Station>> callback);
    void getVisibleStations     (@NonNull LatLngBounds bounds, Callback<Collection<Station>> callback);
    void getStationInfo         (Integer id, Callback<Station> callback);
    void getFilteredStations    (@NonNull StationsListContract.View.Filter filter,
                                    @NonNull LatLng myPosition, Callback<Collection<Station>> callback);
    void getAllFuelTypes        (Callback<List<String>> callback);


}
