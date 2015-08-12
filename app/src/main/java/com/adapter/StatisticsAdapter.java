package com.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.Model.Project;

import java.util.ArrayList;

import huti.material.R;


public class StatisticsAdapter extends BaseAdapter {



    private final ArrayList<Project> data;


    public StatisticsAdapter(ArrayList<Project> data) {
        this.data = data;
    }


    @Override
    public int getCount() {
        return data.size();
    }


    @Override
    public Project getItem(int position) {
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_list_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else holder = (ViewHolder) convertView.getTag();
        holder.item_title_tv.setText(data.get(position).getProjectName());
        holder.item_content_tv.setText(data.get(position).getProjectDescription());

        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    private static class ViewHolder {

        private TextView item_title_tv;
        private TextView item_content_tv;

        private View parent;

        private ViewHolder(View parent) {
            this.parent = parent;
            item_title_tv = (TextView) parent.findViewById(R.id.item_title_tv);
            item_content_tv = (TextView) parent.findViewById(R.id.item_content_tv);
        }
    }
}