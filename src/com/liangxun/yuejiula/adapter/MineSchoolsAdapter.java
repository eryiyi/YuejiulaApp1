package com.liangxun.yuejiula.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.entity.ContractSchool;

import java.util.List;

/**
 * author: ${zhanghailong}
 * Date: 2015/2/6
 * Time: 14:06
 * 类的功能、说明写在此处.
 */
public class MineSchoolsAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<ContractSchool> findEmps;
    private Context mContext;

    public MineSchoolsAdapter(List<ContractSchool> findEmps, Context mContext) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_mine_colleage, null);
            holder.item_mine_school_nickname = (TextView) convertView.findViewById(R.id.item_mine_school_nickname);
            holder.item_mine_school_dateline = (TextView) convertView.findViewById(R.id.item_mine_school_dateline);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final ContractSchool favour = findEmps.get(position);
        if (findEmps != null) {
            holder.item_mine_school_nickname.setText(favour.getSchoolName());
            holder.item_mine_school_dateline.setText(favour.getEndTime());

        }
        return convertView;
    }

    class ViewHolder {
        TextView item_mine_school_nickname;
        TextView item_mine_school_dateline;
    }

}