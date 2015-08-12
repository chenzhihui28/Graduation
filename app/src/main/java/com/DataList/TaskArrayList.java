package com.DataList;

import com.Model.Task;

import java.util.ArrayList;
import java.util.Iterator;

public class TaskArrayList extends ArrayList<Task>{

    public TaskArrayList(){}

    //Removes an item from the list and then sorts it again
    public int sort(Task ti){
        if(ti != null) {
            this.remove(ti);
            return this.insert(ti);
        }

        else return -1;
    }

    //Adds a task to the appropriate place
    //Priority goes first unchecked with red > blue > green, then checked in chronological order
    public int insert(Task ti){

        //Index where we can start adding the task depending if it's checked or not
        int startIndex;

        //Unchecked tasks should be higher up
        if(!ti.getChecked()){
            startIndex = 0;

        } else {

            startIndex = this.size();

            //First occurrence of checked task
            for(int i = 0; i < this.size(); i++){
                if(this.get(i).getChecked()){
                    startIndex = i;
                    break;
                }
            }
        }

        //Place red unchecked, and checked
        if(ti.getColor() == 3 || ti.getChecked()){
            this.add(startIndex, ti);
            return startIndex;
        }

        for(int i = startIndex; i < this.size(); i++){

            //Unchecked have priority over checked
            if(!ti.getChecked() && this.get(i).getChecked()){
                this.add(i, ti);
                return i;
            }

            //If it's not red, blue has priority
            if(ti.getColor() == 2 && this.get(i).getColor() != 3){
                this.add(i, ti);
                return i;
            }

            //Green only has priority over green
            if(ti.getColor() == 1 && this.get(i).getColor() == 1){
                this.add(i, ti);
                return i;
            }
        }

        //Whatever
        this.add(ti);
        return this.size() - 1;
    }

    public void insert(TaskArrayList tal){
        for(Task ti : tal){
            this.insert(ti);
        }
    }

    public boolean hasCompletedTasks(){
        //First occurrence of checked task
        for(int i = 0; i < this.size(); i++){
            if(this.get(i).getChecked()){
                return true;
            }
        }

        return false;
    }

    //Returns a list of all completed tasks
    public TaskArrayList getCompletedTasks(){
        Iterator<Task> it = this.iterator();
        TaskArrayList completed = new TaskArrayList();

        while(it.hasNext()){
            Task ti = it.next();

            if(ti.getChecked()){
                completed.add(ti);
            }
        }

        return completed;
    }

    //Removes all completed tasks, returns a TaskArrayList of completed tasks
    public void removeCompletedTasks(){
        Iterator<Task> it = this.iterator();

        while(it.hasNext()){
            Task ti = it.next();

            if(ti.getChecked()){
                it.remove();
            }
        }
    }
}
