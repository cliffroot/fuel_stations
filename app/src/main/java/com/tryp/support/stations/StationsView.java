package com.tryp.support.stations;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;
import com.tryp.support.HostActivity;
import com.tryp.support.R;
import com.tryp.support.data.Station;
import com.tryp.support.utils.LocationReceivedEvent;
import com.tryp.support.utils.SwipeDismissTouchListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.Collection;
import java.util.Map;

import java8.util.stream.StreamSupport;

@EFragment(R.layout.fragment_host)
public class StationsView extends Fragment implements StationsContract.View {

    private static final float MAP_ZOOM_DEFAULT = 12.f;

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

    Map<Marker, Station> markersToStationsMap = Maps.newHashMap();
    Marker currentMarker;

    StationsContract.UserActionListener actionListener;

    public StationsView() { }

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
        getActivity().runOnUiThread(() -> {
            progressBar.setVisibility(active ? View.VISIBLE : View.INVISIBLE);
            mapView.setVisibility(active ? View.INVISIBLE : View.VISIBLE);
        });
    }

    @Override
    public void displayStations(@NonNull Collection<Station> stations) {
        Preconditions.checkNotNull(stations);
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                mapView.getMapAsync(map -> {
                    StreamSupport.stream(stations).forEach(
                            station -> {
                                Marker m = map.addMarker(new MarkerOptions().position(station.getPosition()).title(station.getName())
                                        .snippet(station.getAddress()));
                                markersToStationsMap.put(m, station);
                            }
                    );
                    map.setOnMarkerClickListener((marker) -> {
                        if (markersToStationsMap.get(marker) == null) { // this must be our current location marker
                            return false;
                        }
                        currentMarker = marker;
                        stationCard.setVisibility(View.VISIBLE);
                        stationNameView.setText(markersToStationsMap.get(marker).getName());
                        stationAddressView.setText(markersToStationsMap.get(marker).getAddress());
                        seeMoreButton.setOnClickListener((view) -> {
                            actionListener.showFullDetails(markersToStationsMap.get(marker));
                        });
                        return false;
                    });

                    map.setOnMapClickListener((point) -> {
                        dismissSelection();
                    });
                });
            });
        }
    }

    @Subscribe
    void updateMap (LocationReceivedEvent event) {
        Log.e("StationsView", "updateMap called");
        mapView.getMapAsync(map -> {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(event.location, MAP_ZOOM_DEFAULT));
            map.addMarker(new MarkerOptions().title("Me").position(event.location));
        });
    }

    @Override
    public void displayBriefDetails(@NonNull Station station) {
        Preconditions.checkNotNull(station);
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

        currentMarker = null;
    }

    @Override
    public void setActionListener(StationsContract.UserActionListener actionListener) {
        Preconditions.checkNotNull(actionListener);
        this.actionListener = actionListener;
    }

    @Override
    public void displayError(@NonNull String error) {

    }

    @Override
    public void getCurrentBounds(Callback<LatLngBounds> callback) {
        mapView.getMapAsync(map -> {
            callback.onDone(map.getProjection().getVisibleRegion().latLngBounds);
        });
    }
}
