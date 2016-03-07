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
import retrofit.http.Query;

/**
 * Created by cliffroot on 21.11.15.
 */
@EBean
public class GoogleApiPathProvider implements PathProvider {


    @Override
    @Background
    public void getSegments(LatLng from, LatLng to, Callback<List<LatLng>> callback) {
        String response = getResponse(from, to);
        JSONObject obj;
        List<LatLng> segmentList = new LinkedList<>();
        try {
            obj = new JSONObject(response);
            JSONArray path = obj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");

            for (int i = 0; i < path.length(); i++ ) {
                JSONObject startLocation = path.getJSONObject(i).getJSONObject("start_location");
                JSONObject endLocation = path.getJSONObject(i).getJSONObject("end_location");
                LatLng start = new LatLng(startLocation.getDouble("lat"), startLocation.getDouble("lng"));
                LatLng end =   new LatLng(endLocation.getDouble("lat"), endLocation.getDouble("lng"));
                if (i == 0) {
                    segmentList.add(start);
                }
                segmentList.add(end);
            }
            callback.onCompleted(segmentList);
        } catch (Exception ex) {
            Log.e("StationActivity", "Google returned incorrect JSON", ex);
            callback.onFail();
        }
    }

    @Override
    @Background
    public void getDistance(LatLng from, LatLng to, Callback<Float> callback) {
        String response = getResponse(from, to);
        try {
            JSONObject obj = new JSONObject(response);
            int distance = obj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs")
                    .getJSONObject(0).getJSONObject("distance").getInt("value");

            callback.onCompleted(distance / 1000.f);
        } catch (Exception ex) {
            Log.e("StationActivity", "Google returned incorrect JSON", ex);
            callback.onFail();
        }
    }

    @Override
    public void getTime(LatLng from, LatLng to, Callback<Integer> callback) {
        //TODO: not implemented
        callback.onFail();
    }

    public String getResponse (LatLng from, LatLng to) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setClient(new OkClient())
                .setConverter(new StringConverter())
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint("https://maps.googleapis.com").build();

        GoogleApiService service = restAdapter.create(GoogleApiService.class);
        String response = service.getSegments(from.latitude + "," + from.longitude,
                to.latitude + "," + to.longitude, "AIzaSyBWazeGmCm-oOw8hQ_X_JqYL0Sixp99kBU");

        return response;
    }


    public interface GoogleApiService {

        @GET("/maps/api/directions/json")
        String getSegments(@Query("origin") String origin, @Query("destination") String destination, @Query("key") String key);

    }

}
