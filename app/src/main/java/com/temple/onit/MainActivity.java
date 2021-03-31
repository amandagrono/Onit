package com.temple.onit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.temple.onit.dataclasses.SmartAlarm;

import java.time.LocalTime;

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
            Intent intent = new Intent(MainActivity.this, SmartAlarmActivity.class);
            launchSmartAlarm(intent);
        });
        newProximityReminderButton.setOnClickListener(v->{
            Intent intent = new Intent(MainActivity.this, SmartAlarmActivity.class);
            SmartAlarm smartAlarm = new SmartAlarm();
            smartAlarm.setAlarmTitle("Work");
            smartAlarm.setArrivalTime(9, 30);
            smartAlarm.setGetReadyTime(8700000);
            smartAlarm.setDays(new boolean[]{false, true, true, true, true, true, false});
            intent.putExtra("alarm", smartAlarm);
            launchSmartAlarm(intent);
        });

    }

    public void launchSmartAlarm(Intent intent){
        startActivity(intent);
    }
    public void launchProximityReminder(Intent intent){

    }
    public void launchGeofencedReminder(Intent intent){

    }


}