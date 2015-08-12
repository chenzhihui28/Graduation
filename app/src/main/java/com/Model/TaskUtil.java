package com.Model;

import android.content.Context;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by chenzhihui on 2015/5/23.
 */
public class TaskUtil {
    private static TaskUtil instance;
    private Context mContext;
    public TaskUtil(Context context) {
        this.mContext = context;
    }

    public static TaskUtil getInstance(Context context) {
        if (instance == null) {
            instance = new TaskUtil(context);
        }
        return instance;
    }


    public void save(Task task) {
        // TODO Auto-generated method stub
        task.save(mContext, new SaveListener(){
            @Override
            public void onFailure(int arg0, String arg1) {

            }

            @Override
            public void onSuccess() {
            }
        });
    }

    public void delete(String id) {
        // TODO Auto-generated method stub
        Task habit=new Task();
        habit.setObjectId(id);
        habit.delete(mContext, new DeleteListener(){

            @Override
            public void onFailure(int arg0, String arg1) {

            }

            @Override
            public void onSuccess() {

            }

        });
    }

    public void update(String id, Task task) {
        // TODO Auto-generated method stub
        task.update(mContext, id, new UpdateListener(){

            @Override
            public void onFailure(int arg0, String arg1) {

            }

            @Override
            public void onSuccess() {

            }

        });
    }

    public void find(String username) {
        // TODO Auto-generated method stub
        BmobQuery<Task> query = new BmobQuery<Task>();
        query.addWhereEqualTo("username", username);
        query.order("habitState");
        query.findObjects(mContext, new FindListener<Task>(){
            public void onError(int arg0, String arg1) {

            }
            @Override
            public void onSuccess(List<Task> list) {
                // TODO Auto-generated method stub
                System.out.println(list.toString());
            }
        });
    }
}
