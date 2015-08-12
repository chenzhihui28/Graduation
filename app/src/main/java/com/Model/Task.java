package com.Model;

import cn.bmob.v3.BmobObject;

/**
 * Created by Adrian on 2014-11-09.
 */
public class Task extends BmobObject {

//    public static enum Color {
//        RED, BLUE, GREEN;
//    }

    private Integer color;
    private Integer TaskState;


    private boolean checked;
    private String username;
    private String ProjectName;
    private String TaskLimitTime;
    private String TaskDescription;
    private String TaskName;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProjectName() {
        return ProjectName;
    }

    public void setProjectName(String projectName) {
        ProjectName = projectName;
    }

    public String getTaskLimitTime() {
        return TaskLimitTime;
    }

    public void setTaskLimitTime(String taskLimitTime) {
        TaskLimitTime = taskLimitTime;
    }

    public String getTaskDescription() {
        return TaskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        TaskDescription = taskDescription;
    }

    public String getTaskName() {
        return TaskName;
    }

    public void setTaskName(String taskName) {
        TaskName = taskName;
    }

    public Task() {
        this.color = 2;
        this.checked = false;
    }

    public Task(Task ti) {
        this.color = ti.getColor();
        this.checked = ti.getChecked();
    }


    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }

    public boolean getChecked() {
        return checked;
    }

    public void setChecked(boolean c) {
        if(c){
            TaskState = 2;
        }else {
            TaskState = 1;
        }
        checked = c;
    }

    public void toggleChecked() {
        checked = !checked;
    }

    public Integer getTaskState() {
        return TaskState;
    }

    public void setTaskState(Integer taskState) {
        TaskState = taskState;
    }


}