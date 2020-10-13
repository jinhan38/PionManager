package kr.co.pionmanager.www.Menu.MyInfo;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import kr.co.pionmanager.www.Menu.LeavePion;
import kr.co.pionmanager.www.OnSingleClickListener;
import kr.co.pionmanager.www.R;
import kr.co.pionmanager.www.UserInfo;
import kr.co.pionmanager.www.Util;

public class MyInfo extends AppCompatActivity {

    public final static int PHONE_CHANGE = 2;
    public final static int BANK_CHANGE = 3;
    public final static int HAVE_NOT_LICENSE = 5;
    public final static int PW_CHANGE = 6;

    private String strUrl = "";
    private GetMyInfoData getMyInfoData;

    private TextView myInfo_name, myInfo_dob, myInfo_phone, myInfo_ref_manager_id, myInfo_bank_name, myInfo_bank_num, myInfo_bank_owner;
    private TextView myInfo_ID;
    private TextView tv_license_num, tv_license_day, tv_license_month, tv_license_year;
    private ImageButton ibtn_add_ref_manager;

    private LinearLayout ll_not_ref_manager;
    private LinearLayout ll_myInfo_contents;
    private LinearLayout ll_myInfo_licenseCard;

    private LinearLayout ll_goPhoneChange;
    private LinearLayout ll_goBankChange;
    private LinearLayout ll_leave_pion;
    private LinearLayout ll_goPWChange;

