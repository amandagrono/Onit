package com.temple.onit.GeofencedReminder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.temple.onit.OnitApplication;
import com.temple.onit.R;
import com.temple.onit.Utils;

public class GeofencedReminderActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GeofenceReminderManager.RemoveReminderInterface {

    private GeofencingClient geofencingClient;
    private GoogleMap googleMap;
    private LocationManager locationManager;

    private FloatingActionButton newReminderFAB;
    private FloatingActionButton currentLocationFAB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geofenced_reminder);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if(mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        else{
            finish();
        }
        newReminderFAB = findViewById(R.id.newReminder);
        newReminderFAB.setVisibility(View.GONE);
        currentLocationFAB = findViewById(R.id.currentLocation);
        currentLocationFAB.setVisibility(View.GONE);

        newReminderFAB.setOnClickListener(v -> {
            Intent intent = NewGeofencedReminderActivity.newIntent(this, googleMap.getCameraPosition().target, googleMap.getCameraPosition().zoom);
            startActivityForResult(intent, 329);
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 330);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 329 && resultCode == Activity.RESULT_OK) {
            showReminders();
            GeofencedReminder geofencedReminder = OnitApplication.instance.getGeofenceReminderManager().getLast();
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(geofencedReminder.getLatLng(), 15f));
            Toast.makeText(this, "Reminder Added!", Toast.LENGTH_LONG).show();


        }
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
            newReminderFAB.setVisibility(View.VISIBLE);
            currentLocationFAB.setVisibility(View.VISIBLE);

            currentLocationFAB.setOnClickListener(v -> {
                String bestProvider = locationManager.getBestProvider(new Criteria(), false);
                Location location = locationManager.getLastKnownLocation(bestProvider);
                //FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

                if(location != null){
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                }
            });

            showReminders();
            centerCamera();
        }
    }

    private void centerCamera(){
        if(getIntent().getExtras() != null && getIntent().getExtras().containsKey("ExtraLatLng")){
            LatLng latLng = (LatLng) getIntent().getExtras().get("ExtraLatLng");
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
        }
    }
    private void showReminders(){
        googleMap.clear();
        for(GeofencedReminder reminder : OnitApplication.instance.getGeofenceReminderManager().getAll()){
            Utils.showReminderInMap(this, googleMap, reminder);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.setOnMarkerClickListener(this::onMarkerClick);

        onMapAndPermissionReady();
    }
    @Override
    public boolean onMarkerClick(Marker marker) {
        GeofencedReminder reminder = OnitApplication.instance.getGeofenceReminderManager().get((String) marker.getTag());
        if(reminder != null){
            showReminderRemoveAlert(reminder);
        }
        return true;
    }

    private void showReminderRemoveAlert(GeofencedReminder reminder){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setMessage("Remove Reminder?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", (dialog, which) -> {
           removeReminder(reminder);
           dialog.dismiss();
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", (dialog, which) -> dialog.dismiss());
        alertDialog.show();
    }
    private void removeReminder(GeofencedReminder reminder){
        OnitApplication.instance.getGeofenceReminderManager().remove(reminder, this, this);
    }


    public static Intent newIntent(Context context, LatLng latLng){
        Intent intent = new Intent(context, GeofencedReminderActivity.class);
        intent.putExtra("extra_lat_lng", latLng);
        return intent;
    }


    @Override
    public void onSuccess() {
        showReminders();
        Toast.makeText(this, "Reminder Removed!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFailure(String error) {
        Toast.makeText(this, "Failed to remove reminder", Toast.LENGTH_LONG).show();
    }
}