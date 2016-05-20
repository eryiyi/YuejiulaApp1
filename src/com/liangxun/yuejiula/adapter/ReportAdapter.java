package com.liangxun.yuejiula.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.entity.Report;
import com.liangxun.yuejiula.util.Constants;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 * 举报
 */
public class ReportAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<Report> findEmps;
    private Context mContext;

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类

    private OnClickContentItemListener onClickContentItemListener;

    public void setOnClickContentItemListener(OnClickContentItemListener onClickContentItemListener) {
        this.onClickContentItemListener = onClickContentItemListener;
    }

    public ReportAdapter(List<Report> findEmps, Context mContext) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.report_item_xml, null);
            holder.report_item_emp_two = (TextView) convertView.findViewById(R.id.report_item_emp_two);
            holder.report_item_emp_one = (TextView) convertView.findViewById(R.id.report_item_emp_one);
            holder.report_item_cont = (TextView) convertView.findViewById(R.id.report_item_cont);
            holder.report_item_detail = (TextView) convertView.findViewById(R.id.report_item_detail);
            holder.report_item_dateline = (TextView) convertView.findViewById(R.id.report_item_dateline);
            holder.report_item_cancle = (TextView) convertView.findViewById(R.id.report_item_cancle);
            holder.report_item_done = (TextView) convertView.findViewById(R.id.report_item_done);
            holder.report_title = (TextView) convertView.findViewById(R.id.report_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Report cell = findEmps.get(position);//获得元素
        if (cell != null) {
            holder.report_item_emp_one.setText(cell.getEmpOneNickName());
            holder.report_item_emp_two.setText(cell.getEmpTwoNickName());
            holder.report_item_cont.setText(cell.getCont());
            holder.report_item_dateline.setText(cell.getDateline());
            //举报类别
            //0   动态  1   商品
            //2   兼职  3    举报个人   4举报作品
            if (cell.getTypeId().equals(Constants.REPORT_TYPE_ZERRO)) {
                holder.report_title.setText("举报动态信息");
            }
            if (cell.getTypeId().equals(Constants.REPORT_TYPE_ONE)) {
                holder.report_title.setText("举报商品信息");
            }
            if (cell.getTypeId().equals(Constants.REPORT_TYPE_tWO)) {
                holder.report_title.setText("举报兼职信息");
            }
            if (cell.getTypeId().equals(Constants.REPORT_TYPE_THREE)) {
                holder.report_title.setText("举报个人");
            }
            if (cell.getTypeId().equals(Constants.REPORT_TYPE_FOUR)) {
                holder.report_title.setText("举报PK作品");
            }
        }

        holder.report_item_emp_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickContentItemListener.onClickContentItem(position, 1, null);
            }
        });
        holder.report_item_emp_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickContentItemListener.onClickContentItem(position, 2, null);
            }
        });
        holder.report_item_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickContentItemListener.onClickContentItem(position, 3, null);
            }
        });
        holder.report_item_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickContentItemListener.onClickContentItem(position, 4, null);
            }
        });
        holder.report_item_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickContentItemListener.onClickContentItem(position, 5, null);
            }
        });
        return convertView;
    }

    class ViewHolder {
        TextView report_item_emp_one;
        TextView report_item_emp_two;
        TextView report_item_cont;
        TextView report_item_detail;
        TextView report_item_dateline;
        TextView report_item_done;
        TextView report_item_cancle;
        TextView report_title;//举报类比
    }

}
