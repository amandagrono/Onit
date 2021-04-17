package com.temple.onit.Alarms.services;


import android.content.Intent;
import android.os.IBinder;

import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.Observer;

import com.temple.onit.Alarms.SmartAlarm;
import com.temple.onit.Alarms.database.SmartAlarmRepository;

import java.util.List;

public class RescheduleSmartAlarmService extends LifecycleService {
    public RescheduleSmartAlarmService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        SmartAlarmRepository repository = new SmartAlarmRepository(getApplication());
        repository.getAlarmsLiveData().observe(this, new Observer<List<SmartAlarm>>() {
            @Override
            public void onChanged(List<SmartAlarm> smartAlarms) {
                for(SmartAlarm alarm : smartAlarms){
                    if(alarm.isStarted()){
                        alarm.schedule(getApplicationContext());
                    }
                }
            }
        });

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        return null;
    }
}