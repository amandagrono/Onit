package com.temple.onit.dataclasses;

import androidx.annotation.Nullable;

public class ProximityReminder extends Reminder{
    private String user;
    private String target;
    private int id;
    private boolean accepted;

    public ProximityReminder(String title, String content, double radius, String user, String target, int id, boolean accepted) {
        super(title, content, radius);
        this.user = user;
        this.target = target;
        this.id = id;
        this.accepted = accepted;
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
    public void setAccepted(){
        this.accepted = true;
    }
    public boolean isAccepted(){
        return this.accepted;
    }
    public int getIntId(){
        return this.id;
    }

    @Override
    public boolean equals(@Nullable @org.jetbrains.annotations.Nullable Object obj) {
        if(obj == null){
            return false;
        }
        if(!(obj instanceof ProximityReminder)){
            return false;
        }
        if(((ProximityReminder) obj).id == this.id){
            return true;
        }
        return false;
    }
}
