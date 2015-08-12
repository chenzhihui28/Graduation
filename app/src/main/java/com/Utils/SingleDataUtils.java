package com.Utils;

import com.Model.Habit;
import com.Model.User;

import java.util.ArrayList;

/**
 * 将账户信息保存到内存中，方便调用经常使用的auth_token等请求字段 单例模式：set一次，全局get
 *
 * @author Ben
 */
public class SingleDataUtils {

    private static SingleDataUtils instance = null;
    private String Username;
    private Habit habit;

    public static SingleDataUtils getInstance() {
        if (instance == null) {
            instance = new SingleDataUtils();
        }
        return instance;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public Habit getHabit() {
        return habit;
    }

    public void setHabit(Habit habit) {
        this.habit = habit;
    }
}
