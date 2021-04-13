package com.temple.onit;

import android.app.Application;
import android.util.Log;

import com.temple.onit.GeofencedReminder.GeofenceReminderManager;


public class OnitApplication extends Application {


    public static OnitApplication instance;
    private GeofenceReminderManager geofenceReminderManager;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Log.d("Application onCreate called", "oncreate called");
        geofenceReminderManager = new GeofenceReminderManager(this);
    }

    public GeofenceReminderManager getGeofenceReminderManager(){
        return geofenceReminderManager;
    }



}
