package com.example.listviewtype;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2016/8/9.
 */
public class MyAdapter extends BaseAdapter {
    Context context;
    List<People> list;



    public MyAdapter(Context context, List<People> list) {
        this.context = context;
        this.list = list;
    }

    private static final int TYPE_1 = 0;
    private static final int TYPE_2 = 1;

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position % 5 < 2) {
            return TYPE_2;
        } else {
            return TYPE_1;
        }
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder1 holder1 = null;
        ViewHolder2 holder2 = null;
        int type = getItemViewType(position);
        if (convertView == null) {
            switch (type) {
                case TYPE_1:
                    convertView = LayoutInflater.from(context).inflate(R.layout.list_item1,null);
                    holder1 = new ViewHolder1();
                    holder1.tvName = (TextView) convertView.findViewById(R.id.tv_name);
                    holder1.tvHabit = (TextView) convertView.findViewById(R.id.tv_habit);
                    holder1.tvAge = (TextView) convertView.findViewById(R.id.tv_age);
                    convertView.setTag(holder1);
                    break;
                case TYPE_2:
                    convertView = LayoutInflater.from(context).inflate(R.layout.list_item2,null);
                    holder2 = new ViewHolder2();
                    holder2.tvName = (TextView) convertView.findViewById(R.id.tv_name);
                    holder2.tvAge = (TextView) convertView.findViewById(R.id.tv_age);
                    convertView.setTag(holder2);
                    break;
            }
        }else{
            switch (type){
                case TYPE_1:
                    holder1 = (ViewHolder1) convertView.getTag();
                    break;
                case TYPE_2:
                    holder2 = (ViewHolder2) convertView.getTag();
                    break;
            }
        }
        People p = list.get(position);
        switch (type){
            case TYPE_1:
                holder1.tvName.setText(p.name);
                holder1.tvAge.setText(p.age+"");
                holder1.tvHabit.setText(p.habit);
                break;
            case TYPE_2:
                holder2.tvName.setText(p.name);
                holder2.tvAge.setText(p.age+"");
                break;
        }
        return convertView;
    }

    class ViewHolder1 {
        TextView tvName;
        TextView tvHabit;
        TextView tvAge;
    }

    class ViewHolder2 {
        TextView tvName;
        TextView tvAge;
    }
}
