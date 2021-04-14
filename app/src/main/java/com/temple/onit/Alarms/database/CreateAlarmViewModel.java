package com.temple.onit.Alarms.database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.temple.onit.Alarms.SmartAlarm;

public class CreateAlarmViewModel extends AndroidViewModel {

    private SmartAlarmRepository repository;
    public CreateAlarmViewModel(@NonNull Application application) {
        super(application);

        repository = new SmartAlarmRepository(application);

    }
    public void insert(SmartAlarm alarm){
        repository.insert(alarm);
    }
}
