package com.tryp.support.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tryp.support.R;

import java.util.List;
import java.util.Map;

/**
 * Created by cliffroot on 21.11.15.
 */
public class PriceAdapter extends RecyclerView.Adapter<PriceAdapter.PriceCardHolder> {


    private Context context;
    private List<Map.Entry> prices;


    public PriceAdapter(Context context, List<Map.Entry> prices) {
        this.context = context;
        this.prices = prices;

    }

    @Override
    public PriceCardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_station_list, parent, false);
        return new PriceCardHolder(view);
    }


    @Override
    public void onBindViewHolder(final PriceCardHolder holder,final int position) {
        final Map.Entry entry = prices.get(position);

        holder.fuelTypeView.setText((String) entry.getKey());
        holder.fuelPriceView.setText(entry.getValue().toString() + " UAH/liter");
    }

    @Override
    public int getItemCount() {
        return prices.size();
    }

    public static class PriceCardHolder extends RecyclerView.ViewHolder {

        TextView fuelTypeView;
        TextView fuelPriceView;

        public PriceCardHolder (View view) {
            super(view);
            fuelTypeView = ((TextView) view.findViewById(R.id.station_list_name));
            fuelPriceView = ((TextView) view.findViewById(R.id.station_list_address));
        }
    }

}
