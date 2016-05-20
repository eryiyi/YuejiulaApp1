/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.liangxun.yuejiula.huanxin.chat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.adapter.OnClickContentItemListener;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.entity.Emp;
import com.liangxun.yuejiula.huanxin.chat.my.MyImageLoader;

import java.util.List;

/**
 * 显示搜到的所有好友adpater
 *
 */
public class ContactsListAdapter extends ArrayAdapter<com.liangxun.yuejiula.entity.Emp> {

    private LayoutInflater inflater;
    private List<Emp> Emp;
    private Context context;
    private OnClickContentItemListener onClickContentItemListener;
    private ImageLoader imageLoader;//图片加载类

    //private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
//	com.nostra13.universalimageloader.core.ImageLoader imageLoader = com.nostra13.universalimageloader.core.ImageLoader.getInstance();//图片加载类
    public void setOnClickContentItemListener(OnClickContentItemListener onClickContentItemListener) {
        this.onClickContentItemListener = onClickContentItemListener;
    }

    public ContactsListAdapter(Context context, int textViewResourceId, List<Emp> objects, ImageLoader imageLoader) {
        super(context, textViewResourceId, objects);
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.imageLoader = imageLoader;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.activity_so_contacts, parent, false);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.add = (Button) convertView.findViewById(R.id.indicator);
            holder.avatar = (NetworkImageView) convertView.findViewById(R.id.avatar);
            convertView.setTag(holder);
        }
//		if (position % 2 == 0) {
        convertView.setBackgroundResource(R.drawable.find_item_select);
//		} else {
//            convertView.setBackgroundResource(R.drawable.mm_listitem_grey);
//		}

        Emp emp = getItem(position);
        String username = emp.getEmpName();
        holder.name.setText(username);

        holder.avatar.setDefaultImageResId(R.drawable.default_image);
        holder.avatar.setErrorImageResId(R.drawable.default_image);
        String url=emp == null ? "" : emp.getEmpCover();
        if(url.indexOf("http")==-1){
            url= InternetURL.INTERNAL+url;
        }
        holder.avatar.setImageUrl(url, MyImageLoader.getInstance());
//		imageLoader.displayImage(InternetURL.INTERNAL+emp.getEmpCover(), holder.avatar , UniversityApplication.txOptions, animateFirstListener);

        final String s = getContext().getResources().getString(R.string.Add_a_friend);
        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickContentItemListener.onClickContentItem(position, 1, null);
            }
        });

        return convertView;
    }


    private static class ViewHolder {
        /** contact name */
        TextView name;
        Button add;
        NetworkImageView avatar;

        /** 整个list中每一行总布局 */

    }

}
