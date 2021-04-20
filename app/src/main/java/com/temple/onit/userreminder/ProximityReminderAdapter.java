package com.temple.onit.userreminder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.temple.onit.R;
import com.temple.onit.dataclasses.ProximityReminder;

import java.util.ArrayList;

public class ProximityReminderAdapter extends RecyclerView.Adapter<ProximityReminderViewHolder>{

    ProximityReminderViewHolder.ReminderListListener listener;
    ArrayList<ProximityReminder> reminderList;

    public ProximityReminderAdapter(ProximityReminderViewHolder.ReminderListListener listener, ArrayList<ProximityReminder> reminderList){
        this.listener = listener;
        this.reminderList = reminderList;
    }


    @NonNull
    @Override
    public ProximityReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_reminder, parent, false);
        return new ProximityReminderViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ProximityReminderViewHolder holder, int position) {
        holder.bind(reminderList.get(position));
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }
}
