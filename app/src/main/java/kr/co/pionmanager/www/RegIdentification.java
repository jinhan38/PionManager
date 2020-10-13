package kr.co.pionmanager.www;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.HttpAuthHandler;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;

import kr.co.pionmanager.www.PopView.PopViewActivity;
import kr.co.pionmanager.www.Registration.RegisterPage_Bank_Account;

public class RegIdentification extends AppCompatActivity {

    private WebView webView;
    private WebSettings webSettings;
    private ProgressBar progressWebView;
    private Boolean isStartAction = true;
    public static RegIdentification regIdentification;
    private static final int REQUEST_GALLERY = 0;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_identification);
        Util.SaveCurrentContext(this);
        Util.SetTagName("RegIdentification");
        regIdentification = this;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        progressWebView = findViewById(R.id.progressWebView);

        webView = findViewById(R.id.webView);

        webSettings = webView.getSettings();
        webView();
        browserSettings();

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

    public void goBank(){
        Util.intentNext(RegisterPage_Bank_Account.class);
    }


    private void webView() {
        browserSettings();
        webView.loadUrl(Util.thepion_URL + "Mobile/m_Views/join/License_IDCard_m");
        Util.SetLog(Util.thepion_URL + "Mobile/m_Views/join/License_IDCard_m");

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

    private void browserSettings() {

        webView.setBackgroundColor(0); //배경색
        webView.setHorizontalScrollBarEnabled(false); //가로 스크롤
        webView.setVerticalScrollBarEnabled(false); //세로 스크롤
        if (Build.VERSION.SDK_INT >= 21) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true); //javascript 허용
        settings.setSupportMultipleWindows(true); //다중웹뷰 허용
        settings.setJavaScriptCanOpenWindowsAutomatically(true);//javascript의 window.open 허용
        settings.setDomStorageEnabled(true);

        //HTML을 파싱하여 웹뷰에서 보여주거나 하는 작업에서 width , height 가 화면 크기와 맞지 않는 현상을 잡아주는 코드
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

        //기본 웹뷰 세팅
        //메인 추가 웹뷰 세팅
        settings.setAllowFileAccess(true);//파일 엑세스
        settings.setLoadWithOverviewMode(true);
        settings.setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
        settings.setPluginState(WebSettings.PluginState.ON);

        webView.setWebViewClient(new WishWebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int progress) {
                progressWebView.setProgress(progress);
                if (Get_Internet(RegIdentification.this) > 0) {
                    if (progress == 100) {
                        progressWebView.setVisibility(View.GONE);
                    } else {
                        if (isStartAction) {
                            progressWebView.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @SuppressLint("SetJavaScriptEnabled")
            @Override
            public boolean onCreateWindow(final WebView view, boolean dialog, boolean userGesture, Message resultMsg) {
                Log.e("onCreateWindow", "진입");

                WebView newWebView = new WebView(RegIdentification.this);
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(newWebView);
                resultMsg.sendToTarget();

                newWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        Intent browserIntent = new Intent(RegIdentification.this, PopViewActivity.class);
                        browserIntent.setData(Uri.parse(url));
                        browserIntent.putExtra("url", url);
                        RegIdentification.this.startActivity(browserIntent);
                        return true;
                    }
                });
                return true;
            }
        });

        webView.setDownloadListener((url, userAgent, contentDisposition, mimeType, contentLength) -> {

            Log.e("WebView", "Auto download Start");
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setMimeType(mimeType);

            //------------------------COOKIE!!------------------------
            String cookies = CookieManager.getInstance().getCookie(url);
            request.addRequestHeader("cookie", cookies);
            request.addRequestHeader("User-Agent", userAgent);
            //------------------------COOKIE!!------------------------

            request.setDescription("Downloading file...");
            request.setTitle(URLUtil.guessFileName(url, contentDisposition,
                    mimeType));
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(
                            url, contentDisposition, mimeType));
            DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            dm.enqueue(request);
            Toast.makeText(getApplicationContext(), "Downloading File",
                    Toast.LENGTH_LONG).show();
        });

    }

    public class WishWebViewClient extends WebViewClient {
        public static final String INTENT_PROTOCOL_START = "intent:";
        public static final String INTENT_PROTOCOL_INTENT = "#Intent;";
        public static final String INTENT_PROTOCOL_END = ";end;";
        public static final String GOOGLE_PLAY_STORE_PREFIX = "market://details?id=";
        public static final String INTENT_PROTOCOL_TEL = "tel:";
        public static final String INTENT_PROTOCOL_MAILTO = "mailto:";

        @SuppressLint("LongLogTag")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.e("shouldOverrideUrlLoading", "진입, client");
            if (url.startsWith(INTENT_PROTOCOL_START)) {
                final int customUrlStartIndex = INTENT_PROTOCOL_START.length();
                final int customUrlEndIndex = url.indexOf(INTENT_PROTOCOL_INTENT);
                if (customUrlEndIndex < 0) {
                    return false;
                } else {
                    final String customUrl = url.substring(customUrlStartIndex, customUrlEndIndex);
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(customUrl)));
                    } catch (ActivityNotFoundException e) {
                        final int packageStartIndex = customUrlEndIndex + INTENT_PROTOCOL_INTENT.length();
                        final int packageEndIndex = url.indexOf(INTENT_PROTOCOL_END);

                        final String packageName = url.substring(packageStartIndex, packageEndIndex < 0 ? url.length() : packageEndIndex);
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_PLAY_STORE_PREFIX + packageName)));
                    }
                    return true;
                }
            } else {
                view.loadUrl(url);
            }


            if (url.startsWith(INTENT_PROTOCOL_TEL)) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            } else if (url.startsWith(INTENT_PROTOCOL_MAILTO)) {
                Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
                startActivity(i);
                return true;
            }

            return false;
        }

        @SuppressLint("LongLogTag")
        @Override
        public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
            Log.e("onReceivedHttpAuthRequest", "진입");
            super.onReceivedHttpAuthRequest(view, handler, host, realm);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);

            switch (errorCode) {
                case ERROR_AUTHENTICATION: // 서버에서 사용자 인증 실패
                case ERROR_BAD_URL: // 잘못된 URL
                case ERROR_CONNECT: // 서버로 연결 실패
                case ERROR_FAILED_SSL_HANDSHAKE: // SSL handshake 수행 실패
                case ERROR_FILE: // 일반 파일 오류
                case ERROR_FILE_NOT_FOUND: // 파일을 찾을 수 없습니다
                case ERROR_HOST_LOOKUP: // 서버 또는 프록시 호스트 이름 조회 실패
                case ERROR_IO: // 서버에서 읽거나 서버로 쓰기 실패
                case ERROR_PROXY_AUTHENTICATION: // 프록시에서 사용자 인증 실패
                case ERROR_REDIRECT_LOOP: // 너무 많은 리디렉션
                case ERROR_TIMEOUT: // 연결 시간 초과
                case ERROR_TOO_MANY_REQUESTS: // 페이지 로드중 너무 많은 요청 발생
                case ERROR_UNKNOWN: // 일반 오류
                case ERROR_UNSUPPORTED_AUTH_SCHEME: // 지원되지 않는 인증 체계
                case ERROR_UNSUPPORTED_SCHEME:
                    webView.loadUrl("about:blank"); // 빈페이지 출력
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegIdentification.this);
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();// 확인버튼 클릭시 이벤트
                        }
                    });
                    builder.setMessage("네트워크 상태가 원활하지 않습니다. 어플을 종료합니다.");
                    builder.setCancelable(false); // 뒤로가기 버튼 차단
                    builder.show(); // 다이얼로그실행
                    break;
            }
        }
    }









    //촬영한 이미지 저장하기
    public void galleryAddPic() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_GALLERY) {
            if (resultCode == RESULT_OK) {
                try {
                    InputStream in = getContentResolver().openInputStream(data.getData());

                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();
//                    iv_identification.setImageBitmap(img);
                } catch (Exception e) {

                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }
    }

}
