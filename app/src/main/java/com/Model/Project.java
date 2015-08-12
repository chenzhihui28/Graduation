package com.Model;

import cn.bmob.v3.BmobObject;

/**
 * Created by chenzhihui on 2015/5/25.
 */
public class Project extends BmobObject{

    private String username;
    private String ProjectName;
    private String ProjectDescription;

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

    public String getProjectDescription() {
        return ProjectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        ProjectDescription = projectDescription;
    }
}
