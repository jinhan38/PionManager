package kr.co.pionmanager.www.Menu.Setting;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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

import kr.co.pionmanager.www.OnSingleClickListener;
import kr.co.pionmanager.www.R;
import kr.co.pionmanager.www.Util;

public class ServiceRule extends AppCompatActivity {

    private String TAG = "ServiceRule";
    private TextView tv_show_rule_contents;
    private TextView tv_top;
    private TextView tv_show_rule_contents_2;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_rule);
        tv_show_rule_contents = findViewById(R.id.tv_show_rule_contents);
        tv_show_rule_contents_2 = findViewById(R.id.tv_show_rule_contents_2);
        tv_top = findViewById(R.id.tv_top);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        ImageButton ibtn_back = findViewById(R.id.ibtn_back);
        ibtn_back.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                onBackPressed();
                finish();
            }
        });



        String str = Util.thepion_URL + "Views/Shared/IsExistManagerIDService?refMId=";
        DownloadRuleTask downloadRuleTask = (DownloadRuleTask) new DownloadRuleTask().execute(str);

        Intent intent = getIntent();
        int data = intent.getExtras().getInt("result");

        getRuleNumber(data);
    }

    public void getRuleNumber(int data) {
        switch (data) {
            case 0:
//                tv_show_rule_contents.setText(getString(R.string.service_rule));
                tv_top.setText("서비스 이용약관");
                break;
            case 1:
                tv_show_rule_contents.setText(getString(R.string.privacy_rule));
                tv_top.setText("개인정보 수집 및 이용안내");
                tv_show_rule_contents_2.setVisibility(View.GONE);
                break;
            case 2:
                tv_show_rule_contents.setText(getString(R.string.thirdPeople_rule));
                tv_top.setText("개인정보의 제3자 제공");
                tv_show_rule_contents_2.setVisibility(View.GONE);
                break;
            case 3:
                tv_show_rule_contents.setText(getString(R.string.marketing_rule));
                tv_top.setText("광고성 정보수신 동의 안내");
                tv_show_rule_contents_2.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class DownloadRuleTask extends AsyncTask<String, Void, String> {
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


            } catch (JSONException e) {
                Log.e(TAG, "Could not parse JSON. Error: " + e.getMessage());
            }
        }
    }
}
