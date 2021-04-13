package com.temple.onit.Alarms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.temple.onit.Constants;
import com.temple.onit.R;

import javax.xml.transform.Templates;

public class AlarmViewerActivity extends AppCompatActivity implements OnMapReadyCallback {

    Button dismissButton;
    GoogleMap googleMap;
    TextView alarmTitleTV;
    TextView leaveTimeTV;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_viewer);

        dismissButton = findViewById(R.id.alarm_view_dismiss_button);
        alarmTitleTV = findViewById(R.id.alarm_title_text_view);
        leaveTimeTV = findViewById(R.id.alarm_text_text_view);

        alarmTitleTV.setText(getAlarmTitleText());
        leaveTimeTV.setText(getLeaveTimeText());

        dismissButton.setOnClickListener(v -> {
            Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
            getApplicationContext().stopService(intentService);
            finish();
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.alarm_view_map);
        if(mapFragment != null){
            mapFragment.getMapAsync(this);
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 330);
        }

    }

    private String getAlarmTitleText(){
        return getIntent().getStringExtra(Constants.ALARM_TITLE);
    }
    private String getLeaveTimeText(){
        Intent intent = getIntent();
        int leaveHour = intent.getIntExtra(Constants.LEAVE_HOUR, 0);
        int leaveMinute = intent.getIntExtra(Constants.LEAVE_MINUTE, 0);
        int arrivalHour = intent.getIntExtra(Constants.ARRIVAL_HOUR, 0);
        int arrivalMinute = intent.getIntExtra(Constants.ARRIVAL_MINUTE, 0);

        String finalString = "Leave at " +
                leaveHour + ":" + leaveMinute +
                " to get to your location at " +
                arrivalHour + ":" + arrivalMinute + ".";

        return finalString;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 330){
            onMapAndPermissionReady();
        }
    }
    private void onMapAndPermissionReady(){
        if(googleMap != null && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            googleMap.setMyLocationEnabled(true);

            String bestProvider = locationManager.getBestProvider(new Criteria(), false);
            Location location = locationManager.getLastKnownLocation(bestProvider);

            if(location != null){
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
            }

            centerCamera();
        }
    }
    private void centerCamera(){
        double destinationLongitude = getIntent().getDoubleExtra(Constants.DESTINATION_LONGITUDE, 0);
        double destinationLatitude = getIntent().getDoubleExtra(Constants.DESTINATION_LATITUDE, 0);
        LatLng destLatLng = new LatLng(destinationLatitude, destinationLongitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destLatLng, 15f));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        onMapAndPermissionReady();
    }
}