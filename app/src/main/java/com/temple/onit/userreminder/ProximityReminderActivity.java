package com.temple.onit.userreminder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.temple.onit.Constants;
import com.temple.onit.OnitApplication;
import com.temple.onit.R;
import com.temple.onit.dataclasses.ProximityReminder;

import java.util.ArrayList;

public class ProximityReminderActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton;
    ArrayList<ProximityReminder> remindersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proximity_reminder);

        recyclerView = findViewById(R.id.reminder_recycler_view);
        floatingActionButton = findViewById(R.id.add_reminder_fab);

        getRemindersFromServer();


    }

    private void getRemindersFromServer(){
        remindersList = new ArrayList<>();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = Constants.API_GET_USER_REMINDERS +
                "?username=" + OnitApplication.instance.getAccountManager().username;
        StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
            Log.d("Response", response);
        }, error -> {

        });
        requestQueue.add(request);
    }
}