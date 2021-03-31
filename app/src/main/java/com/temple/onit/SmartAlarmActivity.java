package com.temple.onit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.temple.onit.dataclasses.SmartAlarm;

import java.util.Arrays;
import java.util.Calendar;


public class SmartAlarmActivity extends AppCompatActivity implements OnMapReadyCallback, MapFragment.MapFragmentInterface {

    private final String[] hoursArray = new String[24];
    private final String[] minutesArray = new String[60];
    private static final int HOUR_IN_MILLIS = 3600000;
    private static final int MINUTE_IN_MILLIS = 60000;

    GoogleMap mapAPI;
    MapFragment mapFragment;
    SmartAlarm smartAlarm;

    EditText titleEditText;
    NumberPicker hoursNumberPicker;
    NumberPicker minutesNumberPicker;
    TimePicker timePicker;
    Button nextButton;
    ToggleButton sunday;
    ToggleButton monday;
    ToggleButton tuesday;
    ToggleButton wednesday;
    ToggleButton thursday;
    ToggleButton friday;
    ToggleButton saturday;

    /** Alarm Values To Be Saved **/
    int hoursInt = 0;
    int minutesInt = 0;
    int arrivalHour = 0;
    int arrivalMinute = 0;
    private boolean[] daysArray;
    String alarmTitle = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_alarm);


        daysArray = new boolean[]{false, false, false, false, false, false, false};

        titleEditText = findViewById(R.id.alarmTitleEditText);
        hoursNumberPicker = findViewById(R.id.numberPickerHours);
        minutesNumberPicker = findViewById(R.id.numberPickerMinutes);
        timePicker = findViewById(R.id.timePicker);
        nextButton = findViewById(R.id.nextButton);

        //Toggle Buttons
        sunday = findViewById(R.id.sun_toggle);
        monday = findViewById(R.id.mon_toggle);
        tuesday = findViewById(R.id.tues_toggle);
        wednesday = findViewById(R.id.wed_toggle);
        thursday = findViewById(R.id.thurs_toggle);
        friday = findViewById(R.id.fri_toggle);
        saturday = findViewById(R.id.sat_toggle);

        //When user wants to edit alarm.
        if(getIntent().hasExtra("alarm")){

            smartAlarm = (SmartAlarm) getIntent().getParcelableExtra("alarm");
            daysArray = smartAlarm.getDays();
            hoursInt = calculateHours(smartAlarm.getGetReadyTime());
            minutesInt = calculateMinutes(smartAlarm.getGetReadyTime(), hoursInt);
            arrivalHour = smartAlarm.getArrivalHour();
            arrivalMinute = smartAlarm.getArrivalMinute();
            alarmTitle = smartAlarm.getAlarmTitle();
            Log.d("Smart Alarm Restore: ", "Smart Alarm Object: \n_______________________\n" + smartAlarm.toString());
            Log.d("Smart Alarm Restore: ", "Field Alarm Title: " + alarmTitle);
            Log.d("Smart Alarm Restore: ", "Field numberpicker hoursInt: " + hoursInt);
            Log.d("Smart Alarm Restore: ", "Field numberpicker minutesInt: " + minutesInt);
            Log.d("Smart Alarm Restore: ", "Field timepicker arrival hour: " + arrivalHour);
            Log.d("Smart Alarm Restore: ", "Field timepicker arrival minute: " + arrivalMinute);
            Log.d("Smart Alarm Restore: ", "Field Days Array:  " + Arrays.toString(daysArray));



        }
        else{
            smartAlarm = new SmartAlarm();
        }



        loadArrays();
        listeners();
        resetViews();

        Log.d("Alarm" , smartAlarm.toString());
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        mapFragment = new MapFragment();
        fragmentTransaction.add(R.id.fullscreenMapLayout, mapFragment);
        fragmentTransaction.addToBackStack(null).commit();
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



            }
        });
        sunday.setOnCheckedChangeListener((buttonView, isChecked) -> daysArray[0] = isChecked);
        monday.setOnCheckedChangeListener((buttonView, isChecked) -> daysArray[1] = isChecked);
        tuesday.setOnCheckedChangeListener((buttonView, isChecked) -> daysArray[2] = isChecked);
        wednesday.setOnCheckedChangeListener((buttonView, isChecked) -> daysArray[3] = isChecked);
        thursday.setOnCheckedChangeListener((buttonView, isChecked) -> daysArray[4] = isChecked);
        friday.setOnCheckedChangeListener((buttonView, isChecked) -> daysArray[5] = isChecked);
        saturday.setOnCheckedChangeListener((buttonView, isChecked) -> daysArray[6] = isChecked);

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
        while(millis > HOUR_IN_MILLIS){
            millis = millis - HOUR_IN_MILLIS;

            hours++;
        }
        return hours;
    }
    private int calculateMinutes(long millis, long hours){
        millis = millis % (hours*HOUR_IN_MILLIS);
        int minutes = 0;

        while(millis >= MINUTE_IN_MILLIS){
            millis = millis - MINUTE_IN_MILLIS;
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

        sunday.setChecked(daysArray[0]);
        monday.setChecked(daysArray[1]);
        tuesday.setChecked(daysArray[2]);
        wednesday.setChecked(daysArray[3]);
        thursday.setChecked(daysArray[4]);
        friday.setChecked(daysArray[5]);
        saturday.setChecked(daysArray[6]);
    }

    @Override
    public void saveLocation(LatLng latLng) {

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(mapFragment).commit();

        View linearLayout = findViewById(R.id.smartAlarmLinearLayout);
        linearLayout.setVisibility(View.VISIBLE);
        View mapLayout = findViewById(R.id.fullscreenMapLayout);
        mapLayout.setVisibility(View.GONE);

        Location location = new Location("");
        location.setLongitude(latLng.longitude);
        location.setLatitude(latLng.latitude);
        smartAlarm.setDestinationLocation(location);
        calculateTransitTime();
    }

    public void calculateTransitTime(){
        LatLng origin = new LatLng(40.238676,-74.660680);
        LatLng destination = new LatLng(smartAlarm.getDestinationLocation().getLatitude(), smartAlarm.getDestinationLocation().getLongitude());
        long arrivalTimeOfDay = calculateMillis(smartAlarm.getArrivalHour(), smartAlarm.getArrivalMinute())/1000;
        long arrivalTimeInSeconds = (ServerManager.calculateArrivalTimeInMillis(Calendar.SUNDAY) + arrivalTimeOfDay);

        ServerManager.sendRequest(origin, destination, arrivalTimeInSeconds);
    }
}