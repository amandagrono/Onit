package com.temple.onit.Alarms.list;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.temple.onit.Alarms.SmartAlarm;
import com.temple.onit.Alarms.SmartAlarmActivity;
import com.temple.onit.R;

import java.util.List;

public class AlarmListActivity extends AppCompatActivity implements OnToggleAlarmListener {

    private SmartAlarmRecyclerView recyclerViewAdapter;
    private SmartAlarmViewModel alarmViewModel;
    private RecyclerView recyclerView;
    private FloatingActionButton createAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);

        recyclerViewAdapter = new SmartAlarmRecyclerView(this);
        alarmViewModel = ViewModelProviders.of(this).get(SmartAlarmViewModel.class);
        alarmViewModel.getAlarmsLiveData().observe(this, smartAlarms -> {
            if(smartAlarms != null){
                recyclerViewAdapter.setAlarms(smartAlarms);
            }
        });

        recyclerView = findViewById(R.id.alarm_list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(recyclerViewAdapter);

        createAlarm = findViewById(R.id.new_alarm_floating_action_button);

        //starts the alarm activity to create a new alarm
        createAlarm.setOnClickListener(v -> {
            Intent intent = new Intent(this, SmartAlarmActivity.class);
            startActivity(intent);
        });

    }

    @Override
    public View onCreateView(@Nullable @org.jetbrains.annotations.Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {


        return super.onCreateView(parent, name, context, attrs);
    }

    // cancels/schedules alarm based on what the user selects
    @Override
    public void onToggle(SmartAlarm alarm) {
        if(alarm.isStarted()){
            alarm.cancel(getApplicationContext());
            alarmViewModel.update(alarm);
        }
        else{
            alarm.schedule(getApplicationContext());
            alarmViewModel.update(alarm);
        }
    }

    // Deleted selected alarm
    @Override
    public void onDelete(SmartAlarm alarm) {
        if(alarm.isStarted()){
            alarm.cancel(getApplicationContext());
        }
        alarmViewModel.delete(alarm);
    }
}