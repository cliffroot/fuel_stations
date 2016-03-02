package com.tryp.support.stations;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import com.tryp.support.HostActivity;
import com.tryp.support.R;
import com.tryp.support.data.Station;
import com.tryp.support.utils.LocationReceivedEvent;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.Collection;

import java8.util.stream.StreamSupport;

@EFragment(R.layout.fragment_host)
public class StationsView extends Fragment implements StationsContract.View {

    private static final float MAP_ZOOM_DEFAULT = 12.f;

    @ViewById(R.id.map_view)
    MapView mapView;

    @ViewById(R.id.progress_bar)
    ProgressBar progressBar;

    StationsContract.UserActionListener actionListener;

    public StationsView() { }

    @AfterViews
    void setupMap () {
        mapView.onCreate(null);
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
        mapView.onDestroy();
        if (getActivity() != null) {
            ((HostActivity) getActivity()).getEventBus().unregister(this);
        }
    }

    @Override
    public void displayProgressBar(boolean active) {
        progressBar.setVisibility(active ? View.VISIBLE : View.INVISIBLE);
        mapView.setVisibility(active ? View.INVISIBLE : View.VISIBLE);
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
                            }
                    );
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
