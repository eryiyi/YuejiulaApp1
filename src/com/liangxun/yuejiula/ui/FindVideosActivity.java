package com.liangxun.yuejiula.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.adapter.ItemNavGridviewAdapter;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.entity.NavObj;
import com.liangxun.yuejiula.widget.PictureGridview;

import java.util.ArrayList;
import java.util.List;

/**
 * author: ${zhanghailong}
 * Date: 2015/2/6
 * Time: 22:01
 * 导航
 */
public class FindVideosActivity extends BaseActivity implements View.OnClickListener {
    private ImageView notice_detail_menu;

    //功能项
    private PictureGridview mine_gridview;
    private ItemNavGridviewAdapter adapter;
    private List<NavObj> pics = new ArrayList<NavObj>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_daohang);
        initView();
        pics.add(new NavObj(R.drawable.icon_tv_youku, "优酷", "http://www.youku.com"));
        pics.add(new NavObj(R.drawable.icon_tv_iqiyi, "爱奇艺", "http://www.iqiyi.com/"));
        pics.add(new NavObj(R.drawable.icon_tv_tudou, "土豆", "http://www.tudou.com"));
        pics.add(new NavObj(R.drawable.icon_tv_leshi, "乐视", "http://m.letv.com"));
        pics.add(new NavObj(R.drawable.icon_tv_tv56, "56", "http://m.56.com"));
        pics.add(new NavObj(R.drawable.icon_tv_ku6, "酷6", "http://m.ku6.com"));
        pics.add(new NavObj(R.drawable.icon_tv_baomihua, "爆米花", "http://wap.baomihua.com"));
        pics.add(new NavObj(R.drawable.icon_tv_fengxing, "风行", "http://m.fun.tv"));
        pics.add(new NavObj(R.drawable.icon_tv_hunan, "湖南卫视", "http://m.hunantv.com"));
    }


    private void initView() {
        notice_detail_menu = (ImageView) this.findViewById(R.id.notice_detail_menu);
        notice_detail_menu.setOnClickListener(this);

        mine_gridview = (PictureGridview) this.findViewById(R.id.mine_gridview);
        mine_gridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new ItemNavGridviewAdapter(pics, FindVideosActivity.this);
        mine_gridview.setAdapter(adapter);
        mine_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent recordView = new Intent(FindVideosActivity.this, WebViewActivity.class);
                NavObj navObj = pics.get(i);
                recordView.putExtra("strurl", (navObj.getUrl()==null?"":navObj.getUrl()));
                startActivity(recordView);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.notice_detail_menu:
                finish();
                break;
        }
    }
}
