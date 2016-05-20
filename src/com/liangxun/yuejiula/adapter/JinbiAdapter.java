package com.liangxun.yuejiula.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.entity.ManagerEmp;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 * 动态
 */
public class JinbiAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<ManagerEmp> findEmps;
    private Context mContext;

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类

    private OnClickContentItemListener onClickContentItemListener;

    public void setOnClickContentItemListener(OnClickContentItemListener onClickContentItemListener) {
        this.onClickContentItemListener = onClickContentItemListener;
    }

    public JinbiAdapter(List<ManagerEmp> findEmps, Context mContext) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.jinbi_item_xml, null);
            holder.jinbi_nickname = (TextView) convertView.findViewById(R.id.jinbi_nickname);
            holder.jinbi_datetime = (TextView) convertView.findViewById(R.id.jinbi_datetime);
            holder.jinbi_dateline = (TextView) convertView.findViewById(R.id.jinbi_dateline);
            holder.jiechu = (TextView) convertView.findViewById(R.id.jiechu);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final ManagerEmp cell = findEmps.get(position);//获得元素
        if (cell != null) {
            holder.jinbi_nickname.setText(cell.getEmpNickName());
            holder.jinbi_datetime.setText(cell.getDateline());
            holder.jinbi_dateline.setText(cell.getStart() + "到" + cell.getEnd());
            holder.jiechu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickContentItemListener.onClickContentItem(position, 1, null);
                }
            });
        }
        return convertView;
    }

    class ViewHolder {
        TextView jinbi_nickname;
        TextView jinbi_datetime;
        TextView jinbi_dateline;
        TextView jiechu;
    }

}
