package com.tryp.support.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.common.collect.Lists;
import com.tryp.support.CustomApplication;
import com.tryp.support.stations_list.StationsListContract;
import com.tryp.support.utils.LocationHelper;

import org.androidannotations.annotations.EBean;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmResults;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

/**
 * Created by cliffroot on 10.03.16.
 */
@EBean
public class MockRealmStationRepository implements StationRepository {

    static List<Station> DUMMY_VALUES = Lists.newArrayList(
            new Station(1, "Барс 2000", "Дмитра Луценка, 11", 50.3846, 30.4447),
            new Station(2, "АГЗП", "Кільцева дорога, 110", 50.3833, 30.4411),
            new Station(3, "Газовик", "Кільцева дорога, 8", 50.3909, 30.4297)
    );

    static {
        Station.addTypeToPriceEntry(DUMMY_VALUES.get(0), Pair.create("A98", String.valueOf(19.45d)));
        Station.addTypeToPriceEntry(DUMMY_VALUES.get(0), Pair.create("A95", String.valueOf(18.21d)));
        Station.addTypeToPriceEntry(DUMMY_VALUES.get(1), Pair.create("A98", String.valueOf(19.82d)));
        Station.addTypeToPriceEntry(DUMMY_VALUES.get(1), Pair.create("A95", String.valueOf(18.01d)));
        Station.addTypeToPriceEntry(DUMMY_VALUES.get(2), Pair.create("ГАЗ", String.valueOf(5.15d)));
    }

    RealmResults<Station> stations;

    public MockRealmStationRepository(Context context) {
        setupRealm(context);
    }

    public void setupRealm (Context context) {

        Realm realm = Realm.getInstance(((CustomApplication) context.getApplicationContext()).getRealmConfiguration());
        stations = realm.where(Station.class).findAll();

        realm.beginTransaction();

        realm.copyToRealmOrUpdate(DUMMY_VALUES.get(0));
        realm.copyToRealmOrUpdate(DUMMY_VALUES.get(1));
        realm.copyToRealmOrUpdate(DUMMY_VALUES.get(2));

        realm.commitTransaction();
    }

    @Override
    public void getAllStations(Callback<Collection<Station>> callback) {
        callback.onDone(stations);
    }

    @Override
    public void getVisibleStations(@NonNull LatLngBounds bounds, Callback<Collection<Station>> callback) {
        callback.onDone(
                StreamSupport.stream(stations).filter(station -> bounds.contains(Station.getPosition(station))).collect(Collectors.toList()));
    }

    @Override
    public void getStationInfo(Integer id, Callback<Station> callback) {
        callback.onDone(stations.where().equalTo("id", id).findFirst());
    }

    @Override
    public void getFilteredStations(@NonNull StationsListContract.View.Filter filter,
                                    @NonNull LatLng myPosition, Callback<Collection<Station>> callback) {
        callback.onDone(StreamSupport.stream(stations)
                .filter(station ->
                        Station.getPriceByFuelType(station, filter.getFuelType()) != null &&
                                LocationHelper.distanceBetween(Station.getPosition(station), myPosition) < filter.getDistance())
                .sorted((s1, s2) ->
                        Float.compare(LocationHelper.distanceBetween(Station.getPosition(s1), myPosition),
                                LocationHelper.distanceBetween(Station.getPosition(s2), myPosition)))
                .collect(Collectors.toList()));
    }

    @Override
    public void getAllFuelTypes(Callback<List<String>> callback) {
        Set<String> res = new HashSet<>();

        for (Station station: stations) {
            Log.e("REALM", station.getFuelTypeToPriceMap().get(0).getLeft());
            res.addAll(Station.getFuelTypes(station));
        }
        callback.onDone(new LinkedList<>(res));
    }
}
