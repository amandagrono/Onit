package com.temple.onit.dataclasses;

public class ProximityReminder extends Reminder{
    private String user;
    private String target;

    public ProximityReminder(String title, String content, double radius, String user, String target) {
        super(title, content, radius);
        this.user = user;
        this.target = target;
    }

    public void setUser(String user){
        this.user = user;
    }
    public void setTarget(String target) {
        this.target = target;
    }

    public String getUser(){
        return user;
    }
    public String getTarget() { return target; }
}
