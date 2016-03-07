package com.tryp.support.station_details;

import android.support.annotation.NonNull;

import com.tryp.support.data.Station;

/**
 * Created by cliffroot on 07.03.16.
 */
public class StationDetailsContract {

    //TODO: actually now this part does not follow MVP design patter. maybe for the best

    interface View {


        void displayProgressBar     (boolean active);
        void displayStation         (@NonNull Station station);
        void displayCurrentRating   ();


    }

    interface UserActionListener {

        void showStation    ();
        void editPrices     ();
        void leftRating     ();
    }
}
