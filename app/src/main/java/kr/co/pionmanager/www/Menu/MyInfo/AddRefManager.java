package kr.co.pionmanager.www.Menu.MyInfo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
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

import kr.co.pionmanager.www.CustomDialog;
import kr.co.pionmanager.www.ExplainDialog.ExplainRefManagerRegisteration;
import kr.co.pionmanager.www.LoginPage;
import kr.co.pionmanager.www.OnSingleClickListener;
import kr.co.pionmanager.www.R;
import kr.co.pionmanager.www.UserInfo;
import kr.co.pionmanager.www.Util;

public class AddRefManager extends AppCompatActivity {

    private static final String TAG = "AddRefManager";
    private ImageButton ibtn_back;
    private EditText et_add_ref_manager;
    private TextView tv_add_ref_manager_error;
    private Button btn_add_ref_manager;
    private String strUrl;
    private AddRefManagerLookUpTask addRefManagerLookUpTask;
    private CustomDialog customDialog;
    private ExplainRefManagerRegisteration explainRefManagerRegisteration;
    private boolean isOpenPopup = false;
    private AddRefManager context;
    private String confirmPopup = "n";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ref_manager);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        context = this;
        Util.SaveCurrentContext(this);
        Util.SetTagName("AddRefManager");
        Util.SetLog("추천인 등록 페이지");

        ibtn_back = findViewById(R.id.ibtn_back);
        et_add_ref_manager = findViewById(R.id.et_add_ref_manager);
        btn_add_ref_manager = findViewById(R.id.btn_add_ref_manager);
        tv_add_ref_manager_error = findViewById(R.id.tv_add_ref_manager_error);
        tv_add_ref_manager_error.setVisibility(View.INVISIBLE);

        btn_add_ref_manager.setBackgroundDrawable(Util.CURRENT_CONTEXT.getResources().getDrawable(R.drawable.back_gray_round));
        btn_add_ref_manager.setEnabled(false);
        Util.buttonBackgroundChange(et_add_ref_manager, btn_add_ref_manager);

        ibtn_back.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                onBackPressed();
            }
        });

        btn_add_ref_manager.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                ShowDialog();
            }
        });
    }

    private void ShowDialog() {
        String str = "추천인을 등록하시겠습니까?";
        customDialog = new CustomDialog(AddRefManager.this, positiveListener, negativeListener, str);
        Objects.requireNonNull(customDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        customDialog.show();
    }

    private OnSingleClickListener positiveListener = new OnSingleClickListener() {
        @Override
        public void onSingleClick(View v) {
            CustomDialog.progressBar.setVisibility(View.VISIBLE);
            CustomDialog.ll_customDialog_button.setVisibility(View.GONE);
            customDialog.dismiss();
            strUrl = Util.thepion_URL + "AjaxControl/AddRefManager?ref_manager=" + et_add_ref_manager.getText().toString().trim()
                    + "&userNum=" + UserInfo.getUserNum() + "&confirmPopup=" + confirmPopup;
            addRefManagerLookUpTask = (AddRefManagerLookUpTask) new AddRefManagerLookUpTask().execute(strUrl);
        }
    };

    private OnSingleClickListener negativeListener = new OnSingleClickListener() {
        @Override
        public void onSingleClick(View v) {
            customDialog.dismiss();
        }
    };

    private void taskKill() {
        if (addRefManagerLookUpTask != null && addRefManagerLookUpTask.getStatus() != AddRefManagerLookUpTask.Status.FINISHED) {
            addRefManagerLookUpTask.cancel(true);
            addRefManagerLookUpTask = null;
            Util.showToast("인터넷 연결상태가 좋지 않습니다.\n다시 시도해주세요");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        taskKill();
    }

    @SuppressLint("StaticFieldLeak")
    private class AddRefManagerLookUpTask extends AsyncTask<String, Void, String> {
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

        @SuppressLint("SetTextI18n")
        private void getRegistStatus(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                String rValue = jsonObject.getString("rValue");
                String existRefManager = jsonObject.getString("existRefManager");
                String text = jsonObject.getString("text");
                int refMGrade = jsonObject.getInt("refMGrade");

                if (refMGrade == 3 && confirmPopup.equals("n")) {
                    Log.e(TAG, "getRegistStatus: refMGrade : " + refMGrade);
                    setRefmanagerPopup();
                } else if (rValue.equals("y")) {
                    Util.showToast("추천인 등록 완료");
                    customDialog.dismiss();
                    LoginPage.completeLicenseApply = true;
                    Intent intent = new Intent(AddRefManager.this, LoginPage.class);
                    intent.putExtra("id", UserInfo.getUserID());
                    intent.putExtra("pw", UserInfo.getPwNotSecret());
                    startActivity(intent);
                    finish();
                } else {

                    customDialog.dismiss();
                    CustomDialog.progressBar.setVisibility(View.GONE);
                    CustomDialog.ll_customDialog_button.setVisibility(View.VISIBLE);

                    if (existRefManager.equals("n")) {
                        tv_add_ref_manager_error.setVisibility(View.VISIBLE);
                        tv_add_ref_manager_error.setText(text);
                    }

                }

            } catch (JSONException e) {
                Log.e(Util.CURRENT_TAG, "Could not parse JSON. Error: " + e.getMessage());
            }
        }
    }

    private void setRefmanagerPopup() {

        explainRefManagerRegisteration = new ExplainRefManagerRegisteration(context, positiveListenerGrade3, negativeListenerGrade3);
        Objects.requireNonNull(explainRefManagerRegisteration.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        explainRefManagerRegisteration.show();
        isOpenPopup = true;
        Window window = explainRefManagerRegisteration.getWindow();

        //디스플레이 해상도 가져오기
        Display display = context.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int x = (int) (size.x * 0.95f);
        int y = (int) (size.y * 0.8f);
        window.setLayout(x, y);
    }

    private OnSingleClickListener positiveListenerGrade3 = new OnSingleClickListener() {
        @Override
        public void onSingleClick(View v) {

            if (isOpenPopup) {
                explainRefManagerRegisteration.dismiss();
                isOpenPopup = false;
                Log.e(TAG, "onSingleClick: posi et_recommendation" + et_add_ref_manager.getText().toString());
                confirmPopup = "y";
                strUrl = Util.thepion_URL + "AjaxControl/AddRefManager?ref_manager=" + et_add_ref_manager.getText().toString().trim()
                        + "&userNum=" + UserInfo.getUserNum() + "&confirmPopup=" + confirmPopup;
                addRefManagerLookUpTask = (AddRefManagerLookUpTask) new AddRefManagerLookUpTask().execute(strUrl);

            }
        }
    };

    private OnSingleClickListener negativeListenerGrade3 = new OnSingleClickListener() {
        @Override
        public void onSingleClick(View v) {
            if (isOpenPopup) {
                explainRefManagerRegisteration.dismiss();
                isOpenPopup = false;
                confirmPopup = "n";
                et_add_ref_manager.setText(null);
                Log.e(TAG, "onSingleClick: nega et_recommendation" + et_add_ref_manager.getText().toString());
            }
        }
    };

}
