package com.tryp.support.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.base.Preconditions;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by cliffroot on 01.03.16.
 */
public class Station implements Parcelable {

    @NonNull
    Integer id;

    @NonNull
    String name;

    @NonNull
    String address;

    @NonNull
    LatLng position;

    Map<String, BigDecimal> fuelTypeToPriceMap = new HashMap<>();

    public Station(@NonNull Integer id, @NonNull String name, @NonNull String address, @NonNull LatLng position,
                   Map<String, BigDecimal> fuelTypeToPriceMap) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.position = position;
        this.fuelTypeToPriceMap = fuelTypeToPriceMap;
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
    public LatLng getPosition() {
        return position;
    }

    public void setPosition(@NonNull LatLng position) {
        this.position = position;
    }

    public Set<String> getFuelTypes () {
        return fuelTypeToPriceMap.keySet();
    }

    @Nullable
    public BigDecimal getPriceByFuelType (@NonNull String fuelType) {
        Preconditions.checkNotNull(fuelType);
        return fuelTypeToPriceMap.get(fuelType);
    }

    Station addTypeToPriceEntry (@NonNull Pair<String, BigDecimal> entry) {
        Preconditions.checkNotNull(entry);
        fuelTypeToPriceMap.put(entry.first, entry.second);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Station station = (Station) o;

        if (!id.equals(station.id)) return false;
        if (!name.equals(station.name)) return false;
        if (!address.equals(station.address)) return false;
        if (!position.equals(station.position)) return false;
        return !(fuelTypeToPriceMap != null ? !fuelTypeToPriceMap.equals(station.fuelTypeToPriceMap) : station.fuelTypeToPriceMap != null);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + address.hashCode();
        result = 31 * result + position.hashCode();
        result = 31 * result + (fuelTypeToPriceMap != null ? fuelTypeToPriceMap.hashCode() : 0);
        return result;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.name);
        dest.writeString(this.address);
        dest.writeParcelable(this.position, 0);
        dest.writeMap(this.fuelTypeToPriceMap);
    }

    protected Station(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.name = in.readString();
        this.address = in.readString();
        this.position = in.readParcelable(LatLng.class.getClassLoader());
        this.fuelTypeToPriceMap = in.readHashMap(BigDecimal.class.getClassLoader());
    }

    public static final Parcelable.Creator<Station> CREATOR = new Parcelable.Creator<Station>() {
        public Station createFromParcel(Parcel source) {
            return new Station(source);
        }

        public Station[] newArray(int size) {
            return new Station[size];
        }
    };
}
