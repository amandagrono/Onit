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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.temple.onit.Constants;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

@Entity(tableName = "alarm_table")
public class SmartAlarm{

    private double destinationLatitude;
    private double destinationLongitude;
    private double startingLatitude;
    private double startingLongitude;

    private int arrivalHour;
    private int arrivalMinute;
    private long getReadyTime; //milliseconds
    private long transitTime; //milliseconds

    private String alarmTitle;
    private String days;

    private boolean started, recurring;
    private long created;

    @PrimaryKey
    @NonNull
    private int alarmId;

    public SmartAlarm(int alarmId, int arrivalHour, int arrivalMinute, long getReadyTime, long transitTime, String alarmTitle, String days, boolean started, boolean recurring, double startingLatitude, double startingLongitude, double destinationLatitude, double destinationLongitude, long created){
        this.alarmId = alarmId;
        this.arrivalHour = arrivalHour;
        this.arrivalMinute = arrivalMinute;
        this.getReadyTime = getReadyTime;
        this.transitTime = transitTime;
        this.alarmTitle = alarmTitle;
        this.days = days;
        this.started = started;
        this.recurring = recurring;
        this.startingLatitude = startingLatitude;
        this.startingLongitude = startingLongitude;
        this.destinationLongitude = destinationLongitude;
        this.destinationLatitude = destinationLatitude;
        this.created = created;
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
    public double getDestinationLatitude(){
        return destinationLatitude;
    }
    public double getDestinationLongitude(){
        return destinationLongitude;
    }
    public double getStartingLatitude(){
        return startingLatitude;
    }
    public double getStartingLongitude(){
        return startingLongitude;
    }
    public int getAlarmId(){
        return alarmId;
    }
    public long getCreated(){
        return created;
    }

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
                "Destination Location: " + destinationLatitude + ", " + destinationLongitude + "\n" +
                " Last Known Location: " + startingLatitude + ", " + startingLongitude + "\n" +
                " Arrival Time: " + arrivalHour + ":" + arrivalMinute + "\n" +
                " Get Ready Time: " + getReadyTime/1000/60 + " Minutes\n" +
                " Transit Time: " + transitTime + "\nDays Enabled: " + daysEnabled;
    }




    public boolean isStarted(){
        return started;
    }


    public static int[] setDayArray(){
        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_WEEK);
        int[] returnArray = new int[7];
        returnArray[0] = today - 1;
        calendar.set(Calendar.DAY_OF_WEEK, today + 1);
        returnArray[1] = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        calendar.set(Calendar.DAY_OF_WEEK, today + 2);
        returnArray[2] = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        calendar.set(Calendar.DAY_OF_WEEK, today + 3);
        returnArray[3] = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        calendar.set(Calendar.DAY_OF_WEEK, today + 4);
        returnArray[4] = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        calendar.set(Calendar.DAY_OF_WEEK, today + 5);
        returnArray[5] = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        calendar.set(Calendar.DAY_OF_WEEK, today + 6);
        returnArray[6] = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        Log.d("SetDayArray", Arrays.toString(returnArray));
        return returnArray;
    }

    public String getDaysPreview(){

        int[] upcomingDays = setDayArray();


        String return_string = "";
        if (days != null){
            for (int i = 0; i < upcomingDays.length; i++){
                Log.i("value", "charAt: " + days.charAt(i));
                if (days.charAt(upcomingDays[i]) == '1'){
                    switch (upcomingDays[i]){
                        case 1:
                            return "Monday";
                        case 2:
                            return "Tuesday";
                        case 3:
                            return "Wednesday";
                        case 4:
                            return "Thursday";
                        case 5:
                            return "Friday";

                        case 6:
                            return "Saturday";

                        case 0:
                            return "Sunday";
                    }
                }
            }
        }
        Log.i("return string", "return string: " + return_string);
        return return_string;
    }


    public boolean enabledOnDay(int day){
        return days.charAt(day) == '1';
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

        intent.putExtra(Constants.DESTINATION_LATITUDE, destinationLatitude);
        intent.putExtra(Constants.DESTINATION_LONGITUDE, destinationLongitude);

        intent.putExtra(Constants.ARRIVAL_HOUR, arrivalHour);
        intent.putExtra(Constants.ARRIVAL_MINUTE, arrivalMinute);

        long arrivalTimeInMillis = arrivalHour*60*60*1000 + arrivalMinute*60*1000;
        long leaveTime = arrivalTimeInMillis - transitTime*1000;
        long wakeupTime = leaveTime - getReadyTime;

        intent.putExtra(Constants.LEAVE_HOUR, millisToHours(leaveTime));
        intent.putExtra(Constants.LEAVE_MINUTE, millisToMinutes(leaveTime));

        intent.putExtra(Constants.ALARM_ID, alarmId);

        Log.d("Alarm Title Intent", "Schedule Alarm: " + intent.getStringExtra(Constants.ALARM_TITLE));

        int wakeupHour = millisToHours(wakeupTime);
        int wakeupMin = millisToMinutes(wakeupTime);

        Log.d("Wake Up Time ", "Time: " + wakeupHour + ":" + wakeupMin);

        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, wakeupHour);
        calendar.set(Calendar.MINUTE, wakeupMin);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Log.d("Save Alarm", calendar.getTime().toString());

        if(calendar.getTimeInMillis() <= System.currentTimeMillis()){
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
        }
        if(!recurring){
            Toast.makeText(context, "Alarm Created", Toast.LENGTH_LONG).show();

            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmPendingIntent);

        }
        else{
            Toast.makeText(context, "Recurring Alarm Scheduled", Toast.LENGTH_LONG).show();
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), Constants.HOUR_IN_MILLIS*24, alarmPendingIntent);
        }

        this.started = true;

    }

    public int millisToHours(long millis){
        return (int) (millis/Constants.HOUR_IN_MILLIS);
    }

    public int millisToMinutes(long millis){
        millis = millis%Constants.HOUR_IN_MILLIS;
        return (int) millis/Constants.MINUTE_IN_MILLIS;
    }

    public void cancel(Context context){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, 0);
        alarmManager.cancel(alarmPendingIntent);
        this.started = false;

        Toast.makeText(context, "Canceled Alarm", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj == null){
            return false;
        }
        if(!(obj instanceof SmartAlarm)){
            return false;
        }
        return ((SmartAlarm) obj).alarmId == this.alarmId;
    }
}
