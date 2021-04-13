package com.temple.onit.Alarms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.List;

import kotlin.collections.CollectionsKt;

public class SmartAlarmManager {

    Context context;
    private SharedPreferences sharedPreferences;
    private Gson gson = new Gson();

    public SmartAlarmManager(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences("Alarms", Context.MODE_PRIVATE);
    }

    public void add(SmartAlarm smartAlarm){
        List<SmartAlarm> tempList = getAll();
        tempList.add(smartAlarm);
        saveAll(tempList);
    }

    public void remove(SmartAlarm smartAlarm){
        List<SmartAlarm> tempList = getAll();
        tempList.remove(smartAlarm);
        smartAlarm.cancelAlarm(context);
        saveAll(tempList);
    }

    public List<SmartAlarm> getAll(){
        if(sharedPreferences.contains("SMART_ALARMS")){
            String gsonString = sharedPreferences.getString("SMART_ALARMS", null);
            SmartAlarm[] arrayOfAlarms = gson.fromJson(gsonString, SmartAlarm[].class);
            if(arrayOfAlarms != null){
                return CollectionsKt.mutableListOf(arrayOfAlarms);
            }
        }
        return CollectionsKt.mutableListOf();
    }

    public void saveAll(List<SmartAlarm> list){
        sharedPreferences.edit().putString("SMART_ALARMS", gson.toJson(list)).apply();
    }

}
