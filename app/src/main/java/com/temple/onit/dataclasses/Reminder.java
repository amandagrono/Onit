package com.temple.onit.dataclasses;

public class Reminder {
    private int radius;
    private String reminderTitle;
    private String reminderText;

    public void setRadius(int radius){
        this.radius = radius;
    }
    public void setReminderTitle(String reminderTitle){
        this.reminderTitle = reminderTitle;
    }
    public void setReminderText(String reminderText){
        this.reminderText = reminderText;
    }

    public int getRadius(){
        return this.radius;
    }
    public String getReminderTitle(){
        return reminderTitle;
    }
    public String getReminderText(){
        return reminderText;
    }
}
