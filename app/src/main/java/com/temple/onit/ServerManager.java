package com.temple.onit;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;


public class ServerManager {
    public static void sendRequest(LatLng origin, LatLng destination, long arrivalTime){
        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        sb.append("origin=").append(origin.latitude).append(",").append(origin.longitude).append("&");
        sb.append("destination=").append(destination.latitude).append(",").append(destination.longitude).append("&");
        //sb.append("traffic_model=best_guess&");
        sb.append("arrival_time=").append(arrivalTime).append("&");
        sb.append("key=").append(Constants.API_KEY);
        String request = sb.toString();
        Log.d("Request URL", request);



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

}
