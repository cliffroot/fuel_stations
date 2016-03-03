package com.tryp.support.utils;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by cliffroot on 03.03.16.
 */
public class LocationHelper {

    private final static int METERS_IN_KILOMETERS = 1000;

    public LocationHelper () {
        throw new UnsupportedOperationException();
    }

    public static float distanceBetween (LatLng from, LatLng to) {
        float[] results = new float[3];
        Location.distanceBetween(from.latitude, from.longitude, to.latitude, to.longitude, results);

        return results[0] / METERS_IN_KILOMETERS;
    }

    public static float roundToHalf(float x) {
        return (float) (Math.ceil(x * 2) / 2);
    }

}
