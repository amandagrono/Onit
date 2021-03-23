package com.temple.onit.dataclasses;

import android.location.Location;

import java.time.LocalTime;


public class SmartAlarm {


    private Location destinationLocation;
    private Location lastKnownLocation;
    private LocalTime arrivalTime;
    private LocalTime getReadyTime;
    private LocalTime transitTime;
    private String alarmTitle;

    public SmartAlarm(){

    }
    public void setDestinationLocation(Location location){
        this.destinationLocation = location;
    }
    public void setLastKnownLocation(Location location){
        this.lastKnownLocation = location;
    }
    public void setArrivalTime(LocalTime time){
        this.arrivalTime = time;
    }
    public void setGetReadyTime(LocalTime time){
        this.getReadyTime = time;
    }
    public void setTransitTime(LocalTime time){
        this.transitTime = time;
    }
    public void setAlarmTitle(String title){
        this.alarmTitle = title;
    }

    public Location getDestinationLocation(){
        return destinationLocation;
    }
    public Location getLastKnownLocation(){
        return lastKnownLocation;
    }
    public LocalTime getArrivalTime(){
        return arrivalTime;
    }
    public LocalTime getGetReadyTime(){
        return getReadyTime;
    }
    public LocalTime getTransitTime(){
        return transitTime;
    }
    public String getAlarmTitle(){
        return alarmTitle;
    }

    public LocalTime updateTransitTime(){



        return transitTime;
    }
}
