package com.tryp.support.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.base.Preconditions;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;
import org.parceler.Transient;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

/**
 * Created by cliffroot on 01.03.16.
 */
@org.parceler.Parcel(value = Parcel.Serialization.BEAN, analyze = { Station.class })
public class Station extends RealmObject {

    private static final int DEFAULT_RATING = 4;

    @NonNull
    @PrimaryKey
    private Integer id;

    @NonNull
    private String name;

    @NonNull
    private String address;

    private double positionLatitude;
    private double positionLongitude;

    @Transient
    private RealmList<RealmPair> fuelTypeToPriceMap = new RealmList<>();

    private int rating = DEFAULT_RATING;

    public Station() {}

    @ParcelConstructor
    public Station(@NonNull Integer id, @NonNull String name, @NonNull String address, double positionLatitude, double positionLongitude) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.positionLatitude   = positionLatitude;
        this.positionLongitude  = positionLongitude;

    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    @NonNull
    public Integer getId() {
        return id;
    }

    public void setId(@NonNull Integer id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getAddress() {
        return address;
    }

    public void setAddress(@NonNull String address) {
        this.address = address;
    }

    @NonNull
    public static LatLng getPosition(@NonNull Station station) {
        return new LatLng(station.getPositionLatitude(), station.getPositionLongitude());
    }

    public static void setPosition(@NonNull Station station, @NonNull LatLng position) {
        station.setPositionLatitude(position.latitude);
        station.setPositionLongitude(position.longitude);
    }

    public double getPositionLatitude() {
        return positionLatitude;
    }

    public void setPositionLatitude(double positionLatitude) {
        this.positionLatitude = positionLatitude;
    }

    public double getPositionLongitude() {
        return positionLongitude;
    }

    public void setPositionLongitude(double positionLongitude) {
        this.positionLongitude = positionLongitude;
    }

    public static Set<String> getFuelTypes (Station station) {
        return new HashSet<>(StreamSupport.stream(station.getFuelTypeToPriceMap()).map((RealmPair::getLeft)).collect(Collectors.toList()));

    }

    @Nullable
    public static String getPriceByFuelType (@NonNull Station station, @NonNull String fuelType) {
        Preconditions.checkNotNull(fuelType);

        List<RealmPair> list =
                StreamSupport.stream(station.getFuelTypeToPriceMap()).filter(pair -> pair.getLeft().equals(fuelType)).collect(Collectors.toList());
        if (list.size() > 0) {
            return list.get(0).getRight();
        } else {
            return null;
        }
    }

    public static Station addTypeToPriceEntry (@NonNull Station station, @NonNull Pair<String, String> entry) {
        Preconditions.checkNotNull(entry);
        station.fuelTypeToPriceMap.add(new RealmPair(entry.first, entry.second));
        return station;
    }

    public static void loadFuelToPriceMap (RealmConfiguration config, Station station) {
        RealmList list = Realm.getInstance(config).where(Station.class).equalTo("id", station.getId()).findFirst().getFuelTypeToPriceMap();
        station.setFuelTypeToPriceMap(list);

    }

    @Transient
    public RealmList<RealmPair> getFuelTypeToPriceMap() {
        return fuelTypeToPriceMap;
    }

    @Transient
    public void setFuelTypeToPriceMap(RealmList<RealmPair> fuelTypeToPriceMap) {
        this.fuelTypeToPriceMap = fuelTypeToPriceMap;
    }
}
