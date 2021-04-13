package com.temple.onit.dataclasses;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;


import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.time.LocalTime;


public class SmartAlarm implements Parcelable {


    private Location destinationLocation;
    private Location lastKnownLocation;
    private int arrivalHour;
    private int arrivalMinute;
    private long getReadyTime;
    private long transitTime;
    private String alarmTitle;
    private String days;

    public SmartAlarm(){
        destinationLocation = new Location("new location provider");
        destinationLocation.setLatitude(0);
        destinationLocation.setLongitude(0);
        lastKnownLocation = destinationLocation;
        arrivalHour = 0;
        arrivalMinute = 0;
        getReadyTime = 0;
        transitTime = 0;
        days = "0000000";
        alarmTitle = null;
    }

    protected SmartAlarm(Parcel in) {
        this();
        destinationLocation = in.readParcelable(Location.class.getClassLoader());
        lastKnownLocation = in.readParcelable(Location.class.getClassLoader());
        arrivalHour = in.readInt();
        arrivalMinute = in.readInt();
        getReadyTime = in.readLong();
        transitTime = in.readLong();
        days = in.readString();
        alarmTitle = in.readString();

    }

    public static final Creator<SmartAlarm> CREATOR = new Creator<SmartAlarm>() {
        @Override
        public SmartAlarm createFromParcel(Parcel in) {
            return new SmartAlarm(in);
        }

        @Override
        public SmartAlarm[] newArray(int size) {
            return new SmartAlarm[size];
        }
    };

    public void setDestinationLocation(Location location){
        this.destinationLocation = location;
    }
    public void setLastKnownLocation(Location location){
        this.lastKnownLocation = location;
    }
    public void setArrivalTime(int hour, int minute){
        this.arrivalHour = hour;
        this.arrivalMinute = minute;
    }
    public void setGetReadyTime(long time){
        this.getReadyTime = time;
    }
    public void setTransitTime(long time){
        this.transitTime = time;
    }
    public void setAlarmTitle(String title){
        this.alarmTitle = title;
    }
    public void setDays(String days){this.days = days;}

    public Location getDestinationLocation(){
        return destinationLocation;
    }
    public Location getLastKnownLocation(){
        return lastKnownLocation;
    }
    public int getArrivalHour(){ return arrivalHour; }
    public int getArrivalMinute() { return  arrivalMinute; }
    public long getGetReadyTime(){
        return getReadyTime;
    }
    public long getTransitTime(){
        return transitTime;
    }
    public String getAlarmTitle(){
        return alarmTitle;
    }
    public String getDays(){ return days; }

    public String toString(){
        String daysEnabled = "";
        if(days.charAt(0) == '1'){
            daysEnabled = daysEnabled + "Sunday, ";
        }
        if(days.charAt(1) == '1'){
            daysEnabled = daysEnabled + "Monday, ";
        }
        if(days.charAt(2)=='1'){
            daysEnabled = daysEnabled + "Tuesday, ";
        }
        if(days.charAt(3) == '1'){
            daysEnabled = daysEnabled + "Wednesday, ";
        }
        if(days.charAt(4) == '1'){
            daysEnabled = daysEnabled + "Thursday, ";
        }
        if(days.charAt(5) == '1'){
            daysEnabled = daysEnabled + "Friday, ";
        }
        if(days.charAt(6) == '1'){
            daysEnabled = daysEnabled + "Saturday, ";
        }
        return "Alarm Title: " + alarmTitle + "\n" +
                "Destination Location: " + destinationLocation.getLatitude() + ", " + destinationLocation.getLongitude() + "\n" +
                " Last Known Location: " + lastKnownLocation.getLatitude() + ", " + lastKnownLocation.getLongitude() + "\n" +
                " Arrival Time: " + arrivalHour + ":" + arrivalMinute + "\n" +
                " Get Ready Time: " + getReadyTime/1000/60 + " Minutes\n" +
                " Transit Time: " + transitTime + "\nDays Enabled: " + daysEnabled;
    }

    public long updateTransitTime(){
        return transitTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }



    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(destinationLocation, flags);
        dest.writeParcelable(lastKnownLocation, flags);
        dest.writeInt(arrivalHour);
        dest.writeInt(arrivalMinute);
        dest.writeLong(getReadyTime);
        dest.writeLong(transitTime);
        dest.writeString(days);
        dest.writeString(alarmTitle);

    }
}