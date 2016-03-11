package com.tryp.support.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.tryp.support.R;
import com.tryp.support.utils.LocationHelper;

import java.util.List;
import java.util.Map;

/**
 * Created by cliffroot on 21.11.15.
 */
public class PriceAdapter extends RecyclerView.Adapter<PriceAdapter.CardHolder> {

    private enum CardType {

        ADDRESS_CARD(0), PRICE_CARD(1), RATING_CARD(2), ESTIMATION_CARD(3);

        private int value;
        CardType (int value) {
            this.value = value;
        }

        public int intValue () {
            return value;
        }

    }


    private Context context;
    private List<Map.Entry> prices;
    private String address;
    private float rating;
    private int estimatedTime;
    private float estimatedDistance;


    public PriceAdapter(Context context, List<Map.Entry> prices, String address, float rating, int estimatedTime, float estimatedDistance) {
        this.context = context;
        this.prices = prices;
        this.address = address;
        this.rating = rating;
        this.estimatedTime = estimatedTime;
        this.estimatedDistance = estimatedDistance;

    }

    @Override
    public CardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == CardType.ADDRESS_CARD.intValue()) {
            View view = LayoutInflater.from(context).inflate(R.layout.address_card, parent, false);
            return new AddressCardHolder(view);
        } else if (viewType == CardType.PRICE_CARD.intValue()) {
            View view = LayoutInflater.from(context).inflate(R.layout.price_card, parent, false);
            return new PriceCardHolder(view);
        } else if (viewType == CardType.ESTIMATION_CARD.intValue()){
            View view = LayoutInflater.from(context).inflate(R.layout.estimation_card, parent, false);
            return new EstimationCardHolder(view);
        } else if (viewType == CardType.RATING_CARD.intValue()){
            View view = LayoutInflater.from(context).inflate(R.layout.rating_card, parent, false);
            return new RatingCardHolder(view);
        }

        return null;
    }


    @Override
    public void onBindViewHolder(final CardHolder holder,final int position) {
        if (position == 0) {
            ((AddressCardHolder) holder).addressView.setText(address);
        } else if (position == 1) {
            ((RatingCardHolder) holder).ratingBar.setRating(rating);
        } else if (position == 2) {
            ((EstimationCardHolder) holder).estimatedTimeView.setText(String.valueOf(estimatedTime) + " min.");
            ((EstimationCardHolder) holder).estimatedDistanceView.setText(
                    String.valueOf(LocationHelper.roundToHalf(estimatedDistance)) +  "km.");
        } else {
            final Map.Entry entry = prices.get(position - 3);
            ((PriceCardHolder) holder).fuelTypeView.setText((String) entry.getKey());
            ((PriceCardHolder) holder).fuelPriceView.setText(entry.getValue().toString() + " UAH/liter");
        }
    }

    @Override
    public int getItemCount() {
        return prices.size() + 3;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0? CardType.ADDRESS_CARD.intValue():
                (position == 1? CardType.RATING_CARD.intValue():
                        (position == 2? CardType.ESTIMATION_CARD.intValue() : CardType.PRICE_CARD.intValue()));
    }


    static abstract class CardHolder extends RecyclerView.ViewHolder {

        public CardHolder(View itemView) {
            super(itemView);
        }
    }

    static class AddressCardHolder extends CardHolder {
        TextView addressView;

        public AddressCardHolder (View view) {
            super(view);
            addressView = ((TextView) view.findViewById(R.id.address));
        }
    }

    static class PriceCardHolder extends CardHolder {

        TextView fuelTypeView;
        TextView fuelPriceView;

        public PriceCardHolder (View view) {
            super(view);
            fuelTypeView = ((TextView) view.findViewById(R.id.price_type));
            fuelPriceView = ((TextView) view.findViewById(R.id.price));
        }
    }

    static class RatingCardHolder extends CardHolder {

        RatingBar ratingBar;

        int tintColor;

        public RatingCardHolder (View view) {
            super(view);
            ratingBar = ((RatingBar) view.findViewById(R.id.station_rating_bar));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tintColor = view.getContext().getColor(R.color.colorAccent);
            } else {
                tintColor = view.getContext().getResources().getColor(R.color.colorAccent);
            }
            setupRatingBar();
        }

        void setupRatingBar () {
            Drawable stars = ratingBar.getProgressDrawable();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                stars.setTint(tintColor);
            } else {
                stars.setColorFilter(tintColor, PorterDuff.Mode.SRC_ATOP);
            }
            ratingBar.setEnabled(false);
        }
    }

    static class EstimationCardHolder extends CardHolder {

        TextView estimatedTimeView;
        TextView estimatedDistanceView;

        public EstimationCardHolder (View view) {
            super(view);
            estimatedTimeView       = ((TextView) view.findViewById(R.id.time));
            estimatedDistanceView   = ((TextView) view.findViewById(R.id.distance));
        }
    }

}
