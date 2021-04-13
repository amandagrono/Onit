package com.temple.onit.Alarms;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.core.app.NotificationCompat;

import com.temple.onit.Constants;
import com.temple.onit.R;

public class AlarmService extends Service {

    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

    public AlarmService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mediaPlayer = MediaPlayer.create(this, R.raw.alarmsound);
        mediaPlayer.setLooping(true);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notificationIntent = new Intent(this, AlarmViewerActivity.class);
        notificationIntent.putExtra(Constants.ALARM_TITLE, intent.getStringExtra(Constants.ALARM_TITLE));
        notificationIntent.putExtra(Constants.LEAVE_MINUTE, intent.getIntExtra(Constants.LEAVE_MINUTE, 0));
        notificationIntent.putExtra(Constants.LEAVE_HOUR, intent.getIntExtra(Constants.LEAVE_HOUR, 0));
        notificationIntent.putExtra(Constants.ARRIVAL_HOUR, intent.getIntExtra(Constants.ARRIVAL_HOUR, 0));
        notificationIntent.putExtra(Constants.ARRIVAL_MINUTE, intent.getIntExtra(Constants.ARRIVAL_MINUTE, 0));
        notificationIntent.putExtra(Constants.DESTINATION_LONGITUDE, intent.getDoubleExtra(Constants.DESTINATION_LONGITUDE, 0));
        notificationIntent.putExtra(Constants.DESTINATION_LATITUDE, intent.getDoubleExtra(Constants.DESTINATION_LATITUDE, 0));

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        String alarmTitle = intent.getStringExtra(Constants.ALARM_TITLE) + " Alarm";

        Notification notification = new NotificationCompat.Builder(this, Constants.ALARM_CHANNEL)
                .setContentTitle(alarmTitle)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .build();
        mediaPlayer.start();
        long[] pattern = {10, 100, 1000};
        int[] amplitudes = {250, 250, 250};
        vibrator.vibrate(VibrationEffect.createWaveform(pattern, amplitudes, 0));

        startForeground(1, notification);

        return START_STICKY;
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