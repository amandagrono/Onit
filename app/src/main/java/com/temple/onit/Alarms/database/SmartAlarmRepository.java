package com.temple.onit.Alarms.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.temple.onit.Alarms.SmartAlarm;

import java.util.List;

public class SmartAlarmRepository {

    private SmartAlarmDao alarmDao;
    private LiveData<List<SmartAlarm>> alarmsLiveData;

    public SmartAlarmRepository(Application application){
        SmartAlarmDatabase db = SmartAlarmDatabase.getDatabase(application);
        alarmDao = db.alarmDao();
        alarmsLiveData = alarmDao.getAlarms();
    }

    public void insert(SmartAlarm alarm){
        SmartAlarmDatabase.databaseWriteExecutor.execute(() -> {
            alarmDao.insert(alarm);
        });
    }
    public void update(SmartAlarm alarm){
        SmartAlarmDatabase.databaseWriteExecutor.execute(() -> {
            alarmDao.update(alarm);
        });
    }
    public void delete(SmartAlarm alarm){
        SmartAlarmDatabase.databaseWriteExecutor.execute(() -> {
            alarmDao.delete(alarm);
        });
    }

    public LiveData<List<SmartAlarm>> getAlarmsLiveData(){
        return alarmsLiveData;
    }

}
