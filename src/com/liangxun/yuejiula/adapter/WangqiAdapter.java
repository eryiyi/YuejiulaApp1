package com.liangxun.yuejiula.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.entity.PKTheme;

import java.util.List;

public class WangqiAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<PKTheme> findEmps;
    private Context mContext;

    public WangqiAdapter(List<PKTheme> findEmps, Context mContext) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_wangqi, null);
            holder.item_datenumber = (TextView) convertView.findViewById(R.id.item_datenumber);
            holder.item_dateline = (TextView) convertView.findViewById(R.id.item_dateline);
            holder.item_cont = (TextView) convertView.findViewById(R.id.item_cont);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final PKTheme cell = findEmps.get(position);//获得元素
        if (cell != null) {
            holder.item_datenumber.setText("第" + cell.getNumber() + "期");
            holder.item_dateline.setText(cell.getDateline());
            holder.item_cont.setText(cell.getContent());
        }

        return convertView;
    }

    class ViewHolder {
        TextView item_datenumber;
        TextView item_dateline;
        TextView item_cont;
    }

}
