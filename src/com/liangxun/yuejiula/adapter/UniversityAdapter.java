package com.liangxun.yuejiula.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.entity.Univerty;

import java.util.List;

/**
 * author: ${zhanghailong}
 * Date: 2015/3/9
 * Time: 9:17
 * 类的功能、说明写在此处.
 */
public class UniversityAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<Univerty> findEmps;
    private Context mContext;

    public UniversityAdapter(List<Univerty> findEmps, Context mContext) {
        this.findEmps = findEmps;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return findEmps.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_university, null);
            holder.university_title = (TextView) convertView.findViewById(R.id.university_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Univerty favour = findEmps.get(position);
        if (findEmps != null) {
            holder.university_title.setText(favour.getName());
        }
        return convertView;
    }

    class ViewHolder {
        TextView university_title;
    }
}
