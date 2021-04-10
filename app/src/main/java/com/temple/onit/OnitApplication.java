package com.temple.onit;

import android.app.AlarmManager;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import com.temple.onit.Alarms.SmartAlarmManager;
import com.temple.onit.GeofencedReminder.GeofenceReminderManager;


public class OnitApplication extends Application {


    public static OnitApplication instance;
    private GeofenceReminderManager geofenceReminderManager;
    private SmartAlarmManager smartAlarmManager;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Log.d("Application onCreate called", "oncreate called");
        geofenceReminderManager = new GeofenceReminderManager(this);
        smartAlarmManager = new SmartAlarmManager(this);
        createNotificationChannelAlarm();
    }

    public SmartAlarmManager getAlarmManager() {
        return smartAlarmManager;
    }
    public GeofenceReminderManager getGeofenceReminderManager(){
        return geofenceReminderManager;
    }

    private void createNotificationChannelAlarm(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel serviceChannel = new NotificationChannel(
                    Constants.ALARM_CHANNEL,
                    "Alarm Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

}
