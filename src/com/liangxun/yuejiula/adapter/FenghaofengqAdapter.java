package com.liangxun.yuejiula.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.entity.Emp;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yixia.camera.demo.UniversityApplication;

import java.util.List;

/**
 */
public class FenghaofengqAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<Emp> findEmps;
    private Context mContext;

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private OnClickContentItemListener onClickContentItemListener;

    public void setOnClickContentItemListener(OnClickContentItemListener onClickContentItemListener) {
        this.onClickContentItemListener = onClickContentItemListener;
    }

    public FenghaofengqAdapter(List<Emp> findEmps, Context mContext) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_fqfh, null);
            holder.ivIcon = (ImageView) convertView.findViewById(R.id.detail_favour_item_icon);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.btn = (TextView) convertView.findViewById(R.id.btn);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Emp cell = findEmps.get(position);//获得元素

        if (cell != null) {
            imageLoader.displayImage(cell.getEmpCover(), holder.ivIcon, UniversityApplication.txOptions, animateFirstListener);
            holder.name.setText(cell.getEmpName());
        }
//        holder.btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onClickContentItemListener.onClickContentItem(position, 1, null);
//            }
//        });
        return convertView;
    }

    class ViewHolder {
        ImageView ivIcon;
        TextView name;
        TextView btn;
    }

}
