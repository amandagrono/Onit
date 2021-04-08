package com.temple.onit;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.DrawableRes;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.android.volley.BuildConfig;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.temple.onit.GeofencedReminder.GeofencedReminderActivity;
import com.temple.onit.dataclasses.GeofencedReminder;

public final class Utils {
    private static final String NOTIFICATION_CHANNEL_ID = BuildConfig.APPLICATION_ID + ".channel";

    public static void sendNotification(Context context, String message, LatLng latLng){
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID) == null){
            String name = "Remind Me There";
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        Intent intent = GeofencedReminderActivity.newIntent(context.getApplicationContext(), latLng);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context)
                .addParentStack(GeofencedReminderActivity.class)
                .addNextIntent(intent);
        PendingIntent notificationPendingIntent = taskStackBuilder.getPendingIntent(getUniqueId(), PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(message)
                .setContentIntent(notificationPendingIntent)
                .setAutoCancel(true)
                .build();
        notificationManager.notify(getUniqueId(), notification);
    }

    private static int getUniqueId(){
        return (int) System.currentTimeMillis() % 10000;
    }

    public static void hideKeyboard(Context context, View view){
        InputMethodManager keyboard = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showReminderInMap(Context context, GoogleMap map, GeofencedReminder reminder){
        if(reminder.getLocation() != null){
            LatLng latLng = new LatLng(reminder.getLocation().latitude, reminder.getLocation().longitude);
            BitmapDescriptor vectorToBitmap = vectorToBitmap(context.getResources(), R.drawable.ic_twotone_location_on_48px);
            Marker marker = map.addMarker(new MarkerOptions().position(latLng).icon(vectorToBitmap));
            marker.setTag(reminder.getId());

            if(reminder.getRadius() != 0){
                double radius = reminder.getRadius();
                map.addCircle(new CircleOptions()
                        .center(reminder.getLatLng())
                        .radius(radius)
                        .strokeColor(ContextCompat.getColor(context, R.color.colorAccent))
                        .fillColor(ContextCompat.getColor(context, R.color.colorReminderFill)));


            }
        }
    }

    public static BitmapDescriptor vectorToBitmap(Resources resources, @DrawableRes int id){
        Drawable vectorDrawable = ResourcesCompat.getDrawable(resources, id, null);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        vectorDrawable.setBounds(0,0,canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

}
