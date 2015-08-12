package com.fragments.TaskManagement;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.Model.Project;
import com.Utils.SharePreferenceUtil;
import com.Utils.Tools;
import com.adapter.ProjectsAdapter;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import huti.material.R;


public class ProjectListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1,mParam2;
    private OnFragmentInteractionListener mListener;
    View myview;
    private View view;
    private TextView emptyprojecthint;
    private ListView projectslistview;
    private ProjectsAdapter projectsAdapter;
    private ProgressBar projectlist_progress;
    private com.melnykov.fab.FloatingActionButton add_project_button;
    private SwipeRefreshLayout swipe_container;
    private ArrayList<Project> dataList;
    public static ProjectListFragment newInstance(String param1, String param2) {
        ProjectListFragment fragment = new ProjectListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ProjectListFragment() {
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
        myview = inflater.inflate(R.layout.project_fragment, container, false);
        initView();
        initData();
        RequestData();

        return myview;
    }

    public void initView(){
        emptyprojecthint = (TextView)myview.findViewById(R.id.emptyhinttextview);
        emptyprojecthint.setVisibility(View.GONE);
        projectlist_progress = (ProgressBar) myview.findViewById(R.id.projectlist_progress);
        projectlist_progress.setVisibility(View.VISIBLE);
        projectslistview = (ListView)myview.findViewById(R.id.noteslistview);
        add_project_button = (com.melnykov.fab.FloatingActionButton)myview.findViewById(R.id.add_note_button);
        swipe_container = (SwipeRefreshLayout)myview.findViewById(R.id.swipe_refresh_3);
        swipe_container.setOnRefreshListener(this);
        add_project_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProject(v);
            }
        });
        projectslistview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                updateProject(view,position);
                return true;
            }
        });
        projectslistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(),TaskListActivity.class);
                intent.putExtra("ProjectName",dataList.get(position).getProjectName());
                startActivity(intent);

            }
        });


    }

    public void initData(){
        dataList = new ArrayList<Project>();
        projectsAdapter = new ProjectsAdapter(dataList);
        projectslistview.setAdapter(projectsAdapter);

    }

    public void RequestData(){
        swipe_container.setRefreshing(true);
        BmobQuery<Project> query = new BmobQuery<Project>();
        query.addWhereEqualTo("username", SharePreferenceUtil.getUserName(getActivity()));
        //query.order("habitState");
        query.findObjects(getActivity(), new FindListener<Project>(){
            public void onError(int arg0, String arg1) {
                Tools.ShowToast(getActivity(),"数据同步失败,"+arg1,1);
                swipe_container.setRefreshing(false);

            }
            @Override
            public void onSuccess(List<Project> list) {
                //Tools.ShowToast(getActivity(),"数据同步成功",1);
                dataList.clear();
                for(Project item : list){
                    dataList.add(item);
                }
                projectsAdapter.notifyDataSetChanged();
                swipe_container.setRefreshing(false);
                projectlist_progress.setVisibility(View.GONE);

            }
        });

    }

    @Override
    public void onRefresh() {
        swipe_container.setRefreshing(false);
        RequestData();
    }


    View positiveAction;
    String projectTitle = "";
    String projectDescription = "";
    // Called when the user clicks the add task FAB button
    public void  addProject(View view) {
        EditText projectTitle_et;
        EditText projectDescription_et;
        final Context ct = getActivity();

        //Creates a dialog for adding a new task
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
        builder.title("增加项目");
        builder.customView(R.layout.project_dialog);
        builder.negativeText("取消");
        builder.positiveText("确定");
        builder.negativeColor(Color.parseColor("#2196F3"));
        builder.positiveColor(Color.parseColor("#2196F3"));

        builder.callback(new MaterialDialog.SimpleCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                final Project project = new Project();
                project.setProjectName(projectTitle);
                project.setProjectDescription(projectDescription);
                project.setUsername(SharePreferenceUtil.getUserName(getActivity()));
                projectlist_progress.setVisibility(View.VISIBLE);
                project.save(getActivity(), new SaveListener() {
                    @Override
                    public void onSuccess() {
                        projectlist_progress.setVisibility(View.GONE);
                        Tools.ShowToast(getActivity(), "项目创建成功", 2);
                        dataList.add(project);
                        projectsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        projectlist_progress.setVisibility(View.GONE);
                        Tools.ShowToast(getActivity(), "项目创建失败," + s, 2);
                    }
                });

            }

        });

        MaterialDialog dialog = builder.build();
        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        projectTitle_et = (EditText) dialog.getCustomView().findViewById(R.id.project_title_et);
        projectDescription_et = (EditText) dialog.getCustomView().findViewById(R.id.project_description_et);
        projectTitle_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                projectTitle = s.toString();
                positiveAction.setEnabled(projectTitle.trim().length() > 0
                        && projectDescription.trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        projectDescription_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                projectDescription = s.toString();
                positiveAction.setEnabled(projectTitle.trim().length() > 0
                        && projectDescription.trim().length() > 0);
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
        EditText projectTitle_et;
        EditText projectDescription_et;
        Button deleteproject_but;
        final Context ct = getActivity();

        //Creates a dialog for adding a new task
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
        builder.title("编辑项目");
        builder.customView(R.layout.project_dialog);
        builder.negativeText("取消");
        builder.positiveText("保存");
        builder.neutralText("删除");
        builder.negativeColor(Color.parseColor("#2196F3"));
        builder.positiveColor(Color.parseColor("#2196F3"));
        builder.neutralColor(Color.parseColor("#F33446"));

        builder.callback(new MaterialDialog.FullCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                final Project project = new Project();
                project.setObjectId(dataList.get(position).getObjectId());
                project.setProjectName(projectTitle);
                project.setProjectDescription(projectDescription);
                project.setUsername(SharePreferenceUtil.getUserName(getActivity()));
                projectlist_progress.setVisibility(View.VISIBLE);
                project.update(getActivity(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        projectlist_progress.setVisibility(View.GONE);
                        Tools.ShowToast(getActivity(), "保存成功", 2);
                        dataList.set(position, project);
                        projectsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        projectlist_progress.setVisibility(View.GONE);
                        Tools.ShowToast(getActivity(), "保存失败," + s, 2);
                    }
                });

            }

            @Override
            public void onNegative(MaterialDialog dialog) {

            }

            @Override
            public void onNeutral(MaterialDialog dialog) {
                Project temp = dataList.get(position);
                projectlist_progress.setVisibility(View.VISIBLE);
                temp.delete(getActivity(), new DeleteListener() {
                    @Override
                    public void onSuccess() {
                        projectlist_progress.setVisibility(View.GONE);
                        Tools.ShowToast(getActivity(), "删除成功", 2);
                        dataList.remove(position);
                        projectsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        projectlist_progress.setVisibility(View.GONE);
                        Tools.ShowToast(getActivity(), "删除失败," + s, 2);
                    }
                });
            }
        });

        MaterialDialog dialog = builder.build();
        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        projectTitle_et = (EditText) dialog.getCustomView().findViewById(R.id.project_title_et);
        projectDescription_et = (EditText) dialog.getCustomView().findViewById(R.id.project_description_et);

        projectTitle_et.setText(dataList.get(position).getProjectName());
        projectDescription_et.setText(dataList.get(position).getProjectDescription());
        projectTitle = dataList.get(position).getProjectName();
        projectDescription = dataList.get(position).getProjectDescription();
        projectTitle_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                projectTitle = s.toString();
                positiveAction.setEnabled(projectTitle.trim().length() > 0
                        && projectDescription.trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        projectDescription_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                projectDescription = s.toString();
                positiveAction.setEnabled(projectTitle.trim().length() > 0
                        && projectDescription.trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
        positiveAction.setEnabled(true);
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

}
