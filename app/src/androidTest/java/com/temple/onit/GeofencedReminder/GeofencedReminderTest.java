package com.temple.onit.GeofencedReminder;

import androidx.test.platform.app.InstrumentationRegistry;

import com.google.android.gms.maps.model.LatLng;
import com.temple.onit.OnitApplication;

import junit.framework.TestCase;

public class GeofencedReminderTest extends TestCase {
    GeofencedReminder reminder = new GeofencedReminder(new LatLng(39, -75), "title", "content", 100);

    public void testAdding(){
        //GeofencedReminder reminder = new GeofencedReminder(new LatLng(39, -75), "title", "content", 100);
        int sizeBefore = OnitApplication.instance.getGeofenceReminderManager().getAll().size();
        OnitApplication.instance.getGeofenceReminderManager().add(reminder, InstrumentationRegistry.getInstrumentation().getContext(), new GeofenceReminderManager.GeofenceManagerInterface() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(String error) {

            }
        });
        //wait 3 seconds because adding the reminder is a async task
        try{
            Thread.sleep(3000);
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        int sizeAfter = OnitApplication.instance.getGeofenceReminderManager().getAll().size();
        assertEquals(sizeBefore + 1, sizeAfter);

    }
    public void testRemoving(){
        OnitApplication.instance.getGeofenceReminderManager().add(reminder, InstrumentationRegistry.getInstrumentation().getContext(), new GeofenceReminderManager.GeofenceManagerInterface() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(String error) {

            }
        });
        // sleep for 3 seconds because this is an async operation
        try{
            Thread.sleep(3000);
        }
        catch (InterruptedException e ){
            e.printStackTrace();
        }

        int sizeBefore = OnitApplication.instance.getGeofenceReminderManager().getAll().size();
        final int sizeAfter;
        OnitApplication.instance.getGeofenceReminderManager().remove(reminder, InstrumentationRegistry.getInstrumentation().getContext(), new GeofenceReminderManager.RemoveReminderInterface() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(String error) {

            }
        });


        try{
            Thread.sleep(3000);
        }
        catch (InterruptedException e ){
            e.printStackTrace();
        }
        sizeAfter = OnitApplication.instance.getGeofenceReminderManager().getAll().size();

        assertEquals(sizeBefore - 1, sizeAfter);
    }

}