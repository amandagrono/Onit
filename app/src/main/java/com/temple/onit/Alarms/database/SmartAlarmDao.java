package com.temple.onit.Alarms.database;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.temple.onit.Alarms.SmartAlarm;

import java.util.List;

@Dao
public interface SmartAlarmDao {

    @Insert
    void insert(SmartAlarm alarm);

    @Query("DELETE FROM alarm_table")
    void deleteAll();

    @Query("SELECT * FROM alarm_table ORDER BY created DESC")
    LiveData<List<SmartAlarm>> getAlarms();

    @Update
    void update(SmartAlarm alarm);

    @Delete
    void delete(SmartAlarm alarm);

}
