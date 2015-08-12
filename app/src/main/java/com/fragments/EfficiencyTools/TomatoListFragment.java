package com.fragments.EfficiencyTools;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.Model.Project;
import com.Model.Tomato;
import com.MyViews.CircularSeekBar;
import com.Utils.SharePreferenceUtil;
import com.Utils.Tools;
import com.adapter.TomatosAdapter;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import huti.material.R;


public class TomatoListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private View view;
    private OnFragmentInteractionListener mListener;
    private TextView time_tv,nodata_tv;
    private ListView tomato_listview;
    CountDownTimer countDownTimer;
    CircularSeekBar seekbar;
    private SwipeRefreshLayout swipe_container;
    int progressTime = 0;
    int progress = 0;
    ProgressBar tomatolist_progress;
    TomatosAdapter tomatosAdapter;
    private List<Tomato> dataList = new ArrayList<Tomato>();
    String startTimeString = "";
    String endTimeString = "";

    public static TomatoListFragment newInstance(String param1, String param2) {
        TomatoListFragment fragment = new TomatoListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public TomatoListFragment() {
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
        view = inflater.inflate(R.layout.tomato_fragment, container, false);

        initView();
        tomatolist_progress.setVisibility(View.VISIBLE);
        initData();

        return view;
    }

    public void initView(){
        seekbar = (CircularSeekBar) view.findViewById(R.id.circularSeekBar1);

        seekbar.getProgress();
        time_tv = (TextView) view.findViewById(R.id.time_tv);
        nodata_tv = (TextView) view.findViewById(R.id.nodata_tv);
        tomato_listview = (ListView) view.findViewById(R.id.tomato_listview);
        tomatolist_progress = (ProgressBar) view.findViewById(R.id.tomatolist_progress);
        tomatolist_progress.setVisibility(View.GONE);

        swipe_container = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh);
        swipe_container.setOnRefreshListener(this);

        countDownTimer = new CountDownTimer(100*1000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //progressTime += 1;
                //if(progressTime == 15){
                 //   progressTime = 0;
                    progress += 1;
                    seekbar.setProgress(progress);
                //}
                long time = millisUntilFinished/1000;
                String timeString = secToTime((int)time);
                time_tv.setText(timeString);
            }

            @Override
            public void onFinish() {
                DateFormat format=new SimpleDateFormat("HH:mm:ss");
                Date date=new Date();
                endTimeString = format.format(date);
                time_tv.setText("开始");
                addTomato();

            }
        };

        time_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressTime = 0;
                progress = 0;
                countDownTimer.start();
                DateFormat format=new SimpleDateFormat("HH:mm:ss");
                Date date=new Date();
                startTimeString = format.format(date);
            }
        });
        swipe_container.setOnRefreshListener(this);
        tomatosAdapter = new TomatosAdapter(dataList);
        tomato_listview.setAdapter(tomatosAdapter);


    }

    public void initData(){
        BmobQuery<Tomato> query = new BmobQuery<Tomato>();
        query.addWhereEqualTo("username", SharePreferenceUtil.getUserName(getActivity()));
        query.order("TomatoDate");
        query.findObjects(getActivity(), new FindListener<Tomato>() {
            @Override
            public void onSuccess(List<Tomato> tomatos) {
                dataList.clear();
                for(Tomato temp : tomatos){
                    dataList.add(temp);
                }
                tomatosAdapter.notifyDataSetChanged();

                    nodata_tv.setVisibility(View.GONE);

            }

            @Override
            public void onError(int i, String s) {

            }
        });
        swipe_container.setRefreshing(false);
        tomatolist_progress.setVisibility(View.GONE);

}


    @Override
    public void onRefresh() {
        swipe_container.setRefreshing(true);
        initData();

    }


    String tomatoDescription = "";
    View positiveAction;
    EditText tomatoDescription_et;
    public void  addTomato() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
        builder.title("备注番茄");
        builder.customView(R.layout.tomato_dialog);
        builder.negativeText("取消");
        builder.positiveText("确定");
        builder.negativeColor(Color.parseColor("#2196F3"));
        builder.positiveColor(Color.parseColor("#2196F3"));

        builder.callback(new MaterialDialog.SimpleCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                final Tomato tomato = new Tomato();
                tomato.setTomatoDescription(tomatoDescription);
                tomato.setUsername(SharePreferenceUtil.getUserName(getActivity()));
                DateFormat format=new SimpleDateFormat("yyyyMMdd");
                Date date=new Date();
                String time=format.format(date);
                tomato.setTomatoDate(time);
                tomato.setTomatoTimeRange(startTimeString+"-"+endTimeString);
                tomatolist_progress.setVisibility(View.VISIBLE);
                tomato.save(getActivity(), new SaveListener() {
                    @Override
                    public void onSuccess() {
                        tomatolist_progress.setVisibility(View.GONE);
                        dataList.add(tomato);
                        tomatosAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        tomatolist_progress.setVisibility(View.GONE);
                        Tools.ShowToast(getActivity(), "同步到云端失败," + s, 2);
                    }
                });

            }

        });

        MaterialDialog dialog = builder.build();
        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        tomatoDescription_et = (EditText) dialog.getCustomView().findViewById(R.id.tomato_description_et);


        tomatoDescription_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tomatoDescription = s.toString();

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
        positiveAction.setEnabled(true);
    }


    // a integer to xx:xx:xx
    public static String secToTime(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
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
