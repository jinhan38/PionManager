package kr.co.pionmanager.www;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import androidx.annotation.RequiresApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import kr.co.pionmanager.www.Menu.LeavePion;
import kr.co.pionmanager.www.PopView.PopMoreMoreViewActivity;
import kr.co.pionmanager.www.PopView.PopMoreViewActivity;
import kr.co.pionmanager.www.PopView.PopViewActivity;
import kr.co.pionmanager.www.Registration.RegisterPage_1;
import static kr.co.pionmanager.www.AddAddressPage.addAddressPage;


/**
 * Created by Administrator on 2017-08-03.
 */

public class AndroidBridge {
    private static final String TAG = "AndroidBridge";
    private final Handler handler = new Handler();
    private final Handler handler2 = new Handler();
    private final Handler handler3 = new Handler();
    private final Handler handler4 = new Handler();
    private final Handler handlerLeaveManager = new Handler();
    private WebView mWebView;
    private Integer curNum;
    private Context context;

    public AndroidBridge() {
    }

    // 생성자
    // 따로 사용할일 없으면 이거 안만들고 위의 변수도 안만들어도 됨.
    public AndroidBridge(WebView mWebView, Integer curNum) {
        this.mWebView = mWebView;
        this.curNum = curNum;
        Log.e(TAG, "AndroidBridge: 브릿지 연결됨");
    }


    public AndroidBridge(Context context) {
        this.context = context;
    }


    @JavascriptInterface
    public void leaveManager() {
        Log.e(TAG, "run : leaveManager ");
        handlerLeaveManager.post(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "run : leaveManager ");
                activityClose();
                UserInfo.LogOut();
                Util.intentNext(LoginPage.class);
            }
        });

    }

    @JavascriptInterface
    public void gradeUpdateAlert(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                //grade sharedprefference에 저장
                // 웹뷰 자동로그인 할 때 기존의 쉐어드에서 저장된 기존의 grade와 다르면 팝업 띄워서 보여주면서 자동로그인 시킴
            }
        });
    }

    /**
     * 회원가입 본인인증 시 본인인증 하고 입력자료 받음
     *
     * @param str
     */
    @JavascriptInterface
    public void activityCloseSendParam(final String str) {
        handler.post(new Runnable() {
            public void run() {
                Log.e("HybridApp", "데이터 요청");
                String[] result = str.split("\\|");
                for (String string : result) {
                    Log.e(TAG, "run: " + string);
                }

                RegisterPage_1.name = result[0];
                RegisterPage_1.dob = result[1];
                RegisterPage_1.phone = result[2];
                RegisterPage_1.sex = result[3];

                String strUrl = Util.thepion_URL + "Views/Shared/ExistMemberCheck?name=" + result[0] + "&mobile=y" + "&dob=" + result[1] + "&isManager=y";
                checkRegister checkRegister = (checkRegister) new checkRegister().execute(strUrl);
                Log.e(TAG, "run: " + strUrl);

                activityClose();
            }
        });
    }


    @JavascriptInterface
    public void webViewScrollEdit(final boolean b){
        handler.post(() -> {
             MyViewPager.isWebViewScrollPossible = b;
            Log.e(TAG, "webViewScrollEdit: " + b );
        });
    }


    // 토큰 생성하기
    @JavascriptInterface
    public void showMyBidList() { // 토큰값을 호출하면 아래 함수로 값을 전송한다.
        handler2.post(new Runnable() {
            public void run() {
                addAddressPage.webView_search.loadUrl(Util.thepion_URL + "Mobile/m_Views/Auction/bidSearch_app");
//                mainActivity.goMyBidList(mainActivity.bt_mypage_main_activity);
//                Util.showMyBidList = true;
//                Log.e("showMyBidList", "진입");
                //mWebView.loadUrl("javascript:getAndroidDataToken('"+Util.TOKEN+"')");
            }
        });
    }

    @JavascriptInterface
    public void showInfoBidding() {
        handler.post(() -> {
            addAddressPage.ShowDialog_explain();
            Log.e("HybridApp", "데이터 요청");
            String test = "닫기버튼이 눌렸습니다.";
            Log.e("HybridApp", test);
            //window.HybridApp.activityClose(); //웹쪽 자바스크립트단에서 activityClose 호출
            //mWebView.loadUrl("javascript:getAndroidData('"+test+"')"); //안드로이드에서 웹페이지 자바스크립트 호출하기
            //function getAndroidData(data) { //웹쪽 자바스크립트에서 데이터 받기
            //        alert(data);
            //}
        });
    }

    @JavascriptInterface
    public void goRegisterBank() {
        handler.post(new Runnable() {
            public void run() {
                RegIdentification.regIdentification.goBank();
                Log.e("HybridApp", "데이터 요청");
                Log.e("HybridApp", "goRegisterBank");
                //window.HybridApp.activityClose(); //웹쪽 자바스크립트단에서 activityClose 호출
                //mWebView.loadUrl("javascript:getAndroidData('"+test+"')"); //안드로이드에서 웹페이지 자바스크립트 호출하기
                //function getAndroidData(data) { //웹쪽 자바스크립트에서 데이터 받기
                //        alert(data);
                //}
            }
        });
    }


    @JavascriptInterface
    public void choicePicture() {
        handler.post(new Runnable() {
            public void run() {
                RegIdentification.regIdentification.galleryAddPic();
                Log.e("HybridApp", "데이터 요청");
                Log.e("HybridApp", "goRegisterBank");
            }
        });
    }


    // 창닫기
    @JavascriptInterface
    public void activityClose() { // must be final
        handler.post(new Runnable() {
            public void run() {
                Log.e("HybridApp", "데이터 요청");
                String test = "닫기버튼이 눌렸습니다.";
                Log.e("HybridApp", test);

                //웹뷰 브릿지 연결할 때 curNum 주의하기
                if (curNum == 1) {
                    PopViewActivity.openerActivity.onBackPressed();
                } else if (curNum == 2) {
                    PopMoreViewActivity.openerMoreActivity.onBackPressed();
                } else if (curNum == 3) {
                    PopMoreMoreViewActivity.openerMoreMoreActivity.onBackPressed();
                } else if (curNum == 4) {
                    LeavePion.leavePion.onBackPressed();
                } else if (curNum == 0) {
                    BottomNaviWrap.bottomNaviWrap.onBackPressed();
                }

            }
        });
    }


    @JavascriptInterface
    public void activityCloseParentRefresh(final String url) { // must be final
        handler.post(new Runnable() {
            @SuppressLint("LongLogTag")
            public void run() {
                String test = "닫기버튼이 눌렸습니다.";
                Log.e("HybridApp", test);

                if (curNum == 1) {
                    addAddressPage.webView_search.loadUrl(url);
                    PopViewActivity.openerActivity.finish();
                } else if (curNum == 2) {
                    PopViewActivity.openerActivity.pWebView.loadUrl(url);
                    PopMoreViewActivity.openerMoreActivity.finish();
                } else if (curNum == 3) {
                    PopViewActivity.openerActivity.pWebView.loadUrl(url);
                    PopMoreViewActivity.openerMoreActivity.finish();
                    PopMoreMoreViewActivity.openerMoreMoreActivity.finish();
                } else if (curNum == 0) {
                    addAddressPage.webView_search.loadUrl(url);
                }

            }
        });
    }


    // 토큰 생성하기
    @JavascriptInterface
    public void callTokenValue(final String num, final String id, final String level) { // 토큰값을 호출하면 아래 함수로 값을 전송한다.
        handler2.post(new Runnable() {
            public void run() {
                Util.NUM = num;
                Util.MID = id;
                Util.MEMBER_LEVEL = level;
                Util.isLogin = true;
                Log.e("loginVal", Util.NUM + Util.MID + Util.MEMBER_LEVEL);
            }
        });
    }


    // 안드로이드 접속
    @JavascriptInterface
    public void connectAndroidApp() { // 토큰값을 호출하면 아래 함수로 값을 전송한다.
        handler3.post(new Runnable() {
            public void run() {
                mWebView.loadUrl("javascript:isAndroidApp('" + Util.TOKEN + "')"); //토큰값 입력
                Log.e(TAG, "run: 토큰값 입력" );
                //mWebView.loadUrl("javascript:nowAndroidAppVersion('"+getVersionInfo(mainActivity)+"')"); //현재 버전 비교
            }
        });
    }




    @JavascriptInterface
    public void savePhoneInWebView(final String name, final String phone, final boolean isManager) {
        handler4.post(new Runnable() {
            public void run() {
                Log.e("savePhoneInWebView", "name : " + name);
                Log.e("savePhoneInWebView", "name : " + phone);
                Log.e("savePhoneInWebView", "name : " + isManager);
                String rename = name;
                if (isManager) rename += "(PION 매니저)";
                else rename += "(PION 고객)";
                Util.savePhone(rename, phone);
            }
        });
    }


