package com.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.Model.Task;
import com.Utils.SharePreferenceUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import huti.material.R;


public class AllTaskListAdapter extends BaseAdapter {

    private ArrayList<Task> listData;

    private LayoutInflater layoutInflater;

    public Context context;
    private TaskItemClickListener taskItemClickListener;

    public AllTaskListAdapter(Context context, ArrayList listData) {
        this.context = context;
        this.listData = listData;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //Do things with the listview
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.listitem, null);
            holder = new ViewHolder();
            holder.buttonView = (ToggleButton) convertView.findViewById(R.id.listbutton);
            holder.textView = (TextView) convertView.findViewById(R.id.listtext);
            holder.taskstate_tv = (TextView) convertView.findViewById(R.id.taskstate_tv);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final int aa = position;

        holder.buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //listData.get(aa).setChecked(true);
                taskItemClickListener.HandleClick(v, 1);


            }
        });

        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskItemClickListener.HandleClick(v, 2);

            }
        });


        //We set the tag to the Task, so we can find it in callbacks
        holder.buttonView.setTag(listData.get(position));
        holder.textView.setTag(listData.get(position));
        if (listData.get(position).getTaskState() == 3) {
            holder.taskstate_tv.setVisibility(View.VISIBLE);
        } else {
            holder.taskstate_tv.setVisibility(View.GONE);
        }

        //Here we set the checkbox of the listview
        if (listData.get(position).getColor() == 3) {
            holder.buttonView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.checkbox_selector_red, 0, 0, 0);
        } else if (listData.get(position).getColor() == 2) {
            holder.buttonView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.checkbox_selector_blue, 0, 0, 0);
        } else if (listData.get(position).getColor() == 1) {
            holder.buttonView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.checkbox_selector_green, 0, 0, 0);
        }

        holder.buttonView.setChecked(listData.get(position).getChecked());

        //Padding, 16dp to pixels
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        holder.buttonView.setCompoundDrawablePadding((int) ((16 * displayMetrics.density) + 0.5));

        //Set the text of the listview
        holder.textView.setText(listData.get(position).getTaskName());

        if (listData.get(position).getChecked()) {
            //Add strike through, set text to gray
            holder.textView.setPaintFlags(holder.buttonView.getPaintFlags()
                    | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.textView.setTextColor(Color.GRAY);
        } else {
            //Remove strike through, set text to black
            holder.textView.setPaintFlags(holder.buttonView.getPaintFlags()
                    & ~Paint.STRIKE_THRU_TEXT_FLAG);
            holder.textView.setTextColor(Color.BLACK);
        }

        return convertView;
    }


    static class ViewHolder {
        ToggleButton buttonView;
        TextView textView;
        TextView taskstate_tv;
    }




    /**
     * type: 1:点击togglebutton 2:点击textView
     */
    public interface TaskItemClickListener{
        void HandleClick(View v,int type);

    }
    public void setTaskItemClickListener(TaskItemClickListener taskItemClickListener){
        this.taskItemClickListener = taskItemClickListener;
    }


}
