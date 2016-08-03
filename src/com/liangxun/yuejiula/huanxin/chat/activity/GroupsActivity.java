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
package com.liangxun.yuejiula.huanxin.chat.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupInfo;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.liangxun.yuejiula.MainActivity;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.ContractSchoolDATA;
import com.liangxun.yuejiula.entity.ContractSchool;
import com.liangxun.yuejiula.entity.FhFqObj;
import com.liangxun.yuejiula.huanxin.applib.controller.HXSDKHelper;
import com.liangxun.yuejiula.huanxin.chat.adapter.GroupAdapter;
import com.liangxun.yuejiula.ui.ProfilePersonalActivity;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.StringUtil;
import org.bitlet.weupnp.Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupsActivity extends BaseActivity {
	public static final String TAG = "GroupsActivity";
	private ListView groupListView;
//	protected List<EMGroup> grouplist = new ArrayList<EMGroup>();
	private GroupAdapter groupAdapter;
	private InputMethodManager inputMethodManager;
	public static GroupsActivity instance;
	private SyncListener syncListener;
	private View progressBar;
	private SwipeRefreshLayout swipeRefreshLayout;

	class SyncListener implements HXSDKHelper.HXSyncListener {
		@Override
		public void onSyncSucess(final boolean success) {
			EMLog.d(TAG, "onSyncGroupsFinish success:" + success);
			runOnUiThread(new Runnable() {
				public void run() {
					swipeRefreshLayout.setRefreshing(false);
					if (success) {
						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								Intent intent1 = new Intent("add_new_group_success");
								sendBroadcast(intent1);
								refresh();
								progressBar.setVisibility(View.GONE);
							}
						}, 1000);
					} else {
						if (!GroupsActivity.this.isFinishing()) {
							String s1 = getResources()
									.getString(
											R.string.Failed_to_get_group_chat_information);
							Toast.makeText(GroupsActivity.this, s1, Toast.LENGTH_LONG).show();
							progressBar.setVisibility(View.GONE);
						}
					}
				}
			});
		}
	}

	//定义Handler对象
	private Handler handler =new Handler(){
		@Override
//当有消息发送出来的时候就执行Handler的这个方法
		public void handleMessage(Message msg){
			super.handleMessage(msg);
//处理UI

		}
	};
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_groups);

		instance = this;
		inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		//过滤下  只要当前管理员建设的群EMGroup
//		grouplist.clear();
//		for(EMGroupInfo emGroup:lists){
//			final EMGroup group = null;
//			group = EMGroupManager.getInstance().getGroupFromServer(emGroup.getGroupId());
//			if(group != null && !StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("manager_hxusername", ""), String.class)) && getGson().fromJson(getSp().getString("manager_hxusername", ""), String.class).equals(group.getOwner())){
//				grouplist.add(group);
//			}
//		}
		groupListView = (ListView) findViewById(R.id.list);
		
		swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
		swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
		                android.R.color.holo_orange_light, android.R.color.holo_red_light);
		swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
			    MainActivity.asyncFetchGroupsFromServer();
			}
		});
		
		groupAdapter = new GroupAdapter(this, 1, MainActivity.grouplist);
		groupListView.setAdapter(groupAdapter);
		groupListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
				if (position == 0) {
					// 新建群聊
					//先判断是否是承包商  只有承包商可以新建群
					if("3".equals(getGson().fromJson(getSp().getString(Constants.EMPTYPE, ""), String.class))){
						//是承包商
						startActivityForResult(new Intent(GroupsActivity.this, NewGroupActivity.class), 0);
					}else {
						showMsg(GroupsActivity.this, "您不是承包商，没有权限新建群组！");
					}

				}
