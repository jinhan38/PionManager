package kr.co.pionmanager.www.Registration;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import kr.co.pionmanager.www.ExplainDialog.ExplainRefManagerRegisteration;
import kr.co.pionmanager.www.LoginPage;
import kr.co.pionmanager.www.OnSingleClickListener;
import kr.co.pionmanager.www.R;
import kr.co.pionmanager.www.Util;

public class RegisterPage_2 extends AppCompatActivity {

    public static final String apiKey = "AAAApiCXOUU:APA91bGpNyNl6LQRMS6B8aWS3aGJAqjOPqQMcp2nWZQeihwpJtBu1T2ZqSrY59qEu1PAEJ4DIB8mwK1D_PPRLetxGtznXFdSGzOLacrYm6zS0MLMIhwJFGTYMUwO57_PJnOTp4WZsh_a";
    public static final String senderId = "713511352645";

    private ExplainRefManagerRegisteration explainRefManagerRegisteration;
    private boolean isOpenPopup = false;
    private ScrollView scrollView_top_parent;

    private EditText et_id;
    private EditText et_pw;
    private EditText et_recommendation;

    private Button bt_confirm;
    private Button bt_pw_confirm;
    private Button bt_ref_manager_confirm;
    private TextView tv_err_id;
    private TextView tv_err_pw;
    private TextView tv_err_recommendation;
    private TextView tv_err_recommendation_noID;
    private boolean isConfirmPW = false;
    private boolean isPressPWCheck = false;
    String strUrl = "";
    String strUrl_ref_manager = "";
    private String token = "";
    private static final String TAG = "RegisterPage_2";
    private DownloadWebpageTask mTask = null;
    private DownloadRefManagerTask managerTask = null;
    private int mrefMemberNum = 0;
    Timer timer;
    private boolean hasRefManager = true;
    private ProgressBar progressBar;

    private String data = "n";
    private ProgressBar confirm_progressBar;

