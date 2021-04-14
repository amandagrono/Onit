package com.temple.onit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.temple.onit.Alarms.SmartAlarmActivity;
import com.temple.onit.Alarms.list.AlarmListActivity;
import com.temple.onit.GeofencedReminder.GeofencedReminderActivity;
import com.temple.onit.Alarms.SmartAlarm;
import com.temple.onit.services.LocationService;

public class MainActivity extends AppCompatActivity {

    private Button newAlarmButton;
    private Button newProximityReminderButton;
    private Button newGeofencedReminderButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newAlarmButton = findViewById(R.id.button_alarm);
        newProximityReminderButton = findViewById(R.id.button_proximity_reminder);
        newGeofencedReminderButton = findViewById(R.id.button_geofenced_reminder);

        newAlarmButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AlarmListActivity.class);
            launchSmartAlarm(intent);
        });
        newProximityReminderButton.setOnClickListener(v->{

        });
        newGeofencedReminderButton.setOnClickListener(v->{
            Intent intent = new Intent(MainActivity.this, GeofencedReminderActivity.class);
            launchGeofencedReminder(intent);
        });

        Intent serviceIntent = new Intent(this, LocationService.class);
        startForegroundService(serviceIntent);



    }

    public void launchSmartAlarm(Intent intent){
        startActivity(intent);
    }
    public void launchProximityReminder(Intent intent){

    }
    public void launchGeofencedReminder(Intent intent){
        startActivity(intent);
    }


}