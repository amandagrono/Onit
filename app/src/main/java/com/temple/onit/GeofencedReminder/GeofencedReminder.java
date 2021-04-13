package com.temple.onit.GeofencedReminder;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.temple.onit.dataclasses.Reminder;

import java.time.LocalTime;

public class GeofencedReminder extends Reminder {
    private LatLng location;

    public GeofencedReminder(LatLng latLng, String title, String content, double radius){
        super(title, content, radius);
        this.location = latLng;
    }

    public void setLocation(LatLng latLng){
        this.location = latLng;
    }
    public LatLng getLocation(){
        return location;
    }
    public LatLng getLatLng(){
        return location;
    }
}
