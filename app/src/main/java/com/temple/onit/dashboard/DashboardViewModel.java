package com.temple.onit.dashboard;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.temple.onit.Alarms.SmartAlarm;
import com.temple.onit.Alarms.database.SmartAlarmRepository;
import com.temple.onit.Constants;
import com.temple.onit.GeofencedReminder.GeofenceReminderManager;
import com.temple.onit.GeofencedReminder.GeofencedReminder;
import com.temple.onit.OnitApplication;
import com.temple.onit.dataclasses.ProximityReminder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class DashboardViewModel extends ViewModel {

    private Application application;
    private SmartAlarmRepository repository;
    private GeofenceReminderManager geofenceReminderManager;
    private LiveData<List<SmartAlarm>> alarmCountLiveData = new MutableLiveData<>();
    private MutableLiveData<ArrayList<ProximityReminder>> proximityCountLiveData = new MutableLiveData<ArrayList<ProximityReminder>>();
    private MutableLiveData<List<GeofencedReminder>>  geoCountLiveData = new MutableLiveData<>();

    public void setRepositoryContext(Application application){
        this.repository = new SmartAlarmRepository(application);
        this.geofenceReminderManager = new GeofenceReminderManager(application);
        this.alarmCountLiveData = repository.getAlarmsLiveData();
        this.application = application;

    }

    public LiveData<List<SmartAlarm>> getAlarmCountLiveData() {
        return alarmCountLiveData;
    }

    public int getProximityCount() {
        updateProximityCountLiveData();
        return (remindersList == null)? 0:remindersList.size();
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

    private void updateProximityCountLiveData(){
        new ThreadFetchCount().execute(new Runnable() {
            @Override
            public void run() {
                getRemindersFromServer();
            }
        });
    }

    private ArrayList<ProximityReminder> remindersList;
    private void getRemindersFromServer(){
        remindersList = new ArrayList<>();
        RequestQueue requestQueue = Volley.newRequestQueue(application.getApplicationContext());
        String url = Constants.API_GET_USER_REMINDERS +
                "?username=" + OnitApplication.instance.getAccountManager().username;
        StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
            Log.d("ResponseUserReminder", response);
            convertResponseToList(response);
        }, error -> {

        });
        requestQueue.add(request);
    }

    private void convertResponseToList(String response){
        try{
            JSONArray jsonArray = new JSONArray(response);
            Log.d("UserReminders", response);
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject object = jsonArray.getJSONObject(i);
                int id = object.getInt("id");
                String issuer_id = object.getString("issuer_id");
                String target_id = object.getString("target_id");
                String title = object.getString("title");
                String body = object.getString("body");
                double distance = object.getDouble("distance");
                boolean accepted = object.getInt("accepted") == 1;
                ProximityReminder reminder = new ProximityReminder(title, body, distance, issuer_id, target_id, id, accepted);
                remindersList.add(reminder);
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }

    }

    private static class ThreadFetchCount implements Executor{
        public void execute(Runnable r){
            new Thread(r).start();
        }
    }

}
