package com.temple.onit;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Calendar;


public class ServerManager {
    public static void sendRequest(LatLng origin, LatLng destination, long arrivalTime, Context context){

        ResponseListener listener = (ResponseListener) context;

        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        sb.append("origin=").append(origin.latitude).append(",").append(origin.longitude).append("&");
        sb.append("destination=").append(destination.latitude).append(",").append(destination.longitude).append("&");
        //sb.append("traffic_model=best_guess&");
        sb.append("arrival_time=").append(arrivalTime).append("&");
        sb.append("key=").append(Constants.API_KEY);
        String request = sb.toString();
        Log.d("Request URL", request);

        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, request, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                listener.gotResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Google Directions API response", "There was an error");
            }
        });
        queue.add(stringRequest);

    }

    public static int parseDirectionsJson(String jsonResponse){
        int duration = 0;
        long before = System.currentTimeMillis();
        try {

            JSONObject jo = new JSONObject(jsonResponse);

            JSONArray routes = jo.getJSONArray("routes");
            JSONObject holder = routes.getJSONObject(0);
            JSONArray legs = holder.getJSONArray("legs");
            JSONObject holderLegs = legs.getJSONObject(0);
            JSONObject dur = holderLegs.getJSONObject("duration");
            duration = dur.getInt("value");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        long after = System.currentTimeMillis();
        Log.d("Parse Directions Json", "Total Time: " + (after-before));
        Log.d("Parse Directions Json", "Duration = " + duration);

        return duration;
    }

    public static long calculateArrivalTimeInMillis(int dow) {
        Calendar date = Calendar.getInstance();
        int diff = dow - date.get(Calendar.DAY_OF_WEEK);
        if (diff <= 0) {
            diff += 7;
        }
        date.add(Calendar.DAY_OF_MONTH, diff);
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        Log.d("Calculate Next Sunday", "Next Sunday: " + date.getTimeInMillis());

        return date.getTimeInMillis()/1000;
    }

    interface ResponseListener{
        public void gotResponse(String jsonObject);
    }

}
