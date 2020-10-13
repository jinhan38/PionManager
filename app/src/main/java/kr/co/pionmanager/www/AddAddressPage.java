package kr.co.pionmanager.www;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.webkit.HttpAuthHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Objects;

import kr.co.pionmanager.www.ExplainDialog.ExplainBidding;
import kr.co.pionmanager.www.Menu.MypageAboutData;
import kr.co.pionmanager.www.PopView.PopViewActivity;

public class AddAddressPage extends Fragment {

    private static final String TAG = "AddAddressPage";
    private View v;
    public ScrollWebView webView_search;
    private ProgressBar progressBar;
    private AndroidBridge androidBridge;
    public static AddAddressPage addAddressPage;
    private Boolean isStartAction = true;
    private ExplainBidding explainBidding;
    public static AddAddressPage singlton;
    private long backBtnTime = 0;
    private SwipeRefreshLayout swipeRefresh;
    private String pw = "";

    // newInstance constructor for creating fragment with arguments
    public static AddAddressPage newInstance() {
        if (singlton == null) {
            singlton = new AddAddressPage();
        }
        return singlton;
    }

    public void clearSingleTon() {
        this.singlton = null;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_add_address_page, container, false);
        Util.SaveCurrentContext(getActivity());
        addAddressPage = this;
        initView();
        setupListener();
        webView();
        return v;
    }

    public void webViewLoad() {
        webView_search.loadUrl(Util.thepion_URL + "Mobile/m_Views/Auction/bidSearch_app");
        Log.e(TAG, Util.thepion_URL + "Mobile/m_Views/Auction/bidSearch_app");
    }

    @SuppressLint("WrongViewCast")
    private void initView() {
        webView_search =  v.findViewById(R.id.webView_search);
        progressBar = v.findViewById(R.id.progress);
        swipeRefresh = v.findViewById(R.id.swipeRefresh);
    }


    private void setupListener() {

        swipeRefresh.setEnabled(false);

    }

    private void webView() {
        browserSettings();
        try {
            pw = URLEncoder.encode(UserInfo.getUserPassword(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        webView_search.loadUrl(Util.thepion_URL + "AjaxControl/LoginForAndroid?id=" + UserInfo.getUserID() + "&pw=" + pw + "&autoLogin=" + UserInfo.getAutoLogin());
        Log.e(TAG, "webView: = " + Util.thepion_URL + "AjaxControl/LoginForAndroid?id=" + UserInfo.getUserID() + "&pw=" + pw + "&autoLogin=" + UserInfo.getAutoLogin());

        webView_search.addJavascriptInterface(new AndroidBridge(webView_search, 0), "HybridApp");

        webView_search.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() != KeyEvent.ACTION_DOWN)
                return true;
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (webView_search.canGoBack()) {
                    //웹뷰상에서 키보드가 열려있을 때 webView_search.goBack()을 하지 않도록 한다.
                    //키보드가 올라온 상태에서 webView_search.goBack()을 하면 키보드가 내려가고 goBack하지만 실제 웹뷰는 goBack하지 않는다.
                    if (Util.CURRENT_CONTEXT.getResources().getConfiguration().hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
                        webView_search.goBack();
                    }
                }
                return true;
            }
            return false;
        });

    }

    /**
     * 주소검색 웹뷰에 있는 question 아이콘 클릭 팝업 메소드
     * android bridge에서 자바스크립트와 연동중
     */
    public void ShowDialog_explain() {
        explainBidding = new ExplainBidding(getActivity(), positiveListener);
        Objects.requireNonNull(explainBidding.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        explainBidding.show();

        //디스플레이 해상도 가져오기
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        Window window = explainBidding.getWindow();
        int x = (int) (size.x * 0.9f);
        int y = (int) (size.y * 0.8f);
        window.setLayout(x, y);
    }

    private OnSingleClickListener positiveListener = new OnSingleClickListener() {
        @Override
        public void onSingleClick(View v) {
            explainBidding.dismiss();
        }
    };

    private void browserSettings() {

        webView_search.setBackgroundColor(0); //배경색
        webView_search.setHorizontalScrollBarEnabled(false); //가로 스크롤
        webView_search.setVerticalScrollBarEnabled(false); //세로 스크롤
        if (Build.VERSION.SDK_INT >= 21) {
            webView_search.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        androidBridge = new AndroidBridge(webView_search, 0);
        webView_search.addJavascriptInterface(androidBridge, "HybridApp");


        WebSettings settings = webView_search.getSettings();
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
        settings.setAppCachePath(addAddressPage.getActivity().getCacheDir().getAbsolutePath());
        settings.setPluginState(WebSettings.PluginState.ON);

        webView_search.setWebViewClient(new WishWebViewClient());


        webView_search.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (getActivity() != null) {
                    progressBar.setProgress(progress);
                    if (Get_Internet(requireActivity()) > 0) {
                        if (progress == 100) {
                            progressBar.setVisibility(View.GONE);

                            Log.e(TAG, "onProgressChanged: 진행");
                            //최초 웹페이지 스크립트를 호출한다. 토큰값 전달
                            androidBridge.connectAndroidApp();


                        } else {
                            if (isStartAction) {
                                progressBar.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            }

            @SuppressLint("SetJavaScriptEnabled")
            @Override
            public boolean onCreateWindow(final WebView view, boolean dialog, boolean userGesture, Message resultMsg) {

                WebView newWebView = new WebView(getActivity());
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(newWebView);
                resultMsg.sendToTarget();

                newWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        Intent browserIntent = new Intent(getActivity(), PopViewActivity.class);
                        browserIntent.setData(Uri.parse(url));
                        browserIntent.putExtra("url", url);
                        getActivity().startActivity(browserIntent);
                        return true;
                    }
                });
                return true;
            }
        });
//        webView_search.setDownloadListener((url, userAgent, contentDisposition, mimeType, contentLength) -> {
//
//            Log.e("WebView", "Auto download Start");
//            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
//            request.setMimeType(mimeType);
//
//            //------------------------COOKIE!!------------------------
//            String cookies = CookieManager.getInstance().getCookie(url);
//            request.addRequestHeader("cookie", cookies);
//            request.addRequestHeader("User-Agent", userAgent);
//            //------------------------COOKIE!!------------------------
//
//            request.setDescription("Downloading file...");
//            request.setTitle(URLUtil.guessFileName(url, contentDisposition,
//                    mimeType));
//            request.allowScanningByMediaScanner();
//            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//            request.setDestinationInExternalPublicDir(
//                    Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(
//                            url, contentDisposition, mimeType));
//            DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
//            dm.enqueue(request);
//            Toast.makeText(getApplicationContext(), "Downloading File",
//                    Toast.LENGTH_LONG).show();
//        });

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
                        addAddressPage.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(customUrl)));
                    } catch (ActivityNotFoundException e) {
                        final int packageStartIndex = customUrlEndIndex + INTENT_PROTOCOL_INTENT.length();
                        final int packageEndIndex = url.indexOf(INTENT_PROTOCOL_END);

                        final String packageName = url.substring(packageStartIndex, packageEndIndex < 0 ? url.length() : packageEndIndex);
                        addAddressPage.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_PLAY_STORE_PREFIX + packageName)));
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
                    webView_search.loadUrl("about:blank"); // 빈페이지 출력
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setPositiveButton("확인", (dialog, which) -> {
                        getActivity().finish();// 확인버튼 클릭시 이벤트
                    });
                    builder.setMessage("네트워크 상태가 원활하지 않습니다. 어플을 종료합니다.");
                    builder.setCancelable(false); // 뒤로가기 버튼 차단
                    builder.show(); // 다이얼로그실행
                    break;
            }
        }
    }

    //    Get_Internet
//: 인터넷 연결환경에 대해 체크한다.
//            0을 리턴할 경우, 인터넷 연결끊김
//1을 리턴할 경우, 와이파이 연결상태
//2를 연결할 경우, 인터넷 연결상태
// */
    public static int Get_Internet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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

    @Override
    public void onPause() {
        super.onPause();
        progressBar.setProgress(0);
    }

    @Override
    public void onResume() {
        super.onResume();
        progressBar.setProgress(0);
    }

}
