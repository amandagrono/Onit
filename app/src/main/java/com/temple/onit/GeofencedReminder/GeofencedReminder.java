package com.temple.onit.GeofencedReminder;

import android.location.Location;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.temple.onit.dataclasses.Reminder;

import java.time.LocalTime;

public class GeofencedReminder extends Reminder {
    private LatLng location;

    public GeofencedReminder(LatLng latLng, String title, String content, double radius){
        super(title, content, radius);
        this.location = latLng;
        this.setReminderTitle("");
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

    @Override
    public boolean equals(@Nullable @org.jetbrains.annotations.Nullable Object obj) {
        if(obj == null){
            return false;
        }
        if(!(obj instanceof GeofencedReminder)){
            return false;
        }
        GeofencedReminder obj1 = (GeofencedReminder) obj;
        if(obj1.getId().equals(this.getId())){
            return true;
        }
        return false;
    }
}
