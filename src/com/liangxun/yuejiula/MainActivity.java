package com.liangxun.yuejiula;

import android.app.ProgressDialog;
import android.content.*;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.chat.*;
import com.easemob.util.EMLog;
import com.easemob.util.HanziToPinyin;

import com.liangxun.yuejiula.base.ActivityTack;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.EmpsDATA;
import com.liangxun.yuejiula.data.RelateDATA;
import com.liangxun.yuejiula.data.SchoolRecordMoodData;
import com.liangxun.yuejiula.entity.Emp;
import com.liangxun.yuejiula.entity.SchoolRecordMood;
import com.liangxun.yuejiula.face.FaceConversionUtil;
import com.liangxun.yuejiula.fragment.*;
import com.liangxun.yuejiula.huanxin.chat.HxConstant;
import com.liangxun.yuejiula.huanxin.chat.activity.ChatActivity;
import com.liangxun.yuejiula.huanxin.chat.db.HxUserDao;
import com.liangxun.yuejiula.huanxin.chat.db.InviteMessgeDao;
import com.liangxun.yuejiula.huanxin.chat.domain.HxUser;
import com.liangxun.yuejiula.huanxin.chat.domain.InviteMessage;
import com.liangxun.yuejiula.ui.LoginActivity;
import com.liangxun.yuejiula.ui.PublishPicActivity;
import com.liangxun.yuejiula.ui.UpdateProfilePersonalActivity;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.StringUtil;
import com.liangxun.yuejiula.util.Utils;
import com.liangxun.yuejiula.widget.popview.MenuPopMenu;
import com.liangxun.yuejiula.widget.popview.MoodPopMenu;
import com.umeng.update.UmengUpdateAgent;
import com.yixia.camera.demo.ui.record.MediaRecorderActivity;
import org.bitlet.weupnp.Main;

import java.util.*;

public class MainActivity extends BaseActivity implements View.OnClickListener,MenuPopMenu.OnItemClickListener,MoodPopMenu.OnItemClickListener {
    protected static final String TAG = "MainActivity";
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fm;

    private HomeFragment homeFragment;//首页
    private HxFragment classFragment;//童心堂
    private FindFragment findFragment;//发现
    private ProfileFragment partFragment;//我的
    private DianpuFragment goodsFragment;//商城

    private ImageView foot_one;
    private ImageView foot_two;
    private ImageView foot_three;
    private ImageView foot_four;
    private ImageView foot_five;

    Emp emp;

    private long waitTime = 2000;
    private long touchTime = 0;

    //设置底部图标
    Resources res;

    // 未读消息textview
    private TextView unreadLabel;
    // 未读通讯录textview
    private TextView unreadAddressLable;
    private int index;
    // 当前fragment的index
    private int currentTabIndex = 0;

    // 账号在别处登录
    public static boolean isConflict = false;
    //账号被移除
    private static boolean isCurrentAccountRemoved = false;
    private InviteMessgeDao inviteMessgeDao;
    private HxUserDao userDao;

    //下拉菜单
    private MenuPopMenu menu;
    List<String> arrayMenu = new ArrayList<>();
    private MoodPopMenu moodMenu;
    public static List<SchoolRecordMood> arrayMood = new ArrayList<SchoolRecordMood>();

    private TextView msg_find;
    private TextView msg_mine;


    /**
     * 检查当前用户是否被删除
     */
    public static boolean getCurrentAccountRemoved() {
        return isCurrentAccountRemoved;
    }

