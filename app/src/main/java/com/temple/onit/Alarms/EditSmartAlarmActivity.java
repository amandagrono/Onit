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
import com.temple.onit.EditSmartAlarmMapFragment;
import com.temple.onit.MapFragment;
import com.temple.onit.R;
import com.temple.onit.ServerManager;

import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class EditSmartAlarmActivity extends AppCompatActivity implements MapFragment.MapFragmentInterface, ServerManager.ResponseListener {
// EditSmartAlarmActivity is a copy of SmartAlarmActivity but designed to display the values of a alarm users wish to edit. it follows the same step by step execution
// The only difference is that this activity deletes the old intent and creates a new one upon completion
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
        private long transitTime;
        private int state = 0;
        private CreateAlarmViewModel createAlarmViewModel;
        private EditSmartAlarmMapFragment mapFragment;

        Bundle args;
        String oldTitle,days;
        double startLat,startLong,destLat,destLong;
        LatLng destination,startPoint;
        int hour,min,id;
        long getReady,oldTransitTime;
        boolean dontSaveOnDestroy = false; // true if activity is destroyed because user saved alterations and not backed out



        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_smart_alarm);

            if (getIntent().getBundleExtra("args") == null) {
                finish();
            }else { // get old smart alrm details to be be displayed
                Bundle args = getIntent().getBundleExtra("args");
                oldTitle = args.getString("title");
                startLat = args.getDouble("startLat");
                startLong = args.getDouble("startLong");
                startPoint = new LatLng(startLat,startLong);
                destLat = args.getDouble("destLat");
                destLong = args.getDouble("destLong");
                destination = new LatLng(destLat,destLong);
                days = args.getString("days");
                hour = args.getInt("startHour");
                min = args.getInt("startMin");
                getReady = args.getLong("getReady");
                id = args.getInt("id");
                oldTransitTime = args.getLong("transitTime");

            }

            // set old title
            alarmTitleEditText = findViewById(R.id.alarmTitleEditText);
            alarmTitleEditText.setText(oldTitle);

            numberPickerHours = findViewById(R.id.numberPickerHours);
            numberPickerMinutes = findViewById(R.id.numberPickerMinutes);
            recurringCheckBox = findViewById(R.id.recurringCheckbox);
            timePicker = findViewById(R.id.timePicker);
            // set old arrival time
            timePicker.setHour(hour);
            timePicker.setMinute(min);

            dayPicker = findViewById(R.id.daypicker);
            nextButton = findViewById(R.id.nextButton);

            sunday = findViewById(R.id.sun_toggle);
            monday = findViewById(R.id.mon_toggle);
            tuesday = findViewById(R.id.tues_toggle);
            wednesday = findViewById(R.id.wed_toggle);
            thursday = findViewById(R.id.thurs_toggle);
            friday = findViewById(R.id.fri_toggle);
            saturday = findViewById(R.id.sat_toggle);
            // display old days alarm is set for
            dayPicker.setVisibility(View.VISIBLE);
            recurringCheckBox.setChecked(true);
            sunday.setChecked(dayToggle(days.substring(0,1)));
            monday.setChecked(dayToggle(days.substring(1,2)));
            tuesday.setChecked(dayToggle(days.substring(2,3)));
            wednesday.setChecked(dayToggle(days.substring(3,4)));
            thursday.setChecked(dayToggle(days.substring(4,5)));
            friday.setChecked(dayToggle(days.substring(5,6)));
            saturday.setChecked(dayToggle(days.substring(6)));


            createAlarmViewModel = ViewModelProviders.of(this).get(CreateAlarmViewModel.class);

            loadArrays();

            numberPickerHours.setDisplayedValues(hoursArray);
            numberPickerMinutes.setDisplayedValues(minutesArray);

            // set old get ready time
            numberPickerHours.setMinValue(0);
            numberPickerHours.setMaxValue(hoursArray.length - 1);
            TimeUnit.MILLISECONDS.toHours(getReady);
            numberPickerHours.setValue( (int) TimeUnit.MILLISECONDS.toHours(getReady));

            numberPickerMinutes.setMinValue(0);
            numberPickerMinutes.setMaxValue(minutesArray.length - 1);
            numberPickerMinutes.setValue((int) TimeUnit.MILLISECONDS.toMinutes(getReady) % 60);// get rid of hours in getReady milliseconds


            setNextButtonListener();

            recurringCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        dayPicker.setVisibility(View.VISIBLE);
                    }
                    else{ // show days enabled
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
        // old alarm is deleted as edit is pressed, this is so if users back out after edit, a new alarm with the old details is created
        @Override
        protected void onDestroy() {
            super.onDestroy();
            if(!dontSaveOnDestroy) {
                SmartAlarm smartAlarm = new SmartAlarm(id, hour, min, getReady, oldTransitTime, oldTitle, days, true, recurringCheckBox.isChecked(), startLat, startLong, destLat, destLong, System.currentTimeMillis());
                Log.d("Save Alarm", smartAlarm.toString());
                createAlarmViewModel.insert(smartAlarm);
                smartAlarm.schedule(this);
            }
        }
        // used to turn 1 into true and everything else false to toggle dayOFWeek.SetChecked()
        private boolean dayToggle(String day){
            if (day.matches("1")) {
                return true;
            }
            return  false;
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
                    mapFragment = EditSmartAlarmMapFragment.newInstance(0,destination);
                    // pass old de to display titles;
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
            dontSaveOnDestroy = true;
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
                mapFragment = EditSmartAlarmMapFragment.newInstance(1,startPoint);
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

