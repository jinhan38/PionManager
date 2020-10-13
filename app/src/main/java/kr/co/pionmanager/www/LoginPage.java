package kr.co.pionmanager.www;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;

import kr.co.pionmanager.www.Registration.RegisterPage_1;

public class LoginPage extends AppCompatActivity {

    private static final String TAG = "LoginPage";
    private long backBtnTime = 0;
    private Button bt_login;
    private Button btn_find_id_pw;
    private CheckBox login_auto;
    private EditText et_id_login;
    private EditText et_pw_login;
    private TextView tv_err_login_id;
    private TextView tv_err_login_pw;
    private String autoLogin = "y";
    int errNum = 0;
    private String strUrl = "";
    private DownloadWebpageTask mTask = null;
    private Timer timer;
    private ProgressBar progressBar;
    public static LoginPage loginPage;
    public static boolean completeLicenseApply = false;
    private ProgressBar match_progress;
    private LinearLayout ll_parent;
    private Button register;
    private ProgressDialog dialog;
    private NetworkCheck networkCheck;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        Util.SaveCurrentContext(getApplicationContext());
        Util.SetTagName("LoginPage");

        initView();
        if (completeLicenseApply) {
            match_progress.setVisibility(View.VISIBLE);
            ll_parent.setVisibility(View.GONE);
        }
        licenseApplyLogin();
        setupListener();

    }

    private void initView() {
        loginPage = this;
        networkCheck = new NetworkCheck(this);
        networkCheck.register();

        match_progress = findViewById(R.id.match_progress);
        ll_parent = findViewById(R.id.ll_parent);
        Util.STATUS_BAR_HEIGHT = getStatusBarHeight();
        Util.SetLog("" + Util.STATUS_BAR_HEIGHT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        progressBar = findViewById(R.id.progressBar);
        register = findViewById(R.id.bt_register_login_page);
        et_id_login = findViewById(R.id.et_id_login);
        et_pw_login = findViewById(R.id.et_pw_login);
        tv_err_login_id = findViewById(R.id.tv_err_login_id);
        tv_err_login_pw = findViewById(R.id.tv_err_login_pw);
        bt_login = findViewById(R.id.bt_login);
        login_auto = findViewById(R.id.cb_login_keep);
        login_auto.setChecked(true);
        btn_find_id_pw = findViewById(R.id.btn_find_id_pw);
    }

    private void setupListener() {

        Log.e(TAG, "setupListener: 토큰");
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(LoginPage.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                Log.e(TAG, "onSuccess: 토큰");
                String newToken = instanceIdResult.getToken();
                Util.TOKEN = newToken;
                Log.e("newToken", newToken);
                Log.e("Util.Token", Util.TOKEN);
            }
        });

        register.setOnClickListener(v -> {
            Intent intent = new Intent(LoginPage.this, RegisterPage_1.class);
            startActivity(intent);
        });


        btn_find_id_pw.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(LoginPage.this, FindIDAndPassword.class);
                startActivity(intent);
            }
        });

        bt_login.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                login();
            }
        });

        login_auto.setOnClickListener(v -> {
            if (login_auto.isChecked()) {
                Util.setSnackbar(ll_parent, "자동로그인 설정");
                autoLogin = "y";
            } else {
                Util.setSnackbar(ll_parent, "자동로그인 해제");
                autoLogin = "n";
            }
        });

    }


    private void login() {
        bt_login.setEnabled(true);
        tv_err_login_id.setVisibility(View.GONE);
        tv_err_login_pw.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        bt_login.setVisibility(View.GONE);
        errNum = 0;
        if (et_id_login.length() == 0) {
            progressBar.setVisibility(View.GONE);
            bt_login.setVisibility(View.VISIBLE);
            errNum++;
            tv_err_login_id.setVisibility(View.VISIBLE);
            tv_err_login_id.setText("아이디를 입력해주세요");
            et_id_login.requestFocus();
            Util.SetLog("1");
        } else if (!Util.isValidEmail(et_id_login.getText().toString().trim())) {
            progressBar.setVisibility(View.GONE);
            bt_login.setVisibility(View.VISIBLE);
            errNum++;
            tv_err_login_id.setText("이메일 형식이 올바르지 않습니다");
            tv_err_login_id.setVisibility(View.VISIBLE);
            et_pw_login.requestFocus();
            Util.SetLog("2");
        } else if (et_pw_login.length() < 4) {
            progressBar.setVisibility(View.GONE);
            bt_login.setVisibility(View.VISIBLE);
            errNum++;
            tv_err_login_pw.setVisibility(View.VISIBLE);
            tv_err_login_pw.setText("비밀번호를 4자리 이상입력해주세요");
            et_pw_login.requestFocus();
            Util.SetLog("3");
        } else {
            dialog = new ProgressDialog(this);
            dialog.setMessage("로그인중입니다.\n잠시만 기다려주세요.");
            dialog.show();

            progressBar.setVisibility(View.VISIBLE);
            bt_login.setVisibility(View.GONE);
            bt_login.setEnabled(false);
            strUrl = Util.thepion_URL + "ajaxcontrol/Login_cert?id=" + et_id_login.getText().toString()
                    + "&pw=" + et_pw_login.getText().toString() + "&autoLogin=" + autoLogin + "&token=" + Util.TOKEN;
            mTask = (DownloadWebpageTask) new DownloadWebpageTask().execute(strUrl);
            Util.SetLog(strUrl);
            timer = new Timer(true);
            final android.os.Handler handler = new android.os.Handler();
            Util.hideKeyboard(LoginPage.this);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(() -> {
                        wepPageDownloadTaskKill();
                        bt_login.setEnabled(true);
                        Toast.makeText(LoginPage.this, "인터넷 연결상태가 좋지 않습니다.\n다시 시도해주세요", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        bt_login.setVisibility(View.VISIBLE);
                    });
                }
            }, 8000); // 시작 기동시간, 간격 (ms)
        }
    }


    @Override
    public void onBackPressed() {
        long curTime = System.currentTimeMillis();
        long gapTime = curTime - backBtnTime;
        if (0 <= gapTime && 2000 >= gapTime) {
            super.onBackPressed();
        } else {
            backBtnTime = curTime;
            Toast.makeText(this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }


    private void licenseApplyLogin() {
        if (completeLicenseApply) {
            Intent intent = getIntent();
            et_id_login.setText(intent.getExtras().getString("id"));
            et_pw_login.setText(intent.getExtras().getString("pw"));
            login();
            completeLicenseApply = false;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        match_progress.setVisibility(View.GONE);
        ll_parent.setVisibility(View.VISIBLE);
    }


    @SuppressLint("StaticFieldLeak")
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

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
            getCertStatus(result);
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


        private void getCertStatus(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                int status = jsonObject.getInt("status");
                String userName = jsonObject.getString("userName");
                String userID = jsonObject.getString("userID");
                String userPassword = jsonObject.getString("userPassword");
                String userPhone = jsonObject.getString("userPhone");
                int userGrade = jsonObject.getInt("userGrade");  //타입과, name의 " " 값이 서버에서 보내오는 키값과 같아야 한다. 이 값으로 정보를 받아올수 있다.
                int userNum = jsonObject.getInt("userNum");
                String userLicenseNum = jsonObject.getString("userLicenseNum");
                String hasCardImage = jsonObject.getString("hasCardImage");
                String userBankNum = jsonObject.getString("userBankNum");
                int refMemberNum = jsonObject.getInt("refM");
                String IsLeave = jsonObject.getString("IsLeave");
                String refManagerGrade = jsonObject.getString("refManagerGrade");
                int refManagerNum = jsonObject.getInt("refManagerNum");

                certControl(status, userName, userID, userPassword, userPhone, userNum, userGrade, userLicenseNum, hasCardImage, userBankNum
                        , refMemberNum, IsLeave, refManagerGrade, refManagerNum);

            } catch (JSONException e) {
                Log.e("LoginActivity", "Could not parse JSON. Error: " + e.getMessage());
            }
        }
    }


    private void loginFail(String str) {
        bt_login.setText("로그인");
        bt_login.setEnabled(true);
        progressBar.setVisibility(View.GONE);
        bt_login.setVisibility(View.VISIBLE);
        Util.showToast(this, str);
        if (timer != null) {
            timer.cancel();
        }
    }

    private void certControl(int status, String userName, String userID, String userPassword, String userPhone, int userNum, int userGrade,
                             String userLicenseNum, String hasCardImage, String userBankNum, int refMemberNum, String IsLeave, String refManagerGrade, int refManagerNum) {
        if (status == 0) loginFail("아이디 또는 비밀번호가 올바르지 않습니다");

        else if (IsLeave.equals("y")) loginFail("회원탈퇴 처리중이므로 로그인이 불가능합니다");

        else {
            UserInfo.setIsLogin(true);
            UserInfo.setUserID(userID);
            UserInfo.setUserPassword(userPassword);
            UserInfo.setUserName(userName);
            UserInfo.setUserPhone(userPhone);
            UserInfo.setUserNum(userNum);
            UserInfo.setUserGrade(userGrade);
            UserInfo.setUserLicenseNum(userLicenseNum);
            UserInfo.setHasCardImage(hasCardImage);
            UserInfo.setUserBankNum(userBankNum);
            UserInfo.setAutoLogin(autoLogin);
            UserInfo.setRefManagerGrade(refManagerGrade);
            Log.e(TAG, "certControl: 유저아이디 : " + userID);
            Log.e(TAG, "certControl: refManagerGrade 추천인 등급 확인 : " + refManagerGrade);
            UserInfo.setPwNotSecret(et_pw_login.getText().toString());
            UserInfo.setRefManagerNum(refManagerNum);

            if (timer != null) {
                timer.cancel();
            }
            if (login_auto.isChecked()) {
                SharedPreference.getSharedPreferences(this);
                SharedPreference.setUserNum(this, userNum);
                SharedPreference.setUserID(this, userID);
                SharedPreference.setUserName(this, userName);
                SharedPreference.setUserPassword(this, userPassword);
                SharedPreference.setUserGrade(this, userGrade);
                SharedPreference.setChecked(this, true);
                SharedPreference.setUserLicenseNum(this, userLicenseNum);
                SharedPreference.setHasCardImage(this, hasCardImage);
                SharedPreference.setUserBankNum(this, userBankNum);
                SharedPreference.setRefMemberNum(this, refMemberNum);
                SharedPreference.setAutoLogin(this, autoLogin);
                SharedPreference.setRefManagerGrade(this, refManagerGrade);
                SharedPreference.setRefManagerNum(this, refManagerNum);

            } else {
                if (SharedPreference.getCloseChecked(LoginPage.this)) {
                    SharedPreference.clearAll(this);

                    SharedPreference.getSharedPreferences(LoginPage.this);
                    SharedPreference.setCloseChecked(LoginPage.this, true);
                }
            }
            Log.e("LoginActivity", "아이디 : " + userID + "비밀번호 : " + userPassword + ", 이름 : " + userName + ", 전번 : " + userPhone + ", 유저번호 : " + userNum + ", 등급 : " + userGrade
                    + ", 라이선스 : " + userLicenseNum + ", 신분증 : " + hasCardImage + ", 계좌번호 : " + userBankNum + ", 추천인 번호 : " + refMemberNum + ", 추천인 등급 : " + refManagerGrade);
            Intent intent = new Intent(LoginPage.this, BottomNaviWrap.class);
            startActivity(intent);
            finish();
            progressBar.setVisibility(View.GONE);
            bt_login.setVisibility(View.VISIBLE);
            dialog.dismiss();
        }
    }

    private void wepPageDownloadTaskKill() {
        if (mTask != null && mTask.getStatus() != DownloadWebpageTask.Status.FINISHED) {
            mTask.cancel(true);
            mTask = null;
            Log.e("task kill", "");
        }
    }

    public int getStatusBarHeight() {

        int statusHeight = 0;
        int screenSizeType = (getApplicationContext().getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK);
        if (screenSizeType != Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            int resourceId = getApplicationContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                statusHeight = getApplicationContext().getResources().getDimensionPixelSize(resourceId);
            }
        }
        return statusHeight;
    }

    @Override
    protected void onDestroy() {
        networkCheck.unregister();
        super.onDestroy();
    }
}
