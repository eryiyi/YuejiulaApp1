package com.liangxun.yuejiula.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.base.BaseFragment;
import com.liangxun.yuejiula.ui.*;

/**
 * 发现
 */
public class FindFragment extends BaseFragment implements View.OnClickListener {
    private TextView find_jianzhi;//兼职
    private TextView find_news;//新闻
    private TextView find_notice;//告示
    private TextView find_pk;//江湖pk
    private TextView find_tv;
    private TextView find_videos;
    private ImageView find_pk_img;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.find, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        find_jianzhi = (TextView) view.findViewById(R.id.find_jianzhi);
        find_jianzhi.setOnClickListener(this);
        find_news = (TextView) view.findViewById(R.id.find_news);
        find_news.setOnClickListener(this);
        find_notice = (TextView) view.findViewById(R.id.find_notices);
        find_notice.setOnClickListener(this);
        find_pk = (TextView) view.findViewById(R.id.find_pk);
        find_tv = (TextView) view.findViewById(R.id.find_tv);
        find_videos = (TextView) view.findViewById(R.id.find_videos);
        find_pk.setOnClickListener(this);
        find_videos.setOnClickListener(this);
        find_tv.setOnClickListener(this);
        find_pk_img = (ImageView) view.findViewById(R.id.find_pk_img);
        find_pk_img.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.find_jianzhi:
                Intent part = new Intent(getActivity(), PartsActivity.class);
                startActivity(part);
                break;
            case R.id.find_news:
                Intent news = new Intent(getActivity(), NewsFragmentActivtiy.class);
                startActivity(news);
                break;
            case R.id.find_notices:
                Intent notice = new Intent(getActivity(), NoticeActivity.class);
                startActivity(notice);
                break;
            case R.id.find_pk:
            case R.id.find_pk_img:
                Intent pk = new Intent(getActivity(), PkActivity.class);
                startActivity(pk);
                break;
            case R.id.find_tv:
                Intent find_tv = new Intent(getActivity(), FindVideosActivity.class);
                startActivity(find_tv);
                break;
            case R.id.find_videos:
                //视频
                Intent videoView =  new Intent(getActivity(), VideosActivity.class);
                startActivity(videoView);
                break;
        }
    }
}
