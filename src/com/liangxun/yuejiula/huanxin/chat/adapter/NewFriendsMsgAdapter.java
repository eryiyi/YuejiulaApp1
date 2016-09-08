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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
import com.android.volley.toolbox.NetworkImageView;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.huanxin.chat.db.InviteMessgeDao;
import com.liangxun.yuejiula.huanxin.chat.domain.InviteMessage;
import com.liangxun.yuejiula.huanxin.chat.my.MyImageLoader;
import com.liangxun.yuejiula.ui.ProfilePersonalActivity;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.StringUtil;

import java.util.List;

public class NewFriendsMsgAdapter extends ArrayAdapter<InviteMessage> {

    private Context context;
    private InviteMessgeDao messgeDao;

    public NewFriendsMsgAdapter(Context context, int textViewResourceId, List<InviteMessage> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        messgeDao = new InviteMessgeDao(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.row_invite_msg, null);
            holder.aaa = (RelativeLayout) convertView.findViewById(R.id.aaa);
            holder.avator = (NetworkImageView) convertView.findViewById(R.id.avatar);
            holder.reason = (TextView) convertView.findViewById(R.id.message);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.status = (Button) convertView.findViewById(R.id.user_state);
            holder.groupContainer = (LinearLayout) convertView.findViewById(R.id.ll_group);
            holder.groupname = (TextView) convertView.findViewById(R.id.tv_groupName);
            // holder.time = (TextView) convertView.findViewById(R.id.time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String str1 = context.getResources().getString(R.string.Has_agreed_to_your_friend_request);
        String str2 = context.getResources().getString(R.string.agree);

        String str3 = context.getResources().getString(R.string.Request_to_add_you_as_a_friend);
        String str4 = context.getResources().getString(R.string.Apply_to_the_group_of);
        String str5 = context.getResources().getString(R.string.Has_agreed_to);
        String str6 = context.getResources().getString(R.string.Has_refused_to);
        final InviteMessage msg = getItem(position);
        if (msg != null) {
            if (msg.getGroupId() != null) { // 显示房间提示
                holder.groupContainer.setVisibility(View.VISIBLE);
                holder.groupname.setText(msg.getGroupName());
            } else {
                holder.groupContainer.setVisibility(View.GONE);
            }

            holder.reason.setText(msg.getReason());
            holder.name.setText(msg.getEmp() == null ? msg.getFrom() : msg.getEmp().getEmpName());
            holder.avator.setDefaultImageResId(R.drawable.default_image);
            holder.avator.setErrorImageResId(R.drawable.default_image);
            holder.avator.setImageUrl((msg.getEmp() == null ? "" : msg.getEmp().getEmpCover()), MyImageLoader.getInstance());
            // holder.time.setText(DateUtils.getTimestampString(new
            // Date(msg.getTime())));
            if (msg.getStatus() == InviteMessage.InviteMesageStatus.BEAGREED) {
                holder.status.setVisibility(View.INVISIBLE);
                holder.reason.setText(str1);
            } else if (msg.getStatus() == InviteMessage.InviteMesageStatus.BEINVITEED || msg.getStatus() == InviteMessage.InviteMesageStatus.BEAPPLYED) {
                holder.status.setVisibility(View.VISIBLE);
                holder.status.setEnabled(true);
                holder.status.setBackgroundResource(android.R.drawable.btn_default);
                holder.status.setText(str2);
                if (msg.getStatus() == InviteMessage.InviteMesageStatus.BEINVITEED) {
                    if (StringUtil.isNullOrEmpty(msg.getReason())) {
                        // 如果没写理由
                        holder.reason.setText(str3);
                    }
                } else { //入房间申请
                    if (TextUtils.isEmpty(msg.getReason())) {
                        holder.reason.setText(str4 + msg.getGroupName());
                    }
                }
                // 设置点击事件
                holder.status.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // 同意别人发的好友请求
                        acceptInvitation(holder.status, msg);
                    }
                });
//                holder.user_refuse.setOnClickListener(new OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        // 拒绝别人发的好友请求
//                        refuseInvitation(holder.user_refuse, msg);
//                    }
//                });
                holder.aaa.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ProfilePersonalActivity.class);
                        intent.putExtra(Constants.EMPID, msg.getEmp().getEmpId());
                        context.startActivity(intent);
                    }
                });
            } else if (msg.getStatus() == InviteMessage.InviteMesageStatus.AGREED) {
                holder.status.setText(str5);
                holder.status.setBackgroundDrawable(null);
                holder.status.setEnabled(false);
            } else if (msg.getStatus() == InviteMessage.InviteMesageStatus.REFUSED) {
                holder.status.setText(str6);
                holder.status.setBackgroundDrawable(null);
                holder.status.setEnabled(false);
            }
