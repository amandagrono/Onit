package com.temple.onit.GeofencedReminder;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.temple.onit.Constants;
import com.temple.onit.OnitApplication;

import java.util.List;

import kotlin.collections.CollectionsKt;


public class GeofenceReminderManager {

    private Context context;
    private SharedPreferences sharedPreferences;
    private Gson gson = new Gson();
    private GeofencingClient geofencingClient;
    private PendingIntent geofencePendingIntent;

    private PendingIntent getGeofencePendingIntent(){
        if(geofencePendingIntent != null){
            return geofencePendingIntent;
        }
        Intent intent = new Intent(context, GeofenceBroadcastReceiver.class);
        geofencePendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }





    public GeofenceReminderManager(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences("GeofenceReminders", Context.MODE_PRIVATE);
        this.geofencingClient = LocationServices.getGeofencingClient(this.context);
    }




    public void add(GeofencedReminder geofencedReminder, Context context, GeofenceManagerInterface activity){
        List<GeofencedReminder> list = getAll();
        list.add(geofencedReminder);
        saveAll(list);
        Geofence geofence = buildGeofence(geofencedReminder);
        if(geofence != null
                && ContextCompat.checkSelfPermission
                (context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            geofencingClient
                    .addGeofences(buildGeofencingRequest(geofence), getGeofencePendingIntent())
                    .addOnSuccessListener(unused -> {
                        Log.d("Added Geofence", "");
                        List<GeofencedReminder> tempList = getAll();
                        tempList.add(geofencedReminder);
                        saveAll(tempList);
                        activity.onSuccess();

                    })
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        Log.d("Failed to add geofenced reminder", GeofenceErrors.getErrorString(context, e));
                        activity.onFailure(GeofenceErrors.getErrorString(context,e ));
                    });

        }


    }
    public void remove(GeofencedReminder geofencedReminder, Context context, RemoveReminderInterface activity){
        geofencingClient.removeGeofences(CollectionsKt.listOf(geofencedReminder.getId()))
                .addOnSuccessListener(unused -> {

                    List<GeofencedReminder> tempList = getAll();
                    Log.d("Remove Reminder List Size Before: " , "" + tempList.size());
                    tempList.remove(geofencedReminder);
                    Log.d("Remove Reminder List Size After: " , "" + tempList.size());
                    saveAll(tempList);
                    Log.d("Successfully removed geofenced reminder", "");
                    activity.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.d("Failed to remove geofence", "Failed to remove geofence");
                    activity.onFailure(GeofenceErrors.getErrorString(context, e));
                });
        removeFromServer(geofencedReminder);

    }
    private void removeFromServer(GeofencedReminder reminder){
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Constants.API_DELETE_GEOFENCED_REMINDER + "?username=" + OnitApplication.instance.getAccountManager().username +
                "&latitude="+reminder.getLatLng().latitude+"&longitude="+reminder.getLatLng().longitude+"&distance="+reminder.getRadius()
                +"&body="+reminder.getReminderContent();
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url, response -> {
            Log.d("Geofence", "Removed Geofenced Reminder from server");
        }, error -> {
            Toast.makeText(context, "Failed to remove reminder from server", Toast.LENGTH_SHORT).show();
        });
        queue.add(stringRequest);
    }

    public List<GeofencedReminder> getAll(){
        if(sharedPreferences.contains("REMINDERS")){
            String gsonString = sharedPreferences.getString("REMINDERS", null);
            GeofencedReminder[] arrayOfReminders = gson.fromJson(gsonString, GeofencedReminder[].class);
            if(arrayOfReminders != null){
                return CollectionsKt.mutableListOf(arrayOfReminders);
            }
        }
        return CollectionsKt.mutableListOf();
    }

    private Geofence buildGeofence(GeofencedReminder geofencedReminder){
        double latitude = geofencedReminder.getLocation().latitude;
        double longitude = geofencedReminder.getLocation().longitude;
        double radius = geofencedReminder.getRadius();
        if(latitude != 0 && longitude != 0 && radius != 0){
            return new Geofence.Builder().setRequestId(geofencedReminder.getId())
                    .setCircularRegion(latitude,longitude, (float) radius)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .build();
        }
        return null;
    }
    private GeofencingRequest buildGeofencingRequest(Geofence geofence){
        return new GeofencingRequest.Builder()
                .setInitialTrigger(0)
                .addGeofences(CollectionsKt.listOf(geofence))
                .build();

    }

    private void saveAll(List<GeofencedReminder> list){
        sharedPreferences.edit().putString("REMINDERS", gson.toJson(list)).apply();
    }
    public GeofencedReminder get(String requestId){

        for(GeofencedReminder item : getAll()){
            if(item.getId().equals(requestId)){
                return item;
            }
        }
        return null;


    }
    public GeofencedReminder getLast(){
        return getAll().get(getAll().size() - 1);
    }

    public interface GeofenceManagerInterface{
        public void onSuccess();
        public void onFailure(String error);
    }
    interface RemoveReminderInterface{
        public void onSuccess();
        public void onFailure(String error);
    }
}
