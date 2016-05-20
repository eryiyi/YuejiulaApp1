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
import android.widget.ImageView;
import com.liangxun.yuejiula.R;

import java.util.List;

public class ExpressionAdapter extends ArrayAdapter<String> {
    private LayoutInflater inflater;

    public ExpressionAdapter(Context context, int textViewResourceId, List<String> objects) {
        super(context, textViewResourceId, objects);
        this.inflater = LayoutInflater.from(context);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        if (convertView == null) {
//            convertView = View.inflate(getContext(), R.layout.row_expression, null);
//        }
        String filename = getItem(position);
        int resId = getContext().getResources().getIdentifier(filename, "drawable", getContext().getPackageName());

        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_face, null);
            viewHolder.iv_face = (ImageView) convertView.findViewById(R.id.item_iv_face);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
//        if (emoji.getId() == R.drawable.face_del_icon) {
//            convertView.setBackgroundDrawable(null);
            viewHolder.iv_face.setImageResource(resId);
//        } else if (TextUtils.isEmpty(emoji.getCharacter())) {
//            convertView.setBackgroundDrawable(null);
//            viewHolder.iv_face.setImageDrawable(null);
//        } else {
//
//            viewHolder.iv_face.setImageResource(emoji.getId());
//        }
//        imageView.setImageResource(resId);
        return convertView;
    }

    class ViewHolder {
        public ImageView iv_face;
    }
}
