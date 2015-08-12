package com.fragments.EfficiencyTools;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.Model.Habit;
import com.Utils.SingleDataUtils;
import com.Utils.Tools;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.bmob.v3.listener.UpdateListener;
import huti.material.R;

public class HabitDetailActivity extends ActionBarActivity {
    Habit habit;
    private TextView day_tv,habit_name_tv,need_time_tv;
    private Button record_btn;
    private ProgressBar habit_detail_progress;
    private boolean alreadyRecord = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        habit = SingleDataUtils.getInstance().getHabit();
        day_tv = (TextView) findViewById(R.id.day_tv);
        habit_name_tv = (TextView) findViewById(R.id.habit_name_tv);
        need_time_tv = (TextView) findViewById(R.id.need_time_tv);
        record_btn = (Button) findViewById(R.id.record_btn);
        habit_detail_progress = (ProgressBar)findViewById(R.id.habit_detail_progress);

        caculateAndSetData();

    }

    public void caculateAndSetData(){
        day_tv.setText(habit.getRecordTime()+"");
        habit_name_tv.setText(habit.getHabitName());


        if(!habit.getLastRecordTime().equals("")){
            if(isToday(habit.getLastRecordTime()+" 03:03") == 1){//看看上一次打卡时间是不是今天
                alreadyRecord = true;
            }else {
                alreadyRecord = false;
            }
        }else {
            alreadyRecord = false;
        }


        if(habit.getHabitState() == 1){
            need_time_tv.setText("习惯养成还需"+(habit.getHabitCost()-habit.getRecordTime())+"天");
            if(alreadyRecord){
                record_btn.setEnabled(false);
            }else {
                record_btn.setOnClickListener(new MyOnClickListener());
            }

        }else if(habit.getHabitState() == 2){
            need_time_tv.setText("好习惯已经养成！继续坚持~");
            if(alreadyRecord){
                record_btn.setEnabled(false);
            }else {
                record_btn.setOnClickListener(new MyOnClickListener());
            }

        }else if(habit.getHabitState() == 3){
            need_time_tv.setText("超过五天未打卡，计划失败");
            record_btn.setEnabled(false);
        }
    }

    public class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.record_btn:
                    habit.setRecordTime(habit.getRecordTime()+1);
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    String now = df.format(new Date());
                    habit.setLastRecordTime(now);
                    habit_detail_progress.setVisibility(View.VISIBLE);
                    habit.update(HabitDetailActivity.this,new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            habit_detail_progress.setVisibility(View.GONE);
                            caculateAndSetData();
                            Tools.ShowToast(HabitDetailActivity.this,"打卡成功!",2);
                            record_btn.setEnabled(false);
                            alreadyRecord = true;

                        }

                        @Override
                        public void onFailure(int i, String s) {
                            habit_detail_progress.setVisibility(View.GONE);
                        }
                    });
                    break;
            }
        }
    }

    /**
     * 格式化时间
     * @param time
     * @return 1:是今天  2:不是
     */
    private static int isToday(String time) {
        SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
        if(time==null ||"".equals(time)){
            return 2;
        }
        Date date = null;
        try {
            date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar current = Calendar.getInstance();

        Calendar today = Calendar.getInstance();	//今天

        today.set(Calendar.YEAR, current.get(Calendar.YEAR));
        today.set(Calendar.MONTH, current.get(Calendar.MONTH));
        today.set(Calendar.DAY_OF_MONTH,current.get(Calendar.DAY_OF_MONTH));


        Calendar yesterday = Calendar.getInstance();	//昨天

        yesterday.set(Calendar.YEAR, current.get(Calendar.YEAR));
        yesterday.set(Calendar.MONTH, current.get(Calendar.MONTH));
        yesterday.set(Calendar.DAY_OF_MONTH,current.get(Calendar.DAY_OF_MONTH)-1);

        current.setTime(date);

        if(current.after(today)){
            return 1;
        }else if(current.before(today) && current.after(yesterday)){

            return 2;
        }else{
            return 2;
        }
    }





}
