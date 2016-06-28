package com.liangxun.yuejiula.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.entity.SchoolThreeTingtaiBd;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yixia.camera.demo.UniversityApplication;

import java.util.List;

/**
 */
public class DetailDianpuThreePtAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<SchoolThreeTingtaiBd> findEmps;
    private Context mContext;
    private int countTmp;

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类

    public DetailDianpuThreePtAdapter(List<SchoolThreeTingtaiBd> findEmps, Context mContext, int countTmp) {
        this.findEmps = findEmps;
        this.mContext = mContext;
        this.countTmp = countTmp;

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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.detail_favour_item, null);
            holder.ivIcon = (ImageView) convertView.findViewById(R.id.detail_favour_item_icon);
            holder.favour_count = (TextView) convertView.findViewById(R.id.favour_count);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final SchoolThreeTingtaiBd cell = findEmps.get(position);//获得元素
//        if (position == (findEmps.size() - 1)) {
//            holder.favour_count.setVisibility(View.VISIBLE);
//            if(countTmp >99){
//                countTmp = 99;
//            }
//            holder.favour_count.setText(String.valueOf(countTmp));
//        } else {
            holder.favour_count.setVisibility(View.GONE);
//        }
        if (cell != null) {
            imageLoader.displayImage(cell.getSchool_three_pingtai_pic(), holder.ivIcon, UniversityApplication.txOptions, animateFirstListener);
        }
        return convertView;
    }

    class ViewHolder {
        ImageView ivIcon;
        TextView favour_count;//喜欢数量
    }

}
