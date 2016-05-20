package com.liangxun.yuejiula.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.liangxun.yuejiula.entity.Emp;
import com.liangxun.yuejiula.huanxin.chat.HxConstant;
import com.liangxun.yuejiula.huanxin.chat.db.HxUserDao;
import com.liangxun.yuejiula.huanxin.chat.domain.HxUser;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.HttpUtils;
import com.liangxun.yuejiula.util.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yixia.camera.demo.UniversityApplication;

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
public class LoginActivity extends BaseActivity implements View.OnClickListener{
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类

    private EditText loginname;
    private EditText password;
    private TextView login_activity_login;

    private String username;
    private String pwr;

    private Emp empTmp;


    private TextView login_pwr;//找回密码
    private TextView login_register;//注册

    List<Emp> emps = new ArrayList<Emp>();
    boolean isMobileNet, isWifiNet;
    private ImageView head;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        initView();
        if (loginname != null) {
            username = getGson().fromJson(getSp().getString(Constants.EMPMOBILE, ""), String.class);
            loginname.setText(username);
        }
        if (password != null) {
            pwr = getGson().fromJson(getSp().getString(Constants.EMPPASS, ""), String.class);
            password.setText(pwr);
        }
        if(head !=null){
            imageLoader.displayImage(getGson().fromJson(getSp().getString(Constants.EMPCOVER, ""), String.class),
                    head, UniversityApplication.txOptions, animateFirstListener);
        }
        if (!StringUtil.isNullOrEmpty(username) && !StringUtil.isNullOrEmpty(pwr)) {
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.show();
            login();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (loginname != null) {
            loginname.setText(getGson().fromJson(getSp().getString(Constants.EMPMOBILE, ""), String.class));
        }
        if (password != null) {
            password.setText(getGson().fromJson(getSp().getString(Constants.EMPPASS, ""), String.class));
        }
    }

    private void initView() {
        loginname = (EditText) this.findViewById(R.id.loginname);
        password = (EditText) this.findViewById(R.id.password);
        login_activity_login = (TextView) this.findViewById(R.id.login_activity_login);
        login_pwr = (TextView) this.findViewById(R.id.login_pwr);
        login_register = (TextView) this.findViewById(R.id.login_register);
        head = (ImageView) this.findViewById(R.id.head);

        login_activity_login.setOnClickListener(this);
        login_pwr.setOnClickListener(this);
        login_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        try {
            isMobileNet = HttpUtils.isMobileDataEnable(getApplicationContext());
            isWifiNet = HttpUtils.isWifiDataEnable(getApplicationContext());
            if (!isMobileNet && !isWifiNet) {
                Toast.makeText(this, "当前网络连接不可用", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (v.getId()) {
            case R.id.login_activity_login:
                username = loginname.getText().toString();
                pwr = password.getText().toString();
                if (StringUtil.isNullOrEmpty(username)) {
                    Toast.makeText(this, R.string.login_error_one, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (StringUtil.isNullOrEmpty(pwr)) {
                    Toast.makeText(this, R.string.login_error_two, Toast.LENGTH_SHORT).show();
                    return;
                }
                progressDialog = new ProgressDialog(LoginActivity.this );

                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                login();
                break;
            case R.id.login_pwr:
                Intent findV = new Intent(LoginActivity.this, FindPwrOneActivity.class);
                startActivity(findV);
                break;
            case R.id.login_register:
                Intent regist = new Intent(this, RegistTwoActivity.class);
                startActivity(regist);
                break;
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
                            } else {
                                if (data.getCode() == 1) {
                                    Toast.makeText(LoginActivity.this, R.string.login_error_three, Toast.LENGTH_SHORT).show();
                                    loginname.setText("");
                                    password.setText("");
                                }
                                if (data.getCode() == 2) {
                                    Toast.makeText(LoginActivity.this, R.string.login_error_four, Toast.LENGTH_SHORT).show();
                                    password.setText("");
                                }
                                if (data.getCode() == 3) {
                                    Toast.makeText(LoginActivity.this, R.string.login_error_five, Toast.LENGTH_SHORT).show();
                                }
                                if (progressDialog != null) {
                                    progressDialog.dismiss();
                                }
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, R.string.login_error_six, Toast.LENGTH_SHORT).show();
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(LoginActivity.this, R.string.login_error_six, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", pwr);
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
        save(Constants.EMPPASS, pwr);
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
                            getMyApp().logout(null);
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
                Intent main = new Intent(LoginActivity.this, MainActivity.class);
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
//                InternetURL.GET_FRIENDS_URL,
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
                                if (progressDialog != null) {
                                    progressDialog.dismiss();
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(LoginActivity.this, R.string.get_data_error , Toast.LENGTH_SHORT).show();
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
            HxUserDao dao = new HxUserDao(LoginActivity.this);
            List<HxUser> users = new ArrayList<HxUser>(hxUserList.values());
            dao.saveContactList(users);

            //获取黑名单列表
            List<String> blackList = EMContactManager.getInstance().getBlackListUsernamesFromServer();
            //保存黑名单
            EMContactManager.getInstance().saveBlackList(blackList);

            // 获取群聊列表(群聊里只有groupid和groupname等简单信息，不包含members),sdk会把群组存入到内存和db中
//            EMGroupManager.getInstance().getGroupsFromServer();
            EMGroupManager.getInstance().getAllGroups();

            Intent main = new Intent(LoginActivity.this, MainActivity.class);
            main.putExtra(Constants.ACCOUNT_KEY, empTmp);
            startActivity(main);
            finish();

        } catch (Exception e) {

        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {

            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            View v = getCurrentFocus();

            if (isShouldHideInput(v, ev)) {
                hideSoftInput(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 多种隐藏软件盘方法的其中一种
     *
     * @param token
     */
    private void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


}
