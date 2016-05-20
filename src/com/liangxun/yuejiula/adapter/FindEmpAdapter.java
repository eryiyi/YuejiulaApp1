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
 * 动态
 */
public class FindEmpAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<Emp> findEmps;
    private Context mContext;

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类

    private OnClickContentItemListener onClickContentItemListener;

    public void setOnClickContentItemListener(OnClickContentItemListener onClickContentItemListener) {
        this.onClickContentItemListener = onClickContentItemListener;
    }

    public FindEmpAdapter(List<Emp> findEmps, Context mContext) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.findemp_item, null);
            holder.findemp_item_cover = (ImageView) convertView.findViewById(R.id.findemp_item_cover);
            holder.findemp_item_name = (TextView) convertView.findViewById(R.id.findemp_item_name);
            holder.findemp_item_in = (ImageView) convertView.findViewById(R.id.findemp_item_in);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Emp cell = findEmps.get(position);//获得元素
        if (cell != null) {
            //加载头像
            imageLoader.displayImage(cell.getEmpCover(), holder.findemp_item_cover, UniversityApplication.txOptions, animateFirstListener);
            holder.findemp_item_name.setText(cell.getEmpName());
        }
        //右侧点击进入该会员的主页
        holder.findemp_item_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickContentItemListener.onClickContentItem(position, 1, null);
            }
        });
        //头像点击进入该会员的主页
        holder.findemp_item_cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickContentItemListener.onClickContentItem(position, 1, null);
            }
        });
        return convertView;
    }

    class ViewHolder {
        ImageView findemp_item_cover;//头像
        TextView findemp_item_name;//昵称
        ImageView findemp_item_in;//进入
    }

}
