package com.temple.onit;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import com.temple.onit.GeofencedReminder.GeofenceReminderManager;
import com.temple.onit.account.AccountManager;


public class OnitApplication extends Application {


    public static OnitApplication instance;
    private GeofenceReminderManager geofenceReminderManager;
    private AccountManager accountManager;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        geofenceReminderManager = new GeofenceReminderManager(this);
        accountManager = new AccountManager(this);
        createNotificationChannelAlarm();
    }

    public AccountManager getAccountManager() {
        return accountManager;
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