    private TextView tv_myInfo_licenseCard;
    private boolean isOpenLicenseCard = true;
    private ImageView ibtn_licenseCard_expand;
    private ImageView ibtn_licenseCard_collapse;
    private ImageView iv_myinfo_licenseCard;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myinfo_contents);
        Util.SaveCurrentContext(this);
        Util.SetTagName("MyInfo");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initView();
        setUpListener();

        ImageButton ibtn_back = findViewById(R.id.ibtn_back);
        ibtn_back.setOnClickListener(v -> onBackPressed());

    }

    private void initView() {

        progressBar = findViewById(R.id.progressBar);
        ll_myInfo_licenseCard = findViewById(R.id.ll_myInfo_licenseCard);
        tv_myInfo_licenseCard = findViewById(R.id.tv_myInfo_licenseCard);
        ibtn_licenseCard_expand = findViewById(R.id.ibtn_licenseCard_expand);
        ibtn_licenseCard_collapse = findViewById(R.id.ibtn_licenseCard_collapse);
        ll_goPWChange = findViewById(R.id.ll_goPWChange);
        ll_goPhoneChange = findViewById(R.id.ll_goPhoneChange);
        ll_goBankChange = findViewById(R.id.ll_goBankChange);
        ll_myInfo_contents = findViewById(R.id.ll_myInfo_contents);
        ll_myInfo_contents.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        myInfo_ID = findViewById(R.id.myInfo_ID);
        myInfo_name = findViewById(R.id.myInfo_name);
        myInfo_dob = findViewById(R.id.myInfo_dob);
        myInfo_phone = findViewById(R.id.myInfo_phone);
        myInfo_bank_name = findViewById(R.id.myInfo_bank_name);
        myInfo_bank_num = findViewById(R.id.myInfo_bank_num);
        myInfo_bank_owner = findViewById(R.id.myInfo_bank_owner);
        ibtn_add_ref_manager = findViewById(R.id.ibtn_add_ref_manager);
        tv_license_num = findViewById(R.id.tv_license_num);
        tv_license_day = findViewById(R.id.tv_license_day);
        tv_license_month = findViewById(R.id.tv_license_month);
        tv_license_year = findViewById(R.id.tv_license_year);
        ll_not_ref_manager = findViewById(R.id.ll_not_ref_manager);
        ll_leave_pion = findViewById(R.id.ll_leave_pion);

        iv_myinfo_licenseCard = findViewById(R.id.iv_myinfo_licenseCard);

        if (UserInfo.getUserGrade() == 1 && UserInfo.getRefManagerGrade().equals("3")) {
            iv_myinfo_licenseCard.setImageDrawable(getDrawable(R.drawable.license_card_blue));
        } else if (UserInfo.getUserGrade() == 1) {
            iv_myinfo_licenseCard.setImageDrawable(getDrawable(R.drawable.license_card_blue));
        } else if (UserInfo.getUserGrade() == 2) {
            iv_myinfo_licenseCard.setImageDrawable(getDrawable(R.drawable.license_card_gold));
        } else if (UserInfo.getUserGrade() == 3) {
            iv_myinfo_licenseCard.setImageDrawable(getDrawable(R.drawable.license_card_gold));
        }
    }

    private void setUpListener() {

        tv_myInfo_licenseCard.setOnClickListener(v -> {
            if (isOpenLicenseCard) {
                ll_myInfo_licenseCard.setVisibility(View.GONE);
                ibtn_licenseCard_expand.setVisibility(View.VISIBLE);
                ibtn_licenseCard_collapse.setVisibility(View.GONE);
                isOpenLicenseCard = false;
            } else {
                ll_myInfo_licenseCard.setVisibility(View.VISIBLE);
                ibtn_licenseCard_collapse.setVisibility(View.VISIBLE);
                ibtn_licenseCard_expand.setVisibility(View.GONE);
                isOpenLicenseCard = true;
            }
        });

        setGoNext(ll_goPWChange);
        setGoNext(ll_goPhoneChange);
        setGoNext(ll_goBankChange);
        setGoNext(ll_not_ref_manager);

        ll_leave_pion.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Util.intentNext(LeavePion.class);
            }
        });
    }

    private void setGoNext(View button) {
        button.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                switch (v.getId()) {
                    case R.id.ll_goPhoneChange:
                        Util.putIntentInfo(PasswordConfirm.class, "result", PHONE_CHANGE);
                        break;
                    case R.id.ll_goBankChange:
                        Util.putIntentInfo(PasswordConfirm.class, "result", BANK_CHANGE);
                        break;
                    case R.id.ll_goPWChange:
                        Util.putIntentInfo(PasswordConfirm.class, "result", PW_CHANGE);
                        break;
                    case R.id.ll_not_ref_manager:
                        Util.putIntentInfo(PasswordConfirm.class, "result", HAVE_NOT_LICENSE);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class GetMyInfoData extends AsyncTask<String, Void, String> {
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

        @SuppressLint("SetTextI18n")
        private void getRegistStatus(String result) {

            try {
                JSONObject jsonObject = new JSONObject(result);
                String userID = jsonObject.getString("userID");
                String userName = jsonObject.getString("userName");
                String userDOB = jsonObject.getString("userDOB");
                String userPhone = jsonObject.getString("userPhone");
                String bankName = jsonObject.getString("bankName");
                String bankNum = jsonObject.getString("bankNum");
                String bankOwner = jsonObject.getString("bankOwner");
                String refManagerID = jsonObject.getString("refManagerID");
                String refManagerNum = jsonObject.getString("refManagerNum");
                String licenseNum = jsonObject.getString("licenseNum");
                String licenseRegDate = jsonObject.getString("licenseRegDate");

                myInfo_ID.setText(userID);
                myInfo_name.setText(userName);
                myInfo_dob.setText(userDOB);
                myInfo_phone.setText(Util.Phone(userPhone));
                myInfo_bank_name.setText(bankName);
                myInfo_bank_num.setText(bankNum);
                myInfo_bank_owner.setText("예금주 : " + bankOwner);

                tv_license_num.setText(licenseNum);
                tv_license_day.setText(licenseRegDate.substring(8, 10) + " /");
                tv_license_month.setText(licenseRegDate.substring(5, 7) + " /");
                tv_license_year.setText(licenseRegDate.substring(2, 4));

                if (UserInfo.getUserGrade() == 1 && refManagerNum.equals("0")) {
                    ll_not_ref_manager.setVisibility(View.VISIBLE);
                    View view_refManager = findViewById(R.id.view_refManager);
                    view_refManager.setVisibility(View.VISIBLE);
                }
                ll_myInfo_contents.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            } catch (JSONException e) {
                Log.e(Util.CURRENT_TAG, "Could not parse JSON. Error: " + e.getMessage());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        strUrl = Util.thepion_URL + "AjaxControl/GetMyInfo?userNum=" + UserInfo.getUserNum();
        getMyInfoData = (GetMyInfoData) new GetMyInfoData().execute(strUrl);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
