package com.tryp.support.stations;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.eventbus.Subscribe;
import com.tryp.support.HostActivity;
import com.tryp.support.R;
import com.tryp.support.data.Station;
import com.tryp.support.logging.LoggingFragment;
import com.tryp.support.utils.LocationReceivedEvent;
import com.tryp.support.utils.SwipeDismissTouchListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;

import java.util.Collection;

import java8.util.stream.StreamSupport;

@EFragment(R.layout.fragment_host)
public class StationsView extends LoggingFragment implements StationsContract.View {

    private static final float MAP_ZOOM_DEFAULT = 12.f;

    static Fragment instance;

    @ViewById(R.id.map_view)
    MapView mapView;

    @ViewById(R.id.progress_bar)
    ProgressBar progressBar;

    @ViewById(R.id.station_address)
    TextView stationAddressView;

    @ViewById(R.id.station_name)
    TextView stationNameView;

    @ViewById(R.id.station_card)
    CardView stationCard;

    @ViewById(R.id.see_more_button)
    Button seeMoreButton;

    BiMap<Marker, Station> markerStationsMap = HashBiMap.create();

    Marker currentMarker;
    Marker myLocationMarker;

    @InstanceState
    Station currentStation;

    @InstanceState
    CameraPosition currentCameraPosition;

    StationsContract.UserActionListener actionListener;

    public StationsView() { }

    @Override
    public void onCreate (Bundle b) {
        super.onCreate(b);
    }


    public static Fragment getInstance() {
        instance = new StationsView_();
        //instance.setRetainInstance(true);
        return instance;
    }

    @AfterViews
    void setupMap () {
        mapView.onCreate(null);
    }


    @AfterViews
    void setupTouchDismissalListener () {
        stationCard.setOnTouchListener(new SwipeDismissTouchListener(stationCard, null, new SwipeDismissTouchListener.DismissCallbacks() {
            @Override
            public boolean canDismiss(Object token) {
                return true;
            }

            @Override
            public void onDismiss(View view, Object token) {
                dismissSelection();
            }
        }));
    }

    @Override
    public void onAttach (Context context) {
        super.onAttach(context);
        if (getActivity() != null) {
            ((HostActivity) getActivity()).getEventBus().register(this);
        }
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
    public void onDetach() {
        super.onDetach();
        if (mapView != null) {
            mapView.onDestroy();
        }
        if (getActivity() != null) {
            ((HostActivity) getActivity()).getEventBus().unregister(this);
        }
    }

    @Override
    public void displayProgressBar(boolean active) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                progressBar.setVisibility(active ? View.VISIBLE : View.INVISIBLE);
                mapView.setVisibility(active ? View.INVISIBLE : View.VISIBLE);
            });
        }
    }

    private GoogleMap map;

    @Override
    public void displayStations(@NonNull Collection<Station> stations) {
        Preconditions.checkNotNull(stations);
        
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                mapView.getMapAsync(map -> {
                    this.map = map;

                    StreamSupport.stream(markerStationsMap.keySet()).forEach(Marker::remove);

                    StreamSupport.stream(stations).forEach(
                            station -> {
                                Marker m = map.addMarker(new MarkerOptions().position(station.getPosition()).title(station.getName())
                                        .snippet(station.getAddress()));
                                markerStationsMap.forcePut(m, station);
                            }
                    );
                    map.setOnMarkerClickListener((marker) -> {
                        for (Marker m: markerStationsMap.keySet()) {
                        }


                        if (markerStationsMap.get(marker) == null) { // this must be our current location marker
                            if (currentStation != null) {
                                dismissSelection();
                            }
                            return false;
                        }
                        currentMarker = marker;
                        currentStation = markerStationsMap.get(marker);
                        displayBriefDetails(currentStation);
                        return false;
                    });


                    map.setOnMapClickListener((point) -> dismissSelection());

                    if (currentStation != null) {
                        currentMarker = markerStationsMap.inverse().get(currentStation);
                        currentMarker.showInfoWindow();
                        displayBriefDetails(currentStation);
                    }

                    map.setOnCameraChangeListener(cameraPosition -> currentCameraPosition = cameraPosition);

                });
            });
        }
    }

    @Subscribe
    void updateMap (LocationReceivedEvent event) {
        mapView.getMapAsync(map -> {
            myLocationMarker = map.addMarker(new MarkerOptions().title("Me").position(event.location));
            if (currentCameraPosition == null) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(event.location, MAP_ZOOM_DEFAULT));
            } else {
                map.moveCamera(CameraUpdateFactory.newCameraPosition(currentCameraPosition));
            }
        });

        ((HostActivity) getActivity()).getStationsPresenter().initialSetup();
    }

    @Override
    public void displayBriefDetails(@NonNull Station station) {
        Preconditions.checkNotNull(station);
        stationCard.setVisibility(View.VISIBLE);
        stationNameView.setText(markerStationsMap.get(currentMarker).getName());
        stationAddressView.setText(markerStationsMap.get(currentMarker).getAddress());
        seeMoreButton.setOnClickListener((view) -> actionListener.showFullDetails(markerStationsMap.get(currentMarker)));
    }

    @Override
    public void displayFullDetails(@NonNull Station station) {
        Preconditions.checkNotNull(station);
    }

    @Override
    public void dismissSelection() {
        stationCard.setVisibility(View.GONE);
        if (currentMarker != null) {
            currentMarker.hideInfoWindow();
        }
        currentMarker   = null;
        currentStation  = null;
    }

    @Override
    public void setActionListener(StationsContract.UserActionListener actionListener) {
        Preconditions.checkNotNull(actionListener);
        this.actionListener = actionListener;
    }

    @Override
    public void displayError(@NonNull String error) {
        Preconditions.checkNotNull(error);
        if (getActivity() != null) {
            Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void getCurrentBounds(Callback<LatLngBounds> callback) {
        if (mapView != null) {
            mapView.getMapAsync(map -> {
                callback.onDone(map.getProjection().getVisibleRegion().latLngBounds);
            });
        } else {
            callback.onFail("Something went wrong with the map");
        }
    }
}
