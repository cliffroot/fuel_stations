package com.tryp.support;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.common.eventbus.EventBus;
import com.tryp.support.data.MockStationRepository;
import com.tryp.support.data.StationRepository;
import com.tryp.support.stations.StationsPresenter;
import com.tryp.support.stations.StationsView;
import com.tryp.support.utils.LocationReceivedEvent;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_host)
@OptionsMenu(R.menu.menu_host)
public class HostActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private final static int LOCATION_PERMESSION_REQUEST_CODE = 17;
    private final static LatLng defaultLocation = new LatLng(50.455939, 30.519617);

    @ViewById(R.id.toolbar)
    Toolbar toolbar;

    @FragmentById(R.id.fragment)
    StationsView view;

    @Bean(MockStationRepository.class)
    StationRepository repository;

    StationsPresenter presenter;
    GoogleApiClient googleApiClient;
    EventBus eventBus;

    @Override
    protected void onCreate (Bundle b) {
        super.onCreate(b);
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        eventBus = new EventBus();
    }

    @Override
    public void onStart () {
        super.onStart();
        googleApiClient.connect();
    }

    @AfterViews
    void injection () {
        presenter = new StationsPresenter(repository, view);

    }

    @NonNull public EventBus getEventBus() {
        return eventBus;
    }

    @AfterViews
    void setupToolbar () {
        setSupportActionBar(toolbar);
    }


    @Override
    public void onConnected(Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMESSION_REQUEST_CODE);
        } else {
            Location lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            Log.e("HostActivity", "location posted coz was granted beforehand");
            eventBus.post(new LocationReceivedEvent(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude())));
        }
    }

    @Override
    public void onRequestPermissionsResult (int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == LOCATION_PERMESSION_REQUEST_CODE) {
            if ( ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Location lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                Log.e("HostActivity", "lastKnownLoaction is " + lastKnownLocation);
                eventBus.post(new LocationReceivedEvent(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude())));
                Log.e("HostActivity", "location posted coz was granted just now");
            } else {
                eventBus.post(new LocationReceivedEvent(defaultLocation));
                Log.e("HostActivity", "location posted coz was not granted");
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
