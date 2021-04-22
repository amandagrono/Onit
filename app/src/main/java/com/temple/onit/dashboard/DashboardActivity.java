package com.temple.onit.dashboard;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
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

import androidx.lifecycle.ViewModelProvider;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.temple.onit.Alarms.SmartAlarm;
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
import com.temple.onit.databinding.DashAlarmViewBinding;
import com.temple.onit.dataclasses.ProximityReminder;
import com.temple.onit.services.LocationService;
import com.temple.onit.userreminder.ProximityReminderActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity implements AccountManager.AccountListener, GeofenceReminderManager.GeofenceManagerInterface, View.OnClickListener, CompoundButton.OnCheckedChangeListener{

    private Button newAlarmButton;
    private Button newProximityReminderButton;
    private Button newGeofencedReminderButton;
    private Button loginButton;
    private FirebaseUser account;
    private FirebaseUser user;
    private static final String PASSWORD = "password";
    private androidx.appcompat.widget.Toolbar toolbar;
    ActivityDashboardBinding activityDashboardBinding;
    DashboardViewModel dashboardViewModel;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityDashboardBinding = ActivityDashboardBinding.inflate(getLayoutInflater());
        View view = activityDashboardBinding.getRoot();
        setContentView(view);

        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        account = FirebaseAuth.getInstance().getCurrentUser();

        activityDashboardBinding.buttonAbout.findViewById(R.id._settingsConstraintLayout).setOnClickListener(this);
        activityDashboardBinding.buttonAbout.findViewById(R.id._logoutTextView).setOnClickListener(this);
        activityDashboardBinding.buttonAbout.setOnClickListener(this);
        activityDashboardBinding.buttonAlarm.setOnClickListener(this);
        activityDashboardBinding.buttonGeofencedReminder.setOnClickListener(this);
        activityDashboardBinding.buttonProximityReminder.setOnClickListener(this);
        SwitchMaterial locationSwitch = ((SwitchMaterial) activityDashboardBinding.buttonAbout.findViewById(R.id._locationSwtich));
        locationSwitch.setOnCheckedChangeListener(this);
        locationSwitch.setChecked(hasGPSPermission());
        SwitchMaterial backgroundSwitch = ((SwitchMaterial) activityDashboardBinding.buttonAbout.findViewById(R.id._backgroundSwitch));
        backgroundSwitch.setOnCheckedChangeListener(this);
        backgroundSwitch.setChecked(hasBackgroundPermission());
        String name = account.getDisplayName();
        if (name == null || name.length() == 0){
            name = account.getEmail().substring(0, getATIndex(account.getEmail()));
        }
        ((TextView) activityDashboardBinding.HelloTextView).setText(getString(R.string.hello_user_prompt) + ", " + name);
        activityDashboardBinding.buttonProximityReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("clicked", "onClick: proximity");
            }
        });

        OnitApplication.instance.accountManager = new AccountManager(getApplicationContext(), this);



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


        dashboardViewModel.setRepositoryContext(getApplication());
        dashboardViewModel.getAlarmCountLiveData().observe(this, s -> {
            if (s.size() > 0) {
                SmartAlarm upcomingAlarm = s.get(0);
                TextView countTextView = (TextView)findViewById(R.id._alarmCountTextView);
                TextView descTextView = ((TextView) findViewById(R.id._alarmDescTextView));
                TextView dateTextView = ((TextView) findViewById(R.id._alarmDateTextView));
                TextView dayTextView = ((TextView) findViewById(R.id._alarmDayTextView));


                countTextView.setText(s.size() + "");
                descTextView.setText(upcomingAlarm.getAlarmTitle());
                dateTextView.setText(upcomingAlarm.getArrivalHour() + ":" + upcomingAlarm.getArrivalMinute());
                dayTextView.setText(upcomingAlarm.getDays());
            }
        });

        Intent serviceIntent = new Intent(this, LocationService.class);
        startForegroundService(serviceIntent);

    }

    public int getATIndex(String email){
        return email.lastIndexOf('@');
    }

    @Override
    protected void onResume() {
        super.onResume();
        ArrayList<GeofencedReminder> geofencedReminderArrayList = (ArrayList<GeofencedReminder>) dashboardViewModel.getGeoCountLiveData().getValue();
        assert geofencedReminderArrayList != null;
        if (geofencedReminderArrayList.size() > 0) {
            GeofencedReminder upcomingGeoReminder = geofencedReminderArrayList.get(0);
            View root = activityDashboardBinding.buttonGeofencedReminder.getRootView();

            TextView countTextView = findViewById(R.id._geoCountTextView);
            TextView addressTextView = findViewById(R.id._geoAddressTextView);
            countTextView.setText(String.valueOf(geofencedReminderArrayList.size()));

            LatLng latLng = upcomingGeoReminder.getLocation();
            Location location = new Location("location");
            location.setLatitude(latLng.latitude);
            location.setLongitude(latLng.longitude);

            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            String address = "";
            try {
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                address = addresses.get(0).getAddressLine(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
            addressTextView.setText(address);
        }

        int proximityReminderCount = dashboardViewModel.getProximityCount();
        View root = activityDashboardBinding.buttonProximityReminder.getRootView();
        TextView proximityCountTextView = root.findViewById(R.id._proximityCountTextView);
        proximityCountTextView.setText(String.valueOf(proximityReminderCount));

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
                if (!hasGPSPermission()){
                    Toast.makeText(this, "ENABLE LOCATION SERVICES IN SETTINGS", Toast.LENGTH_SHORT).show();
                }else {
                    launchGeofencedReminder(new Intent(this, GeofencedReminderActivity.class));
                    Log.i("clicked", "onClick: alarm");
                }
                break;
            case R.id._proximityConstraintLayout:
                launchProximityReminder(new Intent(this, ProximityReminderActivity.class));
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

    public void EnableLocationDialog(){
        if (!hasGPSPermission()){
            requestGPSPermission();
        }
    }

    public void EnableBackgroundDialog(){
        if (!hasBackgroundPermission()){
            requestBackgroundPermission();
        }
    }

    private boolean hasBackgroundPermission(){
        return checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestBackgroundPermission(){
        requestPermissions(new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 1111);
    }

    private boolean hasGPSPermission() {

        return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Request FINE location permission
     */
    private void requestGPSPermission() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1111);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        int id = compoundButton.getId();
        SwitchMaterial locationSwitch = findViewById(R.id._locationSwtich);
        SwitchMaterial backgroundSwitch = findViewById(R.id._backgroundSwitch);
        Log.i("switch", "onCheckedChanged: " + String.valueOf(b));
        switch (id) {
            case R.id._locationSwtich:
                if (b) {
                    EnableLocationDialog();
                    break;
                }

            case R.id._backgroundSwitch:
                if (b) {
                    EnableBackgroundDialog();
                    break;
                }
            default:
                if (!b) {
                    Toast.makeText(this, "DISABLE IN SETTINGS", Toast.LENGTH_SHORT).show();
                }

        }
        backgroundSwitch.setChecked(hasBackgroundPermission());
        locationSwitch.setChecked(hasGPSPermission());

    }

    public void promptLocationService(){
        Toast.makeText(this, "ENABLE LOCATION SERVICES", Toast.LENGTH_SHORT).show();
    }
}