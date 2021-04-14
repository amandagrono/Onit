package com.temple.onit.Alarms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.temple.onit.Alarms.services.RescheduleSmartAlarmService;
import com.temple.onit.Alarms.services.SmartAlarmService;
import com.temple.onit.Constants;

import java.util.Calendar;

public class AlarmBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Alarm Title Intent", "Broadcast Receiver: " + intent.getStringExtra(Constants.ALARM_TITLE));
        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
            Toast.makeText(context, "Alarm Reboot", Toast.LENGTH_LONG).show();
            startRescheduleAlarmService(context);
        }
        else{
            Toast.makeText(context, "Alarm Received", Toast.LENGTH_LONG).show();
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
                if(intent.getBooleanExtra(Constants.MONDAY, false)) return true;
                return false;
            case Calendar.TUESDAY:
                if(intent.getBooleanExtra(Constants.TUESDAY, false)) return true;
                return false;
            case Calendar.WEDNESDAY:
                if(intent.getBooleanExtra(Constants.WEDNESDAY, false)) return true;
                return false;
            case Calendar.THURSDAY:
                if(intent.getBooleanExtra(Constants.THURSDAY, false)) return true;
                return false;
            case Calendar.FRIDAY:
                if(intent.getBooleanExtra(Constants.FRIDAY, false)) return true;
                return false;
            case Calendar.SATURDAY:
                if(intent.getBooleanExtra(Constants.SATURDAY, false)) return true;
                return false;
            case Calendar.SUNDAY:
                if(intent.getBooleanExtra(Constants.SUNDAY, false)) return true;
                return false;

        }
        return false;
    }

    private void startAlarmService(Context context, Intent intent){
        Intent intentService = new Intent(context, SmartAlarmService.class);

        String alarmTitle = intent.getStringExtra(Constants.ALARM_TITLE);
        int leaveHour = intent.getIntExtra(Constants.LEAVE_HOUR, 0);
        int leaveMinute = intent.getIntExtra(Constants.LEAVE_MINUTE, 0);
        int arrivalHour = intent.getIntExtra(Constants.ARRIVAL_HOUR, 0);
        int arrivalMinute = intent.getIntExtra(Constants.ARRIVAL_MINUTE, 0);
        double destinationLongitude = intent.getDoubleExtra(Constants.DESTINATION_LONGITUDE, 0);
        double destinationLatitude = intent.getDoubleExtra(Constants.DESTINATION_LATITUDE, 0);

        intentService.putExtra(Constants.ALARM_TITLE, alarmTitle);

        intentService.putExtra(Constants.LEAVE_HOUR, leaveHour);
        intentService.putExtra(Constants.LEAVE_MINUTE, leaveMinute);

        intentService.putExtra(Constants.ARRIVAL_MINUTE, arrivalMinute);
        intentService.putExtra(Constants.ARRIVAL_HOUR, arrivalHour);

        intentService.putExtra(Constants.DESTINATION_LONGITUDE, destinationLongitude);
        intentService.putExtra(Constants.DESTINATION_LATITUDE, destinationLatitude);

        intentService.putExtra(Constants.ALARM_ID, intent.getIntExtra(Constants.ALARM_ID, 0));

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            context.startForegroundService(intentService);
        }
        else{
            context.startService(intentService);
        }
    }

    private void startRescheduleAlarmService(Context context){
        Intent intentService = new Intent(context, RescheduleSmartAlarmService.class);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            context.startForegroundService(intentService);
        }
        else{
            context.startService(intentService);
        }

    }

}