//            else if(msg.getStatus() == InviteMessage.InviteMesageStatus.BEREFUSED){
//                holder.status.setText(str6);
//                holder.status.setBackgroundDrawable(null);
//                holder.status.setEnabled(false);
//            }

            // 设置用户头像
        }

        return convertView;
    }

    /**
     * 同意好友请求或者房间申请
     *
     * @param button
     * @param
     */
    private void acceptInvitation(final Button button, final InviteMessage msg) {
        final ProgressDialog pd = new ProgressDialog(context);
        String str1 = context.getResources().getString(R.string.Are_agree_with);
        final String str2 = context.getResources().getString(R.string.Has_agreed_to);
        final String str3 = context.getResources().getString(R.string.Agree_with_failure);
        pd.setMessage(str1);
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        new Thread(new Runnable() {
            public void run() {
                // 调用sdk的同意方法
                try {
                    if (msg.getGroupId() == null) //同意好友请求
                        EMChatManager.getInstance().acceptInvitation(msg.getFrom());
                    else //同意加房间申请
                        EMGroupManager.getInstance().acceptApplication(msg.getFrom(), msg.getGroupId());
                    ((Activity) context).runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            pd.dismiss();
                            button.setText(str2);
                            msg.setStatus(InviteMessage.InviteMesageStatus.AGREED);
                            // 更新db
                            ContentValues values = new ContentValues();
                            values.put(InviteMessgeDao.COLUMN_NAME_STATUS, msg.getStatus().ordinal());
                            messgeDao.updateMessage(msg.getId(), values);
                            button.setBackgroundDrawable(null);
                            button.setEnabled(false);

                        }
                    });
                } catch (final Exception e) {
                    ((Activity) context).runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(context, str3 + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }
        }).start();
    }

    /**
     * 拒绝好友请求或者房间申请
     *
     * @param button
     * @param
     */
    private void refuseInvitation(final Button button, final InviteMessage msg) {
        final ProgressDialog pd = new ProgressDialog(context);
        String str1 = context.getResources().getString(R.string.Are_refuse_with);
        final String str2 = context.getResources().getString(R.string.Has_refused_to);
        final String str3 = context.getResources().getString(R.string.Refuse_with_failure);
        pd.setMessage(str1);
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        new Thread(new Runnable() {
            public void run() {
                // 调用sdk的同意方法
                try {
                    if (msg.getGroupId() == null) //拒绝好友请求
                        EMChatManager.getInstance().refuseInvitation(msg.getFrom());
                    else //同意加房间申请
                        EMGroupManager.getInstance().acceptApplication(msg.getFrom(), msg.getGroupId());
                    ((Activity) context).runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            pd.dismiss();
                            button.setText(str2);
                            msg.setStatus(InviteMessage.InviteMesageStatus.AGREED);
                            // 更新db
                            ContentValues values = new ContentValues();
                            values.put(InviteMessgeDao.COLUMN_NAME_STATUS, msg.getStatus().ordinal());
                            messgeDao.updateMessage(msg.getId(), values);
                            button.setBackgroundDrawable(null);
                            button.setEnabled(false);

                        }
                    });
                } catch (final Exception e) {
                    ((Activity) context).runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(context, str3 + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }
        }).start();
    }

    private static class ViewHolder {
        NetworkImageView avator;
        RelativeLayout aaa;
        TextView name;
        TextView reason;
        Button status;
//        Button user_refuse;
        LinearLayout groupContainer;
        TextView groupname;
        // TextView time;
    }

}
