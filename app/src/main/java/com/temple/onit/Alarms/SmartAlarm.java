package com.temple.onit.Alarms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.temple.onit.Constants;

import java.util.Calendar;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;


public class SmartAlarm implements Parcelable {


    private Location destinationLocation;
    private Location lastKnownLocation;

    private int arrivalHour;
    private int arrivalMinute;
    private long getReadyTime; //milliseconds
    private long transitTime; //milliseconds

    private String alarmTitle;
    private String days;

    private boolean enabled, recurring;
    private int alarmId;

    public SmartAlarm(){
        alarmId = (new Random()).nextInt();
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
        this.enabled = true;
        this.recurring = true;
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
        this.transitTime = time*1000;
    }
    public void setAlarmTitle(String title){
        this.alarmTitle = title;
    }
    public void setDays(String days){this.days = days;}
    public void setRecurring(boolean recurring){ this.recurring = recurring; }

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
    public boolean getRecurring(){ return recurring; }

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

    public void schedule(Context context){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        intent.putExtra(Constants.RECURRING, recurring);
        intent.putExtra(Constants.SUNDAY, enabledOnDay(0));
        intent.putExtra(Constants.MONDAY, enabledOnDay(1));
        intent.putExtra(Constants.TUESDAY, enabledOnDay(2));
        intent.putExtra(Constants.WEDNESDAY, enabledOnDay(3));
        intent.putExtra(Constants.THURSDAY, enabledOnDay(4));
        intent.putExtra(Constants.FRIDAY, enabledOnDay(5));
        intent.putExtra(Constants.SATURDAY, enabledOnDay(6));

        intent.putExtra(Constants.ALARM_TITLE, alarmTitle);

        intent.putExtra(Constants.ARRIVAL_HOUR, arrivalHour);
        intent.putExtra(Constants.ARRIVAL_MINUTE, arrivalMinute);
        intent.putExtra(Constants.LEAVE_HOUR, getLeaveHour());
        intent.putExtra(Constants.LEAVE_MINUTE, getLeaveMinute());
        intent.putExtra(Constants.DESTINATION_LATITUDE, destinationLocation.getLatitude());
        intent.putExtra(Constants.DESTINATION_LONGITUDE, destinationLocation.getLongitude());

        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, 0);

        Calendar calendar = setWakeupTime();

        if(calendar.getTimeInMillis() <= System.currentTimeMillis()){
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
        }
        if(!recurring){
            String toastText = null;

            toastText = "Alarm Scheduled";

            Toast.makeText(context, toastText, Toast.LENGTH_LONG).show();

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmPendingIntent);
        }
        else{
            String toastText = "Set Repeating alarm for " + getDaysEnabled() + ".";
            Toast.makeText(context, toastText, Toast.LENGTH_LONG).show();

            final long RUN_DAILY = 24*60*60*1000;
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), RUN_DAILY, alarmPendingIntent);
        }
        this.enabled = true;
    }
    public boolean isEnabled(){
        return enabled;
    }

    private int getLeaveHour(){
        long arrivalTimeInMillis = (arrivalHour*Constants.HOUR_IN_MILLIS) + (arrivalMinute*Constants.MINUTE_IN_MILLIS);
        long timeToLeave = arrivalTimeInMillis-transitTime;
        return (int) (timeToLeave/(Constants.HOUR_IN_MILLIS));
    }
    private int getLeaveMinute(){
        long arrivalTimeInMillis = (arrivalHour*Constants.HOUR_IN_MILLIS) + (arrivalMinute*Constants.MINUTE_IN_MILLIS);
        long timeToLeave = arrivalTimeInMillis-transitTime;
        return (int) (timeToLeave%(Constants.HOUR_IN_MILLIS))/Constants.MINUTE_IN_MILLIS;
    }

    private Calendar setWakeupTime(){
        long arrivalTimeInMillis = (arrivalHour*Constants.HOUR_IN_MILLIS) + (arrivalMinute*Constants.MINUTE_IN_MILLIS);
        long timeToLeave = arrivalTimeInMillis-transitTime;
        long timeToWakeUp = timeToLeave - getReadyTime;

        int wakeupHour = (int) (timeToWakeUp/(Constants.HOUR_IN_MILLIS));
        int wakeupMinute = (int) (timeToWakeUp%(Constants.HOUR_IN_MILLIS))/Constants.MINUTE_IN_MILLIS;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, wakeupHour);
        calendar.set(Calendar.MINUTE, wakeupMinute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
        }
        return calendar;

    }
    private String getDaysEnabled(){
        String returnString = "";
        if(enabledOnDay(0)) returnString = returnString + "Sunday, ";
        if(enabledOnDay(1)) returnString = returnString + "Monday, ";
        if(enabledOnDay(2)) returnString = returnString + "Tuesday, ";
        if(enabledOnDay(3)) returnString = returnString + "Wednesday, ";
        if(enabledOnDay(4)) returnString = returnString + "Thursday, ";
        if(enabledOnDay(5)) returnString = returnString + "Friday, ";
        if(enabledOnDay(6)) returnString = returnString + "Saturday, ";

        return returnString.substring(0, returnString.length() - 2);
    }

    public void cancelAlarm(Context context){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, 0);
        alarmManager.cancel(alarmPendingIntent);
        this.enabled = false;

        String toastText = "Alarm Cancelled";
        Toast.makeText(context, toastText, Toast.LENGTH_LONG).show();
        Log.d("Cancel Alarm", toString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public boolean enabledOnDay(int day){
        return days.charAt(day) == '1';
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
