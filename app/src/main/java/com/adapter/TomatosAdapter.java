package com.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.Model.Tomato;

import huti.material.R;

import java.util.ArrayList;
import java.util.List;


public class TomatosAdapter extends BaseAdapter {



    private final List<Tomato> data;

    /**
     * Constructor.
     *
     * @param data la lista de notas a usar como fuente de datos para este adaptador.
     */
    public TomatosAdapter(List<Tomato> data) {
        this.data = data;
    }

    /** @return cuantos datos hay en la lista de notas. */
    @Override
    public int getCount() {
        return data.size();
    }


    @Override
    public Tomato getItem(int position) {
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tomato_list_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else holder = (ViewHolder) convertView.getTag();
        holder.tomato_date.setText(data.get(position).getTomatoDate());
        holder.tomato_timerange.setText("时间区间:"+data.get(position).getTomatoTimeRange());
        holder.tomato_description.setText(data.get(position).getTomatoDescription());
        return convertView;
    }

    /** Almacena componentes visuales para acceso rápido sin necesidad de buscarlos muy seguido.*/
    private static class ViewHolder {

        private TextView tomato_date;
        private TextView tomato_timerange;
        private TextView tomato_description;

        private View parent;

        /**
         * Constructor. Encuentra todas los componentes visuales en el componente padre dado.
         *
         * @param parent un componente visual.
         */
        private ViewHolder(View parent) {
            this.parent = parent;
            tomato_date = (TextView) parent.findViewById(R.id.tomato_date);
            tomato_timerange = (TextView) parent.findViewById(R.id.tomato_timerange);
            tomato_description = (TextView) parent.findViewById(R.id.tomato_description);
        }
    }
}