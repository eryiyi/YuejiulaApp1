package com.liangxun.yuejiula.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.entity.ContractSchool;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yixia.camera.demo.UniversityApplication;

import java.util.List;

/**
 * 圈主列表
 */
public class SJEmpAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<ContractSchool> findEmps;
    private Context mContext;

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类

    private OnClickContentItemListener onClickContentItemListener;

    public void setOnClickContentItemListener(OnClickContentItemListener onClickContentItemListener) {
        this.onClickContentItemListener = onClickContentItemListener;
    }

    public SJEmpAdapter(List<ContractSchool> findEmps, Context mContext) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_shangjia_person, null);
            holder.item_cover = (ImageView) convertView.findViewById(R.id.item_cover);
            holder.item_nickname = (TextView) convertView.findViewById(R.id.item_nickname);
            holder.itemt_colleage = (TextView) convertView.findViewById(R.id.itemt_colleage);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final ContractSchool cell = findEmps.get(position);//获得元素
        if (cell != null) {
            //加载头像
            imageLoader.displayImage(cell.getEmpCover(), holder.item_cover, UniversityApplication.txOptions, animateFirstListener);
            holder.item_nickname.setText(cell.getEmpName());
            holder.itemt_colleage.setText(cell.getSchoolName());
        }
        return convertView;
    }

    class ViewHolder {
        ImageView item_cover;//头像
        TextView item_nickname;//昵称
        TextView itemt_colleage;//圈子
    }

}
