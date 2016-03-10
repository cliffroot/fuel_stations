package com.tryp.support.station_details;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.common.collect.Maps;
import com.tryp.support.CustomApplication;
import com.tryp.support.R;
import com.tryp.support.adapter.PriceAdapter;
import com.tryp.support.data.Station;
import com.tryp.support.logging.LoggingActivity;
import com.tryp.support.utils.MapBoxApiPathProvider_;
import com.tryp.support.utils.PathProvider;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;
import org.parceler.Parcels;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import java8.util.stream.StreamSupport;

/**
 * Created by cliffroot on 07.03.16.
 */
@EActivity(R.layout.station_details_activity)
public class StationDetailsView extends LoggingActivity implements StationDetailsContract.View {

    public final static String STATION_EXTRA_KEY = "STATION_EXTRA_KEY";
    public final static String CURRENT_LOCATION_EXTRA_KEY = "CURRENT_LOCATION";

    public final static int DEFAULT_PADDING = 128;

    //@Extra(STATION_EXTRA_KEY)
    Station station;

    @Extra(CURRENT_LOCATION_EXTRA_KEY)
    LatLng currentLocation;

    @ViewById(R.id.map_view)
    MapView mapView;

    @ViewById(R.id.station_rating_bar)
    RatingBar stationRatingBar;

    @ViewById(R.id.recycler_view_prices)
    RecyclerView pricesRecyclerView;

    @ColorRes(R.color.colorAccent)
    int tintColor;

    @ViewById(R.id.cafe_image_view)
    ImageView cafeImageView;

    @ViewById(R.id.repair_image_view)
    ImageView repairImageView;

    @ViewById(R.id.shop_image_view)
    ImageView shopwImageView;

    @AfterViews
    void setupMap () {
        mapView.onCreate(null);
    }

    @Override
    public void onCreate (Bundle b) {
        super.onCreate(b);
        station = Parcels.unwrap(getIntent().getParcelableExtra(STATION_EXTRA_KEY));
        Station.loadFuelToPriceMap(((CustomApplication)getApplicationContext()).getRealmConfiguration(), station);
    }

    @AfterViews
    void showMarkersOnMap () {

        mapView.getMapAsync(map -> {
            map.addMarker(new MarkerOptions().position(currentLocation).title("Me"));
            Marker m2 = map.addMarker(new MarkerOptions().position(Station.getPosition(station)).title(station.getName()));
            m2.showInfoWindow();
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(Station.getPosition(station), currentLocation), DEFAULT_PADDING));

            map.getUiSettings().setMyLocationButtonEnabled(false);
            map.getUiSettings().setCompassEnabled(false);

            PathProvider provider = MapBoxApiPathProvider_.getInstance_(this);
            showPath(provider);
        });
    }

    @AfterViews
    void setupRatingBar () {
        Drawable stars = stationRatingBar.getProgressDrawable();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            stars.setTint(tintColor);
        } else {
            stars.setColorFilter(tintColor, PorterDuff.Mode.SRC_ATOP);
        }
    }

    @AfterViews
    void setupRecyclerView () {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        pricesRecyclerView.setLayoutManager(layoutManager);
        pricesRecyclerView.setItemAnimator(new DefaultItemAnimator());

        List<Map.Entry> fuelTypesToPrices = new LinkedList<>();
        for (String type: Station.getFuelTypes(station)) {
            fuelTypesToPrices.add(Maps.immutableEntry(type, Station.getPriceByFuelType(station, type)));
        }
        PriceAdapter adapter = new PriceAdapter(this, fuelTypesToPrices);
        pricesRecyclerView.setAdapter(adapter);

    }

    @AfterViews
    void setupIcons () {
        Drawable drawable = cafeImageView.getDrawable();

        int color = Color.parseColor("#757575");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable.setTint(color);
        } else {
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }

        drawable = shopwImageView.getDrawable();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable.setTint(color);
        } else {
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }


        drawable = repairImageView.getDrawable();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable.setTint(color);
        } else {
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }
    }

    void showPath (PathProvider pathProvider) {
        pathProvider.getSegments(Station.getPosition(station), currentLocation, new PathProvider.Callback<List<LatLng>>() {
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
