package com.temple.onit.userreminder;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.temple.onit.Constants;
import com.temple.onit.OnitApplication;
import com.temple.onit.R;
import com.temple.onit.dataclasses.ProximityReminder;

public class ProximityReminderViewHolder extends RecyclerView.ViewHolder{

    private TextView titleTV;
    private TextView userTV;
    private ImageButton deleteButton;
    private ImageButton acceptButton;
    ReminderListListener listListener;

    public ProximityReminderViewHolder(@NonNull View itemView, ReminderListListener listener) {
        super(itemView);
        titleTV = itemView.findViewById(R.id.reminder_title_textview);
        userTV = itemView.findViewById(R.id.other_user_tv);
        deleteButton = itemView.findViewById(R.id.delete_reminder_button);
        acceptButton = itemView.findViewById(R.id.accept_reminder_button);
        this.listListener = listener;
    }
    public void bind(ProximityReminder reminder, String email){
        titleTV.setText(reminder.getReminderTitle());
        userTV.setText(email);


    }
    public void setup(ProximityReminder reminder, Context context){

        String url = Constants.API_GET_EMAIL_FROM_USER + "?username=";

        String username = OnitApplication.instance.getAccountManager().username;
        if(username.equals(reminder.getTarget())){
            url = url + reminder.getUser();
        }
        else{
            url = url + reminder.getTarget();
        }

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            bind(reminder, response);
        }, error -> {

        });
        queue.add(request);

        deleteButton.setOnClickListener(v -> {
            listListener.onDelete(reminder);
        });
        if(OnitApplication.instance.getAccountManager().username.equals(reminder.getTarget()) && !reminder.isAccepted()){
            acceptButton.setVisibility(View.VISIBLE);
            acceptButton.setOnClickListener(v ->{
                listListener.onAccept(reminder);
            });
        }
    }


    public interface ReminderListListener{
        void onDelete(ProximityReminder reminder);
        void onAccept(ProximityReminder reminder);
    }
}
