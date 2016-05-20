package com.liangxun.yuejiula.ui;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.liangxun.yuejiula.R;
import com.liangxun.yuejiula.base.BaseActivity;
import com.liangxun.yuejiula.util.Constants;

/**
 * Created by Administrator on 2015/9/6.
 */
public class VideoPalyerActivity  extends BaseActivity {
    private FrameLayout videoview;
    //	private Button videolandport;
    private WebView videowebview;
    private Boolean islandport = true;
    private View xCustomView;
    private xWebChromeClient xwebchromeclient;
    private String url = "http://v.youku.com/v_show/id_XODcxMjc5NjYw.html";
    private WebChromeClient.CustomViewCallback xCustomViewCallback;
    private int count = 0;
    private long firClick, secClick;
    private long lastClick;
    private int firstId;
    private TextView videoerror;
    private TextView videorefresh;
    private onDoubleClick listClick = new onDoubleClick();
    private GestureDetector gestureScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_player_layout);
        initwidget();
        initListener();
        url = getIntent().getStringExtra(Constants.VIDEO_URL);
        videowebview.loadUrl(url);
    }

    private void initListener() {
        // TODO Auto-generated method stub
//		videolandport.setOnClickListener(new Listener());
    }

    @SuppressWarnings("deprecation")
    private void initwidget() {
        // TODO Auto-generated method stub
        videoview = (FrameLayout) findViewById(R.id.video_view);
        videoview.setOnTouchListener(listClick);
//		videolandport = (Button) findViewById(R.id.video_landport);
        videowebview = (WebView) findViewById(R.id.video_webview);
        videoerror = (TextView) findViewById(R.id.video_error);
        videorefresh = (TextView) findViewById(R.id.video_refresh);
        videoerror.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                videowebview.loadUrl("about:blank");
                videowebview.loadUrl(url);
                videoerror.setVisibility(View.GONE);
                videorefresh.setVisibility(View.VISIBLE);
                videowebview.setVisibility(View.GONE);
            }
        });
        WebSettings ws = videowebview.getSettings();
        ws.setBuiltInZoomControls(true);
        ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        ws.setUseWideViewPort(true);
        ws.setLoadWithOverviewMode(true);
        ws.setSavePassword(true);
        ws.setSaveFormData(true);// ���������
        ws.setJavaScriptEnabled(true);
        ws.setGeolocationEnabled(true);
        ws.setGeolocationDatabasePath("/data/data/org.itri.html5webview/databases/");
        ws.setDomStorageEnabled(true);
        xwebchromeclient = new xWebChromeClient();
        videowebview.setWebChromeClient(xwebchromeclient);
        videowebview.setWebViewClient(new xWebViewClientent());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (inCustomView()) {
                hideCustomView();
                return true;
            } else {
                videowebview.loadUrl("about:blank");
                VideoPalyerActivity.this.finish();
            }
        }
        return true;
    }


    public boolean inCustomView() {
        return (xCustomView != null);
    }


    public void hideCustomView() {
        xwebchromeclient.onHideCustomView();
    }

    public class xWebChromeClient extends WebChromeClient {
        private Bitmap xdefaltvideo;
        private View xprogressvideo;

        @Override
        public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
            if (islandport) {

            } else {

            }
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            videowebview.setVisibility(View.GONE);
            if (xCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }

            videoview.addView(view);
            xCustomView = view;
            xCustomViewCallback = callback;
            videoview.setVisibility(View.VISIBLE);
        }

        @Override
        public void onHideCustomView() {

            if (xCustomView == null)
                return;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            xCustomView.setVisibility(View.GONE);

            videoview.removeView(xCustomView);
            xCustomView = null;
            videoview.setVisibility(View.GONE);
            xCustomViewCallback.onCustomViewHidden();

            videowebview.setVisibility(View.VISIBLE);
        }

        @Override
        public Bitmap getDefaultVideoPoster() {
            if (xdefaltvideo == null) {
                xdefaltvideo = BitmapFactory.decodeResource(
                        getResources(), R.drawable.videoicon);
            }
            return xdefaltvideo;
        }

        @Override
        public View getVideoLoadingProgressView() {
            if (xprogressvideo == null) {
                LayoutInflater inflater = LayoutInflater.from(VideoPalyerActivity.this);
                xprogressvideo = inflater.inflate(R.layout.video_loading_progress, null);
            }
            return xprogressvideo;
        }

        //��ҳ����
        @Override
        public void onReceivedTitle(WebView view, String title) {
            (VideoPalyerActivity.this).setTitle(title);
        }

    }

    public class xWebViewClientent extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.i("webviewtest", "shouldOverrideUrlLoading: " + url);
            return false;
        }


        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            videowebview.setVisibility(View.GONE);
            videoerror.setVisibility(View.VISIBLE);
            videorefresh.setVisibility(View.GONE);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            videowebview.setVisibility(View.VISIBLE);
            videoerror.setVisibility(View.GONE);
            videorefresh.setVisibility(View.GONE);
            super.onPageFinished(view, url);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.i("testwebview", "=====<<<  onConfigurationChanged  >>>=====");
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.i("webview", "   �����Ǻ���1");
            islandport = false;
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.i("webview", "   ����������1");
            islandport = true;
        }
    }

    class onDoubleClick implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (MotionEvent.ACTION_DOWN == event.getAction()) {
                count++;
                if (count == 1) {
                    firClick = System.currentTimeMillis();

                } else if (count == 2) {
                    secClick = System.currentTimeMillis();
                    if (secClick - firClick < 500) {
                        //˫���¼�
                        Toast.makeText(VideoPalyerActivity.this, "˫������Ļ", Toast.LENGTH_LONG).show();
                    } else {

                    }
                    count = 0;
                    firClick = 0;
                    secClick = 0;

                }
            }
            return true;
        }
    }
}