    private android.app.AlertDialog.Builder conflictBuilder;
    private android.app.AlertDialog.Builder accountRemovedBuilder;
    private boolean isConflictDialogShow;
    private boolean isAccountRemovedDialogShow;
    public static  String school_record_mood_id= "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBoradcastReceiver();
        //umeng自动更新
        UmengUpdateAgent.update(this);
        if (savedInstanceState != null && savedInstanceState.getBoolean(HxConstant.ACCOUNT_REMOVED, false)) {
            // 防止被移除后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
            // 三个fragment里加的判断同理
            getMyApp().logout(null);
            finish();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        } else if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false)) {
            // 防止被T后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
            // 三个fragment里加的判断同理
            finish();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        setContentView(R.layout.main);
        res = getResources();
        //表情
        new Thread(new Runnable() {
            @Override
            public void run() {
                FaceConversionUtil.getInstace().getFileText(getApplication());
            }
        }).start();

        emp = (Emp) getIntent().getSerializableExtra(Constants.ACCOUNT_KEY);
        fm = getSupportFragmentManager();
        initView();
        if (getIntent().getBooleanExtra("conflict", false) && !isConflictDialogShow) {
            showConflictDialog();
        } else if (getIntent().getBooleanExtra(HxConstant.ACCOUNT_REMOVED, false) && !isAccountRemovedDialogShow) {
            showAccountRemovedDialog();
        }
        inviteMessgeDao = new InviteMessgeDao(this);
        userDao = new HxUserDao(this);
        switchFragment(R.id.foot_one);
        // 注册一个接收消息的BroadcastReceiver
        IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
        intentFilter.setPriority(3);
        registerReceiver(msgReceiver, intentFilter);

        // 注册一个ack回执消息的BroadcastReceiver
        IntentFilter ackMessageIntentFilter = new IntentFilter(EMChatManager.getInstance().getAckMessageBroadcastAction());
        ackMessageIntentFilter.setPriority(3);
        registerReceiver(ackMessageReceiver, ackMessageIntentFilter);

        //注册一个透传消息的BroadcastReceiver
        IntentFilter cmdMessageIntentFilter = new IntentFilter(EMChatManager.getInstance().getCmdMessageBroadcastAction());
        cmdMessageIntentFilter.setPriority(3);
        registerReceiver(cmdMessageReceiver, cmdMessageIntentFilter);
        // setContactListener监听联系人的变化等
        EMContactManager.getInstance().setContactListener(new MyContactListener());
        // 注册一个监听连接状态的listener
        EMChatManager.getInstance().addConnectionListener(new MyConnectionListener());
        // 注册群聊相关的listener
        EMGroupManager.getInstance().addGroupChangeListener(new MyGroupChangeListener());
        // 通知sdk，UI 已经初始化完毕，注册了相应的receiver和listener, 可以接受broadcast了
        EMChat.getInstance().setAppInited();
        PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, Utils.getMetaValue(MainActivity.this, "push_api_key"));

        arrayMenu.add("冒泡");
        arrayMenu.add("秒拍");
        arrayMenu.add("相机");

        //查询心情标签
        getMood();
    }

    private void initView() {

        foot_one = (ImageView) this.findViewById(R.id.foot_one);
        foot_two = (ImageView) this.findViewById(R.id.foot_two);
        foot_three = (ImageView) this.findViewById(R.id.foot_three);
        foot_four = (ImageView) this.findViewById(R.id.foot_four);
        foot_five = (ImageView) this.findViewById(R.id.foot_five);
        foot_one.setOnClickListener(this);
        foot_two.setOnClickListener(this);
        foot_three.setOnClickListener(this);
        foot_four.setOnClickListener(this);
        foot_five.setOnClickListener(this);

        unreadLabel = (TextView) findViewById(R.id.home_item_photo).findViewById(R.id.unread_number);
        unreadAddressLable = (TextView) findViewById(R.id.home_item_photo).findViewById(R.id.unread_address_number);

//        msg_find = (TextView) this.findViewById(R.id.msg_find);
//        msg_mine = (TextView) this.findViewById(R.id.msg_mine);
    }

    @Override
    public void onClick(View v) {
        switchFragment(v.getId());
    }

    public void switchFragment(int id) {
        fragmentTransaction = fm.beginTransaction();
        hideFragments(fragmentTransaction);
        switch (id) {
            case R.id.foot_one:
                if (homeFragment == null) {
                    homeFragment = new HomeFragment();
                    fragmentTransaction.add(R.id.content_frame, homeFragment);
                } else {
                    fragmentTransaction.show(homeFragment);
                }
                currentTabIndex = 0;
                foot_one.setImageResource(R.drawable.foot_index_pressed);
                foot_two.setImageResource(R.drawable.foot_paopao);
                foot_three.setImageResource(R.drawable.foot_cart);
                foot_four.setImageResource(R.drawable.foot_find);
                foot_five.setImageResource(R.drawable.foot_mine);

                break;
            case R.id.foot_two:
                if (classFragment == null) {
                    classFragment = new HxFragment();
                    fragmentTransaction.add(R.id.content_frame, classFragment);
                } else {
                    fragmentTransaction.show(classFragment);
                }

                currentTabIndex = 1;
                foot_one.setImageResource(R.drawable.foot_index);
                foot_two.setImageResource(R.drawable.foot_paopao_pressed);
                foot_three.setImageResource(R.drawable.foot_cart);
                foot_four.setImageResource(R.drawable.foot_find);
                foot_five.setImageResource(R.drawable.foot_mine);
                break;
            case R.id.foot_three:
                if (goodsFragment == null) {
                    goodsFragment = new  DianpuFragment();
                    fragmentTransaction.add(R.id.content_frame, goodsFragment);
                } else {
                    fragmentTransaction.show(goodsFragment);
                }
                currentTabIndex = 3;
                foot_one.setImageResource(R.drawable.foot_index);
                foot_two.setImageResource(R.drawable.foot_paopao);
                foot_three.setImageResource(R.drawable.foot_cart_pressed);
                foot_four.setImageResource(R.drawable.foot_find);
                foot_five.setImageResource(R.drawable.foot_mine);
                break;
            case R.id.foot_four:
                if (findFragment == null) {
                    findFragment = new FindFragment();
                    fragmentTransaction.add(R.id.content_frame, findFragment);
                } else {
                    fragmentTransaction.show(findFragment);
                }
                currentTabIndex = 4;
                foot_one.setImageResource(R.drawable.foot_index);
                foot_two.setImageResource(R.drawable.foot_paopao);
                foot_three.setImageResource(R.drawable.foot_cart);
                foot_four.setImageResource(R.drawable.foot_find_pressed);
                foot_five.setImageResource(R.drawable.foot_mine);
                break;
            case R.id.foot_five:
                if (partFragment == null) {
                    partFragment = new  ProfileFragment();
                    fragmentTransaction.add(R.id.content_frame, partFragment);
                } else {
                    fragmentTransaction.show(partFragment);
                }
                currentTabIndex = 2;

                foot_one.setImageResource(R.drawable.foot_index);
                foot_two.setImageResource(R.drawable.foot_paopao);
                foot_three.setImageResource(R.drawable.foot_cart);
                foot_four.setImageResource(R.drawable.foot_find);
                foot_five.setImageResource(R.drawable.foot_mine_pressed);
                break;

        }
        fragmentTransaction.commit();
    }

    private void hideFragments(FragmentTransaction ft) {
        if (homeFragment != null) {
            ft.hide(homeFragment);
        }
        if (classFragment != null) {
            ft.hide(classFragment);
        }
        if (findFragment != null) {
            ft.hide(findFragment);
        }
        if (partFragment != null) {
            ft.hide(partFragment);
        }
        if (goodsFragment != null) {
            ft.hide(goodsFragment);
        }
    }



    //再摁退出程序
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        PackageManager pm = getPackageManager();
        ResolveInfo homeInfo =
                pm.resolveActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME), 0);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ActivityInfo ai = homeInfo.activityInfo;
            Intent startIntent = new Intent(Intent.ACTION_MAIN);
            startIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            startIntent.setComponent(new ComponentName(ai.packageName, ai.name));
            startActivitySafely(startIntent);
            return true;
        } else
            return super.onKeyDown(keyCode, event);
    }

    private void startActivitySafely(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "null",
                    Toast.LENGTH_SHORT).show();
        } catch (SecurityException e) {
            Toast.makeText(this, "null",
                    Toast.LENGTH_SHORT).show();
        }
    }

    void logout() {
        final ProgressDialog pd = new ProgressDialog(this);
        String st = getResources().getString(R.string.Are_logged_out);
        pd.setMessage(st);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        getMyApp().logout(new EMCallBack() {

            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        pd.dismiss();
                        // 重新显示登陆页面
                        finish();

                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {

            }
        });
    }

    /**
     * 显示帐号在别处登录dialog
     */
    private void showConflictDialog() {
        isConflictDialogShow = true;
        getMyApp().logout(null);
        String st = getResources().getString(R.string.Logoff_notification);
        if (!MainActivity.this.isFinishing()) {
            // clear up global variables
            try {
                if (conflictBuilder == null)
                    conflictBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
                conflictBuilder.setTitle(st);
                conflictBuilder.setMessage(R.string.connect_conflict);
                conflictBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        conflictBuilder = null;
                        logout();
                        getSp().edit().remove(Constants.EMPPASS).commit();//清空密码
                        ActivityTack.getInstanse().popUntilActivity(LoginActivity.class);
//                        finish();
//                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    }
                });
                conflictBuilder.setCancelable(false);
                conflictBuilder.create().show();
                isConflict = true;
            } catch (Exception e) {
                EMLog.e(TAG, "---------color conflictBuilder error" + e.getMessage());
            }

        }

    }


    /**
     * 帐号被移除的dialog
     */
    private void showAccountRemovedDialog() {
        isAccountRemovedDialogShow = true;
        getMyApp().logout(null);
        String st5 = getResources().getString(R.string.Remove_the_notification);
        if (!MainActivity.this.isFinishing()) {
            // clear up global variables
            try {
                if (accountRemovedBuilder == null)
                    accountRemovedBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
                accountRemovedBuilder.setTitle(st5);
                accountRemovedBuilder.setMessage(R.string.em_user_remove);
                accountRemovedBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        accountRemovedBuilder = null;
                        finish();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    }
                });
                accountRemovedBuilder.setCancelable(false);
                accountRemovedBuilder.create().show();
                isCurrentAccountRemoved = true;
            } catch (Exception e) {
                EMLog.e(TAG, "---------color userRemovedBuilder error" + e.getMessage());
            }

        }

    }

    /**
     * 新消息广播接收者
     */
    private BroadcastReceiver msgReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 主页面收到消息后，主要为了提示未读，实际消息内容需要到chat页面查看

            String from = intent.getStringExtra("from");
            String msgId = intent.getStringExtra("msgid");

            hxUserNameToEmpName(from, msgId);


        }
    };

    private void hxUserNameToEmpName(final String from, final String msgId) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
