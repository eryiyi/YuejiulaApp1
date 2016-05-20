package com.liangxun.yuejiula.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.entity.GoodsComment;
import com.liangxun.yuejiula.face.FaceConversionUtil;
import com.liangxun.yuejiula.util.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yixia.camera.demo.UniversityApplication;

import java.util.List;

/**
 * 动态
 */
public class GoodsCommentAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<GoodsComment> findEmps;
    private Context mContext;

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类

    private OnClickContentItemListener onClickContentItemListener;

    public void setOnClickContentItemListener(OnClickContentItemListener onClickContentItemListener) {
        this.onClickContentItemListener = onClickContentItemListener;
    }

    public GoodsCommentAdapter(List<GoodsComment> findEmps, Context mContext) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.goods_comment_item, null);
            holder.goods_comment_cover = (ImageView) convertView.findViewById(R.id.goods_comment_cover);
            holder.goods_comment_nickname = (TextView) convertView.findViewById(R.id.goods_comment_nickname);
            holder.goods_comment_dateline = (TextView) convertView.findViewById(R.id.goods_comment_dateline);
            holder.goods_comment_cont = (TextView) convertView.findViewById(R.id.goods_comment_cont);
            holder.floor_comment = (TextView) convertView.findViewById(R.id.floor_comment);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final GoodsComment cell = findEmps.get(position);//获得元素
        if (cell != null) {
            holder.goods_comment_nickname.setText(cell.getNickName());
            holder.goods_comment_dateline.setText(cell.getDateline());
            int textsize = (int) holder.goods_comment_cont.getTextSize();
            textsize = StringUtil.dp2px(mContext, textsize+25);
            if (!StringUtil.isNullOrEmpty(cell.getfNickName())) {
                if (!StringUtil.isNullOrEmpty(cell.getContent())) {
                    holder.goods_comment_cont.setVisibility(View.VISIBLE);
                    holder.goods_comment_cont.setText(FaceConversionUtil.getInstace().getExpressionString(mContext, "回复 " + cell.getfNickName() + ":" + cell.getContent(),textsize));
                }
            } else {
                if (!StringUtil.isNullOrEmpty(cell.getContent())) {
                    holder.goods_comment_cont.setVisibility(View.VISIBLE);
                    holder.goods_comment_cont.setText(FaceConversionUtil.getInstace().getExpressionString(mContext, cell.getContent(),textsize));
                }
            }
            imageLoader.displayImage(cell.getCover(), holder.goods_comment_cover, UniversityApplication.txOptions, animateFirstListener);
        }
        //右侧点击进入该会员的主页
        holder.goods_comment_cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickContentItemListener.onClickContentItem(position, 1, null);
            }
        });
        holder.floor_comment.setText(String.valueOf(position + 1) + "#");
        return convertView;
    }

    class ViewHolder {
        ImageView goods_comment_cover;
        TextView goods_comment_nickname;
        TextView goods_comment_dateline;
        TextView goods_comment_cont;
        TextView floor_comment;//评论层
    }

}