//				else if (position == 1) {
//					// 添加公开群
//					if("1".equals(getGson().fromJson(getSp().getString("is_fengqun", ""), String.class))){
//						//如果fengqun了
//						showMsgFenghao();
//					}else {
//						startActivityForResult(new Intent(GroupsActivity.this, PublicGroupsActivity.class), 0);
//					}
//
//				}
				else {
					// 进入群聊
//					if("1".equals(getGson().fromJson(getSp().getString("is_fengqun", ""), String.class))){
						//如果fengqun了
						boolean flag = true;
						if(MainActivity.listFq != null){
							for(FhFqObj fhFqObj:MainActivity.listFq){
								if(fhFqObj.getSchool_id().equals(getGson().fromJson(getSp().getString(Constants.SCHOOLID, ""), String.class))){
									//当前登录者的学校ID
									flag = false;
									break;
								}
							}
						}
						if(!flag){
							showMsgFenghao();
						}else{
//					}else {
						//自己不在群中
						if(!groupAdapter.getItem(position - 1).getMembers().contains(EMChatManager.getInstance().getCurrentUser())){
//							addToGroup(groupAdapter.getItem(position - 1));
							new Thread(new Runnable() {
								@Override
								public void run() {
									try {
										EMGroupManager.getInstance().joinGroup(groupAdapter.getItem(position - 1).getGroupId());//需异步处理
										Intent intent = new Intent(GroupsActivity.this, ChatActivity.class);
										// it is group chat
										intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
										intent.putExtra("groupId", groupAdapter.getItem(position - 1).getGroupId());
										startActivityForResult(intent, 0);
									} catch (EaseMobException e) {
										e.printStackTrace();
									}
								}
							}).start();
						}else {
							Intent intent = new Intent(GroupsActivity.this, ChatActivity.class);
							// it is group chat
							intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
							intent.putExtra("groupId", groupAdapter.getItem(position - 1).getGroupId());
							startActivityForResult(intent, 0);
						}
//					}
						}

				}
			}

		});
		groupListView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
					if (getCurrentFocus() != null)
						inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
				}
				return false;
			}
		});
		
		progressBar = (View)findViewById(R.id.progress_bar);
		
		syncListener = new SyncListener();
		HXSDKHelper.getInstance().addSyncGroupListener(syncListener);

		if (!HXSDKHelper.getInstance().isGroupsSyncedWithServer()) {
			progressBar.setVisibility(View.VISIBLE);
		} else {
			progressBar.setVisibility(View.GONE);
		}

		refresh();
	}

	//加入群聊
	public void addToGroup(final EMGroup group){
		String st1 = getResources().getString(R.string.Is_sending_a_request);
		final String st2 = getResources().getString(R.string.Request_to_join);
		final String st3 = getResources().getString(R.string.send_the_request_is);
		final String st4 = getResources().getString(R.string.Join_the_group_chat);
		final String st5 = getResources().getString(R.string.Failed_to_join_the_group_chat);
		final ProgressDialog pd = new ProgressDialog(this);
//		getResources().getString(R.string)
		pd.setMessage(st1);
		pd.setCanceledOnTouchOutside(false);
		pd.show();
		new Thread(new Runnable() {
			public void run() {
				try {
					//如果是membersOnly的群，需要申请加入，不能直接join
					if(group.isMembersOnly()){
						EMGroupManager.getInstance().applyJoinToGroup(group.getGroupId(), st2);
					}else{
						EMGroupManager.getInstance().joinGroup(group.getGroupId());
					}
					runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							if(group.isMembersOnly())
								Toast.makeText(GroupsActivity.this, st3, Toast.LENGTH_SHORT).show();
							else
								Toast.makeText(GroupsActivity.this, st4, Toast.LENGTH_SHORT).show();
						}
					});
				} catch (final EaseMobException e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							Toast.makeText(GroupsActivity.this, st5+e.getMessage(), Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
		}).start();
	}

	/**
	 * 进入公开群聊列表
	 */
	public void onPublicGroups(View view) {
		startActivity(new Intent(this, PublicGroupsActivity.class));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onResume() {
		super.onResume();
//		List<EMGroup> lists = EMGroupManager.getInstance().getAllGroups();//获得所有群
//		//过滤下  只要当前管理员建设的群
//		grouplist.clear();
//		for(EMGroup emGroup:lists){
//			if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("manager_hxusername", ""), String.class)) && getGson().fromJson(getSp().getString("manager_hxusername", ""), String.class).equals(emGroup.getOwner())){
//				grouplist.add(emGroup);
//			}
//		}

		groupAdapter = new GroupAdapter(this, 1, MainActivity.grouplist);
		groupListView.setAdapter(groupAdapter);
		groupAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onDestroy() {
		if (syncListener != null) {
			HXSDKHelper.getInstance().removeSyncGroupListener(syncListener);
			syncListener = null;
		}
		super.onDestroy();
		instance = null;
	}
	
	public void refresh() {
		if (groupListView != null && groupAdapter != null) {
//			List<EMGroup> lists = EMGroupManager.getInstance().getAllGroups();//获得所有群
//			//过滤下  只要当前管理员建设的群
//			grouplist.clear();
//			for(EMGroup emGroup:lists){
//				if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("manager_hxusername", ""), String.class)) && getGson().fromJson(getSp().getString("manager_hxusername", ""), String.class).equals(emGroup.getOwner())){
//					grouplist.add(emGroup);
//				}
//			}
			groupAdapter = new GroupAdapter(GroupsActivity.this, 1,
					MainActivity.grouplist);
			groupListView.setAdapter(groupAdapter);
			groupAdapter.notifyDataSetChanged();
//			Intent intent1 = new Intent("add_new_group_success");
//			sendBroadcast(intent1);
		}
	}

	/**
	 * 返回
	 * 
	 * @param view
	 */
	public void back(View view) {
		finish();
	}

	private void showMsgFenghao() {
		final Dialog picAddDialog = new Dialog(GroupsActivity.this, R.style.dialog);
		View picAddInflate = View.inflate(this, R.layout.msg_mine_dialog, null);
		TextView jubao_sure = (TextView) picAddInflate.findViewById(R.id.jubao_sure);
		final TextView content = (TextView) picAddInflate.findViewById(R.id.content);
		content.setText("您已被本区域管理员请出群，快去找他说说好话吧！");
		//举报提交
		jubao_sure.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getManagerBySchoolId();
				picAddDialog.dismiss();
			}
		});

		//举报取消
		TextView jubao_cancle = (TextView) picAddInflate.findViewById(R.id.jubao_cancle);
		jubao_cancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				picAddDialog.dismiss();
			}
		});
		picAddDialog.setContentView(picAddInflate);
		picAddDialog.show();
	}

	void getManagerBySchoolId(){
		StringRequest request = new StringRequest(
				Request.Method.POST,
				InternetURL.GET_SCHOOL_MANAGER_BY_ID,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String s) {
						if (StringUtil.isJson(s)) {
							ContractSchoolDATA data = getGson().fromJson(s, ContractSchoolDATA.class);
							if (data.getCode() == 200) {
								List<ContractSchool> contractSchools1 = data.getData();
								if(contractSchools1 != null && contractSchools1.size()>0){
									ContractSchool contractSchool = contractSchools1.get(0);
									Intent profileV = new Intent(GroupsActivity.this, ProfilePersonalActivity.class);
									profileV.putExtra(Constants.EMPID, contractSchool.getEmpId());
									startActivity(profileV);
								}
							} else {
								Toast.makeText(GroupsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
							}
						} else {
							Toast.makeText(GroupsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
						}
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError volleyError) {
						Toast.makeText(GroupsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
					}
				}
		) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put("school_id", getGson().fromJson(getSp().getString(Constants.SCHOOLID, ""), String.class) );
				return params;
			}

			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put("Content-Type", "application/x-www-form-urlencoded");
				return params;
			}
		};
		getRequestQueue().add(request);
	}
}
