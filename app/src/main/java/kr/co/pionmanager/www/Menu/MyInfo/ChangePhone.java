package kr.co.pionmanager.www.Menu.MyInfo;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import java.util.Objects;
import java.util.Timer;

import kr.co.pionmanager.www.CustomDialog;
import kr.co.pionmanager.www.OnSingleClickListener;
import kr.co.pionmanager.www.R;
import kr.co.pionmanager.www.UserInfo;
import kr.co.pionmanager.www.Util;


public class ChangePhone extends AppCompatActivity {


    private ImageButton ibtn_back;
    private EditText et_change_phone;
    private Button btn_change_phone;
    private CustomDialog customDialog;
    private TextView tv_phone_error;

    private PhoneChangeTask phoneChangeTask;
    private String strUrl;

    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_phone);
        Util.SaveCurrentContext(this);
        Util.SetTagName("ChangePhone");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        ibtn_back = findViewById(R.id.ibtn_back);
        et_change_phone = findViewById(R.id.et_change_phone);
        btn_change_phone = findViewById(R.id.btn_change_phone);
        tv_phone_error = findViewById(R.id.tv_phone_error);
        tv_phone_error.setVisibility(View.GONE);
        btn_change_phone.setBackgroundDrawable(Util.CURRENT_CONTEXT.getResources().getDrawable(R.drawable.back_gray_round));
        btn_change_phone.setEnabled(false);
        Util.buttonBackgroundChange(et_change_phone, btn_change_phone);

        ibtn_back.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                onBackPressed();
            }
        });

        btn_change_phone.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (et_change_phone.getText().toString().trim().length() != 11) {
                    tv_phone_error.setVisibility(View.VISIBLE);
                } else if (!Util.isValidPhone(et_change_phone.getText().toString().trim())){
                    tv_phone_error.setVisibility(View.VISIBLE);
                }
                else {
                    ShowDialog();
                    tv_phone_error.setVisibility(View.GONE);
                }
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        taskKill();
    }

    private void taskKill() {
        if (phoneChangeTask != null && phoneChangeTask.getStatus() != PhoneChangeTask.Status.FINISHED) {
            phoneChangeTask.cancel(true);
            phoneChangeTask = null;
            Util.showToast("인터넷 연결상태가 좋지 않습니다.\n다시 시도해주세요");
        }
    }

    private void ShowDialog() {
        String str = "연락처를 변경하시겠습니까?";
        customDialog = new CustomDialog(Util.CURRENT_CONTEXT, positiveListener, negativeListener, str);
        Objects.requireNonNull(customDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        customDialog.show();
    }

    private View.OnClickListener positiveListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CustomDialog.progressBar.setVisibility(View.VISIBLE);
            CustomDialog.ll_customDialog_button.setVisibility(View.GONE);
            strUrl =
                    Util.thepion_URL + "AjaxControl/Privacy_Info_Change?phone=" + et_change_phone.getText().toString().trim() + "&userNum=" + UserInfo.getUserNum() + "&changeNum" +
                            "=" + MyInfo.PHONE_CHANGE;
            phoneChangeTask = (PhoneChangeTask) new PhoneChangeTask().execute(strUrl);
        }
    };

    private View.OnClickListener negativeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            customDialog.dismiss();
            tv_phone_error.setVisibility(View.GONE);
        }
    };


    @SuppressLint("StaticFieldLeak")
    private class PhoneChangeTask extends AsyncTask<String, Void, String> {
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
            //JsonParsing
            try {
                // create a new instance from a string
                // name이라는 키 값으로 서버에서 값을 가져와서 각 타입의 변수들에 저장, 그리고 registerControl에서 받아온 값들을 control한다
                JSONObject jsonObject = new JSONObject(result);
                String rValue = jsonObject.getString("rValue");

                if (rValue.equals("y")) {
                    UserInfo.setUserPhone(et_change_phone.getText().toString().trim());
                    CustomDialog.progressBar.setVisibility(View.GONE);
                    CustomDialog.ll_customDialog_button.setVisibility(View.VISIBLE);
                    customDialog.dismiss();
//                    Util.intentNext(MyInfo.class);
                    Util.showToast("연락처 변경 완료");
                    onBackPressed();
                    finish();
                } else if (rValue.equals("n")) {
                    CustomDialog.progressBar.setVisibility(View.GONE);
                    CustomDialog.ll_customDialog_button.setVisibility(View.VISIBLE);
                    customDialog.dismiss();
                    Util.showToast("현재 연락처와 동일합니다.");
                }
            } catch (JSONException e) {
                Log.e(Util.CURRENT_TAG, "Could not parse JSON. Error: " + e.getMessage());
            }
        }
    }

    private void ChangePhoneTaskKill() {
        if (phoneChangeTask != null && phoneChangeTask.getStatus() != PhoneChangeTask.Status.FINISHED) {
            phoneChangeTask.cancel(true);
            phoneChangeTask = null;
            Util.showToast("인터넷 연결상태가 좋지 않습니다.\n다시 시도해주세요");

        }
    }
}
