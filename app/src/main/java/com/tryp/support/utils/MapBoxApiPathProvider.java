package com.tryp.support.utils;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

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

    @Override
    @Background
    public void getSegments(LatLng from, LatLng to, Callback<List<LatLng>> callback) {
        String response = getResponse(from, to);
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
        String response = getResponse(from, to);

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
    public void getTime(LatLng from, LatLng to, Callback<Integer> callback) {
            String response = getResponse(from, to);

            JSONObject obj;
            try {
                obj = new JSONObject(response);
                int timeInSeconds = obj.getJSONArray("routes").getJSONObject(0).getInt("duration");
                callback.onCompleted(timeInSeconds / 60);
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
}
