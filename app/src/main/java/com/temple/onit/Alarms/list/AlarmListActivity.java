package com.temple.onit.Alarms.list;

import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.annotation.PluralsRes;
import androidx.appcompat.app.ActionBar;
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
import com.temple.onit.Alarms.EditSmartAlarmActivity;
import com.temple.onit.Alarms.SmartAlarm;
import com.temple.onit.Alarms.SmartAlarmActivity;
import com.temple.onit.R;
import com.temple.onit.dashboard.DashboardActivity;

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
        createAlarm.setOnClickListener(v -> {
            Intent intent = new Intent(this, SmartAlarmActivity.class);
            startActivity(intent);
        });

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        DashboardActivity.finishActivity(item, this);
        return true;

    }


    @Override
    public View onCreateView(@Nullable @org.jetbrains.annotations.Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {


        return super.onCreateView(parent, name, context, attrs);
    }

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

    @Override
    public void onDelete(SmartAlarm alarm) {
        if(alarm.isStarted()){
            alarm.cancel(getApplicationContext());
        }
        alarmViewModel.delete(alarm);
    }

    @Override
    public void editThis(SmartAlarm alarm) {
        Intent intent = new Intent(this, EditSmartAlarmActivity.class);
        // pass old alarm details
        Bundle args = new Bundle();
        args.putString("title",alarm.getAlarmTitle());
        args.putDouble("startLat",alarm.getStartingLatitude());
        args.putDouble("startLong",alarm.getStartingLongitude());
        args.putDouble("destLat",alarm.getDestinationLatitude());
        args.putDouble("destLong",alarm.getDestinationLongitude());
        args.putString("days",alarm.getDays());
        args.putInt("startHour",alarm.getArrivalHour());
        args.putInt("startMin",alarm.getArrivalMinute());
        args.putLong("getReady",alarm.getGetReadyTime());
        args.putInt("id",alarm.getAlarmId());
        args.putLong("transitTime",alarm.getTransitTime());
        intent.putExtra("args",args);
        onDelete(alarm);// delete old intent
        startActivity(intent);

    }
}