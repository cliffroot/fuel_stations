package com.tryp.support;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.tryp.support.stations.StationsView;

/**
 * Created by cliffroot on 02.03.16.
 */
public class HostAdapter extends FragmentPagerAdapter {

    private final static int NUMBER_OF_SECTIONS = 2;

    public HostAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return StationsView.getInstance();
            case 1:
                return new Fragment();
            default:
                return new Fragment();
        }
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
