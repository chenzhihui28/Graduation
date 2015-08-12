package com.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.DataList.TaskArrayList;
import com.Model.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by chenzhihui on 2015/5/23.
 */
public class SharePreferenceUtil {

    public static void setUserName(Context context,String username){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SP_USERNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.SP_USERNAME, username);
        editor.commit();
    }
    public static String getUserName(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SP_USERNAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(Constants.SP_USERNAME, "0");
    }

    public static void setTaskList(Context context,TaskArrayList taskList){
        //Serialize our TaskArrayList to Json
        Type taskArrayListType = new TypeToken<TaskArrayList>(){}.getType();
        String serializedData = new Gson().toJson(taskList, taskArrayListType);
        //Save tasks in SharedPreferences
        SharedPreferences preferencesReader = context.getSharedPreferences(Constants.SP_ALLTASKLIST, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencesReader.edit();
        editor.putString("TaskArrayList", serializedData);
        editor.apply();


    }

    public static TaskArrayList getTaskList(Context context){
        SharedPreferences preferencesReader = context.getSharedPreferences(Constants.SP_ALLTASKLIST,
                Context.MODE_PRIVATE);
        //Return null if preference doesn't exist
        String serializedDataFromPreference = preferencesReader.getString("TaskArrayList", null);
        //Deserializes any taskarraylist we have saved
        Type taskArrayListType = new TypeToken<TaskArrayList>() {}.getType();
        return new Gson().fromJson(serializedDataFromPreference, taskArrayListType);
    }
}

