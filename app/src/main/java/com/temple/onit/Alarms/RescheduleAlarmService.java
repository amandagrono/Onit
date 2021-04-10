package com.temple.onit.Alarms;

import android.app.AlarmManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.temple.onit.OnitApplication;

import java.util.List;

public class RescheduleAlarmService extends Service {
    public RescheduleAlarmService() {
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        super.onStartCommand(intent, flags, startId);
        SmartAlarmManager smartAlarmManager = OnitApplication.instance.getAlarmManager();

        List<SmartAlarm> smartAlarmList = smartAlarmManager.getAll();

        for(SmartAlarm smartAlarm : smartAlarmList){
            if(smartAlarm.isEnabled()){
                smartAlarm.schedule(getApplicationContext());
            }
        }



        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}