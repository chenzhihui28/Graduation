package com.fragments.TaskManagement;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.Model.Project;
import com.Model.Task;
import com.Utils.SharePreferenceUtil;
import com.Utils.Tools;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import huti.material.R;

public class StatisticsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private View view;
    private SwipeRefreshLayout swipe_container;
    private List<Task> taskList = new ArrayList<Task>();
    private List<Project> projectList = new ArrayList<Project>();
    private List<String> projectNameList = new ArrayList<String>();
    private ProgressBar statistics_progress;
    private ListView statistics_listview;
    private boolean haveProject = false;

    public static StatisticsFragment newInstance(String param1, String param2) {
        StatisticsFragment fragment = new StatisticsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public StatisticsFragment() {
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

        view = inflater.inflate(R.layout.fragment_statistics, container, false);
        swipe_container = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipe_container.setOnRefreshListener(this);
        statistics_progress = (ProgressBar) view.findViewById(R.id.statistics_progress);
        statistics_progress.setVisibility(View.VISIBLE);
        statistics_listview = (ListView) view.findViewById(R.id.statistics_listview);
        RequestData();
        return view;
    }

    public void RequestData(){
        //先请求项目列表
        RequestProjectList();
        swipe_container.setRefreshing(true);
        BmobQuery<Task> query = new BmobQuery<Task>();
        query.addWhereEqualTo("username", SharePreferenceUtil.getUserName(getActivity()));
        query.findObjects(getActivity(), new FindListener<Task>(){
            public void onError(int arg0, String arg1) {
                Tools.ShowToast(getActivity(), "数据同步失败," + arg1, 1);
                swipe_container.setRefreshing(false);
            }
            @Override
            public void onSuccess(List<Task> list) {
                taskList.clear();
                taskList = list;

                swipe_container.setRefreshing(false);
                statistics_progress.setVisibility(View.GONE);

            }
        });


    }

    public void RequestProjectList(){
        BmobQuery<Project> query = new BmobQuery<Project>();
        query.addWhereEqualTo("username", SharePreferenceUtil.getUserName(getActivity()));
        query.findObjects(getActivity(), new FindListener<Project>(){
            public void onError(int arg0, String arg1) {
                swipe_container.setRefreshing(false);
            }
            @Override
            public void onSuccess(List<Project> list) {
                projectList.clear();
                projectList = list;

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

    @Override
    public void onRefresh() {
        RequestData();
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
