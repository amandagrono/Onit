package com.temple.onit.userreminder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.temple.onit.OnitApplication;
import com.temple.onit.R;
import com.temple.onit.dataclasses.ProximityReminder;

public class ProximityReminderViewHolder extends RecyclerView.ViewHolder{

    private TextView titleTV;
    private TextView userTV;
    private ImageButton deleteButton;
    ReminderListListener listListener;

    public ProximityReminderViewHolder(@NonNull View itemView, ReminderListListener listener) {
        super(itemView);
        titleTV = itemView.findViewById(R.id.reminder_title_textview);
        userTV = itemView.findViewById(R.id.other_user_tv);
        deleteButton = itemView.findViewById(R.id.delete_reminder_button);
        this.listListener = listener;
    }
    public void bind(ProximityReminder reminder){
        titleTV.setText(reminder.getReminderTitle());
        String username = OnitApplication.instance.getAccountManager().username;
        if(username.equals(reminder.getTarget())){
            userTV.setText(reminder.getUser());
        }
        else{
            userTV.setText(reminder.getTarget());
        }
        deleteButton.setOnClickListener(v -> {
            listListener.onDelete(reminder);
        });
    }


    public interface ReminderListListener{
        void onDelete(ProximityReminder reminder);
    }
}
