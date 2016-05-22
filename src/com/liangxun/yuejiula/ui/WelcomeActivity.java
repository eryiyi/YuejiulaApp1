package com.liangxun.yuejiula.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
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
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.AdDATA;
import com.liangxun.yuejiula.data.EmpDATA;
import com.liangxun.yuejiula.data.EmpsDATA;
import com.liangxun.yuejiula.entity.Ad;
import com.liangxun.yuejiula.entity.Emp;
import com.liangxun.yuejiula.huanxin.chat.HxConstant;
import com.liangxun.yuejiula.huanxin.chat.db.HxUserDao;
import com.liangxun.yuejiula.huanxin.chat.domain.HxUser;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.HttpUtils;
import com.liangxun.yuejiula.util.StringUtil;
import com.liangxun.yuejiula.util.Utils;
import com.yixia.camera.demo.UniversityApplication;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/19.
 */
public class WelcomeActivity extends BaseActivity implements View.OnClickListener,Runnable,AMapLocationListener {
    List<Emp> emps = new ArrayList<Emp>();
    boolean isMobileNet, isWifiNet;
    private ImageView head;

    //定位
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;

    Handler mHandler = new Handler() {
        public void dispatchMessage(android.os.Message msg) {
            switch (msg.what) {
                case Utils.MSG_LOCATION_FINISH:
                    AMapLocation loc = (AMapLocation) msg.obj;
                    String result = Utils.getLocationStr(loc);
                    if("true".equals(result)){
                        //定位成功
                        if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class))){
//                            sendLocation();
                        }
                    }else if("false".equals(result)){

                    }
                    break;
                default:
                    break;
            }
        };
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
//定位
        locationClient = new AMapLocationClient(this.getApplicationContext());
        locationOption = new AMapLocationClientOption();
        // 设置定位模式为高精度模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        // 设置定位监听
        locationClient.setLocationListener(this);
        try {
            isMobileNet = HttpUtils.isMobileDataEnable(getApplicationContext());
            isWifiNet = HttpUtils.isWifiDataEnable(getApplicationContext());
            if (!isMobileNet && !isWifiNet) {
                Toast.makeText(this, R.string.network_unavailable, Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
        mHandler.sendEmptyMessage(Utils.MSG_LOCATION_START);

        // 启动一个线程
        new Thread(WelcomeActivity.this).start();
    }

    @Override
    public void onClick(View view) {}

    @Override
    public void run() {
        try {
            // 3秒后跳转到登录界面
            Thread.sleep(3000);
//            if (ad != null){
//                Intent intent = new Intent(WelcomeActivity.this, LoadingActivity.class);
//                intent.putExtra("ad",ad);
//                startActivity(intent);
//                finish();
//            } else{
//                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
//                finish();
//            }

            SharedPreferences.Editor editor = getSp().edit();
            boolean isFirstRun = getSp().getBoolean("isFirstRun", true);
            if (isFirstRun) {
                editor.putBoolean("isFirstRun", false);
                editor.commit();
                Intent loadIntent = new Intent(WelcomeActivity.this, AboutActivity.class);
                startActivity(loadIntent);
                finish();
            } else {
                //判断是否登陆过
                String username = getGson().fromJson(getSp().getString(Constants.EMPMOBILE, ""), String.class);
                String pwr = getGson().fromJson(getSp().getString(Constants.EMPPASS, ""), String.class);
                if (!StringUtil.isNullOrEmpty(username) && !StringUtil.isNullOrEmpty(pwr)) {
                    login();
                }else{
                    startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                    finish();
                }
            }


        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Emp empTmp;

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
                                Intent login = new Intent(WelcomeActivity.this, LoginActivity.class);
                                startActivity(login);
                            }
                        } else {
                            Intent login = new Intent(WelcomeActivity.this, LoginActivity.class);
                            startActivity(login);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Intent login = new Intent(WelcomeActivity.this, LoginActivity.class);
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
                Intent main = new Intent(WelcomeActivity.this, MainActivity.class);
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
                                Toast.makeText(WelcomeActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(WelcomeActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(WelcomeActivity.this, R.string.get_data_error , Toast.LENGTH_SHORT).show();
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
            HxUserDao dao = new HxUserDao(WelcomeActivity.this);
            List<HxUser> users = new ArrayList<HxUser>(hxUserList.values());
            dao.saveContactList(users);

            //获取黑名单列表
            List<String> blackList = EMContactManager.getInstance().getBlackListUsernamesFromServer();
            //保存黑名单
            EMContactManager.getInstance().saveBlackList(blackList);

            // 获取群聊列表(群聊里只有groupid和groupname等简单信息，不包含members),sdk会把群组存入到内存和db中
//            EMGroupManager.getInstance().getGroupsFromServer();
            EMGroupManager.getInstance().getAllGroups();

            Intent main = new Intent(WelcomeActivity.this, MainActivity.class);
            main.putExtra(Constants.ACCOUNT_KEY, empTmp);
            startActivity(main);
            finish();

        } catch (Exception e) {

        }
    }


    //去取广告图片
//    private void initAd() {
//        StringRequest request = new StringRequest(
//                Request.Method.POST,
//                InternetURL.GET_BIGAD_URL,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String s) {
//                        if (StringUtil.isJson(s)) {
//                            AdDATA data = getGson().fromJson(s, AdDATA.class);
//                            if (data.getCode() == 200) {
//                                ad = data.getData();
//                            }else {
//                                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
//                                finish();
//                            }
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//                        if (ad != null){
//                            Intent intent = new Intent(WelcomeActivity.this, LoadingActivity.class);
//                            intent.putExtra("ad",ad);
//                            startActivity(intent);
//                            finish();
//                        }
//                        else{
//                            startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
//                            finish();
//                        }
//                    }
//                }
//        ) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                return params;
//            }
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("Content-Type", "application/x-www-form-urlencoded");
//                return params;
//            }
//        };
//        getRequestQueue().add(request);
//    }

    // 根据控件的选择，重新设置定位参数
    private void initOption() {
        // 设置是否需要显示地址信息
        locationOption.setNeedAddress(true);
        /**
         * 设置是否优先返回GPS定位结果，如果30秒内GPS没有返回定位结果则进行网络定位
         * 注意：只有在高精度模式下的单次定位有效，其他方式无效
         */
        locationOption.setGpsFirst(true);
//        String strInterval = etInterval.getText().toString();
//        if (!TextUtils.isEmpty(strInterval)) {
//            // 设置发送定位请求的时间间隔,最小值为1000，如果小于1000，按照1000算
        locationOption.setInterval(Long.valueOf("1000"));
//        }

    }



    // 定位监听
    @Override
    public void onLocationChanged(AMapLocation loc) {
        if (null != loc) {
            Message msg = mHandler.obtainMessage();
            msg.obj = loc;
            msg.what = Utils.MSG_LOCATION_FINISH;
            mHandler.sendMessage(msg);
        }
    }
//    void sendLocation(){
//        StringRequest request = new StringRequest(
//                Request.Method.POST,
//                InternetURL.SEND_LOCATION_BYID_URL,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String s) {
//                        if (StringUtil.isJson(s)) {
//                            try {
//                                JSONObject jo = new JSONObject(s);
//                                String code =  jo.getString("code");
//                                if(Integer.parseInt(code) == 200){
//
//                                }
//                                else{
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        if (progressDialog != null) {
//                            progressDialog.dismiss();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//                        if (progressDialog != null) {
//                            progressDialog.dismiss();
//                        }
//                    }
//                }
//        ) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("lat", (UniversityApplication.lat==null?"":UniversityApplication.lat));
//                params.put("lng", (UniversityApplication.lng==null?"":UniversityApplication.lng));
//                params.put("mm_emp_id", getGson().fromJson(getSp().getString("mm_emp_id", ""), String.class) );
//                return params;
//            }
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("Content-Type", "application/x-www-form-urlencoded");
//                return params;
//            }
//        };
//        getRequestQueue().add(request);
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }

}
