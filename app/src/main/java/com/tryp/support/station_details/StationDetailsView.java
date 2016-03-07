package com.tryp.support.station_details;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tryp.support.R;
import com.tryp.support.data.Station;
import com.tryp.support.logging.LoggingActivity;
import com.tryp.support.utils.MapBoxApiPathProvider_;
import com.tryp.support.utils.PathProvider;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import java8.util.stream.StreamSupport;

/**
 * Created by cliffroot on 07.03.16.
 */
@EActivity(R.layout.station_details_activity)
public class StationDetailsView extends LoggingActivity implements StationDetailsContract.View {

    public final static String STATION_EXTRA_KEY = "STATION_EXTRA_KEY";
    public final static String CURRENT_LOCATION_EXTRA_KEY = "CURRENT_LOCATION";

    public final static int DEFAULT_PADDING = 128;

    @Extra(STATION_EXTRA_KEY)
    Station station;

    @Extra(CURRENT_LOCATION_EXTRA_KEY)
    LatLng currentLocation;

    @ViewById(R.id.map_view)
    MapView mapView;

    @AfterViews
    void setupMap () {
        mapView.onCreate(null);
    }

    @AfterViews
    void showMarkersOnMap () {
        mapView.getMapAsync(map -> {
            map.addMarker(new MarkerOptions().position(currentLocation).title("Me"));
            Marker m2 = map.addMarker(new MarkerOptions().position(station.getPosition()).title(station.getName()));
            m2.showInfoWindow();
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(station.getPosition(), currentLocation), DEFAULT_PADDING));

            map.getUiSettings().setMyLocationButtonEnabled(false);
            map.getUiSettings().setCompassEnabled(false);

            PathProvider provider = MapBoxApiPathProvider_.getInstance_(this);
            showPath(provider);
        });
    }

    void showPath (PathProvider pathProvider) {
        pathProvider.getSegments(station.getPosition(), currentLocation, new PathProvider.Callback<List<LatLng>>() {
            @Override
            public void onCompleted(List<LatLng> result) {
                final PolylineOptions polyline = new PolylineOptions().color(Color.BLUE).width(5.f);
                StreamSupport.stream(result).forEach(polyline::add);
                StationDetailsView.this.runOnUiThread(() -> mapView.getMapAsync(map -> map.addPolyline(polyline)));
            }

            @Override
            public void onFail() {
                Log.e("from stationsView", "wow, fail");
            }
        });
    }

    @Override
    public void onResume () {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause () {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy () {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void displayProgressBar(boolean active) {

    }

    @Override
    public void displayStation(@NonNull Station station) {

    }

    @Override
    public void displayCurrentRating() {

    }
}
