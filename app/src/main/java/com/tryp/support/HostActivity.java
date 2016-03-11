package com.tryp.support;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.common.eventbus.EventBus;
import com.tryp.support.data.MockStationRepository_;
import com.tryp.support.data.StationRepository;
import com.tryp.support.logging.LoggingActivity;
import com.tryp.support.network.UpdateService;
import com.tryp.support.stations.StationsPresenter;
import com.tryp.support.utils.LocationReceivedEvent;


public class HostActivity extends LoggingActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ActivityCompat.OnRequestPermissionsResultCallback,
        LocationListener {

    private final static int LOCATION_PERMISSION_REQUEST_CODE = 17;
    private final static LatLng defaultLocation = new LatLng(50.455939, 30.519617);
    private final static String UPDATE_SERVICE_TAG = "updateServiceTag";

    private final static int POLL_STATIONS_TIME = 3600 * 12; // TOOD: get the actual value from preferences

    Toolbar toolbar;

    ViewPager pager;
    StationRepository repository;

    GoogleApiClient googleApiClient;
    EventBus eventBus;
    Location currentLocation;
    FragmentPagerAdapter adapter;
    TabLayout tabLayout;

    GcmNetworkManager gcmNetworkManager;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_host);

        setupGoogleServicesClient();
        setupToolbar();
        setupPager();
        setupUpdateService();

    }

    public void setupUpdateService () {
        gcmNetworkManager = GcmNetworkManager.getInstance(this);
        PeriodicTask task = new PeriodicTask.Builder()
                .setService(UpdateService.class)
                .setTag(UPDATE_SERVICE_TAG)
                .setPeriod(POLL_STATIONS_TIME)
                .setFlex(600)
                .setPersisted(true)
                .setRequiresCharging(false)
                .setUpdateCurrent(true)
                .build();

        gcmNetworkManager.schedule(task);
    }

    @Override
    public void onStart() {
        super.onStart();
        getEventBus().register(this);
        googleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        googleApiClient.disconnect();
        getEventBus().unregister(this);
    }

    @NonNull
    public EventBus getEventBus() {
        if (eventBus == null) {
            eventBus = new EventBus();
        }
        return eventBus;
    }

    public StationsPresenter getStationsPresenter() {
        return ((HostAdapter) adapter).getStationsPresenter();
    }

    public LatLng getCurrentLocation() {
        if (currentLocation == null) {
            if (googleApiClient.isConnected()) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    return new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                } else {
                    Log.w("HostActivity", "No location at all!");
                    return null;
                }
            } else {
                Log.w("HostActivity", "No location at all!");
                return null;
            }
        } else {
            return new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        }
    }

    void setupGoogleServicesClient () {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    void setupToolbar() {
        toolbar = ((Toolbar) findViewById(R.id.toolbar));
        setSupportActionBar(toolbar);
    }

    void setupPager () {
        repository = MockStationRepository_.getInstance_(this);
        adapter = new HostAdapter(getSupportFragmentManager(), this);
        pager = ((ViewPager) findViewById(R.id.pager));
        pager.setAdapter(adapter);

        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(pager);
    }



    @Override
    public void onConnected(Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
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
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ( ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Location lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                currentLocation = lastKnownLocation;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_host, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            settings();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void settings () {
        Intent i = new Intent(this, AppPreferenceActivity_.class);
        startActivity(i);
    }


}
