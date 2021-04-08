package com.temple.onit.dataclasses;

public class ProximityReminder extends Reminder{
    private String user;

    public ProximityReminder(String title, String content, double radius, String user) {
        super(title, content, radius);
        this.user = user;
    }

    public void setUser(String user){
        this.user = user;
    }
    public String getUser(){
        return user;
    }
}