    public static RegisterPage_2 registerPage_2;
    public boolean et_refManagerChange = false;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page_2);
        Util.SaveCurrentContext(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        registerPage_2 = this;
        progressBar = findViewById(R.id.progressBar);
        confirm_progressBar = findViewById(R.id.confirm_progressBar);
        scrollView_top_parent = findViewById(R.id.scrollView_top_parent);

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {  // 토큰 받아오기
            if (!task.isSuccessful()) {
                Log.e("FCM LOG", "getInstatnceId failed", task.getException());
                return;
            }
            // Get new Instance ID token
            token = Objects.requireNonNull(task.getResult()).getToken();
            Log.e("FCM Log", "FCM 토큰 : " + token);
        });

        et_id = findViewById(R.id.et_id_register);
        et_pw = findViewById(R.id.et_pw_register);
        et_recommendation = findViewById(R.id.et_recommendation);

        tv_err_id = findViewById(R.id.tv_err_id);
        tv_err_pw = findViewById(R.id.tv_err_pw);
        tv_err_recommendation = findViewById(R.id.tv_err_recommendation);
        tv_err_recommendation_noID = findViewById(R.id.tv_err_recommendation_noID);
        bt_pw_confirm = findViewById(R.id.bt_pw_confirm_register);
        bt_confirm = findViewById(R.id.bt_confirm_register);
        bt_ref_manager_confirm = findViewById(R.id.bt_ref_manager_confirm);

        progressBar.setVisibility(View.GONE);
        bt_confirm.setVisibility(View.VISIBLE);
        confirm_progressBar.setVisibility(View.GONE);
        bt_ref_manager_confirm.setVisibility(View.VISIBLE);

        bt_pw_confirm.setOnClickListener(v -> {
            tv_err_pw.setVisibility(View.GONE);
            if (!isPressPWCheck) {
                if (et_pw.length() < 4) {
                    String errText = "비밀번호는 4자리 이상이어야 합니다.";
                    tv_err_pw.setVisibility(View.VISIBLE);
                    et_pw.requestFocus();
                    Toast toast = Toast.makeText(getBaseContext(), errText, Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    et_pw.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    tv_err_pw.setVisibility(View.GONE);
                    isPressPWCheck = true;
                    isConfirmPW = true;
                    bt_pw_confirm.setText("숨김");
                }
            } else {
                et_pw.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                tv_err_pw.setVisibility(View.GONE);
                isPressPWCheck = false;
                isConfirmPW = true;
                bt_pw_confirm.setText("보기");
            }
        });

        bt_confirm.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

                tv_err_id.setVisibility(View.GONE);
                tv_err_pw.setVisibility(View.GONE);
                tv_err_recommendation.setVisibility(View.GONE);
                if (et_id.length() == 0) {
                    setErrorText(tv_err_id, "아이디를 입력해주세요");
                    Util.scrollToView(et_id, scrollView_top_parent, 0);
                } else if (!Util.isValidEmail(et_id.getText().toString().trim())) {
                    setErrorText(tv_err_id, "이메일 형식이 올바르지 않습니다");
                    Util.scrollToView(et_id, scrollView_top_parent, 0);
                } else if (et_pw.length() == 0) {
                    setErrorText(tv_err_pw, "패스워드를 입력해주세요");
                    Util.scrollToView(et_pw, scrollView_top_parent, 0);
                } else if (et_pw.length() < 4) {
                    setErrorText(tv_err_pw, "비밀번호는 4자리 이상이어야 합니다.");
                    Util.scrollToView(et_pw, scrollView_top_parent, 0);
                } else if (!isConfirmPW) {
                    setErrorText(tv_err_pw, "비밀번호 확인버튼을 눌러주세요");
                    Util.scrollToView(et_pw, scrollView_top_parent, 0);
                } else if (et_recommendation.length() > 0 && !hasRefManager) {
                    setErrorText(tv_err_recommendation_noID, "추천인 아이디가 존재하지 않습니다");
                    Util.scrollToView(et_recommendation, scrollView_top_parent, 0);
                } else if (hasRefManager && et_refManagerChange) { //추천인 아이디 확인 후에 추천인 EditText에 변경 있으면 다시 확인 요청
                    setErrorText(tv_err_recommendation_noID, "추천인 아이디를 다시 확인해주세요");
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    bt_confirm.setVisibility(View.GONE);

                    if (et_recommendation.length() == 0) {
                        mrefMemberNum = 0;
                    }

                    strUrl = Util.thepion_URL + "ajaxcontrol/join_manager?id=" + et_id.getText().toString().trim() + "&pw=" + et_pw.getText().toString() + "&name=" + RegisterPage_1.name +
                            "&phone=" + RegisterPage_1.phone + "&dob=" + RegisterPage_1.dob + "&sex=" + RegisterPage_1.sex + "&refMemberNum=" + mrefMemberNum + "&token=" + token +
                            "&checkBoxMarketing=" + RegisterPage_1.checkResult;
                    mTask = (DownloadWebpageTask) new DownloadWebpageTask().execute(strUrl);
                    bt_confirm.setEnabled(false);
                    timer = new Timer(true);
                    final android.os.Handler handler = new android.os.Handler();
                    Util.hideKeyboard(RegisterPage_2.this);
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            handler.post(() -> {
                                wepPageDownloadTaskKill();
                                bt_confirm.setEnabled(true);
                                Toast.makeText(RegisterPage_2.this, "인터넷 연결상태가 좋지 않습니다.\n다시 시도해주세요", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                bt_confirm.setVisibility(View.VISIBLE);

                            });
                        }
                    }, 8000); // 시작 기동시간, 간격 (ms)
                }
            }
        });


        et_recommendation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                et_refManagerChange = true;
                Log.e(TAG, "onTextChanged: " + et_refManagerChange);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (et_recommendation.length() == 0) {
                    et_refManagerChange = false;
                }
            }
        });


        bt_ref_manager_confirm.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                tv_err_recommendation.setVisibility(View.GONE);
                tv_err_recommendation_noID.setVisibility(View.GONE);
                if (!Util.isValidEmail(et_recommendation.getText().toString().trim())) {
                    setErrorText(tv_err_recommendation, "이메일 형식의 아이디를 입력해주세요");
                    et_recommendation.requestFocus();
                } else {
                    confirm_progressBar.setVisibility(View.VISIBLE);
                    bt_ref_manager_confirm.setVisibility(View.GONE);
                    strUrl_ref_manager = Util.thepion_URL + "Views/Shared/IsExistManagerIDService?refMId=" + et_recommendation.getText().toString();
                    managerTask = (DownloadRefManagerTask) new DownloadRefManagerTask().execute(strUrl_ref_manager);
                    Log.e("url 태그 ", strUrl_ref_manager);
                    timer = new Timer(true);
                    final android.os.Handler handler = new android.os.Handler();
                    Util.hideKeyboard(RegisterPage_2.this);
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            handler.post(RegisterPage_2.this::refManagerDownloadTaskKill);
                        }
                    }, 8000); // 시작 기동시간, 간격 (ms)
                }
            }
        });
    }

    private void setErrorText(TextView textview, String string) {
        textview.setVisibility(View.VISIBLE);
        textview.setText(string);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (timer != null) {
            timer.cancel();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadRefManagerTask extends AsyncTask<String, Void, String> {

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
                String hasRef = jsonObject.getString("hasRef");
                int refMemberNum = jsonObject.getInt("refM");
                int refMGrade = jsonObject.getInt("refMGrade");
                RefManagerControl(hasRef, refMemberNum, refMGrade);
            } catch (JSONException e) {
                Log.w(TAG, "Could not parse JSON. Error: " + e.getMessage());
            }
        }

        public void RefManagerControl(String hasRef, int refMemberNum, int refMGrade) {
            if (hasRef.equals("y")) {
                Util.showToast("추천인이 확인되었습니다.");
                tv_err_recommendation.setVisibility(View.GONE);
                tv_err_recommendation_noID.setVisibility(View.GONE);
                confirm_progressBar.setVisibility(View.GONE);
                bt_ref_manager_confirm.setVisibility(View.VISIBLE);
                hasRefManager = true;
                et_refManagerChange = false;
                mrefMemberNum = refMemberNum;
                if (timer != null) {
                    timer.cancel();
                }
                if (refMGrade == 3) {
                    setRefmanagerPopup();
                }
            } else {
                tv_err_recommendation_noID.setVisibility(View.VISIBLE);
                Util.showToast("추천인 아이디가 존재하지 않습니다");
                et_recommendation.requestFocus();
                hasRefManager = false;
                confirm_progressBar.setVisibility(View.GONE);
                bt_ref_manager_confirm.setVisibility(View.VISIBLE);
            }
        }
    }

    private void refManagerDownloadTaskKill() {
        if (managerTask != null && managerTask.getStatus() != DownloadWebpageTask.Status.FINISHED) {
            managerTask.cancel(true);
            managerTask = null;
            Util.showToast("인터넷 연결상태가 좋지 않습니다.\n다시 시도해주세요");
            confirm_progressBar.setVisibility(View.GONE);
            bt_ref_manager_confirm.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
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
            getRegistStatus(result);
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

        private void getRegistStatus(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                int isRegister = jsonObject.getInt("isRegister");
                int focus = jsonObject.getInt("focus");
                String text = jsonObject.getString("text");
                int userNum = jsonObject.getInt("userNum");
                registerControl(isRegister, focus, text, userNum);
            } catch (JSONException e) {
                Log.w(TAG, "Could not parse JSON. Error: " + e.getMessage());
            }
        }

        private void registerControl(int isRegister, int focus, String text, int userNum) {
            if (isRegister == 0) {
                Toast.makeText(getBaseContext(), text, Toast.LENGTH_LONG).show();
                timer.cancel();
                progressBar.setVisibility(View.GONE);
                bt_confirm.setVisibility(View.VISIBLE);
                bt_confirm.setEnabled(true);
                if (focus == 1) {
                    Util.scrollToView(et_id, scrollView_top_parent, 0);
                }
            } else {
                if (timer != null) {
                    timer.cancel();
                }

                LoginPage.completeLicenseApply = true;
                Intent intent = new Intent(getApplicationContext(), LoginPage.class);
                intent.putExtra("id", et_id.getText().toString().trim());
                intent.putExtra("pw", et_pw.getText().toString().trim());
                startActivity(intent);
                finish();

                Util.showToast("회원가입이 완료되었습니다.");
                bt_confirm.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                bt_confirm.setVisibility(View.VISIBLE);
            }
        }
    }


    private void wepPageDownloadTaskKill() {
        if (mTask != null && mTask.getStatus() != DownloadWebpageTask.Status.FINISHED) {
            mTask.cancel(true);
            mTask = null;
            progressBar.setVisibility(View.GONE);
            bt_confirm.setVisibility(View.VISIBLE);
        }
    }


    private void setRefmanagerPopup() {

        explainRefManagerRegisteration = new ExplainRefManagerRegisteration(registerPage_2, positiveListener, negativeListener);
        Objects.requireNonNull(explainRefManagerRegisteration.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        explainRefManagerRegisteration.show();
        isOpenPopup = true;
        Window window = explainRefManagerRegisteration.getWindow();

        //디스플레이 해상도 가져오기
        Display display = registerPage_2.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int x = (int) (size.x * 0.95f);
        int y = (int) (size.y * 0.8f);
        window.setLayout(x, y);
    }

    private OnSingleClickListener positiveListener = new OnSingleClickListener() {
        @Override
        public void onSingleClick(View v) {

            if (isOpenPopup) {
                explainRefManagerRegisteration.dismiss();
                isOpenPopup = false;
                Log.e(TAG, "onSingleClick: posi et_recommendation" + et_recommendation.getText().toString());
                Log.e(TAG, "onSingleClick: posi mrefMemberNum" + mrefMemberNum);
            }
        }
    };

    private OnSingleClickListener negativeListener = new OnSingleClickListener() {
        @Override
        public void onSingleClick(View v) {
            if (isOpenPopup) {
                explainRefManagerRegisteration.dismiss();
                isOpenPopup = false;
                et_recommendation.setText(null);
                mrefMemberNum = 0;
                Log.e(TAG, "onSingleClick: nega et_recommendation" + et_recommendation.getText().toString());
                Log.e(TAG, "onSingleClick: nega mrefMemberNum" + mrefMemberNum);
            }
        }
    };
}
