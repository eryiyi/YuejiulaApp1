package com.liangxun.yuejiula.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.entity.PkPrize;
import com.liangxun.yuejiula.util.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yixia.camera.demo.UniversityApplication;

import java.util.List;

public class MinePrizesAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<PkPrize> findEmps;
    private Context mContext;

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private OnClickContentItemListener onClickContentItemListener;

    public void setOnClickContentItemListener(OnClickContentItemListener onClickContentItemListener) {
        this.onClickContentItemListener = onClickContentItemListener;
    }

    public MinePrizesAdapter(List<PkPrize> findEmps, Context mContext) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_pk_prizes, null);
            holder.item_prize_number = (TextView) convertView.findViewById(R.id.item_prize_number);
            holder.item_prize_college = (TextView) convertView.findViewById(R.id.item_prize_college);
            holder.item_prize_dateline = (TextView) convertView.findViewById(R.id.item_prize_dateline);
            holder.item_prize_cont = (TextView) convertView.findViewById(R.id.item_prize_cont);
            holder.item_prize_cover = (ImageView) convertView.findViewById(R.id.item_prize_cover);
            holder.item_delete = (ImageView) convertView.findViewById(R.id.item_delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.item_prize_cover.setVisibility(View.GONE);
        final PkPrize cell = findEmps.get(position);//获得元素
        if (cell != null) {
            holder.item_prize_number.setText("第" + cell.getThemeNumber() + "期");
            holder.item_prize_college.setText(cell.getSchoolName());
            holder.item_prize_dateline.setText(cell.getDateline());
            holder.item_prize_cont.setText(cell.getContent());
            if (!StringUtil.isNullOrEmpty(cell.getPic())) {
                holder.item_prize_cover.setVisibility(View.VISIBLE);
                String[] arr = cell.getPic().split(",");
                imageLoader.displayImage(arr[0], holder.item_prize_cover, UniversityApplication.tpOptions, animateFirstListener);
            }
        }
        //评论
        holder.item_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickContentItemListener.onClickContentItem(position, 1, null);
            }
        });
        return convertView;
    }

    class ViewHolder {
        TextView item_prize_number;
        TextView item_prize_college;
        TextView item_prize_dateline;
        TextView item_prize_cont;
        ImageView item_prize_cover;
        ImageView item_delete;

    }

}
