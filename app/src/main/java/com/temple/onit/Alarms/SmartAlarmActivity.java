package com.temple.onit.Alarms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import com.temple.onit.Constants;
import com.temple.onit.MapFragment;
import com.temple.onit.R;
import com.temple.onit.ServerManager;

import java.util.Calendar;


public class SmartAlarmActivity extends AppCompatActivity implements OnMapReadyCallback, MapFragment.MapFragmentInterface, ServerManager.ResponseListener {

    private final String[] hoursArray = new String[24];
    private final String[] minutesArray = new String[60];


    GoogleMap mapAPI;
    MapFragment mapFragment;
    MapFragment mapFragment2;
    SmartAlarm smartAlarm;

    EditText titleEditText;
    NumberPicker hoursNumberPicker;
    NumberPicker minutesNumberPicker;
    TimePicker timePicker;
    Button nextButton;
    ToggleButton sunday, monday, tuesday, wednesday, thursday, friday, saturday;
    CheckBox checkBox;
    View dayPicker;


    /** Alarm Values To Be Saved **/
    int hoursInt = 0;
    int minutesInt = 0;
    int arrivalHour = 0;
    int arrivalMinute = 0;
    private String daysArray;
    String alarmTitle = "";
    boolean recurring = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_alarm);


        daysArray = "0000000";

        titleEditText = findViewById(R.id.alarmTitleEditText);
        hoursNumberPicker = findViewById(R.id.numberPickerHours);
        minutesNumberPicker = findViewById(R.id.numberPickerMinutes);
        timePicker = findViewById(R.id.timePicker);
        nextButton = findViewById(R.id.nextButton);
        dayPicker = findViewById(R.id.daypicker);

        //Toggle Buttons
        sunday = findViewById(R.id.sun_toggle);
        monday = findViewById(R.id.mon_toggle);
        tuesday = findViewById(R.id.tues_toggle);
        wednesday = findViewById(R.id.wed_toggle);
        thursday = findViewById(R.id.thurs_toggle);
        friday = findViewById(R.id.fri_toggle);
        saturday = findViewById(R.id.sat_toggle);

        //CheckBox
        checkBox = findViewById(R.id.recurringCheckbox);

        //When user wants to edit alarm.
        if(getIntent().hasExtra("alarm")){

            smartAlarm = (SmartAlarm) getIntent().getParcelableExtra("alarm");

            daysArray = smartAlarm.getDays();
            hoursInt = calculateHours(smartAlarm.getGetReadyTime());
            minutesInt = calculateMinutes(smartAlarm.getGetReadyTime(), hoursInt);
            arrivalHour = smartAlarm.getArrivalHour();
            arrivalMinute = smartAlarm.getArrivalMinute();
            alarmTitle = smartAlarm.getAlarmTitle();
            recurring = smartAlarm.getRecurring();
            Log.d("Smart Alarm Restore: ", "Smart Alarm Object: \n_______________________\n" + smartAlarm.toString());
            Log.d("Smart Alarm Restore: ", "Field Alarm Title: " + alarmTitle);
            Log.d("Smart Alarm Restore: ", "Field numberpicker hoursInt: " + hoursInt);
            Log.d("Smart Alarm Restore: ", "Field numberpicker minutesInt: " + minutesInt);
            Log.d("Smart Alarm Restore: ", "Field timepicker arrival hour: " + arrivalHour);
            Log.d("Smart Alarm Restore: ", "Field timepicker arrival minute: " + arrivalMinute);
            Log.d("Smart Alarm Restore: ", "Field Days Array:  " + daysArray);
            Log.d("Smart Alarm Restore: ", "Field recurring: " + recurring);



        }
        else{
            smartAlarm = new SmartAlarm();
        }



        loadArrays();
        listeners();
        resetViews();

        Log.d("Alarm" , smartAlarm.toString());

        mapFragment = MapFragment.newInstance(0);
        mapFragment2 = MapFragment.newInstance(1);

    }

    private void listeners(){
        titleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                alarmTitle = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        hoursNumberPicker.setDisplayedValues(hoursArray);
        minutesNumberPicker.setDisplayedValues(minutesArray);
        hoursNumberPicker.setMinValue(0);
        hoursNumberPicker.setMaxValue(hoursArray.length-1);
        minutesNumberPicker.setMinValue(0);
        minutesNumberPicker.setMaxValue(minutesArray.length-1);
        hoursNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                hoursInt = newVal;
            }
        });
        minutesNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                minutesInt = newVal;
            }
        });
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                arrivalHour = hourOfDay;
                arrivalMinute = minute;
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("OnClick", "OnClickCalled");
                if(titleEditText.getText().toString().equals("")){
                    Log.d("OnClick", "If Statement");
                    Toast.makeText(SmartAlarmActivity.this, "must enter alarm title", Toast.LENGTH_LONG).show();
                    return;
                }
                smartAlarm.setAlarmTitle(titleEditText.getText().toString());
                smartAlarm.setGetReadyTime(calculateMillis(hoursInt, minutesInt));
                smartAlarm.setArrivalTime(arrivalHour, arrivalMinute);
                smartAlarm.setDays(daysArray);
                smartAlarm.setAlarmTitle(alarmTitle);

                Log.d("Smart Alarm Next: ", smartAlarm.toString());

                View layout = findViewById(R.id.smartAlarmLinearLayout);
                layout.setVisibility(View.GONE);

                View mapView = findViewById(R.id.fullscreenMapLayout);
                mapView.setVisibility(View.VISIBLE);
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.add(R.id.fullscreenMapLayout, mapFragment).commit();


            }
        });
        sunday.setOnCheckedChangeListener((buttonView, isChecked) -> daysArray = newString(0, isChecked));
        monday.setOnCheckedChangeListener((buttonView, isChecked) -> daysArray = newString(1, isChecked));
        tuesday.setOnCheckedChangeListener((buttonView, isChecked) -> daysArray = newString(2, isChecked));
        wednesday.setOnCheckedChangeListener((buttonView, isChecked) -> daysArray = newString(3, isChecked));
        thursday.setOnCheckedChangeListener((buttonView, isChecked) -> daysArray = newString(4, isChecked));
        friday.setOnCheckedChangeListener((buttonView, isChecked) -> daysArray = newString(5, isChecked));
        saturday.setOnCheckedChangeListener((buttonView, isChecked) -> daysArray = newString(6, isChecked));
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            recurring = isChecked;
            if(recurring) dayPicker.setVisibility(View.VISIBLE);
            if(!recurring){
                sunday.setChecked(false);
                monday.setChecked(false);
                tuesday.setChecked(false);
                wednesday.setChecked(false);
                thursday.setChecked(false);
                friday.setChecked(false);
                saturday.setChecked(false);
                dayPicker.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapAPI = googleMap;
        LatLng temple = new LatLng(39.981142, -75.156161);
        mapAPI.addMarker(new MarkerOptions().position(temple).title("Temple"));

        CameraUpdate center = CameraUpdateFactory.newLatLngZoom(temple, 15);
        mapAPI.animateCamera(center);

        mapAPI.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Log.d("LatLng", latLng.toString());
                mapAPI.clear();
                mapAPI.addMarker(new MarkerOptions().position(latLng));

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

    private long calculateMillis(int hours, int minutes){
        long millis = 0;
        millis = millis + hours*60*60*1000;
        millis = millis + minutes*60*1000;
        return millis;
    }
    private int calculateHours(long millis){
        int hours = 0;
        while(millis > Constants.HOUR_IN_MILLIS){
            millis = millis - Constants.HOUR_IN_MILLIS;

            hours++;
        }
        return hours;
    }
    private int calculateMinutes(long millis, long hours){
        millis = millis % (hours*Constants.HOUR_IN_MILLIS);
        int minutes = 0;

        while(millis >= Constants.MINUTE_IN_MILLIS){
            millis = millis - Constants.MINUTE_IN_MILLIS;
            minutes++;
        }
        return minutes;
    }
    private void resetViews(){
        titleEditText.setText(alarmTitle);
        hoursNumberPicker.setValue(hoursInt);
        minutesNumberPicker.setValue(minutesInt);

        /** Everything outside of this works as intended **/
        Log.d("TimePicker", "Hour: " + String.valueOf(arrivalHour));
        int temp = arrivalHour;
        int temp2 = arrivalMinute;
        timePicker.setHour(temp);
        Log.d("TimePicker", "Minute: " + String.valueOf(arrivalMinute));
        timePicker.setMinute(temp2);

        /**************************************************/

        sunday.setChecked(toBoolean(daysArray.charAt(0)));
        monday.setChecked(toBoolean(daysArray.charAt(1)));
        tuesday.setChecked(toBoolean(daysArray.charAt(2)));
        wednesday.setChecked(toBoolean(daysArray.charAt(3)));
        thursday.setChecked(toBoolean(daysArray.charAt(4)));
        friday.setChecked(toBoolean(daysArray.charAt(5)));
        saturday.setChecked(toBoolean(daysArray.charAt(6)));
    }

    @Override
    public void saveLocation(LatLng latLng, int state) {

        if(state == 0) { //called when user is saving destination location.

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(mapFragment);
            ft.add(R.id.fullscreenMapLayout, mapFragment2)
                    .commit();
            Location location = new Location("");
            location.setLongitude(latLng.longitude);
            location.setLatitude(latLng.latitude);
            smartAlarm.setDestinationLocation(location);

        }
        else{ // State == 1 Called when user is saving starting location.
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(mapFragment).remove(mapFragment2).commit();

            View linearLayout = findViewById(R.id.smartAlarmLinearLayout);
            linearLayout.setVisibility(View.VISIBLE);
            View mapLayout = findViewById(R.id.fullscreenMapLayout);
            mapLayout.setVisibility(View.GONE);

            Location location = new Location("");
            location.setLongitude(latLng.longitude);
            location.setLatitude(latLng.latitude);
            smartAlarm.setLastKnownLocation(location);
            calculateTransitTime();
            mapFragment = MapFragment.newInstance(0);
            mapFragment2 = MapFragment.newInstance(1);
        }

    }

    @Override
    public void onBackPressed() {
        Log.d("OnBackPressed", "OnBackPressed Called");
        View layout = findViewById(R.id.smartAlarmLinearLayout);
        View mapLayout = findViewById(R.id.fullscreenMapLayout);
        if(layout.getVisibility() == View.GONE && mapFragment.isVisible()){
            layout.setVisibility(View.VISIBLE);
            mapLayout.setVisibility(View.GONE);
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(mapFragment).remove(mapFragment2).commit();
        }
        else if(layout.getVisibility() == View.GONE && mapFragment2.isVisible()){
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(mapLayout.getId(), mapFragment).commit();
            mapFragment2 = MapFragment.newInstance(1);
        }
        else {
            super.onBackPressed();
        }
    }

    public void calculateTransitTime(){
        LatLng origin = new LatLng(smartAlarm.getLastKnownLocation().getLatitude(), smartAlarm.getLastKnownLocation().getLongitude());
        LatLng destination = new LatLng(smartAlarm.getDestinationLocation().getLatitude(), smartAlarm.getDestinationLocation().getLongitude());
        long arrivalTimeOfDay = calculateMillis(smartAlarm.getArrivalHour(), smartAlarm.getArrivalMinute())/1000;
        long arrivalTimeInSeconds = (ServerManager.calculateArrivalTimeInMillis(Calendar.SUNDAY) + arrivalTimeOfDay);

        ServerManager.sendRequest(origin, destination, arrivalTimeInSeconds, this);
    }

    private char to0or1(boolean checked){
         if(checked) return '1';
         else return '0';
    }
    private boolean toBoolean(char zeroOr1){
        if(zeroOr1 == '1') return true;
        else return false;
    }
    private String newString(int index, boolean checked){
        StringBuilder newString = new StringBuilder(daysArray);
        newString.setCharAt(index, to0or1(checked));
        return newString.toString();
    }

    @Override
    public void gotResponse(String jsonObject) {
        smartAlarm.setTransitTime(ServerManager.parseDirectionsJson(jsonObject));
        Log.d("Smart Alarm W Transit Time", smartAlarm.toString());
    }
}