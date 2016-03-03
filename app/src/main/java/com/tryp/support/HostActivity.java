package com.tryp.support;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.tryp.support.data.MockStationRepository;
import com.tryp.support.data.StationRepository;
import com.tryp.support.stations.StationsPresenter;
import com.tryp.support.stations.StationsView;
import com.tryp.support.stations_list.StationsListPresenter;
import com.tryp.support.stations_list.StationsListView;
import com.tryp.support.utils.LocationReceivedEvent;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_host)
@OptionsMenu(R.menu.menu_host)
public class HostActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ActivityCompat.OnRequestPermissionsResultCallback,
        LocationListener {

    private final static int LOCATION_PERMESSION_REQUEST_CODE = 17;
    private final static LatLng defaultLocation = new LatLng(50.455939, 30.519617);

    @ViewById(R.id.toolbar)
    Toolbar toolbar;

    @ViewById(R.id.pager)
    ViewPager pager;

    @FragmentById(R.id.fragment)
    StationsView view;

    @Bean(MockStationRepository.class)
    StationRepository repository;

    StationsPresenter       stationsPresenter;
    StationsListPresenter   stationsListPresenter;
    GoogleApiClient         googleApiClient;
    EventBus                eventBus;
    Location                currentLocation;
    FragmentPagerAdapter    adapter;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        googleApiClient.connect();
        getEventBus().register(this);
    }

    @Override
    public void onStop () {
        super.onStop();
        googleApiClient.disconnect();
        getEventBus().unregister(this);
    }

    @AfterViews
    void injection() {
        adapter = new HostAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        stationsPresenter = new StationsPresenter(repository, (StationsView) adapter.getItem(0));

    }

    @Subscribe
    void initializeStationsListPresenter (LocationReceivedEvent event) {
        stationsListPresenter = new StationsListPresenter(repository, (StationsListView) adapter.getItem(1), getCurrentLocation());
        stationsListPresenter.initialSetup();
    }

    @NonNull
    public EventBus getEventBus() {
        if (eventBus == null) {
            eventBus = new EventBus();
        }
        return eventBus;
    }

    @NonNull
    public StationsPresenter getStationsPresenter() {
        return stationsPresenter;
    }

    public LatLng getCurrentLocation() {
        return new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
    }

    @AfterViews
    void setupToolbar() {
        setSupportActionBar(toolbar);
    }


    @Override
    public void onConnected(Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMESSION_REQUEST_CODE);
        } else {
            currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            eventBus.post(new LocationReceivedEvent(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())));
            requestLocationUpdates();
        }
    }

    private void requestLocationUpdates() {
        LocationRequest request = new LocationRequest();
        request.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient, request, this);
        }
    }

    @Override
    public void onRequestPermissionsResult (int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == LOCATION_PERMESSION_REQUEST_CODE) {
            if ( ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Location lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                eventBus.post(new LocationReceivedEvent(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude())));
                requestLocationUpdates();
            } else {
                eventBus.post(new LocationReceivedEvent(defaultLocation));
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location lastKnownLocation) {
        eventBus.post(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()));
        currentLocation = lastKnownLocation;
    }

    @OptionsItem(R.id.action_settings)
    public void settings () {
        Intent i = new Intent(this, AppPreferenceActivity_.class);
        startActivity(i);
    }
}
