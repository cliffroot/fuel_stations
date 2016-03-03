package com.tryp.support.stations_list;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tryp.support.HostActivity;
import com.tryp.support.R;
import com.tryp.support.adapter.StationsAdapter;
import com.tryp.support.data.MockStationRepository;
import com.tryp.support.data.Station;
import com.tryp.support.data.StationRepository;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by cliffroot on 03.03.16.
 */
@EFragment(R.layout.stations_list_view)
public class StationsListView extends Fragment implements StationsListContract.View {

    static Fragment instance;

    @ViewById(R.id.recycler_view_stations)
    RecyclerView recyclerViewStations;

    @ViewById(R.id.fuel_spinner)
    Spinner fuelPicker;

    @ViewById(R.id.radius_pick)
    SeekBar radiusPicker;

    @ViewById(R.id.radius_pick_text)
    TextView radiusPickerText;

    @ViewById(R.id.station_loading_progress_bar)
    ProgressBar stationLoadingProgressBar;

    @ViewById(R.id.empty_recycler_view)
    TextView emptyRecyclerView;

    @Bean(MockStationRepository.class)
    StationRepository stationRepository;

    @InstanceState
    int currentRadius = 25;

    @InstanceState
    String currentFuelType;

    StationsListContract.UserActionListener actionListener;

    public static Fragment getInstance () {
        if (instance == null) {
            instance = new StationsListView_();
            instance.setRetainInstance(true);
        }
        return instance;
    }

    @Override
    public void displayProgressBar(boolean active) {

    }

    @AfterViews
    void setupPicker () {
        stationRepository.getAllFuelTypes(new StationRepository.Callback<List<String>>() {
            @Override
            public void onDone(List<String> result) {
                Collections.sort(result);
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,
                        result.toArray(new String[result.size()]));
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                fuelPicker.setAdapter(spinnerArrayAdapter);
                currentFuelType = (String) fuelPicker.getSelectedItem();
            }

            @Override
            public void onFail(String reason) {
                displayError(reason);
            }
        });
    }

    @AfterViews
    void setupRecyclerView () {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewStations.setLayoutManager(layoutManager);
        recyclerViewStations.setItemAnimator(new DefaultItemAnimator());
    }

    @AfterViews
    void setupRadiusPicker() {
        radiusPicker.incrementProgressBy(10);
        radiusPicker.setMax(9);
        radiusPicker.setProgress(5);

        radiusPickerText.setText("Radius: 5 km. ");
        radiusPicker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                radiusPickerText.setText("Radius: " + (i + 1) + " km.");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                currentRadius = seekBar.getProgress() + 1;
                actionListener.loadStations();
            }
        });
    }

    @Override
    public void displayStations(@NonNull Collection<Station> stations) {
        if (getActivity() != null) {
            currentFuelType = (String) fuelPicker.getSelectedItem();
            StationsAdapter adapter = new StationsAdapter(getActivity(), stations, ((HostActivity) getActivity()).getCurrentLocation(),
                    currentFuelType, true);
            getActivity().runOnUiThread(() -> {
                recyclerViewStations.setAdapter(adapter);
                recyclerViewStations.setVisibility(View.VISIBLE);
                stationLoadingProgressBar.setVisibility(View.INVISIBLE);
            });
            adapter.notifyDataSetChanged();

            fuelPicker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    currentFuelType = (String) adapterView.getSelectedItem();
                    actionListener.loadStations();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
    }

    @Override
    public void displayFullDetails(@NonNull Station station) {

    }

    @Override
    public void setActionListener(@NonNull StationsListContract.UserActionListener actionListener) {
        this.actionListener = actionListener;
    }

    @Override
    public void displayError(@NonNull String error) {
        if (getActivity() != null) {
            Toast.makeText(getActivity() , error, Toast.LENGTH_LONG).show();
        }
    }

    @NonNull
    @Override
    public Filter getCurrentFilter() {
        return new Filter() {
            @NonNull
            @Override
            public String getFuelType() {
                return currentFuelType;
            }

            @NonNull
            @Override
            public Double getDistance() {
                return Double.valueOf(currentRadius);
            }
        };
    }
}
