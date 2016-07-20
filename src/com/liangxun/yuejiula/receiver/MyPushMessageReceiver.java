package com.liangxun.yuejiula.receiver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baidu.frontia.api.FrontiaPushMessageReceiver;
import com.google.gson.Gson;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.SuccessData;
import com.liangxun.yuejiula.ui.AndMeAcitvity;
import com.liangxun.yuejiula.ui.ExplainActivity;
import com.liangxun.yuejiula.ui.NoticeActivity;
import com.liangxun.yuejiula.util.Constants;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by zhanghl on 2015/1/6.
 */
public class MyPushMessageReceiver extends FrontiaPushMessageReceiver {



    @Override
    public void onBind(Context context, int errorCode, String appid, String userId, String channelId, String requestId) {
        updateChanelId(context, channelId, userId);//程序一运行就执行
    }

    @Override
    public void onUnbind(Context context, int i, String s) {
        Log.e("Message", s);
    }

    @Override
    public void onSetTags(Context context, int i, List<String> strings, List<String> strings2, String s) {
        Log.e("Message", s);
    }

    @Override
    public void onDelTags(Context context, int i, List<String> strings, List<String> strings2, String s) {
        Log.e("Message", s);
    }

    @Override
    public void onListTags(Context context, int i, List<String> strings, String s) {
        Log.e("Message", s);
//        Intent intent = new Intent(context, JiaohuActivity.class);
//        context.startActivity(intent);
    }

    @Override
    public void onMessage(Context context, String s, String s2) {
//        Intent intent = new Intent(context, JiaohuActivity.class);
//        context.startActivity(intent);
        Log.e("Message", s);
    }

    @Override
    public void onNotificationClicked(Context context, String title, String content, String customContent) {
        Log.e("Message", title);

        try {
            Intent intent = new Intent();
            JSONObject custom = new JSONObject(customContent);
            int type = custom.getInt("type");
            switch (type) {
                case 1://公告
                    //改变底部图标
                    Intent msg_notice = new Intent("_msg_notice");
                    context.sendBroadcast(msg_notice);
                    intent.setClass(context.getApplicationContext(), NoticeActivity.class);
                    break;
                case 2://与我相关
                    Intent msg_record = new Intent("_msg_record");
                    context.sendBroadcast(msg_record);
                    intent.setClass(context.getApplicationContext(), AndMeAcitvity.class);
                    break;
                case 3://关禁闭
                    String time = custom.getString("time");
                    intent.putExtra("time", time);
                    intent.setClass(context.getApplicationContext(),ExplainActivity.class);
                    break;

            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.getApplicationContext().startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    public void updateChanelId(final Context context, String chanelId, final String userId) {
        final SharedPreferences sp = context.getSharedPreferences("university_manage", Context.MODE_PRIVATE);
//        String spUserId = sp.getString(Constants.PUSH_USER_ID, "");
//        if(spUserId.equals(userId)){
//            return;
//        }
        String empId = new Gson().fromJson(sp.getString(Constants.EMPID, ""), String.class);
        RequestQueue queue = Volley.newRequestQueue(context);
        String uri = String.format(InternetURL.UPDATE_PUSH_ID + "?id=%s&pushId=%s&type=3", empId, userId);
        StringRequest request = new StringRequest(
                Request.Method.GET,
                uri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            SuccessData successDATA = new Gson().fromJson(s, SuccessData.class);
                            if (successDATA.getCode() == 200) {
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString(Constants.PUSH_USER_ID, userId).commit();
                            }
                        } catch (Exception e) {

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                }
        );
        queue.add(request);
    }
}
