package com.tryp.support.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.tryp.support.R;
import com.tryp.support.data.Station;
import com.tryp.support.utils.LocationHelper;

import java.util.Collection;
import java.util.List;

/**
 * Created by cliffroot on 21.11.15.
 */
public class StationsAdapter extends RecyclerView.Adapter<StationsAdapter.StationCardHolder> {


    private Context context;
    private List<Station> stations;
    private LatLng currentLocation;
    private String fuelType;

    private boolean showPrice;


    public StationsAdapter (Context context, Collection<Station> stations, LatLng currentLocation, String currentFuelType, boolean showPrice) {
        this.context = context;
        this.stations = (List<Station>) stations;
        this.currentLocation = currentLocation;
        this.showPrice = showPrice;
        fuelType = currentFuelType;

    }

    @Override
    public StationCardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_station_list, parent, false);
        if (!showPrice) {
            view.findViewById(R.id.station_list_price).setVisibility(View.INVISIBLE);
        }
        return new StationCardHolder(view);
    }


    @Override
    public void onBindViewHolder(final StationCardHolder holder,final int position) {
        final Station sp = stations.get(position);

        holder.stationNameView.setText(sp.getName());
        String address = sp.getAddress();

        while (address.contains("  ")) {
            address = address.replace("  " , "");
        }
        while (address.endsWith(" ")) {
            address = address.substring(0, address.length() - 1);
        }

        holder.stationNameAddressView.setText(address);
        String price = stations.get(position).getPriceByFuelType(fuelType).toString();
        while (price.contains("  ")) {
            price = price.replace("  ", "");
        }
        holder.priceView.setText(price + "UAH/dm3");
        holder.distanceToView.setText(String.valueOf(
                LocationHelper.roundToHalf(LocationHelper.distanceBetween(sp.getPosition(), currentLocation))) + " km");
        holder.itemView.setOnClickListener(view -> {
//            Intent intent = new Intent(context, StationActivity_.class);
//            intent.putExtra(StationActivity.STATION_EXTRA_ARG, sp);
//            intent.putExtra(StationActivity.CURRENT_POSITION_ARG, currentLocation);
//            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return stations.size();
    }

    public static class StationCardHolder extends RecyclerView.ViewHolder {

        TextView stationNameView;
        TextView stationNameAddressView;
        TextView distanceToView;
        TextView priceView;

        public StationCardHolder (View view) {
            super(view);
            stationNameView = ((TextView) view.findViewById(R.id.station_list_name));
            stationNameAddressView = ((TextView) view.findViewById(R.id.station_list_address));
            distanceToView = ((TextView) view.findViewById(R.id.distance_to));
            priceView = ((TextView) view.findViewById(R.id.station_list_price));
        }
    }

}
