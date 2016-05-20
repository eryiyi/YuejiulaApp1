package com.liangxun.yuejiula.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.entity.PKWork;
import com.liangxun.yuejiula.ui.GalleryUrlActivity;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.StringUtil;
import com.liangxun.yuejiula.widget.PictureGridview;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yixia.camera.demo.UniversityApplication;

import java.util.List;

/**
 * 动态
 */
public class PkNewAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<PKWork> findEmps;
    private Context mContext;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private OnClickContentItemListener onClickContentItemListener;

    public void setOnClickContentItemListener(OnClickContentItemListener onClickContentItemListener) {
        this.onClickContentItemListener = onClickContentItemListener;
    }

    public PkNewAdapter(List<PKWork> findEmps, Context mContext) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_pk_new, null);
            holder.item_cover = (ImageView) convertView.findViewById(R.id.item_cover);
            holder.item_pk_play = (ImageView) convertView.findViewById(R.id.item_pk_play);
            holder.item_title = (TextView) convertView.findViewById(R.id.item_title);
            holder.item_pk_nickname = (TextView) convertView.findViewById(R.id.item_pk_nickname);
            holder.item_pk_dateline = (TextView) convertView.findViewById(R.id.item_pk_dateline);
            holder.item_pk_plnum = (TextView) convertView.findViewById(R.id.item_pk_plnum);
            holder.item_pk_date = (TextView) convertView.findViewById(R.id.item_pk_date);
            holder.item_pk_zannum = (TextView) convertView.findViewById(R.id.item_pk_zannum);
            holder.pk_cover = (ImageView) convertView.findViewById(R.id.pk_cover);
            holder.searchLayoutTx = (RelativeLayout) convertView.findViewById(R.id.searchLayoutTx);
            holder.gridview_detail_picture = (PictureGridview) convertView.findViewById(R.id.gridview_detail_picture);
            holder.item_tp_liner = (LinearLayout) convertView.findViewById(R.id.item_tp_liner);
            holder.home_comment = (LinearLayout) convertView.findViewById(R.id.home_comment);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final PKWork cell = findEmps.get(position);//获得元素

        if (cell != null) {
            imageLoader.displayImage(cell.getEmpCover(), holder.pk_cover, UniversityApplication.txOptions, animateFirstListener);
            holder.item_pk_nickname.setText(cell.getEmpName());
            holder.item_pk_dateline.setText(cell.getSchoolName());
            holder.item_pk_date.setText(cell.getDateline());
            holder.item_pk_zannum.setText(cell.getZanNum());
            holder.item_pk_plnum.setText(cell.getPlNum());
            holder.item_pk_play.setVisibility(View.GONE);//播放按钮
            holder.item_title.setVisibility(View.GONE);//文字
            holder.item_cover.setVisibility(View.GONE);//图片
            holder.searchLayoutTx.setVisibility(View.GONE);
            holder.gridview_detail_picture.setVisibility(View.GONE);//隐藏九宫格
            holder.gridview_detail_picture.setSelector(new ColorDrawable(Color.TRANSPARENT));

            if (!StringUtil.isNullOrEmpty(cell.getTitle())) {
                holder.item_title.setVisibility(View.VISIBLE);
                holder.item_title.setText(cell.getTitle());
            }

            if (cell.getType().equals("1")) {
                //图片
                holder.gridview_detail_picture.setVisibility(View.VISIBLE);
                holder.item_pk_play.setVisibility(View.GONE);
                holder.item_cover.setVisibility(View.GONE);
                holder.searchLayoutTx.setVisibility(View.VISIBLE);
                final String[] picUrls = cell.getPicUrl().split(",");//图片链接切割
                if (picUrls.length > 0) {
                    //有多张图
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
                    holder.gridview_detail_picture.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(mContext, GalleryUrlActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                            intent.putExtra(Constants.IMAGE_URLS, picUrls);
                            intent.putExtra(Constants.IMAGE_POSITION, position);
                            mContext.startActivity(intent);
                        }
                    });
                }
            }
            if (cell.getType().equals("2")) {
                //视频
                holder.gridview_detail_picture.setVisibility(View.GONE);
                holder.item_pk_play.setVisibility(View.VISIBLE);
                holder.item_cover.setVisibility(View.VISIBLE);
                holder.searchLayoutTx.setVisibility(View.VISIBLE);
                imageLoader.displayImage(cell.getPicUrl(), holder.item_cover, UniversityApplication.txOptions, animateFirstListener);
            }
            //投票
            holder.item_tp_liner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickContentItemListener.onClickContentItem(position, 1, null);
                }
            });
            holder.home_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickContentItemListener.onClickContentItem(position, 2, null);
                }
            });

        }
        return convertView;
    }

    class ViewHolder {
        ImageView item_cover;
        ImageView item_pk_play;
        TextView item_title;
        TextView item_pk_nickname;
        TextView item_pk_dateline;
        TextView item_pk_plnum;
        TextView item_pk_date;
        TextView item_pk_zannum;
        ImageView pk_cover;
        RelativeLayout searchLayoutTx;
        PictureGridview gridview_detail_picture;//九宫格--图片
        LinearLayout item_tp_liner;
        LinearLayout home_comment;
    }
}
