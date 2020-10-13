package kr.co.pionmanager.www.Menu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import kr.co.pionmanager.www.AndroidBridge;
import kr.co.pionmanager.www.R;
import kr.co.pionmanager.www.UserInfo;
import kr.co.pionmanager.www.Util;

@SuppressLint("Registered")
public class LeavePion extends AppCompatActivity {
    private static final String TAG = "LeavePion";
    private WebView webView;
    private WebSettings webSettings;
    private ProgressBar progressWebView;
    private Boolean isStartAction = true;
    public static LeavePion leavePion;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_pion);
        Util.SaveCurrentContext(this);
        Util.SetTagName("BoardPager");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        leavePion = this;

        progressWebView = findViewById(R.id.progressWebView);

        ImageButton ibtn_back = findViewById(R.id.ibtn_back);
        ibtn_back.setOnClickListener(v -> onBackPressed());

        webView = findViewById(R.id.webView);
        browserSettings();

//        webSettings = webView.getSettings();
//        webView.getSettings().setJavaScriptEnabled(true); // 자바스크립트 기능 허용
//        webView.getSettings().setDefaultTextEncodingName("UTF-8"); //기본 인코딩 설정
//        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true); //팝업 허용
//        webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//        webView.getSettings().setLoadsImagesAutomatically(true); //웹뷰가 앱에 등록되어 있는 이미지 리소스 자동으로 로드하도록 설정
//        webView.getSettings().setUseWideViewPort(true); //웹뷰가 wide viewport를 사용하도록 설정, html컨텐츠가 웹뷰에 맞게 나타나도록 함
//        webView.getSettings().setSupportZoom(true); //확대 축소 기능 설정
//        webView.getSettings().setDomStorageEnabled(true); //로컬 스토리지 사용여부 설정하는 속성으로 팝업창 등을 '하루동안 보지 않기' 기능 사용에 필요
//        webView.getSettings().setAllowFileAccess(true); //웹뷰 내에서 파일 액세스 활성화 여부
//        webView.getSettings().setAllowFileAccessFromFileURLs(true); // 파일 구성표 URL의 컨텍스트에서 실행중인 JavaScript가 다른 파일 구성표의 URL의 콘텐츠에 엑세스 할 수 있는지 여부를 가져옴
//        webView.getSettings().setAllowUniversalAccessFromFileURLs(true); //파일 구성표 URL의 컨텍스트에서 실행되는 JavaScript가 모든 출처의 콘텐츠에 액세스 할 수 있는지 여부를 가져옴
//        webView.getSettings().setAppCacheEnabled(true); //앱 내부 캐시 사용여부 설정
//        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT ); //웹뷰의 캐시 모드 설정
//        webView.getSettings().setSaveFormData(true); //양식 데이터 저장
//        webView.setWebViewClient(new WebViewClient() {
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                CookieManager.getInstance().flush();
//            }
//        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public void onProgressChanged(WebView view, int progress) {
                progressWebView.setProgress(progress);
                if (Get_Internet(LeavePion.this) > 0) {
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

        webView.addJavascriptInterface(new AndroidBridge(webView, 1), "HybridApp");
        webView.loadUrl(Util.thepion_URL + "Mobile/m_Views/Manager/ManagerLeave_m?" +
                "isAndroidApp=y&leavePage=y&id=" + UserInfo.getUserID() + "&pw=" + UserInfo.getUserPassword() +
                "&autoLogin=y");
        Util.SetLog(Util.thepion_URL + "Mobile/m_Views/Manager/ManagerLeave_m?isAndroidApp=y&leavePage=y&id=" + UserInfo.getUserID() + "&pw=" + UserInfo.getUserPassword() +
                "&autoLogin=y");

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



    private void browserSettings(){

        webView.setBackgroundColor(0); //배경색
        webView.setHorizontalScrollBarEnabled(false); //가로 스크롤
        webView.setVerticalScrollBarEnabled(false); //세로 스크롤
        // Bridge 인스턴스 등록
        webView.addJavascriptInterface(new AndroidBridge(webView, 4), "HybridApp");
        Log.e(TAG, "browserSettings: 첫번재 팝뷰" );
        //mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY); //스크롤 노출타입
        if(Build.VERSION.SDK_INT >= 21) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true); //javascript 허용
        settings.setSupportMultipleWindows(true); //다중웹뷰 허용
        settings.setJavaScriptCanOpenWindowsAutomatically(true);//javascript의 window.open 허용

        //HTML을 파싱하여 웹뷰에서 보여주거나 하는 작업에서 width , height 가 화면 크기와 맞지 않는 현상이 발생한다
        //이를 잡아주기 위한 코드
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        //캐시파일 사용 금지(운영중엔 주석처리 할 것)
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE); //개발중엔 no_cache, 배포중엔 load_default

        //zoom 허용
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(true);

        //zoom 하단 도움바 삭제
        settings.setDisplayZoomControls(false);

        //meta태그의 viewport사용 가능
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()){
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
