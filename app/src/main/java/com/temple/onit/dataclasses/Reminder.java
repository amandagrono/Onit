package com.temple.onit.dataclasses;

import androidx.annotation.Nullable;

import java.util.UUID;

public class Reminder {
    private String id = UUID.randomUUID().toString();
    private double radius;
    private String reminderTitle;
    private String reminderContent;

    public Reminder(String title, String content, double radius){
        this.reminderTitle = title;
        this.reminderContent = content;
        this.radius = radius;
    }

    public void setRadius(double radius){
        this.radius = radius;
    }
    public void setReminderTitle(String reminderTitle){
        this.reminderTitle = reminderTitle;
    }
    public void setReminderContent(String reminderContent){
        this.reminderContent = reminderContent;
    }

    public double getRadius(){
        return this.radius;
    }
    public String getReminderTitle(){
        return reminderTitle;
    }
    public String getReminderContent(){
        return reminderContent;
    }

    public String getId(){
        return  id;
    }

    @Override
    public boolean equals(@Nullable @org.jetbrains.annotations.Nullable Object obj) {
        if(obj == null){
            return false;
        }
        if(!(obj instanceof Reminder)){
            return false;
        }
        Reminder other = (Reminder) obj;
        return other.id.equals(this.id);
    }
}
