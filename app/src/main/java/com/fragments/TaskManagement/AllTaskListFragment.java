package com.fragments.TaskManagement;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.DataList.TaskArrayList;
import com.Model.Project;
import com.Model.Task;
import com.Utils.SharePreferenceUtil;
import com.Utils.Tools;
import com.adapter.AllTaskListAdapter;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.cocosw.undobar.UndoBarController;

import com.melnykov.fab.FloatingActionButton;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import huti.material.R;

public class AllTaskListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener
,AllTaskListAdapter.TaskItemClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String SHARED_PREFS_FILE = "MaterialistPreferences";
    private String mParam1 , mParam2;
    private OnFragmentInteractionListener mListener;
    private SwipeRefreshLayout swipe_container;
    private FloatingActionButton fab_add,fab_remove;
    private ListView listView;
    private ProgressBar alltasklist_progress;
    TaskArrayList tasks;
    TaskArrayList finished;
    AllTaskListAdapter adapter;
    View mainView;
    private List<String> projectNameList = new ArrayList<String>();
    private boolean haveProject = false;

    public static AllTaskListFragment newInstance(String param1, String param2) {
        AllTaskListFragment fragment = new AllTaskListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    public AllTaskListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.alltasklist_fragment, container, false);

        //Arraylist to save all our tasks
        tasks = new TaskArrayList();
        //Completed tasks we have removed
        finished = new TaskArrayList();


        swipe_container = (SwipeRefreshLayout)mainView.findViewById(R.id.swipe_refresh);
        swipe_container.setOnRefreshListener(this);


        alltasklist_progress = (ProgressBar) mainView.findViewById(R.id.alltasklist_progress);



        listView = (ListView) mainView.findViewById(R.id.listview);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //Tools.ShowToast(getActivity(),"longclick",2);
                final Task ti = (Task) view.getTag();
                MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                        .title("删除任务")
                        //.customView(R.layout.task_dialog)
                        .negativeText("取消")
                        .positiveText("确定")
                        .negativeColor(Color.parseColor("#2196F3"))
                        .positiveColor(Color.parseColor("#2196F3"))
                        .callback(new MaterialDialog.SimpleCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                alltasklist_progress.setVisibility(View.VISIBLE);
                                ti.delete(getActivity(), new DeleteListener() {
                                    @Override
                                    public void onSuccess() {
                                        alltasklist_progress.setVisibility(View.GONE);
                                        Tools.ShowToast(getActivity(), "删除成功", 2);
                                        tasks.remove(ti);
                                        adapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {
                                        alltasklist_progress.setVisibility(View.GONE);
                                        Tools.ShowToast(getActivity(),"删除失败,"+s,1);
                                    }
                                });

                            }
                        })


                .build();
                dialog.show();
                return true;
            }
        });

        adapter = new AllTaskListAdapter(getActivity(), tasks);
        adapter.setTaskItemClickListener(this);
        listView.setAdapter(adapter);

        RequestData();
        alltasklist_progress.setVisibility(View.VISIBLE);
        listView.setOnScrollListener(new SwpipeListViewOnScrollListener(swipe_container){
        });

        return mainView;
    }




    public void RequestData(){
        //先请求项目列表
        RequestProjectList();
        swipe_container.setRefreshing(true);
        BmobQuery<Task> query = new BmobQuery<Task>();
        query.addWhereEqualTo("username", SharePreferenceUtil.getUserName(getActivity()));
        //query.order("habitState");
        query.findObjects(getActivity(), new FindListener<Task>(){
            public void onError(int arg0, String arg1) {
                Tools.ShowToast(getActivity(),"数据同步失败,"+arg1,1);
                swipe_container.setRefreshing(false);
            }
            @Override
            public void onSuccess(List<Task> list) {
                //Tools.ShowToast(getActivity(),"数据同步成功",1);
                tasks.clear();
                for(Task it : list){
                    tasks.sort(it);
                }

                for(int i = 0; i < tasks.size(); i++){
                    if(tasks.get(i).getTaskState() == 1){
                        String limitTime = tasks.get(i).getTaskLimitTime();
                        if(isBeforeToday(limitTime) == 1){//已延误
                            tasks.get(i).setTaskState(3);
                            tasks.get(i).update(getActivity());
                        }
                    }


                }

                adapter.notifyDataSetChanged();
                swipe_container.setRefreshing(false);
                alltasklist_progress.setVisibility(View.GONE);

            }
        });


    }

    public void RequestProjectList(){
        BmobQuery<Project> query = new BmobQuery<Project>();
        query.addWhereEqualTo("username", SharePreferenceUtil.getUserName(getActivity()));
        //query.order("habitState");
        query.findObjects(getActivity(), new FindListener<Project>(){
            public void onError(int arg0, String arg1) {
                swipe_container.setRefreshing(false);

            }
            @Override
            public void onSuccess(List<Project> list) {
                projectNameList.clear();
                for(int i = 0; i < list.size(); i++){
                    if(!projectNameList.contains(list.get(i).getProjectName())){
                        projectNameList.add(list.get(i).getProjectName());
                    }
                }
                if(projectNameList.size() == 0){
                    haveProject = false;
                }else {
                    haveProject = true;
                }

            }
        });
    }

    /**
     *
     * @param time
     * @return 1早于今天  2晚于今天
     */
    public int isBeforeToday(String time) {
        java.util.Date nowdate=new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
        Date d;
        try {
            d = sdf.parse(time);
            boolean flag = d.before(nowdate);
            if(flag)
                return 1;
            else
                return 2;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
        return 1;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        swipe_container.setOnRefreshListener(this);
        fab_add = (FloatingActionButton) mainView.findViewById(R.id.fab_add);
        fab_add.setOnClickListener(new MyOnClickListener());
        fab_remove = (FloatingActionButton) mainView.findViewById(R.id.fab_remove);

    }

    @Override
    public void onRefresh() {
        RequestData();
    }

    @Override
    public void HandleClick(View v,int type) {
        if(type == 1)
            completeTask(v);
        else
            updateTask(v);
    }

    public class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.fab_add:
                    if(haveProject)
                        addTask(v);
                    else
                      Tools.ShowToast(getActivity(),"您还没建立过项目哦，请先到项目列表新建项目吧~",1);
                    break;
            }

        }
    }


    // Called when the user completes a task by pressing the checkbox
    public void completeTask(View view) {
        final Task ti = (Task) view.getTag();
        final int index = tasks.indexOf(ti);

        //Check for if we get a null object
        if (index < 0) {
            System.out.println("Weird index?");
            return;
        }


        alltasklist_progress.setVisibility(View.VISIBLE);

        ti.setChecked(!ti.getChecked());

        ti.update(getActivity(),ti.getObjectId(),new UpdateListener() {
            @Override
            public void onSuccess() {
                alltasklist_progress.setVisibility(View.GONE);
                Tools.ShowToast(getActivity(),"同步成功",2);

                //First we fade out the animation, then we fade it in
                final Animation fade_out = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
                final Animation fade_in = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);

                fade_in.setAnimationListener(new Animation.AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationRepeat(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {}
                });

                fade_out.setAnimationListener(new Animation.AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationRepeat(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        //tasks.get(index).toggleChecked();
                        tasks.sort(tasks.get(index));

                        //Shows the second fab depending if we have tasks to remove
                        //WARNING .isVisible() is a hacked method, must re-add if updated
                        if(tasks.hasCompletedTasks() && fab_add.isVisible()){
                            fab_remove.show();
                        } else {
                            fab_remove.hide();
                        }

                        adapter.notifyDataSetChanged();

                        //Sorts the task, then plays the animation for the new one
                        View v = findViewByIndex(tasks.indexOf(ti)
                                , (ListView) mainView.findViewById(R.id.listview));
                        if(v != null){
                            v.startAnimation(fade_in);
                        }
                    }
                });

                //Check != null
                View current = findViewByIndex(tasks.indexOf(ti)
                        , (ListView) mainView.findViewById(R.id.listview));
                if(current != null) {
                    current.startAnimation(fade_out);
                } else {
                    System.out.println("NULL, DO STUFF!");
                }
                adapter.notifyDataSetChanged();
               // SharePreferenceUtil.setTaskList(getActivity(),tasks);
            }

            @Override
            public void onFailure(int i, String s) {
                alltasklist_progress.setVisibility(View.GONE);
                Tools.ShowToast(getActivity(),"同步失败,"+s,1);
                tasks.get(index).toggleChecked();
                tasks.sort(tasks.get(index));
                adapter.notifyDataSetChanged();
                //SharePreferenceUtil.setTaskList(getActivity(),tasks);

            }
        });




    }

    /*This will handle the first time call*/
    public void fadeIn(final View notificationView, final float startY, final float endY
            , final WindowManager.LayoutParams params, final WindowManager mWindowManager){
        final long startTime = System.currentTimeMillis();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            public void run(){
                fadeInHandler(notificationView, startY, endY, params, mWindowManager, startTime);
            }
        }, 16);
    }

    /*This will handle the entire animation*/
    public void fadeInHandler(final View notificationView, final float startY
            , final float endY, final WindowManager.LayoutParams params
            , final WindowManager mWindowManager, final long startTime){
        long timeNow = System.currentTimeMillis();

        float currentY = startY + ((timeNow - startTime)/300.0f) * (endY - startY);

        params.alpha = 0.0f;

        mWindowManager.updateViewLayout(notificationView, params);
        if (timeNow-startTime < 300){
            Handler handler = new Handler();
            handler.postDelayed(new Runnable(){
                public void run(){
                    fadeInHandler(notificationView, startY, endY, params, mWindowManager, startTime);
                }
            }, 16);
        } else {
            mWindowManager.removeView(notificationView);
        }
    }

    View positiveAction;
    Task ti_temp;
    Integer checkedColor;
    String taskTitle;
    String taskDescription;
    String timeString;
    String projectname;

    boolean undoIsVisible = false;

    //Called when the user clicks the remove task FAB button
    public void removeCompletedTasks(View view){

        final int add_margin = ((RelativeLayout.LayoutParams) mainView.findViewById(R.id.fab_add)
                .getLayoutParams()).bottomMargin;
        final int remove_margin = ((RelativeLayout.LayoutParams) mainView.findViewById(R.id.fab_remove)
                .getLayoutParams()).bottomMargin;

        //Removes all completed tasks and notifies the view
        finished = tasks.getCompletedTasks();

        final Animation fade_out = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
        fade_out.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                tasks.removeCompletedTasks();
                adapter.notifyDataSetChanged();
            }
        });

        for(Task ti : finished){
            View v = findViewByIndex(tasks.indexOf(ti), (ListView) mainView.findViewById(R.id.listview));

            if(v != null){
                v.startAnimation(fade_out);
            } else {
                tasks.remove(ti);
                adapter.notifyDataSetChanged();
            }
        }

        fab_remove.hide();

        //When the toast appears, we need to move the fabs up
        final Animation fab_in = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_in);

        fab_in.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //The animation only moves the view, so we need to change the touch target too
                ((RelativeLayout.LayoutParams) mainView.findViewById(R.id.fab_add)
                        .getLayoutParams()).bottomMargin = (int) (remove_margin * 0.6f);
                ((RelativeLayout.LayoutParams) mainView.findViewById(R.id.fab_remove)
                        .getLayoutParams()).bottomMargin = remove_margin - add_margin
                        + (int) (remove_margin * 0.6f);

                fab_add.requestLayout();
                fab_remove.requestLayout();
            }
        });

        final Animation fab_out = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_out);

        fab_out.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                ((RelativeLayout.LayoutParams) mainView.findViewById(R.id.fab_add)
                        .getLayoutParams()).bottomMargin = add_margin;
                ((RelativeLayout.LayoutParams) mainView.findViewById(R.id.fab_remove)
                        .getLayoutParams()).bottomMargin = remove_margin;

                fab_add.requestLayout();
                fab_remove.requestLayout();
            }
        });

        final Context ct = getActivity();

        //Create a snackbar, when the undo button is pressed: re-add all removed tasks
        UndoBarController.UndoBar ub = new UndoBarController.UndoBar(getActivity()).message("Removed completed tasks").listener(new UndoBarController.AdvancedUndoListener() {

            public void onUndo(Parcelable p) {

                tasks.insert(finished);
                adapter.notifyDataSetChanged();

                //Fade in the animation fancily when added
                //DOES NOT WORK CORRECTLY
                final Animation fade_in = AnimationUtils.loadAnimation(ct, R.anim.fade_in);

                for(Task li : finished) {
                    View v = findViewByIndex(tasks.indexOf(li)
                            , (ListView) mainView.findViewById(R.id.listview));

                    if (v != null) {
                        v.startAnimation(fade_in);
                    }
                }

                //We assume since the tasks will be restored as checked, we can reintroduce the remove FAB
                fab_remove.show();
                mainView.findViewById(R.id.fab_add).startAnimation(fab_out);
                mainView.findViewById(R.id.fab_remove).startAnimation(fab_out);
                undoIsVisible = false;
            }

            public void onHide(Parcelable p) {
                mainView.findViewById(R.id.fab_add).startAnimation(fab_out);
                mainView.findViewById(R.id.fab_remove).startAnimation(fab_out);
                undoIsVisible = false;
            }

            public void onClear(Parcelable[] p) {
            }

        }).noicon(true);

        if(!undoIsVisible) {

            ub.show();

            //The task is removed, so the fabs must go up
            mainView.findViewById(R.id.fab_add).startAnimation(fab_in);
            mainView.findViewById(R.id.fab_remove).startAnimation(fab_in);
            undoIsVisible = true;
        }

    }

    // Called when the user clicks the add task FAB button
    public void  addTask(View view) {

        EditText taskTitleText;
        final EditText taskDescription_et;
        final DatePicker datePicker ;
        Spinner dialog_spinner;
        RadioGroup taskPriority;

        taskTitle = "";
        taskDescription = "";
        checkedColor = 2;
        projectname = "";
        final Context ct = getActivity();

        //Creates a dialog for adding a new task
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title("增加待办任务")
                .customView(R.layout.task_dialog)
                .negativeText("取消")
                .positiveText("确定")
                .negativeColor(Color.parseColor("#2196F3"))
                .positiveColor(Color.parseColor("#2196F3"))
                .callback(new MaterialDialog.SimpleCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        //Creating a new Task for the task
                        final Task li = new Task();
                        li.setProjectName(projectname);
                        li.setColor(checkedColor);
                        li.setChecked(false);
                        li.setTaskState(1);
                        li.setUsername(SharePreferenceUtil.getUserName(getActivity()));
                        li.setTaskLimitTime(timeString);
                        li.setTaskDescription(taskDescription);
                        li.setTaskName(taskTitle);
                        //Tools.ShowToast(getActivity(),timeString,2);

                        alltasklist_progress.setVisibility(View.VISIBLE);
                        li.save(getActivity(),new SaveListener() {
                            @Override
                            public void onSuccess() {
                                alltasklist_progress.setVisibility(View.GONE);
                                Tools.ShowToast(getActivity(),"添加任务成功"+li.getObjectId(),2);
                                tasks.insert(li);
                                adapter.notifyDataSetChanged();
                                //SharePreferenceUtil.setTaskList(getActivity(),tasks);
                                View v = findViewByIndex(tasks.indexOf(li)
                                        , (ListView) mainView.findViewById(R.id.listview));
                                final Animation fade_in = AnimationUtils.loadAnimation(ct, R.anim.fade_in);
                                if(v != null) {
                                    v.startAnimation(fade_in);
                                }

                            }

                            @Override
                            public void onFailure(int i, String s) {
                                alltasklist_progress.setVisibility(View.GONE);
                                Tools.ShowToast(getActivity(),"添加任务失败，"+s,1);
                            }
                        });


                    }
                })
                .build();

        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        taskTitleText = (EditText) dialog.getCustomView().findViewById(R.id.task_title);
        datePicker = (DatePicker) dialog.getCustomView().findViewById(R.id.datePicker);
        taskDescription_et = (EditText) dialog.getCustomView().findViewById(R.id.task_description_et);
        dialog_spinner = (Spinner) dialog.getCustomView().findViewById(R.id.dialog_spinner);
        dialog_spinner.setContentDescription("所属项目");
        //将可选内容与ArrayAdapter连接起来
        ArrayAdapter adapter =  new ArrayAdapter<String>(getActivity()
                ,android.R.layout.simple_spinner_item, projectNameList);
        //设置下拉列表的风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //将adapter 添加到spinner中
        dialog_spinner.setAdapter(adapter);

        //添加事件Spinner事件监听
        dialog_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                projectname = projectNameList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //限制可选择的最早日期
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        datePicker.setMinDate(cal.getTimeInMillis());

        datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)
                , cal.get(Calendar.DAY_OF_MONTH),new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String month = "";
                String day = "";
                monthOfYear = monthOfYear + 1;
                if(monthOfYear < 10){
                    month = "0"+monthOfYear;
                }else { month = monthOfYear+""; }

                if(dayOfMonth < 10){
                    day = "0"+dayOfMonth;
                }else { day = dayOfMonth+""; }
                timeString = year+month+day;
            }
        });


        datePicker.setCalendarViewShown(false);
        Date date=new Date();
        DateFormat format = new SimpleDateFormat("yyyyMMdd");
        timeString =format.format(date);

        //If we name a task and it has a priority, enable positive button
        taskTitleText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                taskTitle = s.toString();
                positiveAction.setEnabled(taskTitle.trim().length() > 0
                        && taskDescription_et.getText().toString().length() > 0
                        && checkedColor != null);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //If we name a task and it has a priority, enable positive button
        taskDescription_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                taskDescription = s.toString();
                positiveAction.setEnabled(taskTitle.trim().length() > 0
                        && taskDescription_et.getText().toString().length() > 0
                        && checkedColor != null);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //If we set a priority and the task has a name, enable positive button
        taskPriority = (RadioGroup) dialog.getCustomView().findViewById(R.id.task_priority);
        taskPriority.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup taskPriority, int checkedId) {

                RadioButton checkedRadioButton = (RadioButton) taskPriority.findViewById(checkedId);

                if (checkedRadioButton.isChecked()) {

                    //We save the color value of the radio button
                    if(checkedId == R.id.task_priority_red){
                        checkedColor = 3;
                    } else if(checkedId == R.id.task_priority_blue){
                        checkedColor = 2;
                    } else if(checkedId == R.id.task_priority_green){
                        checkedColor = 1;
                    } else {
                        checkedColor = null;
                    }


                    positiveAction.setEnabled(taskTitle.trim().length() > 0 && checkedColor != null);
                }
            }
        });

        //We want to bring up the keyboard for the title
        //dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();

        //Lastly, default value for positive action should be false
        positiveAction.setEnabled(false);
    }

    // Called when the user clicks the add task FAB button
    public void updateTask(View view) {

        EditText taskTitleText;
        RadioGroup taskPriority;
        final EditText taskDescription_et;
        final DatePicker datePicker ;
        Spinner dialog_spinner;

        ti_temp = (Task) view.getTag();
        int index = tasks.indexOf(ti_temp);

        taskTitle = ti_temp.getTaskName();
        taskDescription = ti_temp.getTaskDescription();
        timeString = ti_temp.getTaskLimitTime();

        checkedColor = ti_temp.getColor();
        final Integer originalColor = ti_temp.getColor();

        //Check for if we get a null object
        if(index < 0){
            System.out.println("Weird index?");
            return;
        }

        //Creates a dialog for adding a new task
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title("任务详情")
                .customView(R.layout.task_dialog)
                .negativeText("取消")
                .positiveText("保存")
                .negativeColor(Color.parseColor("#2196F3"))
                .positiveColor(Color.parseColor("#2196F3"))
                .callback(new MaterialDialog.SimpleCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        ti_temp.setTaskName(taskTitle);
                        ti_temp.setTaskDescription(taskDescription);
                        ti_temp.setTaskLimitTime(timeString);
                        ti_temp.setColor(checkedColor);
                        alltasklist_progress.setVisibility(View.VISIBLE);
                        ti_temp.update(getActivity(),ti_temp.getObjectId(), new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                //If we change the priority, we need to sort it again
                                alltasklist_progress.setVisibility(View.GONE);
                                Tools.ShowToast(getActivity(),"保存成功!",2);
                                if(originalColor != checkedColor){
                                    tasks.sort(ti_temp);
                                }

                                adapter.notifyDataSetChanged();
                                //SharePreferenceUtil.setTaskList(getActivity(),tasks);
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                alltasklist_progress.setVisibility(View.GONE);
                                Tools.ShowToast(getActivity(),"保存失败,"+s,1);

                            }
                        });


                    }
                })
                .build();

        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        positiveAction.setEnabled(true);

        taskTitleText = (EditText) dialog.getCustomView().findViewById(R.id.task_title);
        taskTitleText.append(taskTitle);
        datePicker = (DatePicker) dialog.getCustomView().findViewById(R.id.datePicker);
        taskDescription_et = (EditText) dialog.getCustomView().findViewById(R.id.task_description_et);
        taskDescription_et.setText(taskDescription);
        dialog_spinner = (Spinner) dialog.getCustomView().findViewById(R.id.dialog_spinner);
        //将可选内容与ArrayAdapter连接起来
        ArrayAdapter adapter =  new ArrayAdapter<String>(getActivity()
                ,android.R.layout.simple_spinner_item, projectNameList);
        dialog_spinner.setAdapter(adapter);

        int Defaultindex = 0;
        for(int i = 0 ;i < projectNameList.size(); i++){
            if(projectNameList.get(i).equals(ti_temp.getProjectName())){
                Defaultindex = i;
            }
        }
        dialog_spinner.setSelection(Defaultindex,true);


        //设置下拉列表的风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //将adapter 添加到spinner中

        dialog_spinner.setEnabled(false);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        datePicker.setMinDate(cal.getTimeInMillis());

        String yearString = timeString.substring(0,4);
        String monthString = timeString.substring(4,6);
        String dayString = timeString.substring(6,8);
        //Tools.ShowToast(getActivity(),timeString+" "+yearString+" "+monthString+" "+dayString,1);
        datePicker.init(Integer.parseInt(yearString), (Integer.parseInt(monthString) - 1)
                , Integer.parseInt(dayString), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String month = "";
                String day = "";
                monthOfYear = monthOfYear + 1;
                if (monthOfYear < 10) {
                    month = "0" + monthOfYear;
                } else {
                    month = monthOfYear + "";
                }

                if (dayOfMonth < 10) {
                    day = "0" + dayOfMonth;
                } else {
                    day = dayOfMonth + "";
                }
                timeString = year + month + day;
            }
        });


        datePicker.setCalendarViewShown(false);
        Date date=new Date();
        DateFormat format=new SimpleDateFormat("yyyyMMdd");
        timeString = format.format(date);

        //If we name a task and it has a priority, enable positive button
        taskTitleText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                taskTitle = s.toString();
                positiveAction.setEnabled(taskTitle.trim().length() > 0
                        && taskDescription_et.getText().toString().length() > 0
                        && checkedColor != null);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //If we name a task and it has a priority, enable positive button
        taskDescription_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                taskDescription = s.toString();
                positiveAction.setEnabled(taskTitle.trim().length() > 0
                        && taskDescription_et.getText().toString().length() > 0
                        && checkedColor != null);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //If we set a priority and the task has a name, enable positive button
        taskPriority = (RadioGroup) dialog.getCustomView().findViewById(R.id.task_priority);

        //Set the color
        if(checkedColor == 3) {
            taskPriority.check(R.id.task_priority_red);
        } else if(checkedColor == 2) {
            taskPriority.check(R.id.task_priority_blue);
        } else if(checkedColor == 1) {
            taskPriority.check(R.id.task_priority_green);
        }

        taskPriority.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup taskPriority, int checkedId) {

                RadioButton checkedRadioButton = (RadioButton) taskPriority.findViewById(checkedId);

                if (checkedRadioButton.isChecked()) {

                    //We save the color value of the radio button
                    if(checkedId == R.id.task_priority_red){
                        checkedColor = 3;
                    } else if(checkedId == R.id.task_priority_blue){
                        checkedColor = 2;
                    } else if(checkedId == R.id.task_priority_green){
                        checkedColor = 1;
                    } else {
                        checkedColor = null;
                    }

                    positiveAction.setEnabled(taskTitle.trim().length() > 0 && checkedColor != null);
                }
            }
        });

        //We want to bring up the keyboard for the title
        //dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();

        //Lastly, default value for positive action should be false
        positiveAction.setEnabled(true);
    }

    //We should save our instance here, currently we do nothing
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
    }



    public View findViewByIndex(int index, ListView listView) {

        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (index < firstListItemPosition || index > lastListItemPosition) {

            return null;
            //return listView.getAdapter().getView(index, null, listView);

            //return null;
        } else {
            final int childIndex = index - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    //Clones a task view, it's inflated and filled with the same values
//    public View cloneView(View view){
//        View result = LayoutInflater.from(getActivity()).inflate(R.layout.listitem, null);
//
//        //Sets the text to be the same
//        TextView newTextView = (TextView) result.findViewById(R.id.listtext);
//        TextView oldTextView = (TextView) view.findViewById(R.id.listtext);
//        newTextView.setText(oldTextView.getText());
//
//        //Sets the checkbox to be the same
//        ToggleButton newToggleButton = (ToggleButton) result.findViewById(R.id.listbutton);
//        ToggleButton oldToggleButton = (ToggleButton) view.findViewById(R.id.listbutton);
//
//        newToggleButton.setCompoundDrawables(oldToggleButton.getCompoundDrawables()[0], null, null, null);
//        newToggleButton.setCompoundDrawablePadding(oldToggleButton.getCompoundDrawablePadding());
//
//        return result;
//    }

    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }



    /** 由于Listview与下拉刷新的Scroll事件冲突, 使用这个ScrollListener可以避免Listview滑动异常 */
    public static class SwpipeListViewOnScrollListener implements AbsListView.OnScrollListener {

        private SwipeRefreshLayout mSwipeView;
        private AbsListView.OnScrollListener mOnScrollListener;

        public SwpipeListViewOnScrollListener(SwipeRefreshLayout swipeView) {
            mSwipeView = swipeView;
        }

        public SwpipeListViewOnScrollListener(SwipeRefreshLayout swipeView,
                                              AbsListView.OnScrollListener onScrollListener) {
            mSwipeView = swipeView;
            mOnScrollListener = onScrollListener;
        }

        @Override
        public void onScrollStateChanged(AbsListView absListView, int i) {
        }

        @Override
        public void onScroll(AbsListView absListView, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            View firstView = absListView.getChildAt(firstVisibleItem);

            // 当firstVisibleItem是第0位。如果firstView==null说明列表为空，需要刷新;或者top==0说明已经到达列表顶部, 也需要刷新
            if (firstVisibleItem == 0 && (firstView == null || firstView.getTop() == 0)) {
                mSwipeView.setEnabled(true);
            } else {
                mSwipeView.setEnabled(false);
            }
            if (null != mOnScrollListener) {
                mOnScrollListener.onScroll(absListView, firstVisibleItem,
                        visibleItemCount, totalItemCount);
            }
        }
    }





}
