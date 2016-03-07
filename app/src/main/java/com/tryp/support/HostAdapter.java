package com.tryp.support;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import com.tryp.support.data.MockStationRepository_;
import com.tryp.support.data.StationRepository;
import com.tryp.support.stations.StationsPresenter;
import com.tryp.support.stations.StationsView;
import com.tryp.support.stations_list.StationsListPresenter;
import com.tryp.support.stations_list.StationsListView;

/**
 * Created by cliffroot on 02.03.16.
 */
public class HostAdapter extends FragmentPagerAdapter {

    private final static int NUMBER_OF_SECTIONS = 2;
    private Context context;

    private StationsPresenter       stationsPresenter;
    private StationsListPresenter   stationsListPresenter;

    private Fragment first;
    private Fragment second;

    private StationRepository stationRepository;

    public HostAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        this.stationRepository = MockStationRepository_.getInstance_(context);
    }

    public StationsPresenter getStationsPresenter () {
        return stationsPresenter;
    }

    public StationsListPresenter getStationsListPresenter () {
        return stationsListPresenter;
    }

    @Override
    public Fragment getItem(int position) {
        Log.w("Log from HostAdapter", "getItem was called");
        switch (position) {
            case 0:
                first = StationsView.getInstance();
                stationsPresenter = new StationsPresenter(stationRepository, (StationsView) first);
                return first;
            case 1:
                second = StationsListView.getInstance();
                stationsListPresenter = new StationsListPresenter(stationRepository, (StationsListView) second);
                ((HostActivity) context).getEventBus().register(stationsListPresenter);
                return second;
            default:
                return new Fragment();
        }
    }

    @Override
    public Object instantiateItem (ViewGroup containter, int position) {
        Fragment f = (Fragment) super.instantiateItem(containter, position);

        switch (position) {
            case 0:
                stationsPresenter = new StationsPresenter(stationRepository, (StationsView) f);
                stationsPresenter.initialSetup();
            break;
            case 1:
                stationsListPresenter = new StationsListPresenter(stationRepository, (StationsListView) f);
                ((HostActivity) context).getEventBus().register(stationsListPresenter);
            break;
        }

        return f;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Map";
            case 1:
                return "List";
        }
        return null;
    }

    @Override
    public int getCount() {
        return NUMBER_OF_SECTIONS;
    }

}
