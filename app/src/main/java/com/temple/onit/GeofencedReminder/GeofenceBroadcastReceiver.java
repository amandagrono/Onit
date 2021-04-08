package com.temple.onit.GeofencedReminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.temple.onit.services.GeofenceTransitionsIntentService;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        GeofenceTransitionsIntentService.enqueueWork(context, intent);
    }
}
