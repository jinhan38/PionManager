package kr.co.pionmanager.www.Menu.MyInfo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import java.util.Timer;
import java.util.TimerTask;

import kr.co.pionmanager.www.FindIDAndPassword;
import kr.co.pionmanager.www.OnSingleClickListener;
import kr.co.pionmanager.www.R;
import kr.co.pionmanager.www.UserInfo;
import kr.co.pionmanager.www.Util;


public class PasswordConfirm extends AppCompatActivity {

    private ImageButton ibtn_back;
    private EditText et_password_confirm;
    private Button btn_password_confirm;
    private Button btn_find_id_pw;
    private TextView tv_not_same;

    private PasswordConfirmTask passwordConfirmTask;
    private String strUrl;

    private int data;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_confirm);
        Util.SaveCurrentContext(this);
        Util.SetTagName("PasswordConfirm");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        ibtn_back = findViewById(R.id.ibtn_back);
        ibtn_back.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                onBackPressed();
            }
        });


        btn_find_id_pw = findViewById(R.id.btn_find_id_pw);
        et_password_confirm = findViewById(R.id.et_password_confirm);
        btn_password_confirm = findViewById(R.id.btn_password_confirm);
        tv_not_same = findViewById(R.id.tv_not_same);
        tv_not_same.setVisibility(View.GONE);

        btn_password_confirm.setBackgroundDrawable(Util.CURRENT_CONTEXT.getResources().getDrawable(R.drawable.back_gray_round));
        btn_password_confirm.setEnabled(false);

        Util.buttonBackgroundChange(et_password_confirm, btn_password_confirm);

        Intent intent = getIntent();
        data = intent.getExtras().getInt("result");
        Util.SetLog(String.valueOf(data));

        btn_password_confirm.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (et_password_confirm.length() > 0) {
                    strUrl =
                            Util.thepion_URL + "AjaxControl/Privacy_Info_Change?pw=" + et_password_confirm.getText().toString().trim() + "&userNum=" + UserInfo.getUserNum() + "&id" +
                                    "=" + UserInfo.getUserID();
                    passwordConfirmTask = (PasswordConfirmTask) new PasswordConfirmTask().execute(strUrl);

                    Util.hideKeyboard(PasswordConfirm.this);
                    timer = new Timer(true);
                    final android.os.Handler handler = new android.os.Handler();

                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            handler.post(() -> PasswordConfirmTaskKill());
                        }
                    }, 8000);
                }
            }
        });

        btn_find_id_pw.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Util.intentNext(FindIDAndPassword.class);
            }
        });

    }

    private void goNext(int n) {
        switch (n) {
            case MyInfo.PHONE_CHANGE:
                Util.intentNext(ChangePhone.class);
                finish();
                break;
            case MyInfo.BANK_CHANGE:
                Util.intentNext(ChangeBankInfo.class);
                finish();
                break;
            case MyInfo.HAVE_NOT_LICENSE:
                Util.intentNext(AddRefManager.class);
                finish();
                break;
            case MyInfo.PW_CHANGE:
                Util.intentNext(ChangePW.class);
                finish();
                break;
            default:
                break;
        }
    }


    private class PasswordConfirmTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... urls) {
            try {
                return (String) downloadUrl((String) urls[0]);
            } catch (IOException e) {
                return "다운로드 실패" + e.toString();
            } finally {
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
                String line = null;
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
                String rValue = jsonObject.getString("rValue");

                if (rValue.equals("y")) {
                    goNext(data);
                    finish();
                    tv_not_same.setVisibility(View.GONE);
                } else {
                    tv_not_same.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                Log.e(Util.CURRENT_TAG, "Could not parse JSON. Error: " + e.getMessage());
            }
        }
    }

    private void PasswordConfirmTaskKill() {
        if (passwordConfirmTask != null && passwordConfirmTask.getStatus() != PasswordConfirmTask.Status.FINISHED) {
            passwordConfirmTask.cancel(true);
            passwordConfirmTask = null;
            Util.showToast("인터넷 연결상태가 좋지 않습니다.\n다시 시도해주세요");
        }
    }

}
