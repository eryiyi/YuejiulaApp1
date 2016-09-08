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

import java.util.List;

/**
 * 代理商添加奖品   选择圈子
 */
public class PkColleageAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<ContractSchool> findEmps;
    private Context mContext;

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类

    private OnClickContentItemListener onClickContentItemListener;

    public void setOnClickContentItemListener(OnClickContentItemListener onClickContentItemListener) {
        this.onClickContentItemListener = onClickContentItemListener;
    }

    public PkColleageAdapter(List<ContractSchool> findEmps, Context mContext) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_colleage, null);
            holder.item_colleage_text = (TextView) convertView.findViewById(R.id.item_colleage_text);
            holder.select_button = (ImageView) convertView.findViewById(R.id.select_button);
            holder.selected_button = (ImageView) convertView.findViewById(R.id.selected_button);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final ContractSchool cell = findEmps.get(position);//获得元素
        if (cell != null) {
            holder.item_colleage_text.setText(cell.getSchoolName());
            if ("0".equals(cell.getIsSelected())) {
                //未选中
                holder.select_button.setVisibility(View.VISIBLE);
                holder.selected_button.setVisibility(View.GONE);
            }
            if ("1".equals(cell.getIsSelected())) {
                //选中了
                holder.select_button.setVisibility(View.GONE);
                holder.selected_button.setVisibility(View.VISIBLE);
            }
        }
        return convertView;
    }

    class ViewHolder {
        TextView item_colleage_text;
        ImageView select_button;
        ImageView selected_button;
    }

}
