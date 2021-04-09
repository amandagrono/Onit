package com.temple.onit.services;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.maps.model.LatLng;
import com.temple.onit.OnitApplication;
import com.temple.onit.Utils;
import com.temple.onit.dataclasses.GeofencedReminder;

import java.util.List;

public class GeofenceTransitionsIntentService extends JobIntentService {

    public static void enqueueWork(Context context, Intent intent){
        enqueueWork(context, GeofenceTransitionsIntentService.class, 573, intent);
    }


    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        Log.d("Geofencing Event Triggered", "Event Triggered");
        if(geofencingEvent.hasError()){
            Log.d("Geofencing Error", "" + geofencingEvent.getErrorCode());
            return;
        }
        handleEvent(geofencingEvent);

    }

    private void handleEvent(GeofencingEvent event){
        if(event.getGeofenceTransition() == Geofence.GEOFENCE_TRANSITION_ENTER){
            GeofencedReminder reminder = getFirstReminder(event.getTriggeringGeofences());
            String message = reminder.getReminderContent();
            LatLng latLng = new LatLng(reminder.getLocation().latitude, reminder.getLocation().longitude);
            if(message != null){
                Utils.sendNotification(this, message, latLng);

            }
        }
    }
    private GeofencedReminder getFirstReminder(List<Geofence> triggeredGeofences){
        Application application = this.getApplication();

        Geofence geofence = triggeredGeofences.get(0);
        return ((OnitApplication) application).getGeofenceReminderManager().get(geofence.getRequestId());
    }
}
