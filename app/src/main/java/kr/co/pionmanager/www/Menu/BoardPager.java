package kr.co.pionmanager.www.Menu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.JsResult;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import kr.co.pionmanager.www.R;
import kr.co.pionmanager.www.UserInfo;
import kr.co.pionmanager.www.Util;

@SuppressLint("Registered")
public class BoardPager extends AppCompatActivity {

    private static final String TAG = "BoardPager";
    private WebView webView;
    private WebSettings webSettings;
    private ProgressBar progressWebView;
    private Boolean isStartAction = true;
    private Activity activity;
    private  TextView tv_title;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_pager);
        Util.SaveCurrentContext(this);
        Util.SetTagName("BoardPager");
        activity = this;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        progressWebView = findViewById(R.id.progressWebView);

        ImageButton ibtn_back = findViewById(R.id.ibtn_back);
        ibtn_back.setOnClickListener(v -> onBackPressed());

        webView = findViewById(R.id.webView);

        webSettings = webView.getSettings();
        webView.getSettings().setJavaScriptEnabled(true); // 자바스크립트 기능 허용
        webView.getSettings().setDefaultTextEncodingName("UTF-8"); //기본 인코딩 설정
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true); //팝업 허용
        webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webView.getSettings().setLoadsImagesAutomatically(true); //웹뷰가 앱에 등록되어 있는 이미지 리소스 자동으로 로드하도록 설정
        webView.getSettings().setUseWideViewPort(true); //웹뷰가 wide viewport를 사용하도록 설정, html컨텐츠가 웹뷰에 맞게 나타나도록 함
        webView.getSettings().setSupportZoom(true); //확대 축소 기능 설정
        webView.getSettings().setDomStorageEnabled(true); //로컬 스토리지 사용여부 설정하는 속성으로 팝업창 등을 '하루동안 보지 않기' 기능 사용에 필요
        webView.getSettings().setAllowFileAccess(true); //웹뷰 내에서 파일 액세스 활성화 여부
        webView.getSettings().setAllowFileAccessFromFileURLs(true); // 파일 구성표 URL의 컨텍스트에서 실행중인 JavaScript가 다른 파일 구성표의 URL의 콘텐츠에 엑세스 할 수 있는지 여부를 가져옴
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true); //파일 구성표 URL의 컨텍스트에서 실행되는 JavaScript가 모든 출처의 콘텐츠에 액세스 할 수 있는지 여부를 가져옴
        webView.getSettings().setAppCacheEnabled(true); //앱 내부 캐시 사용여부 설정
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT); //웹뷰의 캐시 모드 설정
        webView.getSettings().setSaveFormData(true); //양식 데이터 저장
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                CookieManager.getInstance().flush();
            }
        });

        webView.setWebChromeClient(new FullscreenableChromeClient(activity) {

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public void onProgressChanged(WebView view, int progress) {
                progressWebView.setProgress(progress);
                if (Get_Internet(BoardPager.this) > 0) {
                    if (progress == 100) {
                        progressWebView.setVisibility(View.GONE);
                    } else {
                        if (isStartAction) {
                            progressWebView.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

        });

        tv_title = findViewById(R.id.tv_title);
        Intent intent = getIntent();

        if (Objects.equals(Objects.requireNonNull(intent.getExtras()).getString("page"), "faq")) setURL("faq");
        else if (Objects.equals(Objects.requireNonNull(intent.getExtras()).getString("page"), "board")) setURL("board");
        else if (intent.getExtras().getString("MypageNotice").equals("1")) setURL("1");
        else if (intent.getExtras().getString("MypageNotice").equals("2")) setURL("2");
        else if (intent.getExtras().getString("MypageNotice").equals("3")) setURL("3");
        else Util.showToast("네트워크 연결상태가 좋지 않습니다. 다시 시도해주시기 바랍니다.");

        webView.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() != KeyEvent.ACTION_DOWN)
                return true;
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    onBackPressed();
                }
                return true;
            }
            return false;
        });

    }

    private void setURL(String pageName){
        switch (pageName){
            case "faq":
                webView.loadUrl(Util.thepion_URL + "Mobile/m_Views/BBS/pion_faq_m");
                Util.SetLog(Util.thepion_URL + "Mobile/m_Views/BBS/pion_faq_m");
                tv_title.setText(R.string.tv_faq);
                break;
            case "board":
                webView.loadUrl(Util.thepion_URL + "Mobile/m_Views/BBS/Index?currentPage=1&tCode=a1&isAndroidApp=y");
                Util.SetLog(Util.thepion_URL + "Mobile/m_Views/BBS/Index?currentPage=1&tCode=a1&isAndroidApp=y");
                tv_title.setText(R.string.tv_notice_board);
                break;
            case "1":
                webView.loadUrl(Util.thepion_URL + "Mobile/m_views/BBS/Detail?tCode=a1&searchValue=&currentPage=1&num=" + UserInfo.getNotice_1_num());
                Util.SetLog(Util.thepion_URL + "Mobile/m_views/BBS/Detail?tCode=a1&searchValue=&currentPage=1&num=" + UserInfo.getNotice_1_num());
                tv_title.setText(R.string.tv_notice_board);
                break;
            case "2":
                webView.loadUrl(Util.thepion_URL + "Mobile/m_views/BBS/Detail?tCode=a1&searchValue=&currentPage=1&num=" + UserInfo.getNotice_2_num());
                Util.SetLog(Util.thepion_URL + "Mobile/m_views/BBS/Detail?tCode=a1&searchValue=&currentPage=1&num=" + UserInfo.getNotice_2_num());
                tv_title.setText(R.string.tv_notice_board);
                break;
            case "3":
                webView.loadUrl(Util.thepion_URL + "Mobile/m_views/BBS/Detail?tCode=a1&searchValue=&currentPage=1&num=" + UserInfo.getNotice_3_num());
                Util.SetLog(Util.thepion_URL + "Mobile/m_views/BBS/Detail?tCode=a1&searchValue=&currentPage=1&num=" + UserInfo.getNotice_3_num());
                tv_title.setText(R.string.tv_notice_board);
                break;

        }


    }

    public static int Get_Internet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return 1;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                return 2;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_WIMAX) {
                return 3;
            }
        }
        return 0;
    }

}
