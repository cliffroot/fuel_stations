package com.tryp.support.stations_list;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.tryp.support.data.Station;
import com.tryp.support.data.StationRepository;

import java.util.Collection;

/**
 * Created by cliffroot on 03.03.16.
 */
public class StationsListPresenter implements StationsListContract.UserActionListener {

    StationRepository   repository;
    StationsListView    view;
    LatLng              currentLocation;

    public StationsListPresenter (StationRepository repository, StationsListView view, LatLng currentLocation) {
        this.repository      = repository;
        this.view            = view;
        this.currentLocation = currentLocation;
    }

    public void initialSetup() {
        view.setActionListener(this);
        loadStations();
    }

    @Override
    public void loadStations() {
        view.displayProgressBar(true);
        repository.getFilteredStations(view.getCurrentFilter(), currentLocation, new StationRepository.Callback<Collection<Station>>() {
            @Override
            public void onDone(Collection<Station> result) {
                view.displayProgressBar(false);
                view.displayStations(result);
            }

            @Override
            public void onFail(String reason) {
                view.displayProgressBar(false);
                view.displayError(reason);
            }
        });
    }

    @Override
    public void showFullDetails(@NonNull Station station) {

    }

    @Override
    public void updateStations() {

    }
}
