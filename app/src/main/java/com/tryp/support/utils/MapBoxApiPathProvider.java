package com.tryp.support.utils;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by cliffroot on 07.03.16.
 */
@EBean
public class MapBoxApiPathProvider implements PathProvider{

    private LoadingCache<Key, String> cachedResponses = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build(new CacheLoader<Key, String>() {
                        public String load(Key key) throws Exception {
                            return getResponse(key.from, key.to);
                        }
                    });

    @Override
    @Background
    public void getSegments(LatLng from, LatLng to, Callback<List<LatLng>> callback) {
        String response;
        try {
            response = cachedResponses.get(new Key(from, to));
        } catch (Exception exception) {
            Log.e("CACHE","reason1",  exception);
            callback.onFail();
            return;
        }
        JSONObject obj;
        List<LatLng> segmentList = new LinkedList<>();
        try {
            obj = new JSONObject(response);
            JSONArray path = obj.getJSONArray("routes").getJSONObject(0).getJSONObject("geometry").getJSONArray("coordinates");
            for (int i = 0; i < path.length(); i++ ) {
                segmentList.add(new LatLng(path.getJSONArray(i).getDouble(1), path.getJSONArray(i).getDouble(0)));
            }
            Log.w("StationActivity", segmentList.get(0) + ", " + segmentList.get(segmentList.size() - 1));
            callback.onCompleted(segmentList);
        } catch (Exception ex) {
            Log.e("StationActivity", "Error parsing JSON", ex);
            callback.onFail();
        }
    }

    @Override
    @Background
    public void getDistance(LatLng from, LatLng to, Callback<Float> callback) {
        String response;
        try {
            response = cachedResponses.get(new Key(from, to));
        } catch (Exception exception) {
            Log.e("CACHE", "raison", exception);
            callback.onFail();
            return;
        }
        JSONObject obj;
        try {
            obj = new JSONObject(response);
            int distanceInMeters = obj.getJSONArray("routes").getJSONObject(0).getInt("distance");
            callback.onCompleted(distanceInMeters / 1000.f);
        } catch (Exception ex) {
            Log.e("StationActivity", "Google returned incorrect JSON", ex);
            callback.onFail();
        }
    }

    @Override
    @Background
    public void getTime(LatLng from, LatLng to, Callback<Integer> callback) {
        String response;
        try {
            response = cachedResponses.get(new Key(from, to));
        } catch (Exception exception) {
            Log.e("CACHE", "reason", exception);
            callback.onFail();
            return;
        }
        JSONObject obj;
        try {
            obj = new JSONObject(response);
            int timeInSeconds = obj.getJSONArray("routes").getJSONObject(0).getInt("duration");
            callback.onCompleted(timeInSeconds / 60 + 1);
        } catch (Exception ex) {
            Log.e("StationActivity", "Google returned incorrect JSON", ex);
            callback.onFail();
        }
    }

    public String getResponse (LatLng from, LatLng to) {
        LatLng actualFrom = new LatLng(from.longitude, from.latitude);
        LatLng actualTo   = new LatLng(to.longitude, to.latitude);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setClient(new OkClient())
                .setConverter(new StringConverter())
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint("https://api.mapbox.com/v4/").build();

        MapBoxApiService service = restAdapter.create(MapBoxApiService.class);
        String response = service.getSegments(actualFrom.latitude + "," + actualFrom.longitude,
                actualTo.latitude + "," + actualTo.longitude, "pk.eyJ1IjoiY2xpZmZyb290IiwiYSI6ImNpbGh0djB1dDAwNjl2bGx6NXI3N2VsamsifQ.VHmegmidpyIZpxgoZD9Tlw");

        return response;
    }

    public interface MapBoxApiService {
        @GET("/directions/mapbox.driving/{origin};{destination}.json")
        String getSegments(@Path("origin") String origin, @Path("destination") String destination, @Query("access_token") String key);

    }

    private static class Key {
        LatLng from;
        LatLng to;

        public Key () {}
        public Key (LatLng from, LatLng to) {
            this.from   = from;
            this.to     = to;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Key key = (Key) o;

            if (from != null ? !from.equals(key.from) : key.from != null) return false;
            return !(to != null ? !to.equals(key.to) : key.to != null);

        }

        @Override
        public int hashCode() {
            int result = from != null ? from.hashCode() : 0;
            result = 31 * result + (to != null ? to.hashCode() : 0);
            return result;
        }
    }
}
