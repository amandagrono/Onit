package com.temple.onit.Alarms.list;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.temple.onit.Alarms.SmartAlarm;
import com.temple.onit.Alarms.database.SmartAlarmRepository;

import java.util.List;

public class SmartAlarmViewModel extends AndroidViewModel {

    private SmartAlarmRepository repository;
    private LiveData<List<SmartAlarm>> listLiveData;

    public SmartAlarmViewModel(@NonNull Application application) {
        super(application);

        repository = new SmartAlarmRepository(application);
        listLiveData = repository.getAlarmsLiveData();
    }

    public void update(SmartAlarm alarm){
        repository.update(alarm);
    }
    public void delete(SmartAlarm alarm){
        repository.delete(alarm);
    }
    public LiveData<List<SmartAlarm>> getAlarmsLiveData(){
        return listLiveData;
    }
}