//                InternetURL.GET_FRIENDS_URL,
                InternetURL.GET_INVITE_CONTACT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            EmpsDATA data = getGson().fromJson(s, EmpsDATA.class);
                            if (data.getCode() == 200) {
                                if (data.getData().size() > 0) {
                                    myNotify(from, data.getData().get(0).getEmpName(), msgId);
                                } else {
                                    myNotify(from, from, msgId);
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "获得数据失败，请稍后重试", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "获得数据失败，请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(MainActivity.this, "获得数据失败，请稍后重试", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("hxUserNames", from);
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

    private void myNotify(String from, String empName, String msgId) {
        // 消息id
        EMMessage message = EMChatManager.getInstance().getMessage(msgId);
        // 2014-10-22 修复在某些机器上，在聊天页面对方发消息过来时不立即显示内容的bug
        if (ChatActivity.activityInstance != null) {
            if (message.getChatType() == EMMessage.ChatType.GroupChat) {
                if (message.getTo().equals(ChatActivity.activityInstance.getToChatUsername()))
                    return;
            } else {
                if (from.equals(ChatActivity.activityInstance.getToChatUsername()))
                    return;
            }
        }

        // 注销广播接收者，否则在ChatActivity中会收到这个广播
        msgReceiver.abortBroadcast();

        notifyNewMessage(message, empName);

        // 刷新bottom bar消息未读数
        updateUnreadLabel();
        if (currentTabIndex == 1) {
            // 当前页面如果为聊天历史页面，刷新此页面
            if (classFragment != null) {
                classFragment.refresh();
            }
        }
    }

    /**
     * 消息回执BroadcastReceiver
     */
    private BroadcastReceiver ackMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            abortBroadcast();

            String msgid = intent.getStringExtra("msgid");
            String from = intent.getStringExtra("from");

            EMConversation conversation = EMChatManager.getInstance().getConversation(from);
            if (conversation != null) {
                // 把message设为已读
                EMMessage msg = conversation.getMessage(msgid);

                if (msg != null) {

                    // 2014-11-5 修复在某些机器上，在聊天页面对方发送已读回执时不立即显示已读的bug
                    if (ChatActivity.activityInstance != null) {
                        if (msg.getChatType() == EMMessage.ChatType.Chat) {
                            if (from.equals(ChatActivity.activityInstance.getToChatUsername()))
                                return;
                        }
                    }

                    msg.isAcked = true;
                }
            }

        }
    };


    /**
     * 透传消息BroadcastReceiver
     */
    private BroadcastReceiver cmdMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            abortBroadcast();
            EMLog.d(TAG, "收到透传消息");
            //获取cmd message对象
            String msgId = intent.getStringExtra("msgid");
            EMMessage message = intent.getParcelableExtra("message");
            //获取消息body
            CmdMessageBody cmdMsgBody = (CmdMessageBody) message.getBody();
            String action = cmdMsgBody.action;//获取自定义action

            //获取扩展属性 此处省略
