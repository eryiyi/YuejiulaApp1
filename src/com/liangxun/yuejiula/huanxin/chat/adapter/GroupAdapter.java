/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import android.widget.TextView;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.liangxun.yuejiula.MainActivity;
import com.liangxun.yuejiula.R;

import java.util.List;

public class GroupAdapter extends ArrayAdapter<EMGroup> {

	private LayoutInflater inflater;
	private String newGroup;

	public GroupAdapter(Context context, int res, List<EMGroup> groups) {
		super(context, res, groups);
		this.inflater = LayoutInflater.from(context);
		newGroup = context.getResources().getString(R.string.The_new_group_chat);
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		if (position == 0) {
			return 0;
		}
		else {
			return 1;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (getItemViewType(position) == 0) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.row_add_group, null);
			}
			((ImageView) convertView.findViewById(R.id.avatar)).setImageResource(R.drawable.create_group);
			((TextView) convertView.findViewById(R.id.name)).setText(newGroup);
		}else {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.row_group, null);
			}
			((TextView) convertView.findViewById(R.id.name)).setText(getItem(position -1).getGroupName());
			TextView unread_msg_number = (TextView) convertView.findViewById(R.id.unread_msg_number);
			for(EMGroup emGroup: MainActivity.grouplist){
				if(getItem(position -1).getGroupId().equals(emGroup.getGroupId())){
					EMConversation conversation = EMChatManager.getInstance().getConversation(emGroup.getGroupId());
					int qun = conversation.getUnreadMsgCount();
					if (qun > 0) {
						unread_msg_number.setText(String.valueOf(qun));
						unread_msg_number.setVisibility(View.VISIBLE);
					} else {
						unread_msg_number.setVisibility(View.INVISIBLE);
					}
				}

			}
		}
		return convertView;
	}

	@Override
	public int getCount() {
		return super.getCount() + 1;
	}

}