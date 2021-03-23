package com.temple.onit.dataclasses;

import android.location.Location;

import java.time.LocalTime;

public class GeofencedReminder extends Reminder{
    private Location location;

    public void setLocation(Location location){
        this.location = location;
    }
    public Location getLocation(){
        return location;
    }
}
