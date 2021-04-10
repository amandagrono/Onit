package com.temple.onit.Alarms;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.temple.onit.R;

import java.util.List;

public class AlarmListAdapter extends RecyclerView.Adapter<AlarmListAdapter.ViewHolder> {

    private List<SmartAlarm> smartAlarmList;
    private AlarmListAdapterInterface listener;

    public AlarmListAdapter(List<SmartAlarm> smartAlarmList, AlarmListAdapterInterface listener){
        this.smartAlarmList = smartAlarmList;
        this.listener = listener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_alarm, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SmartAlarm alarm = smartAlarmList.get(position);

        holder.alarmTitleTV.setText(alarm.getAlarmTitle());
        String arrivalTime = alarm.getArrivalHour() + ":" + alarm.getArrivalMinute();
        holder.arrivalTimeTV.setText(arrivalTime);
        holder.sunday.setBackgroundColor(returnColor(0, alarm));
        holder.monday.setBackgroundColor(returnColor(1, alarm));
        holder.tuesday.setBackgroundColor(returnColor(2, alarm));
        holder.wednesday.setBackgroundColor(returnColor(3, alarm));
        holder.thursday.setBackgroundColor(returnColor(4, alarm));
        holder.friday.setBackgroundColor(returnColor(5, alarm));
        holder.saturday.setBackgroundColor(returnColor(6, alarm));
        holder.toggleSwitch.setChecked(alarm.isEnabled());

        holder.toggleSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            listener.toggleAlarm(isChecked, alarm);
        });
        holder.deleteButton.setOnClickListener(v -> {
            listener.deleteAlarm(alarm);
        });

    }

    @Override
    public int getItemCount() {
        return smartAlarmList.size();
    }

    private int returnColor(int day, SmartAlarm alarm){
        if(alarm.enabledOnDay(day)){
            return Color.GREEN;
        }
        else{
            return Color.RED;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView alarmTitleTV;
        TextView arrivalTimeTV;
        ImageButton deleteButton;
        TextView sunday;
        TextView monday;
        TextView tuesday;
        TextView wednesday;
        TextView thursday;
        TextView friday;
        TextView saturday;
        SwitchCompat toggleSwitch;


        public ViewHolder(@NonNull View view) {
            super(view);
            alarmTitleTV = view.findViewById(R.id.alarm_recycler_view_text_title);
            arrivalTimeTV = view.findViewById(R.id.alarm_recycler_view_arrival_time);
            deleteButton = view.findViewById(R.id.alarm_list_delete_button);
            sunday = view.findViewById(R.id.alarm_list_sunday);
            monday = view.findViewById(R.id.alarm_list_monday);
            tuesday = view.findViewById(R.id.alarm_list_tuesday);
            wednesday = view.findViewById(R.id.alarm_list_wednesday);
            thursday = view.findViewById(R.id.alarm_list_thursday);
            friday = view.findViewById(R.id.alarm_list_friday);
            saturday = view.findViewById(R.id.alarm_list_saturday);
            toggleSwitch = view.findViewById(R.id.alarm_list_switch);
        }

    }

    interface AlarmListAdapterInterface{
        public void toggleAlarm(boolean enabled, SmartAlarm alarm);
        public void deleteAlarm(SmartAlarm alarm);
    }

}
