package com.temple.onit.Alarms.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.temple.onit.Alarms.SmartAlarm;
import com.temple.onit.R;

import java.util.ArrayList;
import java.util.List;

public class SmartAlarmRecyclerView extends RecyclerView.Adapter<SmartAlarmViewHolder>{

    private List<SmartAlarm> alarmList;
    private OnToggleAlarmListener listener;

    public SmartAlarmRecyclerView(OnToggleAlarmListener listener){
        this.alarmList = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public SmartAlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_alarm, parent, false);
        return new SmartAlarmViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull SmartAlarmViewHolder holder, int position) {
        SmartAlarm alarm = alarmList.get(position);
        holder.bind(alarm);
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    public void setAlarms(List<SmartAlarm> alarms){
        this.alarmList = alarms;
        notifyDataSetChanged();
    }

    @Override
    public void onViewRecycled(@NonNull SmartAlarmViewHolder holder) {
        super.onViewRecycled(holder);
        holder.toggle.setOnCheckedChangeListener(null);
    }

}
