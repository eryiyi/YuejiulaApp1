package com.liangxun.yuejiula.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.entity.News;
import com.liangxun.yuejiula.util.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yixia.camera.demo.UniversityApplication;

import java.util.List;

public class NewsAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<News> findEmps;
    private Context mContext;

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private OnClickContentItemListener onClickContentItemListener;

    public void setOnClickContentItemListener(OnClickContentItemListener onClickContentItemListener) {
        this.onClickContentItemListener = onClickContentItemListener;
    }

    public NewsAdapter(List<News> findEmps, Context mContext) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.news_item, null);
            holder.item_news_pic = (ImageView) convertView.findViewById(R.id.item_news_pic);
            holder.item_news_title = (TextView) convertView.findViewById(R.id.item_news_title);
            holder.item_news_date = (TextView) convertView.findViewById(R.id.item_news_date);
            holder.pic_liner = (LinearLayout) convertView.findViewById(R.id.pic_liner);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.pic_liner.setVisibility(View.GONE);
        final News cell = findEmps.get(position);//获得元素
        if (cell != null) {
            holder.item_news_title.setText(cell.getTitle());
            holder.item_news_date.setText(cell.getDateLine());
            if (!StringUtil.isNullOrEmpty(cell.getPic())) {
                holder.pic_liner.setVisibility(View.VISIBLE);
                imageLoader.displayImage(cell.getPic(), holder.item_news_pic, UniversityApplication.options, animateFirstListener);
            } else {
                holder.pic_liner.setVisibility(View.GONE);
            }
        }
        return convertView;
    }

    class ViewHolder {
        ImageView item_news_pic;
        TextView item_news_title;
        TextView item_news_date;
        LinearLayout pic_liner;
    }

}
