package com.liangxun.yuejiula.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.adapter.Publish_mood_GridView_Adapter;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.base.InternetURL;
import com.liangxun.yuejiula.data.GoodsTypeDATA;
import com.liangxun.yuejiula.data.SuccessData;
import com.liangxun.yuejiula.entity.Goodstype;
import com.liangxun.yuejiula.util.*;
import com.liangxun.yuejiula.widget.NoScrollGridView;
import com.liangxun.yuejiula.widget.popview.CustomerSpinner;
import com.liangxun.yuejiula.widget.popview.SelectPhoTwoPopWindow;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: ${zhanghailong}
 * Date: 2015/2/6
 * Time: 19:10
 * 类的功能、说明写在此处.
 */
public class PublishGoodsActivity extends BaseActivity implements View.OnClickListener {
    private final static int SELECT_LOCAL_PHOTO = 110;
    private static final String TAG = PublishGoodsActivity.class.getSimpleName();
    private ImageView publish_goods_imv;//发布商品图片按钮
    private EditText publish_good_title;//标题
    private EditText publish_good_content;//内容
    private EditText publish_good_money;//价格
    private EditText publish_good_address;//地址
    private EditText publish_good_person;//联系人
    private EditText publish_good_tel;//电话
    private ImageView publis_goods_back;
    private TextView publish_goods_run;//发布按钮
    private TextView publish_goods_notice;//发布须知

    private CustomerSpinner goodstype;
    private ArrayAdapter<String> adapterspin;
    private ArrayList<String> goodstypes = new ArrayList<String>();
    private ArrayList<Goodstype> goodstypeslst = new ArrayList<Goodstype>();

    private String title;
    private String content;
    private String money;
    private String person;
    private String address;
    private String tel;
    private String typeId = "";
    private String typeTitle = "";
    private String typeIsBusiness = "";
    private NoScrollGridView publish_moopd_gridview_image;//图片
    private ArrayList<String> dataList = new ArrayList<String>();
    private ArrayList<String> tDataList = new ArrayList<String>();
    private List<String> uploadPaths = new ArrayList<String>();

    private Uri uri;
    private String schoolId = "";
    private String emp_id = "";//当前登陆者UUID
    private String emp_typeid = "";//当前登陆者类别
    private Publish_mood_GridView_Adapter adapter;

    private SelectPhoTwoPopWindow deleteWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publish_goods_xml);
        initView();
        schoolId = getGson().fromJson(getSp().getString(Constants.SCHOOLID, ""), String.class);
        emp_id = getGson().fromJson(getSp().getString(Constants.EMPID, ""), String.class);
        emp_typeid = getGson().fromJson(getSp().getString(Constants.EMPTYPE, ""), String.class);
        initData();
    }

    private void initView() {
        publish_goods_imv = (ImageView) this.findViewById(R.id.publish_goods_imv);
        publish_goods_imv.setOnClickListener(this);
        publish_good_title = (EditText) this.findViewById(R.id.publish_good_title);
        publish_good_content = (EditText) this.findViewById(R.id.publish_good_content);
        publish_good_money = (EditText) this.findViewById(R.id.publish_good_money);
        publis_goods_back = (ImageView) this.findViewById(R.id.publis_goods_back);
        publis_goods_back.setOnClickListener(this);
        publish_goods_run = (TextView) this.findViewById(R.id.publish_goods_run);
        publish_goods_run.setOnClickListener(this);

        adapterspin = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, goodstypes);
        goodstype = (CustomerSpinner) findViewById(R.id.publish_good_type);
        goodstype.setList(goodstypes);
        goodstype.setAdapter(adapterspin);
        goodstype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG, position + "");
                if (position > 0) {
                    typeId = goodstypeslst.get(position - 1).getTypeId();//获得类别UUID
                    typeTitle = goodstypeslst.get(position - 1).getTypeName();//获得类别名称
                    typeIsBusiness = goodstypeslst.get(position - 1).getTypeIsBusiness();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        publish_moopd_gridview_image = (NoScrollGridView) this.findViewById(R.id.publish_goods_gridview_image);
        adapter = new Publish_mood_GridView_Adapter(this, dataList);
        publish_moopd_gridview_image.setAdapter(adapter);
        publish_moopd_gridview_image.setOnItemClickListener(new GridView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String path = dataList.get(position);
                if (path.contains("default") && position == dataList.size() - 1 && dataList.size() - 1 != 9) {
                    showSelectImageDialog();
                } else {
                    Intent intent = new Intent(PublishGoodsActivity.this, ImageDelActivity.class);
                    intent.putExtra("position", position);
                    intent.putExtra("path", dataList.get(position));
                    startActivityForResult(intent, CommonDefine.DELETE_IMAGE);
                }
            }
        });
        publish_good_address = (EditText) this.findViewById(R.id.publish_good_address);
        publish_good_person = (EditText) this.findViewById(R.id.publish_good_person);
        publish_good_tel = (EditText) this.findViewById(R.id.publish_good_tel);
        publish_goods_notice = (TextView) this.findViewById(R.id.publish_goods_notice);
        publish_goods_notice.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.publis_goods_back:
                finish();
                break;
            case R.id.publish_goods_run:
