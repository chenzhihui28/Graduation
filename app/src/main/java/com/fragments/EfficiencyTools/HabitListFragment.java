package com.fragments.EfficiencyTools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.Model.Habit;
import com.Model.Project;
import com.Utils.SharePreferenceUtil;
import com.Utils.SingleDataUtils;
import com.Utils.Tools;
import com.adapter.HabitAdapter;
import com.adapter.ProjectsAdapter;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import huti.material.R;


public class HabitListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1,mParam2;
    private OnFragmentInteractionListener mListener;
    View myview;
    private TextView emptyprojecthint;
    private ListView habitlistview;
    private HabitAdapter habitAdapter;
    private ProgressBar habitlist_progress;
    private com.melnykov.fab.FloatingActionButton add_habit_button;
    private SwipeRefreshLayout swipe_container;
    private ArrayList<Habit> dataList;
    public static HabitListFragment newInstance(String param1, String param2) {
        HabitListFragment fragment = new HabitListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public HabitListFragment() {
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
        myview = inflater.inflate(R.layout.habit_fragment, container, false);

        initView();
        initData();
        RequestData();

        return myview;
    }

    public void initView(){
        emptyprojecthint = (TextView)myview.findViewById(R.id.emptyhinttextview);
        emptyprojecthint.setVisibility(View.GONE);
        habitlist_progress = (ProgressBar) myview.findViewById(R.id.habitlist_progress);
        habitlist_progress.setVisibility(View.VISIBLE);
        habitlistview = (ListView)myview.findViewById(R.id.noteslistview);
        add_habit_button = (com.melnykov.fab.FloatingActionButton)myview.findViewById(R.id.add_note_button);
        swipe_container = (SwipeRefreshLayout)myview.findViewById(R.id.swipe_refresh_3);
        swipe_container.setOnRefreshListener(this);
        add_habit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProject(v);
            }
        });
        habitlistview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                updateProject(view,position);
                return true;
            }
        });
        habitlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Habit habit = dataList.get(position);
                Intent intent = new Intent(getActivity(),HabitDetailActivity.class);
                SingleDataUtils.getInstance().setHabit(habit);
                startActivity(intent);

            }
        });


    }

    public void initData(){
        dataList = new ArrayList<Habit>();
        habitAdapter = new HabitAdapter(dataList,getActivity());
        habitlistview.setAdapter(habitAdapter);

    }

    public void RequestData(){
        swipe_container.setRefreshing(true);
        BmobQuery<Habit> query = new BmobQuery<Habit>();
        query.addWhereEqualTo("username", SharePreferenceUtil.getUserName(getActivity()));
        //query.order("habitState");
        query.findObjects(getActivity(), new FindListener<Habit>(){
            public void onError(int arg0, String arg1) {
                Tools.ShowToast(getActivity(),"数据同步失败,"+arg1,1);
                swipe_container.setRefreshing(false);

            }
            @Override
            public void onSuccess(List<Habit> list) {
                dataList.clear();
                for(Habit item : list){
                    dataList.add(item);
                }
                //判断状态
                for(int i = 0; i < dataList.size(); i++){
                    Habit temp = dataList.get(i);
                    if(temp.getHabitState() == 1){//正在养成
                        if(temp.getRecordTime() >= temp.getHabitCost()){//状态应该改为已养成
                            temp.setHabitState(2);
                            temp.update(getActivity());
                        }
                        String LastRecordTime = temp.getLastRecordTime();
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        String now = df.format(new Date());
                        Long days = getDistanceDays(now,LastRecordTime);
                        if(days >= 5){
                            temp.setHabitState(3);
                            temp.update(getActivity());
                        }


                    }

                }

                habitAdapter.notifyDataSetChanged();
                swipe_container.setRefreshing(false);
                habitlist_progress.setVisibility(View.GONE);

            }
        });

    }

    @Override
    public void onRefresh() {
        swipe_container.setRefreshing(false);
        RequestData();
    }


    View positiveAction;
    String HabitName = "";
    int HabitCost = 0;
    // Called when the user clicks the add task FAB button
    public void  addProject(View view) {
        EditText habit_name_et;
        Spinner habit_cost_spinner;
        final Context ct = getActivity();
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
        builder.title("增加习惯养成计划");
        builder.customView(R.layout.habit_dialog);
        builder.negativeText("取消");
        builder.positiveText("确定");
        builder.negativeColor(Color.parseColor("#2196F3"));
        builder.positiveColor(Color.parseColor("#2196F3"));

        builder.callback(new MaterialDialog.SimpleCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                final Habit habit = new Habit();
                habit.setHabitName(HabitName);
                habit.setUsername(SharePreferenceUtil.getUserName(getActivity()));
                if(HabitCost == 0){
                    habit.setHabitCost(15);
                }else if(HabitCost == 1){
                    habit.setHabitCost(30);
                }else if(HabitCost == 2){
                    habit.setHabitCost(60);
                }else if(HabitCost == 3){
                    habit.setHabitCost(180);
                }else if(HabitCost == 4){
                    habit.setHabitCost(360);
                }
                habit.setHabitState(1);
                habit.setRecordTime(0);//打卡次数
                habit.setLastRecordTime("");

                habitlist_progress.setVisibility(View.VISIBLE);
                habit.save(getActivity(), new SaveListener() {
                    @Override
                    public void onSuccess() {
                        habitlist_progress.setVisibility(View.GONE);
                        Tools.ShowToast(getActivity(), "计划创建成功", 2);
                        dataList.add(habit);
                        habitAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        habitlist_progress.setVisibility(View.GONE);
                        Tools.ShowToast(getActivity(), "计划创建失败," + s, 2);
                    }
                });

            }

        });

        MaterialDialog dialog = builder.build();
        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        habit_name_et = (EditText) dialog.getCustomView().findViewById(R.id.habit_name_et);
        habit_cost_spinner = (Spinner) dialog.getCustomView().findViewById(R.id.habit_cost_spinner);
        ArrayList<Integer> planLengthList = new ArrayList<Integer>();
        planLengthList.add(15);planLengthList.add(30);
        planLengthList.add(90);planLengthList.add(180);
        planLengthList.add(360);
        //将可选内容与ArrayAdapter连接起来
        ArrayAdapter adapter =  new ArrayAdapter<Integer>(getActivity()
                ,android.R.layout.simple_spinner_item, planLengthList);
        habit_cost_spinner.setAdapter(adapter);
        habit_cost_spinner.setSelection(0);
        habit_cost_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                HabitCost = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //设置下拉列表的风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        habit_name_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                HabitName = s.toString();
                positiveAction.setEnabled(HabitName.trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
        positiveAction.setEnabled(false);
    }

    public void  updateProject(View view, final int position) {
        EditText habit_name_et;
        Spinner habit_cost_spinner;
        final Context ct = getActivity();
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
        builder.title("编辑习惯");
        builder.customView(R.layout.habit_dialog);
        builder.negativeText("取消");
        builder.positiveText("保存");
        builder.neutralText("删除");
        builder.negativeColor(Color.parseColor("#2196F3"));
        builder.positiveColor(Color.parseColor("#2196F3"));
        builder.neutralColor(Color.parseColor("#F33446"));

        builder.callback(new MaterialDialog.FullCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                final Habit habit = new Habit();
                habit.setObjectId(dataList.get(position).getObjectId());
                habit.setUsername(SharePreferenceUtil.getUserName(getActivity()));
                habit.setLastRecordTime(dataList.get(position).getLastRecordTime());
                habit.setHabitState(dataList.get(position).getHabitState());
                habit.setHabitCost(dataList.get(position).getHabitCost());
                habit.setRecordTime(dataList.get(position).getRecordTime());
                habit.setHabitName(HabitName);
                habitlist_progress.setVisibility(View.VISIBLE);
                habit.update(getActivity(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        habitlist_progress.setVisibility(View.GONE);
                        Tools.ShowToast(getActivity(), "保存成功", 2);
                        dataList.set(position, habit);
                        habitAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        habitlist_progress.setVisibility(View.GONE);
                        Tools.ShowToast(getActivity(), "保存失败," + s, 2);
                    }
                });

            }

            @Override
            public void onNegative(MaterialDialog dialog) {

            }

            @Override
            public void onNeutral(MaterialDialog dialog) {
                Habit temp = dataList.get(position);
                habitlist_progress.setVisibility(View.VISIBLE);
                temp.delete(getActivity(), new DeleteListener() {
                    @Override
                    public void onSuccess() {
                        habitlist_progress.setVisibility(View.GONE);
                        Tools.ShowToast(getActivity(), "删除成功", 2);
                        dataList.remove(position);
                        habitAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        habitlist_progress.setVisibility(View.GONE);
                        Tools.ShowToast(getActivity(), "删除失败," + s, 2);
                    }
                });
            }
        });

        MaterialDialog dialog = builder.build();
        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        habit_name_et = (EditText) dialog.getCustomView().findViewById(R.id.habit_name_et);
        habit_name_et.setText(dataList.get(position).getHabitName());
        HabitName = dataList.get(position).getHabitName();

        habit_cost_spinner = (Spinner) dialog.getCustomView().findViewById(R.id.habit_cost_spinner);
        //将可选内容与ArrayAdapter连接起来
        ArrayList<Integer> planLengthList = new ArrayList<Integer>();
        planLengthList.add(15);planLengthList.add(30);
        planLengthList.add(90);planLengthList.add(180);
        planLengthList.add(360);
        ArrayAdapter adapter =  new ArrayAdapter<Integer>(getActivity()
                ,android.R.layout.simple_spinner_item, planLengthList);
        habit_cost_spinner.setAdapter(adapter);
        int selection = 0;
        if(dataList.get(position).getHabitCost() == 30){
            selection = 1;
        }else if(dataList.get(position).getHabitCost() == 60){
            selection = 2;
        }else if(dataList.get(position).getHabitCost() == 180){
            selection = 3;
        }else if(dataList.get(position).getHabitCost() == 360){
            selection = 4;
        }

        habit_cost_spinner.setSelection(selection,true);
        //设置下拉列表的风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //将adapter 添加到spinner中
        habit_cost_spinner.setEnabled(false);

        habit_name_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                HabitName = s.toString();
                positiveAction.setEnabled(HabitName.trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
        positiveAction.setEnabled(true);
    }



    /**
     * 两个时间之间相差距离多少天
     * @return 相差天数
     */
    public static long getDistanceDays(String str1, String str2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date one;
        Date two;
        long days=0;
        try {
            one = df.parse(str1);
            two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff ;
            if(time1<time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            days = diff / (1000 * 60 * 60 * 24);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return days;
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

}
