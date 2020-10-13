package kr.co.pionmanager.www.Menu.MyInfo;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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


public class ChangePW extends AppCompatActivity {

    private static final String TAG = "ChangePW";

    private ImageButton ibtn_back;
    private EditText et_change_pw;
    private Button bt_pw_confirm_register;
    private Button btn_change_pw;
    private CustomDialog customDialog;
    private TextView tv_phone_error;

    private PWChangeTask pwChangeTask;
    private String strUrl;

    private boolean isPressPWCheck = false;
    private boolean isConfirmPW = true;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pw);
        Util.SaveCurrentContext(this);
        Util.SetTagName("ChangePhone");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        ibtn_back = findViewById(R.id.ibtn_back);
        et_change_pw = findViewById(R.id.et_change_pw);
        bt_pw_confirm_register = findViewById(R.id.bt_pw_confirm_register);
        btn_change_pw = findViewById(R.id.btn_change_pw);
        tv_phone_error = findViewById(R.id.tv_phone_error);
        tv_phone_error.setVisibility(View.GONE);
        btn_change_pw.setBackgroundDrawable(Util.CURRENT_CONTEXT.getResources().getDrawable(R.drawable.back_gray_round));
        btn_change_pw.setEnabled(false);
        Util.buttonBackgroundChange(et_change_pw, btn_change_pw);

        ibtn_back.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                onBackPressed();
            }
        });

        bt_pw_confirm_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: 버튼 클릭" );
                if (!isPressPWCheck) {
                    if (et_change_pw.length() < 4) {
                        String errText = "비밀번호는 4자리 이상이어야 합니다.";
                        Toast toast = Toast.makeText(getBaseContext(), errText, Toast.LENGTH_LONG);
                        toast.show();
                    } else {
                        et_change_pw.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        isPressPWCheck = true;
                        isConfirmPW = true;
                        bt_pw_confirm_register.setText("숨김");
                    }
                } else {
                    et_change_pw.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    isPressPWCheck = false;
                    isConfirmPW = true;
                    bt_pw_confirm_register.setText("보기");
                }
            }
        });


        btn_change_pw.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (et_change_pw.getText().toString().trim().length() < 4) {
                    tv_phone_error.setVisibility(View.VISIBLE);
                } else if(!isConfirmPW){
                    Toast.makeText(ChangePW.this, "비밀번호 확인 버튼을 눌러주세요", Toast.LENGTH_LONG).show();
                } else {
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
        if (pwChangeTask != null && pwChangeTask.getStatus() != PWChangeTask.Status.FINISHED) {
            pwChangeTask.cancel(true);
            pwChangeTask = null;
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
                    Util.thepion_URL + "AjaxControl/Privacy_Info_Change?changePW=" + et_change_pw.getText().toString().trim() + "&userNum=" + UserInfo.getUserNum() + "&changeNum" +
                            "=" + MyInfo.PW_CHANGE;
            pwChangeTask = (PWChangeTask) new PWChangeTask().execute(strUrl);
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
    private class PWChangeTask extends AsyncTask<String, Void, String> {
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
                    UserInfo.setUserPhone(et_change_pw.getText().toString().trim());
                    CustomDialog.progressBar.setVisibility(View.GONE);
                    CustomDialog.ll_customDialog_button.setVisibility(View.VISIBLE);
                    customDialog.dismiss();
//                    Util.intentNext(MyInfo.class);
                    Util.showToast("비밀번호 변경 완료");
                    onBackPressed();
                    finish();
                } else if (rValue.equals("n")) {
                    CustomDialog.progressBar.setVisibility(View.GONE);
                    CustomDialog.ll_customDialog_button.setVisibility(View.VISIBLE);
                    customDialog.dismiss();
                    Util.showToast("현재 비밀번호와 동일합니다.");
                }
            } catch (JSONException e) {
                Log.e(Util.CURRENT_TAG, "Could not parse JSON. Error: " + e.getMessage());
            }
        }
    }


}
