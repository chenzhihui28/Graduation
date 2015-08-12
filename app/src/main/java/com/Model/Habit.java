package com.Model;

import cn.bmob.v3.BmobObject;

public class Habit extends BmobObject  {
    private String username;
    private int HabitState;
    private String HabitName;
    private int HabitCost;
    private int RecordTime;
    private String LastRecordTime;
    public Habit(){}

    public Habit(String username, int habitState, String habitName, int habitCost, int recordTime, String lastRecordTime) {
        this.username = username;
        HabitState = habitState;
        HabitName = habitName;
        HabitCost = habitCost;
        RecordTime = recordTime;
        LastRecordTime = lastRecordTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getHabitState() {
        return HabitState;
    }

    public void setHabitState(int habitState) {
        HabitState = habitState;
    }

    public String getHabitName() {
        return HabitName;
    }

    public void setHabitName(String habitName) {
        HabitName = habitName;
    }

    public int getHabitCost() {
        return HabitCost;
    }

    public void setHabitCost(int habitCost) {
        HabitCost = habitCost;
    }

    public int getRecordTime() {
        return RecordTime;
    }

    public void setRecordTime(int recordTime) {
        RecordTime = recordTime;
    }

    public String getLastRecordTime() {
        return LastRecordTime;
    }

    public void setLastRecordTime(String lastRecordTime) {
        LastRecordTime = lastRecordTime;
    }
}
