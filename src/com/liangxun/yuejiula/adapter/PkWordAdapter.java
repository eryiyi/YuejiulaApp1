package com.liangxun.yuejiula.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.entity.PKComment;
import com.liangxun.yuejiula.face.FaceConversionUtil;
import com.liangxun.yuejiula.util.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yixia.camera.demo.UniversityApplication;

import java.util.List;

/**
 * pk评论
 */
public class PkWordAdapter extends BaseAdapter {
    private ViewHolder holder;
    private Context context;
    private List<PKComment> list;
    private LayoutInflater inflater;
    private OnClickContentItemListener onClickContentItemListener;
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    public void setOnClickContentItemListener(OnClickContentItemListener onClickContentItemListener) {
        this.onClickContentItemListener = onClickContentItemListener;
    }

    public PkWordAdapter(List<PKComment> list, Context context) {
        this.context = context;
        this.list = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.detail_comment, null);
            holder.comment_photo = (ImageView) convertView.findViewById(R.id.comment_photo);
            holder.comment_nickname = (TextView) convertView.findViewById(R.id.comment_nickname);
            holder.comment_f_nickname = (TextView) convertView.findViewById(R.id.comment_f_nickname);
            holder.comment_time = (TextView) convertView.findViewById(R.id.comment_time);
            holder.comment_content = (TextView) convertView.findViewById(R.id.comment_content);
            holder.floor_comment = (TextView) convertView.findViewById(R.id.floor_comment);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final PKComment commentContent = list.get(position);
        if (commentContent != null) {
            imageLoader.displayImage(commentContent.getCover(), holder.comment_photo, UniversityApplication.options);
            holder.comment_nickname.setText(commentContent.getNickName());
            holder.comment_time.setText(commentContent.getDateline());
            if (!StringUtil.isNullOrEmpty(commentContent.getfNickName())) {
                holder.comment_f_nickname.setVisibility(View.VISIBLE);
                holder.comment_f_nickname.setText("回复 " + commentContent.getfNickName() + ": ");
            } else {
                holder.comment_f_nickname.setVisibility(View.GONE);
            }
            if (!StringUtil.isNullOrEmpty(commentContent.getCommentCont())) {
                holder.comment_content.setVisibility(View.VISIBLE);
                int textsize = (int)  holder.comment_content.getTextSize();
                textsize = StringUtil.dp2px(context, textsize+25);
                holder.comment_content.setText(FaceConversionUtil.getInstace().getExpressionString(context, commentContent.getCommentCont(),textsize));
            }
        }
        holder.floor_comment.setText(String.valueOf(position + 1) + "#");
        return convertView;
    }

    class ViewHolder {
        ImageView comment_photo;//头像
        TextView comment_nickname;//用户昵称
        TextView comment_f_nickname;//父评论昵称
        TextView comment_time;//评论时间
        TextView comment_content;//评论内容
        TextView floor_comment;//评论楼层

    }
}