//			message.getStringAttribute("");
            EMLog.d(TAG, String.format("透传消息：action:%s,message:%s", action, message.toString()));
            String st9 = getResources().getString(R.string.receive_the_passthrough);
            Toast.makeText(MainActivity.this, st9 + action, Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 刷新未读消息数
     */
    public void updateUnreadLabel() {
        runOnUiThread(new Runnable() {
            public void run() {
                int count = getUnreadMsgCountTotal();
                int count1 = getUnreadAddressCountTotal();
                count += count1;
                if (count > 0) {
                    if(count > 99){
                        unreadLabel.setText("..");
                    }else {
                        unreadLabel.setText(String.valueOf(count));
                    }
                    unreadLabel.setVisibility(View.VISIBLE);
                } else {
                    unreadLabel.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    /**
     * 获取未读消息数
     *
     * @return
     */
    public int getUnreadMsgCountTotal() {
        int unreadMsgCountTotal = 0;
        unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
        return unreadMsgCountTotal;
    }




    /**
     * 好友变化listener
     */
    private class MyContactListener implements EMContactListener {

        @Override
        public void onContactAdded(List<String> usernameList) {

            StringBuffer strUser = new StringBuffer();
            for (int i = 0; i < usernameList.size(); i++) {
                strUser.append(usernameList.get(i));
                if (i < usernameList.size() - 1) {
                    strUser.append(",");
                }
            }
            getFriends(strUser.toString());

        }

        @Override
        public void onContactDeleted(final List<String> usernameList) {
            // 被删除
            Map<String, HxUser> localUsers = getMyApp().getContactList();
            for (String username : usernameList) {
                localUsers.remove(username);
                userDao.deleteContact(username);
                inviteMessgeDao.deleteMessage(username);
            }
            runOnUiThread(new Runnable() {
                public void run() {
                    // 如果正在与此用户的聊天页面
                    String st10 = getResources().getString(R.string.have_you_removed);
                    if (ChatActivity.activityInstance != null && usernameList.contains(ChatActivity.activityInstance.getToChatUsername())) {
                        Toast.makeText(MainActivity.this, ChatActivity.activityInstance.getToChatUsername() + st10, Toast.LENGTH_LONG).show();
                        ChatActivity.activityInstance.finish();
                    }
                    updateUnreadLabel();
                    // 刷新ui
                    if (currentTabIndex == 1)
                        classFragment.refresh();
                }
            });

        }

        @Override
        public void onContactInvited(String username, String reason) {
            // 接到邀请的消息，如果不处理(同意或拒绝)，掉线后，服务器会自动再发过来，所以客户端不需要重复提醒
            List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();

            for (InviteMessage inviteMessage : msgs) {
                if (inviteMessage.getGroupId() == null && inviteMessage.getFrom().equals(username)) {
                    inviteMessgeDao.deleteMessage(username);
                }
            }
            // 自己封装的javabean
            InviteMessage msg = new InviteMessage();
            msg.setFrom(username);
            msg.setTime(System.currentTimeMillis());
            msg.setReason(reason);
            Log.d(TAG, username + "请求加你为好友,reason: " + reason);
            // 设置相应status
            msg.setStatus(InviteMessage.InviteMesageStatus.BEINVITEED);
            notifyNewIviteMessage(msg);

        }

        @Override
        public void onContactAgreed(String username) {
            List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
            for (InviteMessage inviteMessage : msgs) {
                if (inviteMessage.getFrom().equals(username)) {
                    return;
                }
            }
            // 自己封装的javabean
            InviteMessage msg = new InviteMessage();
            msg.setFrom(username);
            msg.setTime(System.currentTimeMillis());
            Log.d(TAG, username + "同意了你的好友请求");
            msg.setStatus(InviteMessage.InviteMesageStatus.BEAGREED);
            notifyNewIviteMessage(msg);

        }

        @Override
        public void onContactRefused(String username) {
            // 参考同意，被邀请实现此功能,demo未实现
            Log.d(username, username + "拒绝了你的好友请求");
        }

    }

    /**
     * set head
     *
     * @param username
     * @return
     */
    HxUser setUserHead(String username) {
        HxUser user = new HxUser();
        user.setUsername(username);
        String headerName = null;
        if (!TextUtils.isEmpty(user.getNick())) {
            headerName = user.getNick();
        } else {
            headerName = user.getUsername();
        }
        if (username.equals(HxConstant.NEW_FRIENDS_USERNAME)) {
            user.setHeader("");
        } else if (Character.isDigit(headerName.charAt(0))) {
            user.setHeader("#");
        } else {
            user.setHeader(HanziToPinyin.getInstance().get(headerName.substring(0, 1)).get(0).target.substring(0, 1).toUpperCase());
            char header = user.getHeader().toLowerCase().charAt(0);
            if (header < 'a' || header > 'z') {
                user.setHeader("#");
            }
        }
        return user;
    }

    /**
     * 保存提示新消息
     *
     * @param msg
     */
    private void notifyNewIviteMessage(InviteMessage msg) {
        saveInviteMsg(msg);
        // 提示有新消息
        EMNotifier.getInstance(getApplicationContext()).notifyOnNewMsg();
        updateUnreadLabel();
        // 刷新好友页面ui
        if (currentTabIndex == 1)
            classFragment.refresh();
    }

    /**
     * 保存邀请等msg
     *
     * @param msg
     */
    private void saveInviteMsg(InviteMessage msg) {
        // 保存msg
        inviteMessgeDao.saveMessage(msg);
        // 未读数加1
        HxUser user = getMyApp().getContactList().get(HxConstant.NEW_FRIENDS_USERNAME);
//        if (user.getUnreadMsgCount() == 0)
        user.setUnreadMsgCount(user.getUnreadMsgCount() + 1);
    }


    /**
     * 获取未读申请与通知消息
     *
     * @return
     */
    public int getUnreadAddressCountTotal() {
        int unreadAddressCountTotal = 0;
        if (getMyApp().getContactList().get(HxConstant.NEW_FRIENDS_USERNAME) != null)
            unreadAddressCountTotal = getMyApp().getContactList().get(HxConstant.NEW_FRIENDS_USERNAME).getUnreadMsgCount();
        return unreadAddressCountTotal;
    }

    /**
     * 连接监听listener
     */
    private class MyConnectionListener implements EMConnectionListener {

        @Override
        public void onConnected() {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
//                    chatHistoryFragment.errorItem.setVisibility(View.GONE);
                }

            });
        }

        @Override
        public void onDisconnected(final int error) {
            final String st1 = getResources().getString(R.string.Less_than_chat_server_connection);
            final String st2 = getResources().getString(R.string.the_current_network);
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (error == EMError.USER_REMOVED) {
                        // 显示帐号已经被移除
                        showAccountRemovedDialog();
                    } else if (error == EMError.CONNECTION_CONFLICT) {
                        // 显示帐号在其他设备登陆dialog
                        showConflictDialog();
                    } else {
//                        chatHistoryFragment.errorItem.setVisibility(View.VISIBLE);
//                        if (NetUtils.hasNetwork(HxMainActivity.this))
//                            chatHistoryFragment.errorText.setText(st1);
//                        else
//                            chatHistoryFragment.errorText.setText(st2);

                    }
                }

            });
        }
    }

    /**
     * MyGroupChangeListener
     */
    private class MyGroupChangeListener implements GroupChangeListener {

        @Override
        public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {
            boolean hasGroup = false;
//			for (EMGroup group : EMGroupManager.getInstance().getAllGroups()) {
//				if (group.getGroupId().equals(groupId)) {
//					hasGroup = true;
//					break;
//				}
//			}
//			if (!hasGroup)
//				return;

            // 被邀请
            String st3 = getResources().getString(R.string.Invite_you_to_join_a_group_chat);
            EMMessage msg = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
            msg.setChatType(EMMessage.ChatType.GroupChat);
            msg.setFrom(inviter);
            msg.setTo(groupId);
            msg.setMsgId(UUID.randomUUID().toString());
            msg.addBody(new TextMessageBody(inviter + st3));
            // 保存邀请消息
            EMChatManager.getInstance().saveMessage(msg);
            // 提醒新消息
            EMNotifier.getInstance(getApplicationContext()).notifyOnNewMsg();

            runOnUiThread(new Runnable() {
                public void run() {
                    updateUnreadLabel();
                    // 刷新ui
                    if (currentTabIndex == 0)
                        classFragment.refresh();

                }
            });

        }

        @Override
        public void onInvitationAccpted(String groupId, String inviter, String reason) {

        }

        @Override
        public void onInvitationDeclined(String groupId, String invitee, String reason) {

        }

        @Override
        public void onUserRemoved(String groupId, String groupName) {
            // 提示用户被T了，demo省略此步骤
            // 刷新ui
            runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        updateUnreadLabel();
                        if (currentTabIndex == 0)
                            classFragment.refresh();

                    } catch (Exception e) {
                        EMLog.e(TAG, "refresh exception " + e.getMessage());
                    }
                }
            });
        }

        @Override
        public void onGroupDestroy(String groupId, String groupName) {
            // 群被解散
            // 提示用户群被解散,demo省略
            // 刷新ui
            runOnUiThread(new Runnable() {
                public void run() {
                    updateUnreadLabel();
                    if (currentTabIndex == 0)
                        classFragment.refresh();

                }
            });

        }

        @Override
        public void onApplicationReceived(String groupId, String groupName, String applyer, String reason) {
            // 用户申请加入群聊
            InviteMessage msg = new InviteMessage();
            msg.setFrom(applyer);
            msg.setTime(System.currentTimeMillis());
            msg.setGroupId(groupId);
            msg.setGroupName(groupName);
            msg.setReason(reason);
            Log.d(TAG, applyer + " 申请加入群聊：" + groupName);
            msg.setStatus(InviteMessage.InviteMesageStatus.BEAPPLYED);
            notifyNewIviteMessage(msg);
        }

        @Override
        public void onApplicationAccept(String groupId, String groupName, String accepter) {
            String st4 = getResources().getString(R.string.Agreed_to_your_group_chat_application);
            // 加群申请被同意
            EMMessage msg = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
            msg.setChatType(EMMessage.ChatType.GroupChat);
            msg.setFrom(accepter);
            msg.setTo(groupId);
            msg.setMsgId(UUID.randomUUID().toString());
            msg.addBody(new TextMessageBody(accepter + st4));
            // 保存同意消息
            EMChatManager.getInstance().saveMessage(msg);
            // 提醒新消息
            EMNotifier.getInstance(getApplicationContext()).notifyOnNewMsg();

            runOnUiThread(new Runnable() {
                public void run() {
                    updateUnreadLabel();
                    // 刷新ui
                    if (currentTabIndex == 0)
                        classFragment.refresh();

                }
            });
        }

        @Override
        public void onApplicationDeclined(String groupId, String groupName, String decliner, String reason) {
            // 加群申请被拒绝，demo未实现
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(msgReceiver);
        unregisterReceiver(ackMessageReceiver);
        unregisterReceiver(cmdMessageReceiver);
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isConflict || !isCurrentAccountRemoved) {
            updateUnreadLabel();
            EMChatManager.getInstance().activityResumed();
        }

    }

    private void convertoHxUser(List<Emp> hxUserList) {

        // 保存增加的联系人
        Map<String, HxUser> localUsers = getMyApp().getContactList();
        Map<String, HxUser> toAddUsers = new HashMap<String, HxUser>();
        for (Emp emp : hxUserList) {
            HxUser user = setUserHead(emp.getEmpName());
            user.setEmp(emp);
            // 添加好友时可能会回调added方法两次
            if (!localUsers.containsKey(emp.getEmpName())) {
                userDao.saveContact(user);
            }
            toAddUsers.put(emp.getEmpName(), user);
        }
        localUsers.putAll(toAddUsers);
        // 刷新ui
        if (currentTabIndex == 1)
            classFragment.refresh();
    }

    private void getFriends(final String userNames) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
