package com.liangxun.yuejiula.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.entity.ContractSchool;
import com.liangxun.yuejiula.util.SexRadioGroup;
import com.liangxun.yuejiula.util.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 * 动态
 */
public class ManageColleageSjAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<ContractSchool> findEmps;
    private Context mContext;

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类

    private OnClickContentItemListener onClickContentItemListener;

    public void setOnClickContentItemListener(OnClickContentItemListener onClickContentItemListener) {
        this.onClickContentItemListener = onClickContentItemListener;
    }

    public ManageColleageSjAdapter(List<ContractSchool> findEmps, Context mContext) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_manage_colleage, null);
            holder.item_colleage_switch = (SexRadioGroup) convertView.findViewById(R.id.item_colleage_switch);
            holder.item_colleage_name = (TextView) convertView.findViewById(R.id.item_colleage_name);
            holder.item_colleage_datetime = (TextView) convertView.findViewById(R.id.item_colleage_datetime);
            holder.button_one = (RadioButton) convertView.findViewById(R.id.button_one);
            holder.button_two = (RadioButton) convertView.findViewById(R.id.button_two);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final ContractSchool cell = findEmps.get(position);//获得元素
        holder.item_colleage_name.setText(cell.getSchoolName());
        holder.item_colleage_datetime.setText(cell.getEndTime());
        if (!StringUtil.isNullOrEmpty(cell.getIsOpen())) {
            if ("1".equals(cell.getIsOpen())) {//是关闭的
                holder.button_one.setChecked(false);
                holder.button_two.setChecked(true);
            } else {
                holder.button_one.setChecked(true);
                holder.button_two.setChecked(false);
            }
        } else {
            holder.button_one.setChecked(true);
            holder.button_two.setChecked(false);
        }

        holder.item_colleage_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickContentItemListener.onClickContentItem(position, 1, null);
            }
        });

        holder.item_colleage_datetime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickContentItemListener.onClickContentItem(position, 2, null);
            }
        });
        holder.button_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickContentItemListener.onClickContentItem(position, 3, null);
            }
        });
        holder.button_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickContentItemListener.onClickContentItem(position, 4, null);
            }
        });
        return convertView;
    }

    class ViewHolder {
        SexRadioGroup item_colleage_switch;
        TextView item_colleage_name;
        TextView item_colleage_datetime;//时间
        RadioButton button_one;
        RadioButton button_two;
    }

}
