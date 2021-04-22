package com.temple.onit.dashboard;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.temple.onit.Alarms.SmartAlarm;
import com.temple.onit.Alarms.database.SmartAlarmRepository;
import com.temple.onit.GeofencedReminder.GeofenceReminderManager;
import com.temple.onit.GeofencedReminder.GeofencedReminder;

import java.util.List;

public class DashboardViewModel extends ViewModel {

    private SmartAlarmRepository repository;
    private GeofenceReminderManager geofenceReminderManager;
    private LiveData<List<SmartAlarm>> alarmCountLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> proximityCountLiveData = new MutableLiveData<>();
    private MutableLiveData<List<GeofencedReminder>>  geoCountLiveData = new MutableLiveData<>();

    public void setRepositoryContext(Application application){
        this.repository = new SmartAlarmRepository(application);
        this.geofenceReminderManager = new GeofenceReminderManager(application);
        this.alarmCountLiveData = repository.getAlarmsLiveData();

    }

    public LiveData<List<SmartAlarm>> getAlarmCountLiveData() {
        return alarmCountLiveData;
    }

    public MutableLiveData<Integer> getProximityCountLiveData() {
        return proximityCountLiveData;
    }

    public MutableLiveData<List<GeofencedReminder>> getGeoCountLiveData() {
        updateGeoCountLiveData();
        return geoCountLiveData;
    }

    private void updateGeoCountLiveData(){
        if (geofenceReminderManager.getAll() != null) {
            geoCountLiveData.setValue(geofenceReminderManager.getAll());
        }
    }

}
