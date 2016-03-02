package com.tryp.support.stations;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLngBounds;
import com.tryp.support.data.Station;

import java.util.Collection;

/**
 * Created by cliffroot on 01.03.16.
 */
public interface StationsContract {


    interface View {

        interface Callback<T> {
            void onDone(T result);
            void onFail(String reason);
        }

        void displayProgressBar     (boolean active);
        void displayStations        (@NonNull Collection<Station> stations);
        void displayBriefDetails    (@NonNull Station station);
        void displayFullDetails     (@NonNull Station station);
        void dismissSelection       ();
        void setActionListener      (@NonNull UserActionListener actionListener);
        void displayError           (@NonNull String error);
        void getCurrentBounds       (Callback<LatLngBounds> callback);


    }

    interface UserActionListener {

        void loadStations     ();
        void showBriefDetails (@NonNull Station station);
        void showFullDetails  (@NonNull Station station);
    }

}
