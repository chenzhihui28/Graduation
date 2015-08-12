package com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.Model.Habit;
import com.Model.Project;

import java.util.ArrayList;

import huti.material.R;


public class HabitAdapter extends BaseAdapter {



    private final ArrayList<Habit> data;
    private Context context;


    public HabitAdapter(ArrayList<Habit> data,Context context) {
        this.data = data;
        this.context = context;
    }


    @Override
    public int getCount() {
        return data.size();
    }


    @Override
    public Habit getItem(int position) {
        return data.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) { // inflar componente visual
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.habit_list_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else holder = (ViewHolder) convertView.getTag();
        if(data.size() > 0){
            holder.item_title_tv.setText(data.get(position).getHabitName());
            holder.item_content_tv.setText(data.get(position).getRecordTime()
                    +"/"+data.get(position).getHabitCost());
            if(data.get(position).getHabitState() == 1){
                holder.item_tips_tv.setText("状态：正在养成，加油！，记得坚持打卡哦~");
            }else if(data.get(position).getHabitState() == 2){
                holder.item_tips_tv.setText("状态：已经养成！继续坚持吧~");
                holder.item_tips_tv.setTextColor(context.getResources().getColor(R.color.buttonGreen));
            }else if(data.get(position).getHabitState() == 3){
                holder.item_tips_tv.setText("状态：连续超过五天没打卡，计划已失败~");
                holder.item_tips_tv.setTextColor(context.getResources().getColor(R.color.buttonRed));
            }
        }


        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    private static class ViewHolder {

        private TextView item_title_tv;
        private TextView item_content_tv;
        private TextView item_tips_tv;

        private View parent;

        private ViewHolder(View parent) {
            this.parent = parent;
            item_title_tv = (TextView) parent.findViewById(R.id.item_title_tv);
            item_content_tv = (TextView) parent.findViewById(R.id.item_content_tv);
            item_tips_tv = (TextView) parent.findViewById(R.id.item_tips_tv);
        }
    }
}