package com.liangxun.yuejiula.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.entity.Goods;
import com.liangxun.yuejiula.entity.RecordJp;
import com.liangxun.yuejiula.util.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yixia.camera.demo.UniversityApplication;

import java.util.List;

/**
 */
public class RecordJpAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<RecordJp> findEmps;
    private Context mContext;

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类

    public RecordJpAdapter(List<RecordJp> findEmps, Context mContext) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_record_jp, null);
            holder.head = (ImageView) convertView.findViewById(R.id.head);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.money = (TextView) convertView.findViewById(R.id.money);
            holder.dateline = (TextView) convertView.findViewById(R.id.dateline);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final RecordJp cell = findEmps.get(position);//获得元素
        if (cell != null) {
            imageLoader.displayImage(cell.getEmpCoverJp(), holder.head, UniversityApplication.txOptions, animateFirstListener);
            holder.name.setText(cell.getEmpNameJp());
            holder.money.setText("加价：" + cell.getMoney() +"元");
            holder.dateline.setText(cell.getDateline());
        }
        return convertView;
    }

    class ViewHolder {
        ImageView head;//
        TextView name;//
        TextView money;//
        TextView dateline;//
    }
}
