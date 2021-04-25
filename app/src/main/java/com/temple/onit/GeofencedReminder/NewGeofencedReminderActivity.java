package com.temple.onit.GeofencedReminder;

import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.temple.onit.OnitApplication;
import com.temple.onit.R;
import com.temple.onit.Utils;
import com.temple.onit.dashboard.DashboardActivity;

import java.util.Objects;

public class NewGeofencedReminderActivity extends AppCompatActivity implements OnMapReadyCallback, GeofenceReminderManager.GeofenceManagerInterface {

    private GoogleMap googleMap;
    private GeofencedReminder geofencedReminder = new GeofencedReminder(null, null, null, 0);


    private TextView instructionTitle;
    private TextView instructionSubtitle;
    private SeekBar radiusBar;
    private TextView radiusDesc;
    private EditText message;
    private Button next;
    private ImageView marker;


    private SeekBar.OnSeekBarChangeListener radiusBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            updateRadiusWithProgress(progress);
            showReminderUpdate();

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
    private void updateRadiusWithProgress(int progress){
        double radius = getRadius(progress);
        geofencedReminder.setRadius(radius);
        radiusDesc.setText(String.valueOf(radius));
    }
    private double getRadius(int progress){
        return (100 + (2* (double) progress + 1) * 100);
    }

    public static Intent newIntent(Context context, LatLng latLng, float zoom){
        Intent intent = new Intent(context, NewGeofencedReminderActivity.class);
        intent.putExtra("ExtraLatLng", latLng);
        intent.putExtra("ExtraZoom", zoom);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_geofenced_reminder);
        radiusDesc = findViewById(R.id.radiusDescription);
        instructionTitle = findViewById(R.id.instructionTitle);
        instructionSubtitle = findViewById(R.id.instructionSubtitle);
        radiusBar = findViewById(R.id.radiusBar);
        message = findViewById(R.id.messageGeofence);
        next = findViewById(R.id.next);
        marker = findViewById(R.id.marker);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if(mapFragment != null){
            mapFragment.getMapAsync(this);
        }


        radiusDesc.setVisibility(View.GONE);
        instructionTitle.setVisibility(View.GONE);
        instructionSubtitle.setVisibility(View.GONE);
        radiusBar.setVisibility(View.GONE);
        message.setVisibility(View.GONE);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


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
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        Log.d("OnMapReady Called", "");
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        centerCamera();
        showConfigLocationStep();


    }

    private void centerCamera(){
        LatLng latLng = (LatLng) getIntent().getExtras().get("ExtraLatLng");
        float zoom = getIntent().getExtras().getFloat("ExtraZoom");
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void showConfigLocationStep(){
        Log.d("ShowConfigLocationStep Called", "");
        marker.setVisibility(View.VISIBLE);
        instructionTitle.setVisibility(View.VISIBLE);
        instructionSubtitle.setVisibility(View.VISIBLE);
        radiusBar.setVisibility(View.GONE);
        radiusDesc.setVisibility(View.GONE);
        message.setVisibility(View.GONE);

        next.setOnClickListener(v -> {
            geofencedReminder.setLocation(googleMap.getCameraPosition().target);
            showConfigRadiusStep();
        });

        showReminderUpdate();

    }

    private void showConfigRadiusStep(){
        marker.setVisibility(View.GONE);
        instructionTitle.setVisibility(View.GONE);
        instructionSubtitle.setVisibility(View.GONE);
        radiusBar.setVisibility(View.VISIBLE);
        radiusDesc.setVisibility(View.VISIBLE);
        message.setVisibility(View.GONE);

        instructionTitle.setText("Choose the radius");
        next.setOnClickListener(v -> {
            showConfigMessageStep();
        });
        radiusBar.setOnSeekBarChangeListener(radiusBarChangeListener);
        updateRadiusWithProgress(radiusBar.getProgress());
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15f));

        showReminderUpdate();
    }

    private void showConfigMessageStep(){
        marker.setVisibility(View.GONE);
        instructionTitle.setVisibility(View.VISIBLE);
        instructionSubtitle.setVisibility(View.GONE);
        radiusBar.setVisibility(View.GONE);
        radiusDesc.setVisibility(View.GONE);
        message.setVisibility(View.VISIBLE);

        instructionTitle.setText("Enter The Message");
        next.setOnClickListener(v -> {
            Utils.hideKeyboard(this, message);

            geofencedReminder.setReminderContent(message.getText().toString());
            if(geofencedReminder.getReminderContent().isEmpty()){
                message.setError("Required");
            }
            else{
                addReminder(geofencedReminder);
            }

        });

    }
    private void addReminder(GeofencedReminder reminder){
        OnitApplication.instance.getGeofenceReminderManager().add(reminder, this, this);

    }

    private void showReminderUpdate(){
        googleMap.clear();
        Utils.showReminderInMap(this, googleMap, geofencedReminder);
    }


    @Override
    public void onSuccess() {
        setResult(Activity.RESULT_OK);
        finish();
    }

    @Override
    public void onFailure(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }
}