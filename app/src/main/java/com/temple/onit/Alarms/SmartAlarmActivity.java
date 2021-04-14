package com.temple.onit.Alarms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.temple.onit.Alarms.database.CreateAlarmViewModel;
import com.temple.onit.Constants;
import com.temple.onit.MapFragment;
import com.temple.onit.R;
import com.temple.onit.ServerManager;

import java.util.Calendar;
import java.util.Random;


public class SmartAlarmActivity extends AppCompatActivity implements MapFragment.MapFragmentInterface, ServerManager.ResponseListener {

    private final String[] hoursArray = new String[24];
    private final String[] minutesArray = new String[60];

    private EditText alarmTitleEditText;
    private NumberPicker numberPickerHours;
    private NumberPicker numberPickerMinutes;
    private CheckBox recurringCheckBox;
    private TimePicker timePicker;
    private View dayPicker;
    private ToggleButton sunday;
    private ToggleButton monday;
    private ToggleButton tuesday;
    private ToggleButton wednesday;
    private ToggleButton thursday;
    private ToggleButton friday;
    private ToggleButton saturday;


    private Button nextButton;
    private LatLng destinationLocation;
    private LatLng startingLocation;

    private boolean hasLocations = false;
    private boolean hasAllData = false;

    private long transitTime = 0;

    private int state = 0;

    private CreateAlarmViewModel createAlarmViewModel;

    private MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_alarm);
        alarmTitleEditText = findViewById(R.id.alarmTitleEditText);
        numberPickerHours = findViewById(R.id.numberPickerHours);
        numberPickerMinutes = findViewById(R.id.numberPickerMinutes);
        recurringCheckBox = findViewById(R.id.recurringCheckbox);
        timePicker = findViewById(R.id.timePicker);
        dayPicker = findViewById(R.id.daypicker);
        nextButton = findViewById(R.id.nextButton);

        sunday = findViewById(R.id.sun_toggle);
        monday = findViewById(R.id.mon_toggle);
        tuesday = findViewById(R.id.tues_toggle);
        wednesday = findViewById(R.id.wed_toggle);
        thursday = findViewById(R.id.thurs_toggle);
        friday = findViewById(R.id.fri_toggle);
        saturday = findViewById(R.id.sat_toggle);
        createAlarmViewModel = ViewModelProviders.of(this).get(CreateAlarmViewModel.class);

        loadArrays();

        numberPickerHours.setDisplayedValues(hoursArray);
        numberPickerMinutes.setDisplayedValues(minutesArray);

        numberPickerHours.setMinValue(0);
        numberPickerHours.setMaxValue(hoursArray.length - 1);

        numberPickerMinutes.setMinValue(0);
        numberPickerMinutes.setMaxValue(minutesArray.length - 1);

        setNextButtonListener();

        recurringCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    dayPicker.setVisibility(View.VISIBLE);
                }
                else{
                    dayPicker.setVisibility(View.GONE);
                    sunday.setChecked(false);
                    monday.setChecked(false);
                    tuesday.setChecked(false);
                    wednesday.setChecked(false);
                    thursday.setChecked(false);
                    friday.setChecked(false);
                    saturday.setChecked(false);
                }
            }

        });

    }
    private void loadArrays(){
        for(int i = 0; i < 24; i++){
            hoursArray[i] = String.valueOf(i);
        }
        for(int i = 0; i< 60; i++){
            minutesArray[i] = String.valueOf(i);
        }
    }

    private void setNextButtonListener(){
        nextButton.setOnClickListener(v -> {
            if(alarmTitleEditText.getText().toString().equals("")) return;

            if(!hasLocations) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                mapFragment = MapFragment.newInstance(0);
                ft.add(R.id.fullscreenMapLayout, mapFragment).addToBackStack(null).commit();
                View mapLayout = findViewById(R.id.fullscreenMapLayout);
                mapLayout.setVisibility(View.VISIBLE);
                View smartAlarmLinearLayout = findViewById(R.id.smartAlarmLinearLayout);
                smartAlarmLinearLayout.setVisibility(View.GONE);
            }
            else{
                if(hasAllData){
                    saveAlarm();
                    finish();
                }
            }
        });
    }

    private void saveAlarm(){
        int id = new Random().nextInt(Integer.MAX_VALUE);


        SmartAlarm smartAlarm = new SmartAlarm(id, timePicker.getHour(), timePicker.getMinute(), calculateGetReadyTime(), transitTime, alarmTitleEditText.getText().toString(), getDaysEnabled(), true, recurringCheckBox.isChecked(), startingLocation.latitude, startingLocation.longitude, destinationLocation.latitude, destinationLocation.longitude, System.currentTimeMillis());
        Log.d("Save Alarm", smartAlarm.toString());
        createAlarmViewModel.insert(smartAlarm);
        smartAlarm.schedule(this);

    }

    private long calculateGetReadyTime(){
        long millis = 0;
        millis = millis + numberPickerHours.getValue()*60*60*1000;
        millis = millis + numberPickerMinutes.getValue()*60*1000;
        return millis;
    }

    private String getDaysEnabled(){
        String returnString = "";
        if(sunday.isChecked()){
            returnString += "1";
        }
        else{
            returnString += "0";
        }
        if(monday.isChecked()){
            returnString += "1";
        }
        else{
            returnString += "0";
        }
        if(tuesday.isChecked()){
            returnString += "1";
        }
        else{
            returnString += "0";
        }
        if(wednesday.isChecked()){
            returnString += "1";
        }
        else{
            returnString += "0";
        }
        if(thursday.isChecked()){
            returnString += "1";
        }
        else{
            returnString += "0";
        }
        if(friday.isChecked()){
            returnString += "1";
        }
        else{
            returnString += "0";
        }
        if(saturday.isChecked()){
            returnString += "1";
        }
        else{
            returnString += "0";
        }
        return returnString;

    }


    @Override
    public void saveLocation(LatLng latLng, int state) {
        if(state == 0){
            destinationLocation = latLng;
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(mapFragment);
            mapFragment = MapFragment.newInstance(1);
            ft.add(R.id.fullscreenMapLayout, mapFragment);
            ft.commit();
        }
        if(state == 1){
            startingLocation = latLng;
            hasLocations();
            calculateTransitTime();
        }
    }

    private void calculateTransitTime(){
        LatLng origin = startingLocation;
        LatLng destination = destinationLocation;
        long arrivalTimeOfDay = calculateMillis();
        long arrivalTimeInSeconds = (ServerManager.calculateArrivalTimeInMillis(Calendar.SUNDAY) + arrivalTimeOfDay)/1000;

        ServerManager.sendRequest(origin, destination, arrivalTimeInSeconds, this);

        //this will forward to the call back gotResponse to set the transit time
    }

    private long calculateMillis(){
        long millis = 0;
        millis = millis + timePicker.getHour() * 60*60*1000;
        millis = millis + timePicker.getMinute()*60*1000;
        return millis;
    }

    private void hasLocations(){
        hasLocations = true;
        nextButton.setText("Save Alarm");
        View mapLayout = findViewById(R.id.fullscreenMapLayout);
        mapLayout.setVisibility(View.GONE);
        View smartAlarmLinearLayout = findViewById(R.id.smartAlarmLinearLayout);
        smartAlarmLinearLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void gotResponse(String jsonObject) {
        transitTime = ServerManager.parseDirectionsJson(jsonObject);
        hasAllData = true;
    }
}