package com.liangxun.yuejiula.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.entity.PartTime;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yixia.camera.demo.UniversityApplication;

import java.util.List;

/**
 * 动态
 */
public class MinePartTimeAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<PartTime> findEmps;
    private Context mContext;

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private OnClickContentItemListener onClickContentItemListener;

    public void setOnClickContentItemListener(OnClickContentItemListener onClickContentItemListener) {
        this.onClickContentItemListener = onClickContentItemListener;
    }

    public MinePartTimeAdapter(List<PartTime> findEmps, Context mContext) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.mine_parttime_item, null);
            holder.parttime_item_cont = (TextView) convertView.findViewById(R.id.parttime_item_cont);
            holder.parttime_item_title = (TextView) convertView.findViewById(R.id.parttime_item_title);
            holder.parttime_item_dateline = (TextView) convertView.findViewById(R.id.parttime_item_dateline);
            holder.delete_parttime_item = (ImageView) convertView.findViewById(R.id.delete_parttime_item);
            holder.parttime_item_cover = (ImageView) convertView.findViewById(R.id.parttime_item_cover);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final PartTime cell = findEmps.get(position);//获得元素
        if (cell != null) {
            holder.parttime_item_title.setText(cell.getTitle());
            holder.parttime_item_dateline.setText(cell.getDateline());
            holder.parttime_item_cont.setText(cell.getTypeName());
            holder.delete_parttime_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickContentItemListener.onClickContentItem(position, 1, null);
                }
            });
            imageLoader.displayImage(cell.getTypeCover(), holder.parttime_item_cover, UniversityApplication.txOptions, animateFirstListener);
        }
        return convertView;
    }

    class ViewHolder {
        TextView parttime_item_title;
        TextView parttime_item_cont;
        TextView parttime_item_dateline;
        ImageView delete_parttime_item;//删除
        ImageView parttime_item_cover;//类别
    }

}
