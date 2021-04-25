package com.temple.onit.GeofencedReminder;


import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.temple.onit.R;
import com.temple.onit.Utils;

import java.io.IOException;
import java.util.List;

public class EditGeofencedPopup implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {
    // define popup window size
    int width = LinearLayout.LayoutParams.MATCH_PARENT;
    int height = LinearLayout.LayoutParams.MATCH_PARENT;


    EditText editTitle, editBody; // display fields for user to edit
    TextView editLocation, editDistance; // supposed to show address of marker
    Button cancel, submit;
    ConstraintLayout editGeoBackground;
    PopupWindow geoWindow;
    Context context;
    FloatingActionButton zoomOut;
    Geocoder geoCoder;
    SeekBar distanceChange;

    // stuff for google map below
    Bundle bundle;
    MapView mapView;
    Marker marker;
    GoogleMap gMap;
    CameraUpdate cam;
    onSubmitEditGeoReminder interfaceImplementor;
    GeofencedReminder oldReminder;

    public void showGeoEditPopUp(View v, GeofencedReminder reminder, Context context, onSubmitEditGeoReminder listener) {
        // initialize things that class uses
        this.context = context;
        oldReminder = reminder;
        geoCoder = new Geocoder(context); // supposed to help get address of marker, not working
        interfaceImplementor = listener;

        // get layout inflater
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // inflate geo_reminder_popup layout
        View geoPopUp = inflater.inflate(R.layout.geo_reminder_popup, null);
        // set size
        width = LinearLayout.LayoutParams.MATCH_PARENT;
        height = LinearLayout.LayoutParams.MATCH_PARENT;
        // create popup
        geoWindow = new PopupWindow(geoPopUp, width, height, true); // true makes items outside popup inactive
        geoWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
        // assign values to views in layout
        editTitle = geoPopUp.findViewById(R.id.geoEditTitle);
        editTitle.setText(reminder.getReminderTitle());
        editBody = geoPopUp.findViewById(R.id.geoEditBody);
        editBody.setText(reminder.getReminderContent());
        editDistance = geoPopUp.findViewById(R.id.geoEditDistance);
        editDistance.setText(String.valueOf(reminder.getRadius()));
        distanceChange = geoPopUp.findViewById(R.id.editDistanceSeekbar);
        distanceChange.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double raduis = 100 + (2 * (double) progress + 1) * 100;
                editDistance.setText(String.valueOf(raduis)); // display numeric value of seekbar
                oldReminder.setRadius(raduis); // update radius
                gMap.clear(); // clear old one
                Utils.showReminderInMap(context, gMap, oldReminder); // show update
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // not conceded
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // not conceded
            }
        });
        editGeoBackground = geoPopUp.findViewById(R.id.editGeoBackground);
        editGeoBackground.setBackgroundColor(Color.parseColor("#BFCCCCCC")); //75% transparency ,Snow white

        // define map and wait on onMapReady callback
        mapView = geoPopUp.findViewById(R.id.mapView);
        mapView.onCreate(bundle);
        mapView.onStart();
        mapView.getMapAsync(this);

        // button functionality
        zoomOut = geoPopUp.findViewById(R.id.popupZoomOut);
        zoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gMap.moveCamera(CameraUpdateFactory.zoomOut());
            }
        });
        cancel = geoPopUp.findViewById(R.id.editGeoCancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Title =", editTitle.getText().toString());
                geoWindow.dismiss();
            }
        });

        submit = geoPopUp.findViewById(R.id.editGeoSubmit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPressed(editTitle.getText().toString(), editBody.getText().toString(), editDistance.getText().toString()); // hardcoded values will be changed
            }
        });

        // dismiss if touch outside of edit card
        geoPopUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                geoWindow.dismiss();
                return true;
            }
        });

    }

    private void submitPressed(String title, String body, String distance) {
        String postTitle = title,
                postBody = body,
                postDist = distance;

        LatLng destination = new LatLng(oldReminder.getLatLng().latitude, oldReminder.getLatLng().longitude);
        GeofencedReminder newReminder = new GeofencedReminder(destination, postTitle, postBody, Double.parseDouble(postDist));
        interfaceImplementor.createProximityReminder(newReminder, oldReminder);
        geoWindow.dismiss();

    }

/*
    // server post request to edit selected entry
    private void editGeoReminder( String title, String body , String distance, double Lat, double Long){
        final String postTitle= title,
                postBody= body,
                postDist = distance,
                id = oldReminder.getId();

        final double postLat = Lat,
                postLong = Long;

        RequestQueue queue = Volley.newRequestQueue(context);
        String server = "http://10.0.2.2:8000/editgeo";

        StringRequest makeRequest = new StringRequest(Request.Method.POST,server,new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                // once updated in database, pass new GeofencedReminder to GeoFencedReminderActivity to delete old one and save new one
                LatLng destination = new LatLng(marker.getPosition().latitude,marker.getPosition().longitude);
                GeofencedReminder newReminder = new GeofencedReminder(destination,title,body,Double.parseDouble(distance));
                interfaceImplementor.createProximityReminder(newReminder,oldReminder);
                geoWindow.dismiss();
                String toast = "Updated " + postTitle;
                Toast.makeText(context,toast,Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error",error.toString());
            }

        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> editGeoReminder = new HashMap();
                editGeoReminder.put("newTitle",postTitle);
                editGeoReminder.put("newBody",postBody);
                editGeoReminder.put("newLat",String.valueOf(postLat));
                editGeoReminder.put("newLong",String.valueOf(postLong));
                editGeoReminder.put("newDistance",postDist);
                editGeoReminder.put("id",id);
                return editGeoReminder;
            }
        };
        queue.add(makeRequest);

    }
*/
    // ---- google map stuff ----------------------
    @Override
    public void onMapLongClick(LatLng latLng) {
        // move camera to long click position
        cam = CameraUpdateFactory.newLatLngZoom(latLng,10);
        gMap.animateCamera(cam);
        gMap.clear();
        // move reminder pin
        oldReminder.setLocation(latLng);
        Utils.showReminderInMap(context, gMap, oldReminder);



        // get address of dropped marker from coordinates
        //getAddress();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.setOnMapLongClickListener(this);

        LatLng latLng = new LatLng(oldReminder.getLatLng().latitude,oldReminder.getLatLng().longitude);
        cam = CameraUpdateFactory.newLatLngZoom(latLng,13);
        if(gMap!= null)
            gMap.animateCamera(cam);
        // display old reminder
        oldReminder.setLocation(latLng);
        Utils.showReminderInMap(context, gMap, oldReminder);

        //getAddress();

    }
/* // not working removed for time being as not fundermental
    public void getAddress(){
        try {
            List<Address> addresses = geoCoder.getFromLocation(oldReminder.getLatLng().latitude,oldReminder.getLatLng().longitude,1);
            if(addresses.size()==0){
                editLocation.setText("No Address");
            } else {
                editLocation.setText(addresses.get(0).getPremises());
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
*/
    public interface onSubmitEditGeoReminder{
        void createProximityReminder(GeofencedReminder newOneToSave, GeofencedReminder oldReminderToDelete);
    }


}
