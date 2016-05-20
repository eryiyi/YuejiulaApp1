package com.liangxun.yuejiula.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.entity.Goods;
import com.liangxun.yuejiula.util.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yixia.camera.demo.UniversityApplication;

import java.util.List;

/**
 * 动态
 */
public class GoodsAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<Goods> findEmps;
    private Context mContext;

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类

    public GoodsAdapter(List<Goods> findEmps, Context mContext) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.goods_item, null);
            holder.goods_item_cover = (ImageView) convertView.findViewById(R.id.goods_item_cover);
            holder.goods_item_title = (TextView) convertView.findViewById(R.id.goods_item_title);
            holder.goods_item_money = (TextView) convertView.findViewById(R.id.goods_item_money);
            holder.goods_item_dateline = (TextView) convertView.findViewById(R.id.goods_item_dateline);
            holder.cover_emp = (ImageView) convertView.findViewById(R.id.cover_emp);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Goods cell = findEmps.get(position);//获得元素
        if (cell != null) {
            if (!StringUtil.isNullOrEmpty(cell.getCover())) {
                String[] picarr = cell.getCover().split(",");
                if (picarr != null) {
                    imageLoader.displayImage(picarr[0], holder.goods_item_cover, UniversityApplication.txOptions, animateFirstListener);
                }
            }
            holder.goods_item_title.setText(cell.getName());
            imageLoader.displayImage(cell.getEmpCover(), holder.cover_emp, UniversityApplication.txOptions, animateFirstListener);
            holder.goods_item_money.setText("￥" + cell.getMoney());
            holder.goods_item_dateline.setText(cell.getDateline());
        }
        return convertView;
    }

    class ViewHolder {
        ImageView goods_item_cover;//商品封面
        TextView goods_item_title;//商品名字
        TextView goods_item_money;//价格
        TextView goods_item_dateline;//时间
        ImageView cover_emp;
    }
}
