package com.tryp.support.data;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.tryp.support.stations_list.StationsListContract;
import com.tryp.support.utils.LocationHelper;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;

import java.util.Collection;
import java.util.List;

import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

/**
 * Created by cliffroot on 01.03.16.
 */
@EBean
public class MockStationRepository implements StationRepository {

    public MockStationRepository () {}

    static List<Station> DUMMY_VALUES = Lists.newArrayList(
            new Station(1, "Барс 2000", "Дмитра Луценка, 11", 50.3846, 30.4447),
            new Station(2, "АГЗП", "Кільцева дорога, 110", 50.3833, 30.4411),
            new Station(2, "Газовик", "Кільцева дорога, 8", 50.3909, 30.4297)
            );

    static {
        Station.addTypeToPriceEntry(DUMMY_VALUES.get(0), Pair.create("A98", String.valueOf(19.45d)));
        Station.addTypeToPriceEntry(DUMMY_VALUES.get(0), Pair.create("A95", String.valueOf(18.21d)));
        Station.addTypeToPriceEntry(DUMMY_VALUES.get(1), Pair.create("A98", String.valueOf(19.82d)));
        Station.addTypeToPriceEntry(DUMMY_VALUES.get(1), Pair.create("A95", String.valueOf(18.01d)));
        Station.addTypeToPriceEntry(DUMMY_VALUES.get(2), Pair.create("ГАЗ", String.valueOf(5.15d)));
    }

    @Override
    @Background
    public void getAllStations(Callback<Collection<Station>> callback) {
        callback.onDone(DUMMY_VALUES);
    }

    @Override
    @Background
    public void getVisibleStations(@NonNull LatLngBounds bounds, Callback<Collection<Station>> callback) {
        Preconditions.checkNotNull(bounds);
        callback.onDone(DUMMY_VALUES);
    }

    @Override
    @Background
    public void getFilteredStations(@NonNull StationsListContract.View.Filter filter,
                                    @NonNull LatLng myPosition, Callback<Collection<Station>> callback) {
        Preconditions.checkNotNull(filter);
        callback.onDone(StreamSupport.stream(DUMMY_VALUES)
                .filter(station ->
                        Station.getPriceByFuelType(station, filter.getFuelType()) != null &&
                                LocationHelper.distanceBetween(Station.getPosition(station), myPosition) < filter.getDistance())
                .sorted((s1, s2) ->
                        Float.compare(LocationHelper.distanceBetween(Station.getPosition(s1), myPosition),
                                LocationHelper.distanceBetween(Station.getPosition(s2), myPosition)))
                .collect(Collectors.toList()));
    }

    @Override
    @Background
    public void getAllFuelTypes(Callback<List<String>> callback) {
        callback.onDone(Lists.newArrayList("A98", "A95", "ГАЗ"));
    }

    @Override
    @Background
    public void getStationInfo(@IntRange(from = 1, to = 3) Integer id, Callback<Station> callback) {
        getAllStations(new Callback<Collection<Station>>() {
            @Override
            public void onDone(Collection<Station> result) {
                StreamSupport.stream(result).filter(station ->
                        station.getId().equals(id)).forEach((Station station) ->
                            callback.onDone(station));
            }

            @Override
            public void onFail(String reason) {
                callback.onFail(reason);
            }
        });
    }

}
