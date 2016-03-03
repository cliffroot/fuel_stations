package com.tryp.support.stations_list;

import android.support.annotation.NonNull;

import com.tryp.support.data.Station;

import java.util.Collection;

/**
 * Created by cliffroot on 02.03.16.
 */
public interface StationsListContract {

    interface View {

        interface Filter {
            @NonNull String     getFuelType ();
            @NonNull Double     getDistance ();
        }

        void displayProgressBar     (boolean active);
        void displayStations        (@NonNull Collection<Station> stations);
        void displayFullDetails     (@NonNull Station station);
        void setActionListener      (@NonNull UserActionListener actionListener);
        void displayError           (@NonNull String error);

        @NonNull Filter getCurrentFilter ();

    }

    interface UserActionListener {

        void loadStations       ();
        void showFullDetails    (@NonNull Station station);
        void updateStations     ();

    }

}
