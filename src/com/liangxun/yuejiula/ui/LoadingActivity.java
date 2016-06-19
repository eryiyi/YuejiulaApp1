package com.liangxun.yuejiula.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.HanziToPinyin;
import com.liangxun.yuejiula.MainActivity;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.adapter.AnimateFirstDisplayListener;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.EmpDATA;
import com.liangxun.yuejiula.data.EmpsDATA;
import com.liangxun.yuejiula.entity.Ad;
import com.liangxun.yuejiula.entity.Emp;
import com.liangxun.yuejiula.huanxin.chat.HxConstant;
import com.liangxun.yuejiula.huanxin.chat.db.HxUserDao;
import com.liangxun.yuejiula.huanxin.chat.domain.HxUser;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: ${zhanghailong}
 * Date: 2015/1/29
 * Time: 21:55
 * 类的功能、说明写在此处.
 */
public class LoadingActivity extends BaseActivity implements View.OnClickListener, Runnable {
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private Emp empTmp;
    List<Emp> emps = new ArrayList<Emp>();

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    private ImageView loading_ad;
    private Ad ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ad = (Ad) getIntent().getExtras().get("ad");
        setContentView(R.layout.loading_activity);
        loading_ad = (ImageView) this.findViewById(R.id.loading_ad);
        imageLoader.displayImage(ad.getAdPic(), loading_ad, null, animateFirstListener);
        // 启动一个线程
        new Thread(LoadingActivity.this).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    public void run() {
        try {
            // 3秒后跳转到登录界面
            Thread.sleep(1000);
            SharedPreferences.Editor editor = getSp().edit();
            boolean isFirstRun = getSp().getBoolean("isFirstRun", true);
            if (isFirstRun) {
                editor.putBoolean("isFirstRun", false);
                editor.commit();
                Intent loadIntent = new Intent(LoadingActivity.this, AboutActivity.class);
                startActivity(loadIntent);
                finish();
            } else {
                //判断是否登陆过
                String username = getGson().fromJson(getSp().getString(Constants.EMPMOBILE, ""), String.class);
                String pwr = getGson().fromJson(getSp().getString(Constants.EMPPASS, ""), String.class);
                if (!StringUtil.isNullOrEmpty(username) && !StringUtil.isNullOrEmpty(pwr)) {
                    login();
                }else{
                    startActivity(new Intent(LoadingActivity.this, LoginActivity.class));
                    finish();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void login() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            EmpDATA data = getGson().fromJson(s, EmpDATA.class);
                            if (data.getCode() == 200) {
                                saveAccount(data.getData());
                                empTmp = data.getData();
                                hxLogin();//进行环信账号的登陆
                            }else{
                                Intent login = new Intent(LoadingActivity.this, LoginActivity.class);
                                startActivity(login);
                            }
                        } else {
                            Intent login = new Intent(LoadingActivity.this, LoginActivity.class);
                            startActivity(login);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Intent login = new Intent(LoadingActivity.this, LoginActivity.class);
                        startActivity(login);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", getGson().fromJson(getSp().getString(Constants.EMPMOBILE, ""), String.class));
                params.put("password", getGson().fromJson(getSp().getString(Constants.EMPPASS, ""), String.class));
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

    public void saveAccount(Emp emp) {
        // 登陆成功，保存用户名密码
        getMyApp().setUserName(emp.getHxUsername());
        getMyApp().setPassword(emp.getEmpPass());
        getMyApp().setCurrentEmp(emp);
        getMyApp().setGroupId(emp.getGroupId());
        save(Constants.EMPID, emp.getEmpId());
        save(Constants.EMPMOBILE, emp.getEmpMobile());
        save(Constants.EMPNAME, emp.getEmpName());
        save(Constants.EMPCOVER, emp.getEmpCover());
        save(Constants.EMPSEX, emp.getEmpSex());
        save(Constants.EMPSIGN, emp.getEmpSign());
        save(Constants.EMPQQ, emp.getEmpQQ());
        save(Constants.SCHOOLID, emp.getSchoolId());
        save(Constants.ISUSE, emp.getIsUse());
        save(Constants.DATELINE, emp.getDateline());
        save(Constants.LEVELNAME, emp.getLevelName());
        save(Constants.UNIVERSITYNAME, emp.getUniversityName());
        save("is_fenghao", emp.getIs_fenghao());
        save("is_fengqun", emp.getIs_fengqun());
        //类别
        save(Constants.EMPTYPE, emp.getEmpTypeId());
        save(Constants.HX_USERNAME, emp.getHxUsername());
    }


    /**
     * 环信账号登陆，
     * 获取好友列表，黑名单列表，群聊列表
     */
    private void hxLogin() {
        final String userName = getMyApp().getUserName();
        final String md5 = getMyApp().getPassword();
        getMyApp().setCurrentUserNick(getMyApp().getCurrentEmp().getHxUsername());
        // 调用sdk登陆方法登陆聊天服务器
        EMChatManager.getInstance().login(userName, md5, new EMCallBack() {
            @Override
            public void onSuccess() {
                try {

                    // ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
                    // ** manually load all local groups and
                    // conversations in case we are auto login
                    EMGroupManager.getInstance().loadAllGroups();
                    EMChatManager.getInstance().loadAllConversations();
                    //处理好友和群组
                    processContactsAndGroups();
                } catch (Exception e) {
                    e.printStackTrace();
                    //取好友或者群聊失败，不让进入主页面
                    runOnUiThread(new Runnable() {
                        public void run() {
                            getMyApp().logout(false,null);
                        }
                    });
                    return;
                }
                //更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
                EMChatManager.getInstance().updateCurrentUserNick(getMyApp().getCurrentUserNick().trim());
            }
            @Override
            public void onProgress(int progress, String status) {
            }
            @Override
            public void onError(final int code, final String message) {
                Intent main = new Intent(LoadingActivity.this, MainActivity.class);
                main.putExtra(Constants.ACCOUNT_KEY, empTmp);
                startActivity(main);
            }
        });
    }

    private void processContactsAndGroups() throws EaseMobException {
        // demo中简单的处理成每次登陆都去获取好友username，开发者自己根据情况而定
        //
        List<String> hxUserNames = EMContactManager.getInstance().getContactUserNames();
        //需要获取好友的头像等信息
        StringBuffer strUser = new StringBuffer();
        for (int i = 0; i < hxUserNames.size(); i++) {
            strUser.append(hxUserNames.get(i));
            if (i < hxUserNames.size() - 1) {
                strUser.append(",");
            }
        }
        //调用
        getFriends(strUser.toString());

    }


    /**
     * 设置hearder属性，方便通讯中对联系人按header分类显示，以及通过右侧ABCD...字母栏快速定位联系人
     *
     * @param username
     * @param user
     */
    protected void setUserHeader(String username, HxUser user) {
        String headerName = null;
        if (!TextUtils.isEmpty(username)) {
            headerName = username;
        } else {
            headerName = user.getUsername();
        }
        if (!StringUtil.isNullOrEmpty(headerName)) {
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
        }
    }

    public void getFriends(final String names) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_INVITE_CONTACT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            EmpsDATA data = getGson().fromJson(s, EmpsDATA.class);
                            if (data.getCode() == 200) {
                                emps = data.getData();
                                getMyApp().setEmps(emps);
                                initData();
                            } else {
                                Toast.makeText(LoadingActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LoadingActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(LoadingActivity.this, R.string.get_data_error , Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("hxUserNames", names);
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

    public void initData() {
        try {
            Map<String, HxUser> hxUserList = new HashMap<String, HxUser>();
            for (Emp emp : emps) {
                HxUser hxUser = new HxUser();
                hxUser.setUsername(emp.getEmpName());
                setUserHeader(emp.getEmpName(), hxUser);
                hxUser.setCover(emp.getEmpCover());
                hxUser.setEmp(emp);
                hxUserList.put(emp.getHxUsername(), hxUser);
            }
            // 添加user"申请与通知"
            HxUser newFriends = new HxUser();
            newFriends.setUsername(HxConstant.NEW_FRIENDS_USERNAME);
            String strChat = getResources().getString(R.string.Application_and_notify);
            newFriends.setNick(strChat);

            hxUserList.put(HxConstant.NEW_FRIENDS_USERNAME, newFriends);
            // 添加"群聊"
            HxUser groupUser = new HxUser();
            String strGroup = getMyApp().getCurrentEmp().getUniversityName();
            groupUser.setUsername(HxConstant.GROUP_USERNAME);
            groupUser.setNick(strGroup);
            groupUser.setHeader("");
            hxUserList.put(HxConstant.GROUP_USERNAME, groupUser);

            // 存入内存
            getMyApp().setContactList(hxUserList);
            // 存入db
            HxUserDao dao = new HxUserDao(LoadingActivity.this);
            List<HxUser> users = new ArrayList<HxUser>(hxUserList.values());
            dao.saveContactList(users);

            //获取黑名单列表
            List<String> blackList = EMContactManager.getInstance().getBlackListUsernamesFromServer();
            //保存黑名单
            EMContactManager.getInstance().saveBlackList(blackList);

            // 获取群聊列表(群聊里只有groupid和groupname等简单信息，不包含members),sdk会把群组存入到内存和db中
//            EMGroupManager.getInstance().getGroupsFromServer();
            EMGroupManager.getInstance().getAllGroups();

            Intent main = new Intent(LoadingActivity.this, MainActivity.class);
            main.putExtra(Constants.ACCOUNT_KEY, empTmp);
            startActivity(main);
            finish();

        } catch (Exception e) {

        }
    }

}
