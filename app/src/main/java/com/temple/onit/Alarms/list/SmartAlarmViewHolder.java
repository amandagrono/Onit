package com.temple.onit.Alarms.list;

import android.graphics.Color;
import android.media.Image;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.temple.onit.Alarms.SmartAlarm;
import com.temple.onit.R;

public class SmartAlarmViewHolder extends RecyclerView.ViewHolder{

    private TextView alarmTitle;
    private TextView arrivalTime;
    private ImageButton alarmDeleteButton;
    private TextView sunday;
    private TextView monday;
    private TextView tuesday;
    private TextView wednesday;
    private TextView thursday;
    private TextView friday;
    private TextView saturday;
    SwitchCompat toggle;
    private OnToggleAlarmListener listener;

    public SmartAlarmViewHolder(@NonNull View itemView, OnToggleAlarmListener listener) {
        super(itemView);
        alarmTitle = itemView.findViewById(R.id.alarm_recycler_view_text_title);//
        arrivalTime = itemView.findViewById(R.id.alarm_recycler_view_arrival_time);//
        alarmDeleteButton = itemView.findViewById(R.id.alarm_list_delete_button);
        sunday = itemView.findViewById(R.id.alarm_list_sunday);//
        monday = itemView.findViewById(R.id.alarm_list_monday);//
        tuesday = itemView.findViewById(R.id.alarm_list_tuesday);//
        wednesday = itemView.findViewById(R.id.alarm_list_wednesday);//
        thursday = itemView.findViewById(R.id.alarm_list_thursday);//
        friday = itemView.findViewById(R.id.alarm_list_friday);//
        saturday = itemView.findViewById(R.id.alarm_list_saturday);//
        toggle = itemView.findViewById(R.id.alarm_list_switch);//
        this.listener = listener;//
    }

    public void bind(SmartAlarm alarm){
        alarmTitle.setText(alarm.getAlarmTitle());
        toggle.setChecked(alarm.isStarted());

        String arrivalTimeText = getArrivalTimeText(alarm);

        Log.d("View Holder: ","Arrival Time Text: " +  arrivalTimeText);

        arrivalTime.setText(arrivalTimeText);

        if(alarm.enabledOnDay(0)) sunday.setBackgroundColor(Color.GREEN);
        if(alarm.enabledOnDay(1)) monday.setBackgroundColor(Color.GREEN);
        if(alarm.enabledOnDay(2)) tuesday.setBackgroundColor(Color.GREEN);
        if(alarm.enabledOnDay(3)) wednesday.setBackgroundColor(Color.GREEN);
        if(alarm.enabledOnDay(4)) thursday.setBackgroundColor(Color.GREEN);
        if(alarm.enabledOnDay(5)) friday.setBackgroundColor(Color.GREEN);
        if(alarm.enabledOnDay(6)) saturday.setBackgroundColor(Color.GREEN);

        toggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            listener.onToggle(alarm);
        });
        alarmDeleteButton.setOnClickListener(v -> {
            listener.onDelete(alarm);
        });

    }
    private String getArrivalTimeText(SmartAlarm alarm){
        StringBuilder returnString = new StringBuilder("Arrival Hour: ");
        if(alarm.getArrivalHour() < 10){
            returnString.append("0").append(alarm.getArrivalHour());
        }
        else{
            returnString.append(alarm.getArrivalHour());
        }

        returnString.append(":");

        if(alarm.getArrivalMinute() < 10){
            returnString.append(":").append(alarm.getArrivalMinute());
        }
        else{
            returnString.append(alarm.getArrivalMinute());
        }
        Log.d("View Holder", returnString.toString());
        return returnString.toString();

    }
}

interface OnToggleAlarmListener{
    void onToggle(SmartAlarm alarm);
    void onDelete(SmartAlarm alarm);
}
