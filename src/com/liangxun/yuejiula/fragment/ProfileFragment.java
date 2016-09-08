package com.liangxun.yuejiula.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.adapter.AnimateFirstDisplayListener;
import com.liangxun.yuejiula.adapter.ItemMineGridviewAdapter;
import com.liangxun.yuejiula.base.BaseFragment;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.EmpDATA;
import com.liangxun.yuejiula.entity.Emp;
import com.liangxun.yuejiula.entity.MinePicsObj;
import com.liangxun.yuejiula.huanxin.chat.activity.ContactlistActivity;
import com.liangxun.yuejiula.ui.*;
import com.liangxun.yuejiula.util.Constants;
import com.liangxun.yuejiula.util.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yixia.camera.demo.UniversityApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 我的主页
 */
public class ProfileFragment extends BaseFragment implements View.OnClickListener {
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    private TextView profile_name;//标题
    private ImageView profile_cover;//头像
    private TextView profile_nickname;//昵称
    private TextView profile_sign;//签名
//    private TextView level_title;//等级
    private ImageView profile_sex;//性别图片
    private ImageView profile_type;//类别  商家还是代理

    private String emp_id = "";//当前登陆者UUID
    private Emp emp;//被访问用户的资料

    private String empType = "";

    //功能项
    private ListView mine_gridview;
    private ItemMineGridviewAdapter adapter;
    private List<MinePicsObj> pics = new ArrayList<MinePicsObj>();
    //头部区域
    private RelativeLayout liner_one;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBoradcastReceiver();
        emp_id = getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class);
        getData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile, null);
        initView(view);
        return view;
    }

    public void initView(View view) {
        liner_one = (RelativeLayout) view.findViewById(R.id.liner_one);
        liner_one.setOnClickListener(this);
        profile_name = (TextView) view.findViewById(R.id.profile_name);
        profile_cover = (ImageView) view.findViewById(R.id.profile_cover);
        profile_nickname = (TextView) view.findViewById(R.id.profile_nickname);
        profile_sign = (TextView) view.findViewById(R.id.profile_sign);
//        level_title = (TextView) view.findViewById(R.id.level_title);
        profile_sex = (ImageView) view.findViewById(R.id.profile_sex);
        profile_type = (ImageView) view.findViewById(R.id.profile_type);

        mine_gridview = (ListView) view.findViewById(R.id.mine_gridview);
        mine_gridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new ItemMineGridviewAdapter(pics, getActivity());
        mine_gridview.setAdapter(adapter);
        mine_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if("0".equals(empType)){
                    //普通会员
                    switch (i){
                        case 0:
                            Intent recordView = new Intent(getActivity(), MineRecordActivity.class);
                            startActivity(recordView);
                            break;
//                        case 1:
//                            Intent relateView = new Intent(getActivity(), AndMeAcitvity.class);
//                            startActivity(relateView);
//                            break;
                        case 1:
                            Intent friendView = new Intent(getActivity(), ContactlistActivity.class);
                            startActivity(friendView);
                            break;
                        case 2:
                            //收藏
                            Intent favourView = new Intent(getActivity(), MineFavoursDianpuActivity.class);
                            startActivity(favourView);
                            break;
                        case 3:
                            //订单
                            Intent orderView = new Intent(getActivity(), MineOrdersActivity.class);
                            startActivity(orderView);
                            break;
                        case 4:
                            //购物车
                            Intent cartView = new Intent(getActivity(), MineCartActivity.class);
                            startActivity(cartView);
                            break;
//                        case 6:
//                            //帮助与反馈
//                            break;
                        case 5:
                            //我的收货地址
                            Intent addressView = new Intent(getActivity(), MineAddressActivity.class);
                            startActivity(addressView);
                            break;
                        case 6:
                            //设置
                            Intent setView = new Intent(getActivity(), SetActivity.class);
                            startActivity(setView);
                            break;
                        case 7:
                            //关注的标签
                            Intent bqV = new Intent(getActivity(), MineMoodBqActivity.class);
                            startActivity(bqV);
                            break;
                    }
                }
                if("1".equals(empType)){
                    //是管理员
                    switch (i){
                        case 0:
                            Intent reportView = new Intent(getActivity(), ReportActivity.class);
                            startActivity(reportView);
                            break;
                        case 1:
                            Intent pingbiView = new Intent(getActivity(), JinbiActivity.class);
                            startActivity(pingbiView);
                            break;
                        case 2:
                            Intent recordView = new Intent(getActivity(), MineRecordActivity.class);
                            startActivity(recordView);
                            break;
//                        case 3:
//                            Intent relateView = new Intent(getActivity(), AndMeAcitvity.class);
//                            startActivity(relateView);
//                            break;
                        case 3:
                            Intent friendView = new Intent(getActivity(), ContactlistActivity.class);
                            startActivity(friendView);
                            break;
                        case 4:
                            //收藏
                            Intent favourView = new Intent(getActivity(), MineFavoursDianpuActivity.class);
                            startActivity(favourView);
                            break;
                        case 5:
                            //订单
                            Intent orderView = new Intent(getActivity(), MineOrdersActivity.class);
                            startActivity(orderView);
                            break;
                        case 6:
                            //购物车
                            Intent cartView = new Intent(getActivity(), MineCartActivity.class);
                            startActivity(cartView);
                            break;
//                        case 8:
//                            //帮助与反馈
//                            break;
                        case 7:
                            //我的收货地址
                            Intent addressView = new Intent(getActivity(), MineAddressActivity.class);
                            startActivity(addressView);
                            break;
                        case 8:
                            //设置
                            Intent setView = new Intent(getActivity(), SetActivity.class);
                            startActivity(setView);
                            break;
                        case 9:
                            //关注的标签
                            Intent bqV = new Intent(getActivity(), MineMoodBqActivity.class);
                            startActivity(bqV);
                            break;
                    }
                }
                if("2".equals(empType)){
                    //是商家
                    switch (i){
                        case 0:
                            //我的店铺
                            Intent dianpu = new Intent(getActivity(), MineShangpuActivity.class);
                            startActivity(dianpu);
                            break;
                        case 1:
                            Intent mineschoolView = new Intent(getActivity(), MineSchoolsSjActivity.class);
                            startActivity(mineschoolView);
                            break;
                        case 2:
                            Intent relateView = new Intent(getActivity(), MineDailisActivity.class);
                            startActivity(relateView);
                            break;

                        case 3:
                            Intent recordView = new Intent(getActivity(), MineRecordActivity.class);
                            startActivity(recordView);
                            break;

                        case 4:
                            Intent friendView = new Intent(getActivity(), ContactlistActivity.class);
                            startActivity(friendView);
                            break;
                        case 5:
                            //收藏
                            Intent favourView = new Intent(getActivity(), MineFavoursDianpuActivity.class);
                            startActivity(favourView);
                            break;
                        case 6:
                            //订单
                            Intent orderView = new Intent(getActivity(), MineOrdersActivity.class);
                            startActivity(orderView);
                            break;
                        case 7:
                            //购物车
                            Intent cartView = new Intent(getActivity(), MineCartActivity.class);
                            startActivity(cartView);
                            break;
//                        case 8:
//                            //帮助与反馈
//                            break;
                        case 8:
                            //我的收货地址
                            Intent addressView = new Intent(getActivity(), MineAddressActivity.class);
                            startActivity(addressView);
                            break;
                        case 9:
                            //设置
                            Intent setView = new Intent(getActivity(), SetActivity.class);
                            startActivity(setView);
                            break;
                        case 10:
                            //关注的标签
                            Intent bqV = new Intent(getActivity(), MineMoodBqActivity.class);
                            startActivity(bqV);
                            break;
                    }
                }
                if("3".equals(empType)){
                    //是代理商
                    switch (i){
                        case 0:
                            Intent mineschool = new Intent(getActivity(), MineSchoolsActivity.class);
                            startActivity(mineschool);
                            break;
                        case 1:
                            Intent shangjia = new Intent(getActivity(), MineShangjiaActivity.class);
                            startActivity(shangjia);
                            break;
                        case 2:
                            Intent tg = new Intent(getActivity(), MineTuiguangActivity.class);
                            startActivity(tg);
                            break;
                        case 3:
                            //pk奖品
//                            Intent prizesView = new Intent(getActivity(), PkPrizesActivity.class);
//                            startActivity(prizesView);
                        {
                            //封号封群
                            Intent fhfq = new Intent(getActivity(), FenghaoFengqunActivity.class);
                            startActivity(fhfq);
                        }
                            break;
                        case 4:
                            Intent recordView = new Intent(getActivity(), MineRecordActivity.class);
                            startActivity(recordView);
                            break;
//                        case 5:
//                            Intent relateView = new Intent(getActivity(), AndMeAcitvity.class);
//                            startActivity(relateView);
//                            break;
                        case 5:
                            Intent friendView = new Intent(getActivity(), ContactlistActivity.class);
                            startActivity(friendView);
                            break;
                        case 6:
                            //收藏
                            Intent favourView = new Intent(getActivity(), MineFavoursDianpuActivity.class);
                            startActivity(favourView);
                            break;
                        case 7:
                            //订单
                            Intent orderView = new Intent(getActivity(), MineOrdersActivity.class);
                            startActivity(orderView);
                            break;
                        case 8:
                            //购物车
                            Intent cartView = new Intent(getActivity(), MineCartActivity.class);
                            startActivity(cartView);
                            break;
//                        case 10:
//                            //帮助与反馈
//                            break;
                        case 9:
                            //我的收货地址
                            Intent addressView = new Intent(getActivity(), MineAddressActivity.class);
                            startActivity(addressView);
                            break;
                        case 10:
                            //设置
                            Intent setView = new Intent(getActivity(), SetActivity.class);
                            startActivity(setView);
                            break;
                        case 11:
                            //关注的标签
                            Intent bqV = new Intent(getActivity(), MineMoodBqActivity.class);
                            startActivity(bqV);
                            break;
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.liner_one:
                Intent updateprofile = new Intent(getActivity(), UpdateProfilePersonalActivity.class);
                startActivity(updateprofile);
                break;

        }
    }

    //根据用户UUID获取用户信息
    private void getData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                getGson().fromJson(getSp().getString("select_big_area", ""), String.class) + InternetURL.GET_EMP_DETAIL_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            EmpDATA data = getGson().fromJson(s, EmpDATA.class);
                            if (data.getCode() == 200) {
                                emp = data.getData();
                                initData();
                            } else {
                                Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("empId", emp_id);
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
        empType = emp.getEmpTypeId();
        pics.clear();
        if (empType.equals("1")) {//是管理员
            //显示举报和屏蔽
            pics.add(new MinePicsObj(R.drawable.mine_report_n, "举报列表"));
            pics.add(new MinePicsObj(R.drawable.mine_pingbi_n, "举报列表"));
        }
        if (empType.equals("2")) {//是商家
            pics.add( new MinePicsObj(R.drawable.mine_dianpu_n, "我的店铺"));
            pics.add(new MinePicsObj(R.drawable.mine_school_n, "入驻圈子"));
            pics.add(new MinePicsObj(R.drawable.mine_relate_n, "我的代理"));
        }
        if (empType.equals("3")) {//是代理商
            //显示我的商家、圈子、推广
            pics.add(new MinePicsObj(R.drawable.mine_school_n ,"我的圈子"));
            pics.add(new MinePicsObj(R.drawable.mine_shangjia_n ,"我的商家"));
            pics.add(new MinePicsObj(R.drawable.mine_tg_n ,"我的推广"));
            pics.add(new MinePicsObj(R.drawable.mine_pingbi_n ,"禁帖禁群"));
        }


        pics.add(new MinePicsObj(R.drawable.mine_record_n ,"我的动态"));
//        pics.add(new MinePicsObj(R.drawable.mine_relate_n ,"与我相关"));
        pics.add(new MinePicsObj(R.drawable.mine_friends_n ,"兴趣好友"));

        pics.add( new MinePicsObj(R.drawable.mine_favour_n ,"我的收藏"));
        pics.add(new MinePicsObj(R.drawable.mine_order_n ,"我的订单"));
        pics.add(new MinePicsObj(R.drawable.mine_cart_n ,"购物车"));


//        pics.add(R.drawable.mine_help_n);

        pics.add(new MinePicsObj(R.drawable.mine_address_n ,"收货地址"));
        pics.add(new MinePicsObj(R.drawable.mine_set_n ,"我的设置"));
        pics.add(new MinePicsObj(R.drawable.mine_set_n ,"关注的标签"));

        adapter.notifyDataSetChanged();

        imageLoader.displayImage(emp.getEmpCover(), profile_cover, UniversityApplication.txOptions, animateFirstListener);
        profile_nickname.setText(emp.getEmpName());
        if ("0".equals(emp.getEmpSex())) {
            profile_sex.setImageResource(R.drawable.icon_sex_male);
        }
        if ("1".equals(emp.getEmpSex())) {
            profile_sex.setImageResource(R.drawable.icon_sex_female);
        }

//        level_title.setText(emp.getJfcount());
//        level_title.setText(emp.getLevelName());
        if (!StringUtil.isNullOrEmpty(emp.getEmpSign())) {
            profile_sign.setText(emp.getEmpSign());
        } else {
            profile_sign.setText(R.string.sign);
        }

        if (empType.equals("0")) {
            profile_type.setImageResource(R.drawable.icon_type_min);
        }
        if (empType.equals("1")) {
            profile_type.setImageResource(R.drawable.icon_type_guan);
        }
        if (empType.equals("2")) {
            profile_type.setImageResource(R.drawable.icon_type_shang);
        }
        if (empType.equals("3")) {
            profile_type.setImageResource(R.drawable.icon_type_official);
        }
    }

    //广播接收动作
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.SEND_PIC_TX_SUCCESS)) {
                getData();
            }
        }
    };

    //注册广播
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.SEND_PIC_TX_SUCCESS);//设置头像的广播事件
        //注册广播
        getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }

}