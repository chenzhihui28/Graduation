package com.Model;

import cn.bmob.v3.BmobObject;

public class Tomato extends BmobObject {

    private String username;
    private String TomatoDate;
    private String TomatoTimeRange;
    private String TomatoDescription;



    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getTomatoDate() {
        return TomatoDate;
    }
    public void setTomatoDate(String tomatoDate) {
        TomatoDate = tomatoDate;
    }
    public String getTomatoTimeRange() {
        return TomatoTimeRange;
    }
    public void setTomatoTimeRange(String tomatoTimeRange) {
        TomatoTimeRange = tomatoTimeRange;
    }
    public String getTomatoDescription() {
        return TomatoDescription;
    }
    public void setTomatoDescription(String tomatoDescription) {
        TomatoDescription = tomatoDescription;
    }
    @Override
    public String toString() {
        return "Tomato [tomatoid="+this.getObjectId()+", username=" + username + ", TomatoDate=" + TomatoDate
                + ", TomatoTimeRange=" + TomatoTimeRange
                + ", TomatoDescription=" + TomatoDescription + "]";
    }





}
