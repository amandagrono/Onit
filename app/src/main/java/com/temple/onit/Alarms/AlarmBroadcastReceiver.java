package com.temple.onit.Alarms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import com.temple.onit.Constants;

import java.util.Calendar;

public class AlarmBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
            String toastText = "Alarm Reboot";
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
            startRescheduleAlarmsService(context);
        }
        else{
            String toastText = "Alarm Received";
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
            if(!intent.getBooleanExtra(Constants.RECURRING, false)){
                startAlarmService(context, intent);
            }{
                if(alarmIsToday(intent)){
                    startAlarmService(context, intent);
                }
            }
        }
    }
    private boolean alarmIsToday(Intent intent){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int today = calendar.get(Calendar.DAY_OF_WEEK);

        switch(today){
            case Calendar.MONDAY:
                return intent.getBooleanExtra(Constants.MONDAY, false);
            case Calendar.TUESDAY:
                return intent.getBooleanExtra(Constants.TUESDAY, false);
            case Calendar.WEDNESDAY:
                return intent.getBooleanExtra(Constants.WEDNESDAY, false);
            case Calendar.THURSDAY:
                return intent.getBooleanExtra(Constants.THURSDAY, false);
            case Calendar.FRIDAY:
                return intent.getBooleanExtra(Constants.FRIDAY, false);
            case Calendar.SATURDAY:
                return intent.getBooleanExtra(Constants.SATURDAY, false);
            case Calendar.SUNDAY:
                return intent.getBooleanExtra(Constants.SUNDAY, false);
        }
        return false;
    }

    private void startAlarmService(Context context, Intent intent){
        Intent intentService = new Intent(context, AlarmService.class);
        intentService.putExtra(Constants.ALARM_TITLE, intent.getStringExtra(Constants.ALARM_TITLE));
        intentService.putExtra(Constants.LEAVE_MINUTE, intent.getIntExtra(Constants.LEAVE_MINUTE, 0));
        intentService.putExtra(Constants.LEAVE_HOUR, intent.getIntExtra(Constants.LEAVE_HOUR, 0));
        intentService.putExtra(Constants.ARRIVAL_HOUR, intent.getIntExtra(Constants.ARRIVAL_HOUR, 0));
        intentService.putExtra(Constants.ARRIVAL_MINUTE, intent.getIntExtra(Constants.ARRIVAL_MINUTE, 0));
        intentService.putExtra(Constants.DESTINATION_LONGITUDE, intent.getDoubleExtra(Constants.DESTINATION_LONGITUDE, 0));
        intentService.putExtra(Constants.DESTINATION_LATITUDE, intent.getDoubleExtra(Constants.DESTINATION_LATITUDE, 0));


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            context.startForegroundService(intentService);
        }
        else{
            context.startService(intentService);
        }
    }
    private void startRescheduleAlarmsService(Context context){
        Intent intentService = new Intent(context, RescheduleAlarmService.class);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            context.startForegroundService(intentService);
        }
        else{
            context.startService(intentService);
        }
    }


}