//                InternetURL.GET_FRIENDS_URL,
                InternetURL.GET_INVITE_CONTACT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            EmpsDATA data = getGson().fromJson(s, EmpsDATA.class);
                            if (data.getCode() == 200) {
                                convertoHxUser(data.getData());

                            } else {
                                Toast.makeText(MainActivity.this, "获得数据失败，请稍后重试", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "获得数据失败，请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(MainActivity.this, "获得数据失败，请稍后重试", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("hxUserNames", userNames);
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


    //广播接收动作
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("_msg_notice")) {
                runOnUiThread(new Runnable() {
                    public void run() {
//                        String num_notice = msg_find.getText().toString();
//                        if(StringUtil.isNullOrEmpty(num_notice) || "0".equals(msg_find)){
//                            msg_find.setText("1");
//                        }else {
//                            msg_find.setText(String.valueOf(Integer.parseInt(msg_find.getText().toString())+1));
//                        }
//                        msg_find.setVisibility(View.VISIBLE);
                    }
                });


            }
            if (action.equals("_msg_record")) {
                runOnUiThread(new Runnable() {
                    public void run() {
//                        String num_notice = msg_mine.getText().toString();
//                        if(StringUtil.isNullOrEmpty(num_notice) || "0".equals(msg_mine)){
//                            msg_mine.setText("1");
//                        }else {
//                            msg_mine.setText(String.valueOf(Integer.parseInt(msg_mine.getText().toString())+1));
//                        }
//                        msg_mine.setVisibility(View.VISIBLE);
                    }
                });

            }
        }
    }  ;

    //注册广播
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("_msg_notice");//有新的通知notice
        myIntentFilter.addAction("_msg_record");//有新的动态
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }


    //弹出顶部主菜单
    public void onTopMenuPopupButtonClick(View view) {
        //顶部右侧按钮
        menu = new MenuPopMenu(MainActivity.this, arrayMenu);
        menu.setOnItemClickListener(this);
        menu.showAsDropDown(view);
    }
    public void onTopHeadButtonClick(View view){
//        Intent profile = new Intent(MainActivity.this, UpdateProfilePersonalActivity.class);
//        startActivity(profile);
        moodMenu = new MoodPopMenu(MainActivity.this, arrayMood);
        moodMenu.setOnItemClickListener(this);
        moodMenu.showAsDropDown(view);
    }

    @Override
    public void onItemClick(int index, String str) {
        if("000".equals(str)){
            switch (index) {
                case 0:
                    Intent pic = new Intent(MainActivity.this, PublishPicActivity.class);
                    pic.putExtra(Constants.SELECT_PHOTOORPIIC, "0");
                    startActivity(pic);
                    break;
                case 1:
                    save(Constants.PK_ADD_VIDEO_TYPE, "0");
                    Intent intent = new Intent(MainActivity.this, MediaRecorderActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
                    break;
                case  2:
                    Intent photo = new Intent(MainActivity.this, PublishPicActivity.class);
                    photo.putExtra(Constants.SELECT_PHOTOORPIIC, "1");
                    startActivity(photo);
                    break;
            }
        }
        if("111".equals(str)){
            //根据心情查询动态
            SchoolRecordMood schoolRecordMood = arrayMood.get(index);
            if(schoolRecordMood != null){
                school_record_mood_id = schoolRecordMood.getSchool_record_mood_id();
                //发通知  更新朋友圈
                homeFragment.IS_REFRESH = true;
                homeFragment.initData();
            }
        }
    }

    void getMood(){
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_MOOD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SchoolRecordMoodData data = getGson().fromJson(s, SchoolRecordMoodData.class);
                            if (data.getCode() == 200) {
                               arrayMood.clear();
                               arrayMood.addAll(data.getData());
                            } else {
                                Toast.makeText(MainActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(MainActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
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