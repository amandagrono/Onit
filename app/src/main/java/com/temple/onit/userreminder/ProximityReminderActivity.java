package com.temple.onit.userreminder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.temple.onit.Constants;
import com.temple.onit.OnitApplication;
import com.temple.onit.R;
import com.temple.onit.account.AccountManager;
import com.temple.onit.dataclasses.ProximityReminder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProximityReminderActivity extends AppCompatActivity implements ProximityReminderViewHolder.ReminderListListener, EditUserReminderPopup.afterEdit {

    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton;
    ArrayList<ProximityReminder> remindersList;
    CreateProximityReminderPopup CPRP;
    EditUserReminderPopup EURP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proximity_reminder);

        recyclerView = findViewById(R.id.reminder_recycler_view);

        floatingActionButton = findViewById(R.id.add_reminder_fab);

        floatingActionButton.setOnClickListener( view ->{
            CPRP = new CreateProximityReminderPopup();
            CPRP.showUserProximityCreatePopUp(view,this);
            });

        getRemindersFromServer();


    }

    private void getRemindersFromServer(){
        remindersList = new ArrayList<>();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = Constants.API_GET_USER_REMINDERS +
                "?username=" + OnitApplication.instance.accountManager.username;
        StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
            Log.d("ResponseUserReminder", response);
            convertResponseToList(response);
        }, error -> {

        });
        requestQueue.add(request);
    }

    private void convertResponseToList(String response){
        try{
            JSONArray jsonArray = new JSONArray(response);
            Log.d("UserReminders", response);
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject object = jsonArray.getJSONObject(i);
                int id = object.getInt("id");
                String issuer_id = object.getString("issuer_id");
                String target_id = object.getString("target_id");
                String title = object.getString("title");
                String body = object.getString("body");
                double distance = object.getDouble("distance");
                boolean accepted = object.getInt("accepted") == 1;
                ProximityReminder reminder = new ProximityReminder(title, body, distance, issuer_id, target_id, id, accepted);
                remindersList.add(reminder);
            }
            populateRecyclerView();
        }
        catch (JSONException e){
            e.printStackTrace();
            Toast.makeText(this, "Failed to get User Reminders From Server", Toast.LENGTH_LONG).show();
        }

    }
    private void populateRecyclerView(){
        recyclerView = findViewById(R.id.reminder_recycler_view);
        ProximityReminderAdapter adapter = new ProximityReminderAdapter(this, remindersList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDelete(ProximityReminder reminder) {
        if(remindersList.remove(reminder)){
            deleteReminderFromServer(reminder);
            if(recyclerView.getAdapter() != null){
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        }
    }
    public void onAccept(ProximityReminder reminder){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Constants.API_ACCEPT_USER_REMINDER + "?id=" + reminder.getIntId() + "&username=" + OnitApplication.instance.getAccountManager().username;

        StringRequest request = new StringRequest(Request.Method.POST, url, response -> {

           Toast.makeText(this, "Reminder Accepted", Toast.LENGTH_SHORT).show();
           if(recyclerView.getAdapter() != null){

               getRemindersFromServer();
           }
        }, error -> {
            Toast.makeText(this, "Failed to accept user reminder", Toast.LENGTH_SHORT).show();
        });
        queue.add(request);
    }

    @Override
    public void onEdit(View v, ProximityReminder reminder) {
        EURP = new EditUserReminderPopup(); // create and show show popup window
        EURP.showUserReminderEditPopUp(v,reminder.getReminderTitle(),reminder.getReminderContent(),String.valueOf(reminder.getRadius()),reminder.getTarget(),reminder.getIntId(),this,this,reminder.isAccepted());
    }

    public void deleteReminderFromServer(ProximityReminder reminder){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Constants.API_DELETE_USER_REMINDER + "?id="+reminder.getIntId();

        StringRequest request = new StringRequest(Request.Method.DELETE, url, response -> {
            Toast.makeText(this, "Successfully Deleted User Reminder", Toast.LENGTH_SHORT).show();
        }, error -> {
            Toast.makeText(this, "Failed to Delete User Reminder", Toast.LENGTH_SHORT).show();
        });
        queue.add(request);
    }
//  interface from EditUserReminderPopup
    @Override
    public void editDone() {
        getRemindersFromServer();
    }

}