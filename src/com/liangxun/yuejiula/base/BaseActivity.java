package com.liangxun.yuejiula.base;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.easemob.chat.EMMessage;
import com.easemob.util.EasyUtils;
import com.google.gson.Gson;
import com.liangxun.yuejiula.MainActivity;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.huanxin.chat.utils.CommonUtils;
import com.umeng.analytics.MobclickAgent;
import com.yixia.camera.demo.UniversityApplication;

import java.util.concurrent.ExecutorService;

/**
 * Created by zhanghl on 2014/11/11.
 */
public class BaseActivity extends FragmentActivity {
    public ProgressDialog progressDialog;
    private static final int notifiId = 11;
    protected NotificationManager notificationManager;
    /**
     * 屏幕的宽度和高度
     */
    protected int mScreenWidth;
    protected int mScreenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        /**
         * 获取屏幕宽度和高度
         */
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        mScreenWidth = metric.widthPixels;
        mScreenHeight = metric.heightPixels;

        ActivityTack.getInstanse().addActivity(this);
    }

    /**
     * Toast的封装
     * @param mContext 上下文，来区别哪一个activity调用的
     * @param msg 你希望显示的值。
     */
    public static void showMsg(Context mContext,String msg) {
        Toast toast=new Toast(mContext);
        toast= Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_HORIZONTAL,0,0);//设置居中
        toast.show();//显示,(缺了这句不显示)
    }

    /**
     * 获取当前Application
     *
     * @return
     */
    public UniversityApplication getMyApp() {
        return (UniversityApplication) getApplication();
    }

    //存储sharepreference
    public void save(String key, Object value) {
        getMyApp().getSp().edit()
                .putString(key, getMyApp().getGson().toJson(value))
                .commit();
    }


    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    /**
     * 当应用在前台时，如果当前消息不是属于当前会话，在状态栏提示一下
     * 如果不需要，注释掉即可
     *
     * @param message
     */
    protected void notifyNewMessage(EMMessage message, String empName) {
        //如果是设置了不提醒只显示数目的群组(这个是app里保存这个数据的，demo里不做判断)
        //以及设置了setShowNotificationInbackgroup:false(设为false后，后台时sdk也发送广播)
        if (!EasyUtils.isAppRunningForeground(this)) {
            return;
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(getApplicationInfo().icon)
                .setWhen(System.currentTimeMillis()).setAutoCancel(true);

        String ticker = CommonUtils.getMessageDigest(message, this);
        String st = getResources().getString(R.string.expression);
        if (message.getType() == EMMessage.Type.TXT)
            ticker = ticker.replaceAll("\\[.{2,3}\\]", st);
        //设置状态栏提示
        mBuilder.setTicker(empName + ": " + ticker);

        //必须设置pendingintent，否则在2.3的机器上会有bug
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, notifiId, intent, PendingIntent.FLAG_ONE_SHOT);
        mBuilder.setContentIntent(pendingIntent);

        Notification notification = mBuilder.build();
        notificationManager.notify(notifiId, notification);
        notificationManager.cancel(notifiId);
    }

    /**
     * 返回
     *
     * @param view
     */
    public void back(View view) {
        finish();
    }

    /**
     * 获取Volley请求队列
     *
     * @return
     */
    public RequestQueue getRequestQueue() {
        return getMyApp().getRequestQueue();
    }

    /**
     * 获取Gson
     *
     * @return
     */
    public Gson getGson() {
        return getMyApp().getGson();
    }

    /**
     * 获取SharedPreferences
     *
     * @return
     */
    public SharedPreferences getSp() {
        return getMyApp().getSp();
    }

    /**
     * 获取自定义线程池
     *
     * @return
     */
    public ExecutorService getLxThread() {
        return getMyApp().getLxThread();
    }

    protected void onDestroy() {
        ActivityTack.getInstanse().removeActivity(this);
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        super.onDestroy();
    }
}