//                title = publish_good_title.getText().toString();
//                content = publish_good_content.getText().toString();
//                money = publish_good_money.getText().toString();
//                address = publish_good_address.getText().toString();
//                person = publish_good_person.getText().toString();
//                tel = publish_good_tel.getText().toString();
//                if (StringUtil.isNullOrEmpty(title)) {
//                    Toast.makeText(PublishGoodsActivity.this, R.string.publishgoods_error_one, Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (title.length() > 100) {
//                    Toast.makeText(PublishGoodsActivity.this, R.string.publishgoods_error_two, Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (StringUtil.isNullOrEmpty(content)) {
//                    Toast.makeText(PublishGoodsActivity.this, R.string.publishgoods_error_three, Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (content.length() > 4000) {
//                    Toast.makeText(PublishGoodsActivity.this, R.string.publishgoods_error_four, Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (StringUtil.isNullOrEmpty(money)) {
//                    Toast.makeText(PublishGoodsActivity.this, R.string.publishgoods_error_five, Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (money.length() > 100) {
//                    Toast.makeText(PublishGoodsActivity.this, R.string.publishgoods_error_six, Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (StringUtil.isNullOrEmpty(address)) {
//                    Toast.makeText(PublishGoodsActivity.this, R.string.publishgoods_error_nine, Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (address.length() > 250) {
//                    Toast.makeText(PublishGoodsActivity.this, R.string.publishgoods_error_ten, Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (StringUtil.isNullOrEmpty(person)) {
//                    Toast.makeText(PublishGoodsActivity.this, R.string.publishgoods_error_evelen, Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (person.length() > 50) {
//                    Toast.makeText(PublishGoodsActivity.this, R.string.publishgoods_error_twelve, Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (StringUtil.isNullOrEmpty(tel)) {
//                    Toast.makeText(PublishGoodsActivity.this, R.string.publishgoods_error_thirty, Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (tel.length() > 50) {
//                    Toast.makeText(PublishGoodsActivity.this, R.string.publishgoods_error_fourty, Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (StringUtil.isNullOrEmpty(typeId)) {
//                    Toast.makeText(PublishGoodsActivity.this, R.string.publishgoods_error_seven, Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (typeIsBusiness.equals("1")) {//1说明该类别是商家的
//                    if (!(emp_typeid.equals("2") || emp_typeid.equals("3"))) {
//                        Toast.makeText(PublishGoodsActivity.this, R.string.publishgoods_error_eight, Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                }
//                Resources res = getBaseContext().getResources();
//                String message = res.getString(R.string.check_publish).toString();
//                progressDialog = new ProgressDialog(PublishGoodsActivity.this);
//                progressDialog.setCanceledOnTouchOutside(false);
//                progressDialog.setMessage(message);
//                progressDialog.show();
//                //检查有没有选择图片
//                if (dataList.size() == 0) {
//                    progressDialog.dismiss();
//                    Toast.makeText(PublishGoodsActivity.this, R.string.check_is_picture, Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                for (int i = 0; i < dataList.size(); i++) {
//                    File file = new File(dataList.get(i));
//                    Map<String, File> files = new HashMap<String, File>();
//                    files.put("file", file);
//                    Map<String, String> params = new HashMap<String, String>();
//                    CommonUtil.addPutUploadFileRequest(
//                            this,
//                            InternetURL.UPLOAD_FILE,
//                            files,
//                            params,
//                            new Response.Listener<String>() {
//                                @Override
//                                public void onResponse(String s) {
//                                    if (StringUtil.isJson(s)) {
//                                        SuccessData data = getGson().fromJson(s, SuccessData.class);
//                                        if (data.getCode() == 200) {
//                                            uploadPaths.add(data.getData());
//                                            //说明文件已经上传完毕
//                                            if (uploadPaths.size() == dataList.size()) {
//                                                publishAll();
//                                            }
//                                        } else {
//                                            Toast.makeText(PublishGoodsActivity.this, R.string.publish_error_one, Toast.LENGTH_SHORT).show();
//                                            if (progressDialog != null) {
//                                                progressDialog.dismiss();
//                                            }
//                                        }
//                                    }
//                                }
//                            },
//                            new Response.ErrorListener() {
//                                @Override
//                                public void onErrorResponse(VolleyError volleyError) {
//                                    Toast.makeText(PublishGoodsActivity.this, R.string.publish_error_two, Toast.LENGTH_SHORT).show();
//                                    if (progressDialog != null) {
//                                        progressDialog.dismiss();
//                                    }
//                                }
//                            },
//                            null);
//                }
                break;
            case R.id.publish_goods_imv:
                //选择照片
                showSelectImageDialog();
                break;
            case R.id.publish_goods_notice:
                //发布须知
                Intent noticegoods = new Intent(PublishGoodsActivity.this, GoodsNoticesActivity.class);
                startActivity(noticegoods);
                break;
        }
    }

    //获得二手市场类别
    private void initData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_GOODSTYPE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            GoodsTypeDATA data = getGson().fromJson(s, GoodsTypeDATA.class);
                            if (data.getCode() == 200) {
                                goodstypes.clear();
                                goodstypes.add("选择商品分类");
                                goodstypeslst.addAll(data.getData());
                                for (Goodstype goodstype1 : goodstypeslst) {
                                    goodstypes.add(goodstype1.getTypeName());
                                }
                                adapterspin.notifyDataSetChanged();
                            } else {
                                Toast.makeText(PublishGoodsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(PublishGoodsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(PublishGoodsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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

    // 选择相册，相机
    private void showSelectImageDialog() {
        deleteWindow = new SelectPhoTwoPopWindow(PublishGoodsActivity.this, itemsOnClick);
        //显示窗口
        deleteWindow.showAtLocation(PublishGoodsActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private ArrayList<String> getIntentArrayList(ArrayList<String> dataList) {

        ArrayList<String> tDataList = new ArrayList<String>();

        for (String s : dataList) {
//            if (!s.contains("default")) {
            tDataList.add(s);
//            }
        }
        return tDataList;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case SELECT_LOCAL_PHOTO:
                    tDataList = data.getStringArrayListExtra("datalist");
                    if (tDataList != null) {
                        for (int i = 0; i < tDataList.size(); i++) {
                            String string = tDataList.get(i);
                            dataList.add(string);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        finish();
                    }
                    break;
                case CommonDefine.TAKE_PICTURE_FROM_CAMERA:
                    String sdStatus = Environment.getExternalStorageState();
                    if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
                        return;
                    }
                    Bitmap bitmap = ImageUtils.getUriBitmap(this, uri, 400, 400);
                    String cameraImagePath = FileUtils.saveBitToSD(bitmap, System.currentTimeMillis() + ".jpg");
                    dataList.add(cameraImagePath);
                    adapter.notifyDataSetChanged();
                    break;
                case CommonDefine.TAKE_PICTURE_FROM_GALLERY:
                    tDataList = data.getStringArrayListExtra("datalist");
                    if (tDataList != null) {
                        dataList.clear();
                        for (int i = 0; i < tDataList.size(); i++) {
                            String string = tDataList.get(i);
                            dataList.add(string);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    break;
                case CommonDefine.DELETE_IMAGE:
                    int position = data.getIntExtra("position", -1);
                    dataList.remove(position);
                    adapter.notifyDataSetChanged();
                    break;
            }

        }
    }

    //上传完图片后开始发布
    private void publishAll() {
        final StringBuffer filePath = new StringBuffer();
        for (int i = 0; i < uploadPaths.size(); i++) {
            filePath.append(uploadPaths.get(i));
            if (i != uploadPaths.size() - 1) {
                filePath.append(",");
            }
        }
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.PUBLISH_GOODS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                if (progressDialog != null) {
                                    progressDialog.dismiss();
                                }
                                Toast.makeText(PublishGoodsActivity.this, R.string.publish_success, Toast.LENGTH_SHORT).show();
                                Intent intent1 = new Intent(Constants.SEND_GOOD_SUCCESS);
                                sendBroadcast(intent1);
                                finish();
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
                        Toast.makeText(PublishGoodsActivity.this, R.string.publish_error_one, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("typeId", typeId);
                params.put("name", title);
                params.put("cover", String.valueOf(filePath));
                params.put("content", content);
                params.put("money", money);
                params.put("empId", emp_id);
                params.put("schoolId", schoolId);
                params.put("address", address);
                params.put("person", person);
                params.put("tel", tel);
                params.put("type", emp_typeid);
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

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            deleteWindow.dismiss();
            switch (v.getId()) {
                case R.id.camera: {
                    Intent cameraIntent = new Intent();
                    cameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    // 根据文件地址创建文件
                    File file = new File(CommonDefine.FILE_PATH);
                    if (file.exists()) {
                        file.delete();
                    }
                    uri = Uri.fromFile(file);
                    // 设置系统相机拍摄照片完成后图片文件的存放地址
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                    // 开启系统拍照的Activity
                    startActivityForResult(cameraIntent, CommonDefine.TAKE_PICTURE_FROM_CAMERA);
                }
                break;
                case R.id.mapstorage: {
                    Intent intent = new Intent(PublishGoodsActivity.this, AlbumActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("dataList", getIntentArrayList(dataList));
                    bundle.putString("editContent", "");
                    intent.putExtras(bundle);
                    startActivityForResult(intent, CommonDefine.TAKE_PICTURE_FROM_GALLERY);
                }
                break;
                default:
                    break;
            }
        }
    };
}
