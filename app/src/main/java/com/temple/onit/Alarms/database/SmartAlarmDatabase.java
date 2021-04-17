package com.temple.onit.Alarms.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.temple.onit.Alarms.SmartAlarm;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {SmartAlarm.class}, version = 1, exportSchema = false)
public abstract class SmartAlarmDatabase extends RoomDatabase {
    public abstract SmartAlarmDao alarmDao();

    private static volatile SmartAlarmDatabase instance;
    private static final int NUM_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUM_THREADS);

    static SmartAlarmDatabase getDatabase(final Context context){
        if(instance == null){
            synchronized (SmartAlarmDatabase.class){
                if(instance == null){
                    instance = Room.databaseBuilder(context.getApplicationContext(), SmartAlarmDatabase.class, "alarm_database").build();
                }
            }
        }
        return instance;
    }
}