//    //웹 자바스크립트에서 호출
//    @JavascriptInterface
//    public void onDownloadStart(String url, String fileName) {
//
//        Log.e("WebView", "clicked!");
//        Toast toast = new Toast(mainActivity);
//        //다운로드 모듈
//        Util.DOWNLOAD_FILE_NAME = fileName;
//        toast.makeText(mainActivity, "파일다운로드 중입니다.", Toast.LENGTH_LONG).show();
//        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//        String strDir = file.getAbsolutePath();
//
//        DownloadManager.getInstance().setSavePath(strDir + "/THEPION"); // 저장하려는 경로 지정.
//        DownloadManager.getInstance().setDownloadUrl("https://www.thepion.co.kr" + url);
//        Log.e("WebView", "true");
//
//    }

    public String getVersionInfo(Context context) {
        String version = null;
        try {
            PackageInfo i = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = i.versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return version;
    }


    @SuppressLint("StaticFieldLeak")
    private class checkRegister extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "다운로드 실패" + e.toString();
            }
        }

        protected void onPostExecute(String result) {
            Log.e("result", result);
            getRefManagerStatus(result);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        private String downloadUrl(String myurl) throws IOException {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(myurl);
                Log.e("url", myurl);
                conn = (HttpURLConnection) url.openConnection();
                BufferedInputStream buf = new BufferedInputStream(conn.getInputStream());
                BufferedReader bufreader = new BufferedReader(new InputStreamReader(buf, StandardCharsets.UTF_8));
                String line;
                StringBuilder page = new StringBuilder();
                while ((line = bufreader.readLine()) != null) {
                    page.append(line);
                }
                return page.toString();
            } finally {
                assert conn != null;
                conn.disconnect();
            }
        }

        private void getRefManagerStatus(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                String rValue = jsonObject.getString("result");
                String name = jsonObject.getString("name");
                String mobile = jsonObject.getString("mobile");
                String isAgePass = jsonObject.getString("isAgePass");
                String age = jsonObject.getString("age");

                RegisterPage_1.rValue = rValue;
                RegisterPage_1.isAgePass = isAgePass;

                Log.e(TAG, "getRefManagerStatus: rValue : " + rValue);
                Log.e(TAG, "getRefManagerStatus: isAgePass : " + isAgePass);

                PopViewActivity.openerActivity.finish();
                RegisterPage_1.registerPage_1.goNextRegister();
            } catch (JSONException e) {
                Log.w(TAG, "Could not parse JSON. Error: " + e.getMessage());
            }
        }

    }

}

