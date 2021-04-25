package com.temple.onit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.temple.onit.Alarms.SmartAlarmActivity;
import com.temple.onit.Alarms.list.AlarmListActivity;
import com.temple.onit.GeofencedReminder.GeofenceReminderManager;
import com.temple.onit.GeofencedReminder.GeofencedReminder;
import com.temple.onit.GeofencedReminder.GeofencedReminderActivity;
import com.temple.onit.Alarms.SmartAlarm;
import com.temple.onit.account.AccountManager;
import com.temple.onit.authentication.AuthenticationActivity;
import com.temple.onit.services.LocationService;
import com.temple.onit.userreminder.ProximityReminderActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MainActivity extends AppCompatActivity implements AccountManager.AccountListener, GeofenceReminderManager.GeofenceManagerInterface {

    private Button newAlarmButton;
    private Button newProximityReminderButton;
    private Button newGeofencedReminderButton;
    private Button loginButton;
    private FirebaseUser account;
    private FirebaseUser user;
    private static final String PASSWORD = "password";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OnitApplication.instance.accountManager = new AccountManager(getApplicationContext(), this);


        account = FirebaseAuth.getInstance().getCurrentUser();
        OnitApplication.instance.getAccountManager().addUser(this, account.getUid(), PASSWORD , PASSWORD, getApplicationContext(), account.getEmail());
        Log.i("loginAccountManager", "account " + account.getUid() + " pass: " + PASSWORD);



        if(getIntent().getExtras() != null){
            String status = getIntent().getStringExtra("status");
            if(getIntent().getExtras() != null){
                Log.d("Intent Keys", getIntent().getExtras().keySet().toString());
                if(status.equals("17")){
                    createReminderAlertDialog();
                }
            }
        }


        newAlarmButton = findViewById(R.id.button_alarm);
        newProximityReminderButton = findViewById(R.id.button_proximity_reminder);
        newGeofencedReminderButton = findViewById(R.id.button_geofenced_reminder);
        loginButton = findViewById(R.id.loginButton);

        newAlarmButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AlarmListActivity.class);
            launchSmartAlarm(intent);
        });
        newProximityReminderButton.setOnClickListener(v->{
            Intent intent = new Intent(MainActivity.this, ProximityReminderActivity.class);
            launchProximityReminder(intent);
        });
        newGeofencedReminderButton.setOnClickListener(v->{
            Intent intent = new Intent(MainActivity.this, GeofencedReminderActivity.class);
            launchGeofencedReminder(intent);
        });

        if(OnitApplication.instance.getAccountManager().loggedIn){
            changeToLogOut();
        }
        else{
            changeToLogIn();
        }

        //if(!OnitApplication.instance.getAccountManager().loggedIn){
            //FirebaseAuth.getInstance().signOut();
            //Intent intent = new Intent(MainActivity.this, AuthenticationActivity.class);
            //startActivity(intent);
        //}

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 330);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            Intent serviceIntent = new Intent(this, LocationService.class);
            startForegroundService(serviceIntent);
        }



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Intent serviceIntent = new Intent(this, LocationService.class);
        startForegroundService(serviceIntent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    private void changeToLogIn(){
        loginButton.setText("Login");
        loginButton.setOnClickListener(v -> {

        });

    }

    @Override
    public void onLoginFailed(boolean loggedIn) {

    }

    private void changeToLogOut(){
        loginButton.setText("Logout");
        loginButton.setOnClickListener(v -> {
            OnitApplication.instance.getAccountManager().logout(this);
            changeToLogIn();
        });
    }

    public void launchSmartAlarm(Intent intent){
        startActivity(intent);
    }
    public void launchProximityReminder(Intent intent){
        startActivity(intent);
    }
    public void launchGeofencedReminder(Intent intent){
        startActivity(intent);
    }

    public void getGeofenceRemindersFromServer(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Constants.API_GET_GEOFENCED_REMINDERS + "?username=" + OnitApplication.instance.getAccountManager().username;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            Log.d("GeofenceResponse", response);
            addNewGeofenceReminders(response);
        }, error -> {
            Log.d("GeofenceResponse error", error.toString());
        });
        queue.add(stringRequest);
    }
    public void addNewGeofenceReminders(String data){
        try{
            JSONArray jsonArray = new JSONArray(data);
            List<GeofencedReminder> reminderList = OnitApplication.instance.getGeofenceReminderManager().getAll();
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                double latitude = jsonObject.getDouble("latitude");
                double longitude = jsonObject.getDouble("longitude");
                double distance = jsonObject.getInt("distance");
                String body = jsonObject.getString("body");
                String title = jsonObject.getString("title");
                GeofencedReminder geofencedReminder = new GeofencedReminder(new LatLng(latitude, longitude), title, body, distance);
                if(!reminderList.contains(geofencedReminder)){
                    OnitApplication.instance.getGeofenceReminderManager().add(geofencedReminder, this, this);
                }

            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }

    }
    public void createReminderAlertDialog(){
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        Bundle extras = getIntent().getExtras();
        String titleString = extras.getString("title", "Title");
        String contentString = extras.getString("body", "Body");
        String issuerString = "From: " + extras.getString("issuer", "Issuer");
        String distanceString = "Distance: " + extras.getString("distance", "Distance")+" M";
        int id = Integer.parseInt(extras.getString("id"));

        final TextView title = new TextView(this);
        final TextView content = new TextView(this);
        final TextView issuer = new TextView(this);
        final TextView distance = new TextView(this);
        title.setText(titleString);
        content.setText(contentString);
        issuer.setText(issuerString);
        distance.setText(distanceString);
        title.setGravity(LinearLayout.TEXT_ALIGNMENT_CENTER);
        content.setGravity(LinearLayout.TEXT_ALIGNMENT_CENTER);
        issuer.setGravity(LinearLayout.TEXT_ALIGNMENT_CENTER);
        distance.setGravity(LinearLayout.TEXT_ALIGNMENT_CENTER);

        layout.addView(title);
        layout.addView(content);
        layout.addView(issuer);
        layout.addView(distance);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("User Reminder Request")
                .setView(layout)
                .setPositiveButton("Accept", (dialog1, which) -> {
                    acceptUserReminder(id, OnitApplication.instance.getAccountManager().username);

                })
                .setNegativeButton("Decline", (dialog1, which) -> {
                    dialog1.cancel();
                }).show();
    }

    public void acceptUserReminder(int id, String username){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Constants.API_ACCEPT_USER_REMINDER+"?username="+username+"&id="+id;
        StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
            Toast.makeText(this, "Accepted User Reminder", Toast.LENGTH_SHORT).show();
        }, error -> {
            Toast.makeText(this, "Failed to accept. Try again from user reminder page", Toast.LENGTH_LONG).show();
        });
        queue.add(request);
    }


    @Override
    public void onLoginResponse(boolean loggedIn) {
        if(loggedIn) {
            changeToLogOut();
            getGeofenceRemindersFromServer();
        }
    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onFailure(String error) {

    }
}