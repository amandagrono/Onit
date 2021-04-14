package com.temple.onit.Alarms.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.temple.onit.Alarms.AlarmViewActivity;
import com.temple.onit.Constants;
import com.temple.onit.R;

import java.util.Objects;

public class SmartAlarmService extends Service {

    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

   @Override
   public void onCreate(){
       super.onCreate();
       mediaPlayer = MediaPlayer.create(this, R.raw.alarmsound);
       mediaPlayer.setLooping(true);

       vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

   }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent notificationIntent = new Intent(this, AlarmViewActivity.class);


       String alarmTitle = intent.getStringExtra(Constants.ALARM_TITLE);
       int leaveHour = intent.getIntExtra(Constants.LEAVE_HOUR, 0);
       int leaveMinute = intent.getIntExtra(Constants.LEAVE_MINUTE, 0);
       int arrivalHour = intent.getIntExtra(Constants.ARRIVAL_HOUR, 0);
       int arrivalMinute = intent.getIntExtra(Constants.ARRIVAL_MINUTE, 0);
       double destinationLongitude = intent.getDoubleExtra(Constants.DESTINATION_LONGITUDE, 0);
       double destinationLatitude = intent.getDoubleExtra(Constants.DESTINATION_LATITUDE, 0);

       Log.d("Intent Alarm View","Alarm Service: " + alarmTitle + " leave hour: " + leaveHour + " leave minute: " + leaveMinute
       + "arrival hour: " + arrivalHour  + " arrival minute: " + arrivalMinute +
               " destination longitude: " + destinationLongitude + " destination latitude: " + destinationLatitude);

       notificationIntent.putExtra(Constants.ALARM_TITLE, alarmTitle);
       notificationIntent.putExtra(Constants.LEAVE_HOUR, leaveHour);
       notificationIntent.putExtra(Constants.LEAVE_MINUTE, leaveMinute);
       notificationIntent.putExtra(Constants.ARRIVAL_HOUR, arrivalHour);
       notificationIntent.putExtra(Constants.ARRIVAL_MINUTE, arrivalMinute);
       notificationIntent.putExtra(Constants.DESTINATION_LONGITUDE, destinationLongitude);
       notificationIntent.putExtra(Constants.DESTINATION_LATITUDE, destinationLatitude);
       PendingIntent pendingIntent = PendingIntent.getActivity(this, intent.getIntExtra(Constants.ALARM_ID, 0), notificationIntent, 0);
       Notification notification = new NotificationCompat.Builder(this, Constants.ALARM_CHANNEL)
               .setContentTitle(intent.getStringExtra(Constants.ALARM_TITLE))
               .setContentText("Alarm is going off")
               .setSmallIcon(R.drawable.ic_launcher_foreground)
               .setContentIntent(pendingIntent)
               .build();
       mediaPlayer.start();
       long[] pattern = {10, 100, 1000};
       int[] amplitudes = {100, 200, 250};
       vibrator.vibrate(VibrationEffect.createWaveform(pattern, amplitudes, 0));

       startForeground(1, notification);


        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mediaPlayer.stop();
        vibrator.cancel();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}