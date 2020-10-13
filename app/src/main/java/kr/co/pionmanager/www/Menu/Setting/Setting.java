package kr.co.pionmanager.www.Menu.Setting;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;

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
import kr.co.pionmanager.www.OnSingleClickListener;
import kr.co.pionmanager.www.R;
import kr.co.pionmanager.www.SharedPreference;
import kr.co.pionmanager.www.UserInfo;
import kr.co.pionmanager.www.Util;

public class Setting extends AppCompatActivity {

    private CustomDialog customDialog;
    private Button service_rule, privacy_rule, thirdPeople_rule, marketing_rule;
    public static int SERVICE_RULE = 0;
    public static int PRIVACY_RULE = 1;
    public static int THIRDPEOPLE_RULE = 2;
    public static int MARKETING_RULE = 3;
    private LinearLayout ll_home_page;
    private ImageButton ibtn_back;
    private ImageButton ibton_call_icon;
    private SendPushSetting sendPushSetting;
    private String strUrl;
    private Switch toggle_push_all;
    private Switch toggle_push_auction;
    private Switch toggle_push_notice;
    private Switch toggle_push_marketing;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Util.SaveCurrentContext(this);
        Util.SetTagName("Setting");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initView();
        setUpListener();


        goRule(service_rule);
        goRule(privacy_rule);
        goRule(thirdPeople_rule);
        goRule(marketing_rule);


    }


    private void setUpListener() {

        ibtn_back.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                onBackPressed();
            }
        });

        ll_home_page.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                ShowDialog();
            }
        });

        ibton_call_icon.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                String company_tel = "tel:024230825";
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(company_tel));
                startActivity(intent);
            }
        });

        putSettingPush(toggle_push_all);
        putSettingPush(toggle_push_auction);
        putSettingPush(toggle_push_notice);
        putSettingPush(toggle_push_marketing);

    }

    private void initView() {
        service_rule = findViewById(R.id.service_rule);
        privacy_rule = findViewById(R.id.privacy_rule);
        thirdPeople_rule = findViewById(R.id.thirdPeople_rule);
        marketing_rule = findViewById(R.id.marketing_rule);
        ll_home_page = findViewById(R.id.ll_home_page);
        ibtn_back = findViewById(R.id.ibtn_back);
        ibton_call_icon = findViewById(R.id.ibton_call_icon);
        toggle_push_all = findViewById(R.id.toggle_push_all);
        toggle_push_auction = findViewById(R.id.toggle_push_auction);
        toggle_push_notice = findViewById(R.id.toggle_push_notice);
        toggle_push_marketing = findViewById(R.id.toggle_push_marketing);

        if (UserInfo.getPushAll().equals("y")) toggle_push_all.setChecked(true);
        else  toggle_push_all.setChecked(false);
        Log.e("UserInfo.getPushAll : ", UserInfo.getPushAll());

        if (UserInfo.getPushAuction().equals("y")) toggle_push_auction.setChecked(true);
        else  toggle_push_auction.setChecked(false);

        if (UserInfo.getPushNotice().equals("y")) toggle_push_notice.setChecked(true);
        else  toggle_push_notice.setChecked(false);

        if (UserInfo.getPushMarketing().equals("y")) toggle_push_marketing.setChecked(true);
        else  toggle_push_marketing.setChecked(false);

    }

    private void settingPush(String type, String value) {
        strUrl = Util.thepion_URL + "Views/Shared/SetPush?userNum=" + UserInfo.getUserNum() + "&userLevel=11" + "&type=" + type + "&value=" + value;
        sendPushSetting = (SendPushSetting) new SendPushSetting().execute(strUrl);
    }


    private void ShowDialog() {
        String str = "PION 홈페이지로 \n이동하시겠습니까?";
        customDialog = new CustomDialog(Setting.this, positiveListener, negativeListener, str);
        Objects.requireNonNull(customDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        customDialog.show();
    }


    private View.OnClickListener positiveListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Util.thepion_URL));
            startActivity(intent);
            CustomDialog.progressBar.setVisibility(View.VISIBLE);
            CustomDialog.ll_customDialog_button.setVisibility(View.GONE);
            customDialog.dismiss();
        }
    };


    private View.OnClickListener negativeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            customDialog.dismiss();
        }
    };


    private void putSettingPush(Switch s) {
        s.setOnCheckedChangeListener((buttonView, isChecked) -> {
            switch (buttonView.getId()) {
                case R.id.toggle_push_all:
                    if (isChecked){
                        settingPush("pushSetAll", "y");
                        SharedPreference.setTogglePushAll(this, "y");
                        UserInfo.setPushAll("y");
                    }
                    else{
                        settingPush("pushSetAll", "n");
                        UserInfo.setPushAll("n");
                        SharedPreference.setTogglePushAll(this, "n");
                    }
                    break;
                case R.id.toggle_push_auction:
                    if (isChecked) {
                        settingPush("pushSetAuction", "y");
                        SharedPreference.setTogglePushAuction(this, "y");
                        UserInfo.setPushAuction("y");
                    }
                    else {
                        settingPush("pushSetAuction", "n");
                        UserInfo.setPushAuction("n");
                        SharedPreference.setTogglePushAuction(this, "n");
                    }
                    break;
                case R.id.toggle_push_notice:
                    if (isChecked){
                        settingPush("pushSetNotice", "y");
                        SharedPreference.setTogglePushNotice(this, "y");
                        UserInfo.setPushNotice("y");
                    }
                    else {
                        settingPush("pushSetNotice", "n");
                        UserInfo.setPushNotice("n");
                        SharedPreference.setTogglePushAuction(this, "n");
                    }
                    break;
                case R.id.toggle_push_marketing:
                    if (isChecked){
                        settingPush("pushSetMarketting", "y");
                        SharedPreference.setTogglePushMarketing(this, "y");
                        UserInfo.setPushMarketing("y");
                    }
                    else {
                        settingPush("pushSetMarketting", "n");
                        SharedPreference.setTogglePushMarketing(this, "n");
                        UserInfo.setPushMarketing("n");
                    }
                    break;
                default:
                    break;
            }
        });
    }


    private void goRule(Button button) {
        button.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                switch (v.getId()) {
                    case R.id.service_rule:
                        Util.putIntentInfo(ServiceRule.class, "result", SERVICE_RULE);
                        break;
                    case R.id.privacy_rule:
                        Util.putIntentInfo(ServiceRule.class, "result", PRIVACY_RULE);
                        break;
                    case R.id.thirdPeople_rule:
                        Util.putIntentInfo(ServiceRule.class, "result", THIRDPEOPLE_RULE);
                        break;
                    case R.id.marketing_rule:
                        Util.putIntentInfo(ServiceRule.class, "result", MARKETING_RULE);
                        break;
                }
            }
        });
    }


    @SuppressLint("StaticFieldLeak")
    private class SendPushSetting extends AsyncTask<String, Void, String> {

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
            getResult(result);
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

        private void getResult(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                String rValue = jsonObject.getString("result");
                Log.e("result(y는 저장완료, n은 저장 실패)", rValue);

            } catch (JSONException e) {
                Log.e("TAG", "Could not parse JSON. Error: " + e.getMessage());
            }
        }

    }

}