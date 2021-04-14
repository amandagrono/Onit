package com.temple.onit.Alarms;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.temple.onit.Alarms.services.SmartAlarmService;
import com.temple.onit.Constants;
import com.temple.onit.R;

public class AlarmViewActivity extends AppCompatActivity implements OnMapReadyCallback {

    Button dismissButton;
    TextView alarmTitleTextView;
    TextView alarmTextTextView;
    SupportMapFragment mapFragment;
    GoogleMap map;

    double destinationLatitude;
    double destinationLongitude;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_view);

        dismissButton = findViewById(R.id.alarm_view_dismiss_button);
        alarmTitleTextView = findViewById(R.id.alarm_title_text_view);
        alarmTextTextView = findViewById(R.id.alarm_text_text_view);

        dismissButton.setOnClickListener(v -> {
            Intent intentService = new Intent(getApplicationContext(), SmartAlarmService.class);
            getApplicationContext().stopService(intentService);
            finish();
        });
        String alarmTitleString = getIntent().getStringExtra(Constants.ALARM_TITLE);
        alarmTitleTextView.setText(alarmTitleString);

        String alarmTextString = "Leave at " +
                setTimeText(getIntent().getIntExtra(Constants.LEAVE_HOUR, 0), getIntent().getIntExtra(Constants.LEAVE_MINUTE, 0)) +
                " to get to your destination at " +
                setTimeText(getIntent().getIntExtra(Constants.ARRIVAL_HOUR, 0), getIntent().getIntExtra(Constants.ARRIVAL_MINUTE, 0)) + ".";

        destinationLatitude = getIntent().getDoubleExtra(Constants.DESTINATION_LATITUDE, 0);
        destinationLongitude = getIntent().getDoubleExtra(Constants.DESTINATION_LONGITUDE, 0);

        Log.d("Alarm View", "Arrival Time: " + getIntent().getIntExtra(Constants.ARRIVAL_HOUR, 0) + ":" + getIntent().getIntExtra(Constants.ARRIVAL_MINUTE, 0) + "Destination Latitude: " + getIntent().getDoubleExtra(Constants.DESTINATION_LATITUDE, 0) + " Destination Longitude: " + getIntent().getDoubleExtra(Constants.DESTINATION_LONGITUDE, 0));

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.alarm_view_map);
        if(mapFragment != null){
            mapFragment.getMapAsync(this);
        }
    }

    private String setTimeText(int hour, int minute){
        String returnVal = "";
        if(hour < 10){
            returnVal = returnVal + "0" + hour;
        }
        else{
            returnVal = returnVal + hour;
        }
        returnVal = returnVal + ":";
        if(minute < 10){
            returnVal = returnVal + "0" + minute;
        }
        else{
            returnVal = returnVal + minute;
        }
        return returnVal;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        LatLng destinationLatLng = new LatLng(destinationLatitude, destinationLongitude);

        map.addMarker(new MarkerOptions().position(destinationLatLng));

        CameraUpdate center = CameraUpdateFactory.newLatLngZoom(destinationLatLng, 15F);
        map.animateCamera(center);

        map.getUiSettings().setScrollGesturesEnabled(false);

    }
}