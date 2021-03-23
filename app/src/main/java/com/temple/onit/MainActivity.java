package com.temple.onit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.temple.onit.dataclasses.SmartAlarm;

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

    }

    public void launchSmartAlarm(Intent intent){
        startActivity(intent);
    }
    public void launchProximityReminder(Intent intent){

    }
    public void launchGeofencedReminder(Intent intent){

    }


}