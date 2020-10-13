package kr.co.pionmanager.www;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.Random;

import kr.co.pionmanager.www.ExplainDialog.UpdatePopup;
import kr.co.pionmanager.www.PionIntroduce.IntroduceParent;

public class Loading extends AppCompatActivity {


    private static final String TAG = "Loading";
    private ProgressBar progressBar;
    private ProgressBar progressBar_2;
    int pStatus = 0;
    private Handler handler = new Handler();
    private String strVersion;
    private String intVersion;
    private String mNeedUpdate = "y";
    public static Loading loading;
    private UpdatePopup updatePopup;
    private NetworkCheck networkCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        Util.SaveCurrentContext(this);
        loading = this;
        setIntroTips();

        //FCM 데이터 보낸 데이터 받는 코드
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Log.e(TAG, "onCreate: type" + bundle.get("type"));
            Log.e(TAG, "onCreate: userNum" + bundle.get("userNum"));
            Log.e(TAG, "onCreate: url : " + bundle.get("url"));
            UserInfo.setPushOpen(true);
            UserInfo.setPushType(bundle.get("type"));
            UserInfo.setPushUserNum(bundle.get("userNum"));
            String str = String.valueOf(bundle.get("url"));
            int index = str.indexOf("LiNum=");
            str = str.substring(index + 6);
            UserInfo.setPushUrl(str);
            Log.e(TAG, "onCreate: url : " + str);
        }


        final ProgressBar mProgress = (ProgressBar) findViewById(R.id.circularProgressbar);
        final ProgressBar mProgress_2 = (ProgressBar) findViewById(R.id.circularProgressbar_2);

        progressBar = new ProgressBar(getBaseContext(), null, android.R.attr.progressBarStyleHorizontal);
        progressBar_2 = new ProgressBar(getBaseContext(), null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setIndeterminate(false);
        progressBar_2.setIndeterminate(false);


        new Thread(() -> {
            // TODO Auto-generated method stub
            while (pStatus < 100) {
                pStatus += 1;
                handler.post(() -> {
                    // TODO Auto-generated method stub
                    mProgress.setProgress(pStatus);
                    mProgress_2.setProgress(pStatus);
                });
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        getHashKey();

        strVersion = getString(R.string.version).replaceAll("ver", "").trim();
        intVersion = strVersion.replaceAll("\\.", "");
        Log.e(TAG, "login: intVersion" + intVersion);


    }


    private void setIntroTips() {
        String[] pion_tips_array = getResources().getStringArray(R.array.pion_tips);
        TextView pion_tips = findViewById(R.id.pion_tips);
        int randomIndex = new Random().nextInt(pion_tips_array.length);
        String randomText = pion_tips_array[randomIndex];
        pion_tips.setText(randomText);
    }

    private void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            //프리퍼런스가 있는지 확인
            if (SharedPreference.getAutoLogin(Loading.this).equals("y")) {//체크박스가 체크된 상태라면

                UserInfo.setIsLogin(true);
                UserInfo.setUserName(SharedPreference.getUserName(Loading.this));
                UserInfo.setUserID(SharedPreference.getUserID(Loading.this));
                UserInfo.setUserNum(SharedPreference.getUserNum(Loading.this));
                UserInfo.setUserPassword(SharedPreference.getUserPassword(Loading.this));
                UserInfo.setUserGrade(SharedPreference.getUserGrade(Loading.this));
                UserInfo.setUserLicenseNum(SharedPreference.getUserLicenseNum(Loading.this));
                UserInfo.setUserBankNum(SharedPreference.getUserBankNum(Loading.this));
                UserInfo.setHasCardImage(SharedPreference.getHasCardImage(Loading.this));
                UserInfo.setRefMemberNum(SharedPreference.getRefMemberNum(Loading.this));
                UserInfo.setAutoLogin(SharedPreference.getAutoLogin(Loading.this));
                UserInfo.setAutoLogin(SharedPreference.getUserBankName(Loading.this));
                UserInfo.setAutoLogin(SharedPreference.getUserBankOwner(Loading.this));
                UserInfo.setAutoLogin(SharedPreference.getUserDOB(Loading.this));
                UserInfo.setAutoLogin(SharedPreference.getUserPhone(Loading.this));
                UserInfo.setAutoLogin(SharedPreference.getUserRefManagerID(Loading.this));
                UserInfo.setRefManagerGrade(SharedPreference.getRefManagerGrade(Loading.this));
                UserInfo.setAutoLogin("y");
                UserInfo.setManagerRate(SharedPreference.getManagerRate(Loading.this));
                UserInfo.setPlatformRate(SharedPreference.getPlatformRate(Loading.this));
                UserInfo.setTeamLeaderRate(SharedPreference.getTeamLeaderRate(Loading.this));
                UserInfo.setRefManagerNum(SharedPreference.getRefManagerNum(Loading.this));

                Intent intent = new Intent(Loading.this, BottomNaviWrap.class);
                startActivity(intent);

            } else {
                if (SharedPreference.getCloseChecked(Loading.this)) {
                    Util.intentNext(LoginPage.class);
                } else {
                    Util.intentNext(IntroduceParent.class);
                }
            }

            finish();
        }, 1500);
    }

    @SuppressLint("PackageManagerGetSignatures")
    private void getHashKey() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("KeyHash", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
    }

    @Override
    protected void onResume() {

        networkCheck = new NetworkCheck(this);
        networkCheck.register();
        if (mNeedUpdate.equals("y")) {
            String strUrl = Util.thepion_URL + "AjaxControl/versionCheck?strVersion=" + strVersion + "&intVersion=" + intVersion;
            VersionCheckTask versionCheckTask = (VersionCheckTask) new VersionCheckTask().execute(strUrl);
        }
        super.onResume();
    }

    @SuppressLint("StaticFieldLeak")
    private class VersionCheckTask extends AsyncTask<String, Void, String> {

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

        protected void onPostExecute(String result) {
            Log.e("result", result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                mNeedUpdate = jsonObject.getString("needUpdate");
                Log.e(TAG, "needUpdate: " + mNeedUpdate);
                if (mNeedUpdate.equals("y")) {
                    //show 팝업
                    showPopup();
                } else {
                    startLoading();
                }

            } catch (JSONException e) {
                Log.e("TAG", "Could not parse JSON. Error: " + e.getMessage());
            }
        }

    }

    private void showPopup() {
        Window window = null;

        updatePopup = new UpdatePopup(loading, positiveListener);
        Objects.requireNonNull(updatePopup.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        updatePopup.show();
        window = updatePopup.getWindow();

        //디스플레이 해상도 가져오기
        Display display = loading.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int x = (int) (size.x * 0.8f);
        int y = (int) (size.y * 0.45f);

        window.setLayout(x, y);
    }

    private OnSingleClickListener positiveListener = new OnSingleClickListener() {
        @Override
        public void onSingleClick(View v) {
            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        }
    };

    @Override
    protected void onDestroy() {
        networkCheck.unregister();
        super.onDestroy();
    }
}
