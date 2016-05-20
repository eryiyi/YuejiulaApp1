package com.liangxun.yuejiula.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.entity.Champion;
import com.liangxun.yuejiula.util.StringUtil;
import com.liangxun.yuejiula.widget.PictureGridview;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yixia.camera.demo.UniversityApplication;

import java.util.List;

/**
 * 动态
 */
public class ChampionAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<Champion> findEmps;
    private Context mContext;

    Drawable img_one, img_two;

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类

    public ChampionAdapter(List<Champion> findEmps, Context mContext) {
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
        Resources res = mContext.getResources();
        img_one = res.getDrawable(R.drawable.prizes_isure_not);
        img_two = res.getDrawable(R.drawable.prizes_isure);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_champion, null);
            holder.item_pic = (ImageView) convertView.findViewById(R.id.item_pic);
            holder.item_pk_play = (ImageView) convertView.findViewById(R.id.item_pk_play);
            holder.item_nickname = (TextView) convertView.findViewById(R.id.item_nickname);
            holder.item_datenumber = (TextView) convertView.findViewById(R.id.item_datenumber);
            holder.item_cont = (TextView) convertView.findViewById(R.id.item_cont);
            holder.item_colleage = (TextView) convertView.findViewById(R.id.item_colleage);
            holder.item_cover = (ImageView) convertView.findViewById(R.id.item_cover);
            holder.prizes_issure = (ImageView) convertView.findViewById(R.id.prizes_issure);
            holder.gridview_detail_picture = (PictureGridview) convertView.findViewById(R.id.gridview_detail_picture);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.item_pic.setVisibility(View.GONE);
        holder.gridview_detail_picture.setVisibility(View.GONE);//隐藏九宫格
        holder.gridview_detail_picture.setSelector(new ColorDrawable(Color.TRANSPARENT));
        holder.item_pk_play.setVisibility(View.GONE);
        final Champion cell = findEmps.get(position);//获得元素
        if (cell != null) {
            if (!StringUtil.isNullOrEmpty(cell.getPicUrl())&& cell.getZpType().equals("1")) {
                //图片
                holder.item_pic.setVisibility(View.GONE);
                holder.item_pk_play.setVisibility(View.GONE);
                holder.gridview_detail_picture.setVisibility(View.VISIBLE);
                final String[] picUrls = cell.getPicUrl().split(",");//图片链接切割
                if (picUrls.length > 0) {
                    //有多张图
                    holder.gridview_detail_picture.setVisibility(View.VISIBLE);
                    holder.gridview_detail_picture.setAdapter(new ImageGridViewAdapter(picUrls, mContext));
                    if(picUrls.length == 1){
                        holder.gridview_detail_picture.setClickable(false);
                        holder.gridview_detail_picture.setPressed(false);
                        holder.gridview_detail_picture.setEnabled(false);
                    }else {
                        holder.gridview_detail_picture.setClickable(true);
                        holder.gridview_detail_picture.setPressed(true);
                        holder.gridview_detail_picture.setEnabled(true);
                    }
                }
            }
            if(!StringUtil.isNullOrEmpty(cell.getPicUrl())&& cell.getZpType().equals("2")) {
                //视频
                holder.item_pic.setVisibility(View.VISIBLE);
                holder.gridview_detail_picture.setVisibility(View.GONE);//隐藏九宫格
                holder.item_pk_play.setVisibility(View.VISIBLE);
                String[] picarr = cell.getPicUrl().split(",");
                if (picarr != null) {
                    imageLoader.displayImage(picarr[0], holder.item_pic, UniversityApplication.options, animateFirstListener);
                }
            }
            holder.item_nickname.setText(cell.getEmpName());
            imageLoader.displayImage(cell.getEmpCover(), holder.item_cover, UniversityApplication.txOptions, animateFirstListener);
            holder.item_datenumber.setText("第" + cell.getThemeNumber() + "期");
            holder.item_cont.setText(cell.getZpContent());
            holder.item_colleage.setText(cell.getSchoolName());
            if ("0".equals(cell.getIsSure())) {
                holder.prizes_issure.setImageDrawable(img_one);
            }
            if ("1".equals(cell.getIsSure())) {
                holder.prizes_issure.setImageDrawable(img_two);
            }
        }
        return convertView;
    }

    class ViewHolder {
        ImageView item_pic;
        TextView item_nickname;
        TextView item_datenumber;
        TextView item_cont;
        TextView item_colleage;
        ImageView item_cover;
        ImageView prizes_issure;
        ImageView item_pk_play;
        PictureGridview gridview_detail_picture;//九宫格--图片
    }

}
