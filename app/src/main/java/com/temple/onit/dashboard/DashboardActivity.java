package com.temple.onit.dashboard;

import android.content.DialogInterface;
import android.view.Menu;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.temple.onit.Alarms.database.SmartAlarmRepository;
import com.temple.onit.Alarms.list.AlarmListActivity;
import com.temple.onit.Constants;
import com.temple.onit.GeofencedReminder.GeofenceReminderManager;
import com.temple.onit.GeofencedReminder.GeofencedReminder;
import com.temple.onit.GeofencedReminder.GeofencedReminderActivity;
import com.temple.onit.OnitApplication;
import com.temple.onit.R;
import com.temple.onit.account.AccountManager;
import com.temple.onit.authentication.AuthenticationActivity;
import com.temple.onit.databinding.ActivityDashboardBinding;
import com.temple.onit.services.LocationService;
import com.temple.onit.userreminder.ProximityReminderActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity implements AccountManager.AccountListener, GeofenceReminderManager.GeofenceManagerInterface, View.OnClickListener{

    private Button newAlarmButton;
    private Button newProximityReminderButton;
    private Button newGeofencedReminderButton;
    private Button loginButton;
    private FirebaseUser account;
    private FirebaseUser user;
    private static final String PASSWORD = "password";
    private androidx.appcompat.widget.Toolbar toolbar;
    ActivityDashboardBinding activityDashboardBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityDashboardBinding = ActivityDashboardBinding.inflate(getLayoutInflater());
        View view = activityDashboardBinding.getRoot();
        setContentView(view);

        activityDashboardBinding.buttonAbout.findViewById(R.id._settingsConstraintLayout).setOnClickListener(this);
        activityDashboardBinding.buttonAbout.findViewById(R.id._logoutTextView).setOnClickListener(this);
        activityDashboardBinding.buttonAbout.setOnClickListener(this);
        activityDashboardBinding.buttonAlarm.setOnClickListener(this);
        activityDashboardBinding.buttonGeofencedReminder.setOnClickListener(this);
        activityDashboardBinding.buttonProximityReminder.setOnClickListener(this);
        activityDashboardBinding.buttonProximityReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("clicked", "onClick: proximity");
            }
        });

        OnitApplication.instance.accountManager = new AccountManager(getApplicationContext(), this);


        account = FirebaseAuth.getInstance().getCurrentUser();
        OnitApplication.instance.getAccountManager().addUser(this, account.getUid(), PASSWORD , PASSWORD, getApplicationContext(), account.getEmail());
        Log.i("loginAccountManager", "account " + account.getUid() + " pass: " + PASSWORD);

        if(getIntent().getExtras() != null){
            String status = getIntent().getStringExtra("status");
            if(status != null){
                if(status.equals("17")){
                    createReminderAlertDialog();
                }
            }
        }



        /*newAlarmButton = findViewById(R.id.button_alarm);
        newProximityReminderButton = findViewById(R.id.button_proximity_reminder);
        newGeofencedReminderButton = findViewById(R.id.button_geofenced_reminder);
        loginButton = findViewById(R.id.loginButton);

        newAlarmButton.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, AlarmListActivity.class);
            launchSmartAlarm(intent);
        });
        newProximityReminderButton.setOnClickListener(v->{
            Intent intent = new Intent(DashboardActivity.this, ProximityReminderActivity.class);
            launchProximityReminder(intent);
        });
        newGeofencedReminderButton.setOnClickListener(v->{
            Intent intent = new Intent(DashboardActivity.this, GeofencedReminderActivity.class);
            launchGeofencedReminder(intent);
        });*/

       /* if(OnitApplication.instance.getAccountManager().loggedIn){
            changeToLogOut();
        }
        else{
            changeToLogIn();
        }*/


        Intent serviceIntent = new Intent(this, LocationService.class);
        startForegroundService(serviceIntent);

    }

    private void changeToLogIn(){
        loginButton.setText("Login");
        loginButton.setOnClickListener(v->{
            if(OnitApplication.instance.getAccountManager().loggedIn){
                Toast.makeText(this, "Already Logged In!", Toast.LENGTH_SHORT).show();
                return;
            }
            Context context = this;
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);

            final EditText username = new EditText(context);
            username.setHint("Username");
            layout.addView(username);

            final EditText password = new EditText(context);
            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            password.setHint("Password");
            layout.addView(password);

            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle("Login")
                    .setView(layout)
                    .setPositiveButton("Enter", (dialog1, which) -> {
                        if(username.getText().toString().equals("") || password.getText().toString().equals("")){
                            Toast.makeText(context, "Please Enter A Username", Toast.LENGTH_SHORT).show();

                        }
                        else{
                            OnitApplication.instance.getAccountManager().regularLogin(username.getText().toString(), password.getText().toString(), context);
                        }

                    })
                    .setNegativeButton("Cancel", ((dialog1, which) ->{
                        dialog1.cancel();
                    }))
                    .show();
        });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dash_menu, menu);
        return true;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id._alarmConstraintLayout:
                launchSmartAlarm(new Intent(getApplicationContext(), AlarmListActivity.class));
                Log.i("clicked", "onClick: alarm");
                break;
            case R.id._geoConstraintLayout:
                launchGeofencedReminder(new Intent(this, ProximityReminderActivity.class));
                Log.i("clicked", "onClick: alarm");
                break;
            case R.id._proximityConstraintLayout:
                launchProximityReminder(new Intent(this, GeofencedReminderActivity.class));
                Log.i("clicked", "onClick: alarm");
                break;
            case R.id._logoutTextView:
                alert();
                break;
                // call remove

        }
    }

    public void logout(){
        FirebaseAuth.getInstance().signOut();
        SmartAlarmRepository smartAlarmRepository = new SmartAlarmRepository(getApplication());
        OnitApplication.instance.accountManager.logout(getApplicationContext());
        smartAlarmRepository.deleteAll();
        GeofenceReminderManager manager = new GeofenceReminderManager(getApplicationContext());
        ArrayList<GeofencedReminder> geofencedReminders = (ArrayList<GeofencedReminder>) manager.getAll();
        geofencedReminders.forEach(s -> {
            manager.remove(s, getApplicationContext(), new GeofenceReminderManager.RemoveReminderInterface() {
                @Override
                public void onSuccess() {
                    // go back to main
                    Log.i("logout", "onFailure: logout succeeded");

                }

                @Override
                public void onFailure(String error) {
                    // failed to logout
                    Log.i("logout", "onFailure: logout failed");
                }
            });
        });
        Intent intent = new Intent(getApplicationContext(), AuthenticationActivity.class);
        startActivity(intent);
        finish();
    }

    public void alert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.logout_alert)
                .setPositiveButton(R.string.logout_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        logout();
                    }
                })
                .setNegativeButton(R.string.logout_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}