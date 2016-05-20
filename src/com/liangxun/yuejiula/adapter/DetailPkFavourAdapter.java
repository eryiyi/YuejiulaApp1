package com.liangxun.yuejiula.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.entity.PkZan;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yixia.camera.demo.UniversityApplication;

import java.util.List;

/**
 * 动态
 */
public class DetailPkFavourAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<PkZan> findEmps;
    private int favourCount;
    private Context mContext;

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类

    public DetailPkFavourAdapter(List<PkZan> findEmps, Context mContext,int favourCount) {
        this.findEmps = findEmps;
        this.mContext = mContext;
        this.favourCount = favourCount;
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
        final PkZan cell = findEmps.get(position);//获得元素
        if (position == (findEmps.size() - 1)) {
            holder.favour_count.setVisibility(View.VISIBLE);//最后一个的话显示
            if(favourCount >99){
                favourCount = 99;
            }
            holder.favour_count.setText(String.valueOf(favourCount));
        } else {
            holder.favour_count.setVisibility(View.GONE);
        }
        if (cell != null) {
            imageLoader.displayImage(cell.getEmpCover(), holder.ivIcon, UniversityApplication.txOptions, animateFirstListener);
        }
        return convertView;
    }

    class ViewHolder {
        ImageView ivIcon;
        TextView favour_count;//喜欢数量
    }

}
