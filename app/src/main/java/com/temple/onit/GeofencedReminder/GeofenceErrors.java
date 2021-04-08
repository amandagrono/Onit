package com.temple.onit.GeofencedReminder;

import android.content.Context;
import android.content.res.Resources;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.GeofenceStatusCodes;

public class GeofenceErrors {
    public static String getErrorString(Context context, Exception e){
        if(e instanceof ApiException){
            return getErrorString(context, ((ApiException) e).getStatusCode());
        }
        else{
            return "Unknown Error Unfortunately";
        }
    }
    public static String getErrorString(Context context, int errorCode){
        Resources resources = context.getResources();
        if(errorCode == GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE){
            return "Geofence service is not available now. Go to Settings>Location>Mode and choose High accuracy.";
        }
        else if(errorCode == GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES){
            return "Your app has registered too many geofences";
        }
        else if(errorCode == GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS){
            return "You have added too many pending intents";
        }
        else{
            return "Unknown Error: shit don't work";
        }
    }
}
