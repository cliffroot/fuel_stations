package com.tryp.support.stations;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.model.LatLngBounds;
import com.tryp.support.data.Station;
import com.tryp.support.data.StationRepository;

import java.util.Collection;

/**
 * Created by cliffroot on 01.03.16.
 */
public class StationsPresenter implements StationsContract.UserActionListener {

    StationRepository repository;
    StationsView view;

    public StationsPresenter (StationRepository repository, StationsView view) {
        this.repository = repository;
        this.view       = view;
    }

    public void initialSetup() {
        loadStations();
        view.setActionListener(this);
    }

    @Override
    public void loadStations() {

        Log.w("Log from HostAdapter", "loadStations was called!") ;

        view.displayProgressBar(true);
        view.getCurrentBounds(new StationsContract.View.Callback<LatLngBounds>() {
            @Override
            public void onDone(LatLngBounds result) {
                repository.getVisibleStations(result, new StationRepository.Callback<Collection<Station>>() {
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
            public void onFail(String reason) {
                view.displayProgressBar(false);
                view.displayError(reason);
            }
        });
    }

    @Override
    public void showBriefDetails(@NonNull Station station) {
        view.displayBriefDetails(station);
    }

    @Override
    public void showFullDetails(@NonNull Station station) {
        view.displayFullDetails(station);
    }
}
