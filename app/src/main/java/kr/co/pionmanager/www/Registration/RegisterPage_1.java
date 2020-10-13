package kr.co.pionmanager.www.Registration;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import kr.co.pionmanager.www.Menu.Setting.ServiceRule;
import kr.co.pionmanager.www.Menu.Setting.Setting;
import kr.co.pionmanager.www.OnSingleClickListener;
import kr.co.pionmanager.www.PopView.PopViewActivity;
import kr.co.pionmanager.www.R;
import kr.co.pionmanager.www.Util;


public class RegisterPage_1 extends AppCompatActivity {

    private static final String TAG = "RegisterPage_1";
    private String webUrl = "";
    private CheckBox checkBox_all;
    private CheckBox checkBox_1;
    private CheckBox checkBox_2;
    private CheckBox checkBox_3;
    private CheckBox checkBox_4;
    private TextView tv_see_terms_1;
    private TextView tv_see_terms_2;
    private TextView tv_see_terms_3;
    private TextView tv_see_terms_4;
    private Button btn_next;
    private ProgressBar progressBar;
    private ImageButton ibtn_back;
    public static RegisterPage_1 registerPage_1;

    public static String name = "";
    public static String phone = "";
    public static String dob = "";
    public static String sex = "";
    public static String rValue = "";
    public static String isAgePass = "";
    public static String checkResult = "";

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page_term);
        Util.SaveCurrentContext(this);
        Util.SetTagName("RegisterPage_1");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        webUrl = Util.thepion_URL + "/okcert/phone_popup_join_server.asp?CP_CD=V44890000000&androidCookie=y";

        registerPage_1 = this;
        initView();
        setUpListener();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

    }

    private void initView() {
        progressBar = findViewById(R.id.progressBar);
        checkBox_all = findViewById(R.id.checkBox_all);
        checkBox_1 = findViewById(R.id.checkBox_1);
        checkBox_2 = findViewById(R.id.checkBox_2);
        checkBox_3 = findViewById(R.id.checkBox_3);
        checkBox_4 = findViewById(R.id.checkBox_4);
        tv_see_terms_1 = findViewById(R.id.tv_see_terms_1);
        tv_see_terms_2 = findViewById(R.id.tv_see_terms_2);
        tv_see_terms_3 = findViewById(R.id.tv_see_terms_3);
        tv_see_terms_4 = findViewById(R.id.tv_see_terms_4);
        ibtn_back = findViewById(R.id.ibtn_back);
        btn_next = findViewById(R.id.bt_next_register_page_1);
        btn_next.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        Util.setTextColor(Util.CURRENT_CONTEXT, R.string.tv_term_1, checkBox_1, getResources().getColor(R.color.my_red_light), 0, 4);
        Util.setTextColor(Util.CURRENT_CONTEXT, R.string.tv_term_2, checkBox_2, getResources().getColor(R.color.my_red_light), 0, 4);
        Util.setTextColor(Util.CURRENT_CONTEXT, R.string.tv_term_3, checkBox_3, getResources().getColor(R.color.my_red_light), 0, 4);
        Util.setTextColor(Util.CURRENT_CONTEXT, R.string.tv_term_4, checkBox_4, getResources().getColor(R.color.pion_basic_soft_blue), 0, 4);
    }

    private void setUpListener() {
        tv_see_terms_1.setOnClickListener(goRule);
        tv_see_terms_2.setOnClickListener(goRule);
        tv_see_terms_3.setOnClickListener(goRule);
        tv_see_terms_4.setOnClickListener(goRule);

        checkBox_all.setOnClickListener(v -> {
            checkBox_1.setChecked(checkBox_all.isChecked());
            checkBox_2.setChecked(checkBox_all.isChecked());
            checkBox_3.setChecked(checkBox_all.isChecked());
            checkBox_4.setChecked(checkBox_all.isChecked());
        });

        checkBox_1.setOnClickListener(v -> checkBoxControl());
        checkBox_2.setOnClickListener(v -> checkBoxControl());
        checkBox_3.setOnClickListener(v -> checkBoxControl());
        checkBox_4.setOnClickListener(v -> checkBoxControl());


        btn_next.setOnClickListener(v -> {
            btn_next.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            if ((checkBox_all.isChecked()) || (checkBox_1.isChecked() && checkBox_2.isChecked() && checkBox_3.isChecked())) {
                btn_next.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                Intent browserIntent = new Intent(RegisterPage_1.this, PopViewActivity.class);
                browserIntent.setData(Uri.parse(webUrl));
                browserIntent.putExtra("url", webUrl);
                if (checkBox_4.isChecked()) {
                    checkResult = "y";
                }
                else {
                    checkResult = "n";
                    btn_next.setVisibility(View.VISIBLE);
                }
                browserIntent.putExtra("checkResult", checkResult); //마케팅 체크여부 전달
                RegisterPage_1.this.startActivity(browserIntent);
            } else {
                Util.showToast(getApplicationContext(), "약관에 동의해주시기 바랍니다.");
                btn_next.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });

        ibtn_back.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                onBackPressed();
            }
        });
    }

    private void checkBoxControl() {
        if (checkBox_1.isChecked() && checkBox_2.isChecked() && checkBox_3.isChecked() && checkBox_4.isChecked()) {
            checkBox_all.setChecked(true);
        } else {
            checkBox_all.setChecked(false);
        }
    }

    private Button.OnClickListener goRule = v -> {
        switch (v.getId()) {
            case R.id.tv_see_terms_1:
                Util.putIntentInfo(ServiceRule.class, "result", Setting.SERVICE_RULE);
                break;
            case R.id.tv_see_terms_2:
                Util.putIntentInfo(ServiceRule.class, "result", Setting.PRIVACY_RULE);
                break;
            case R.id.tv_see_terms_3:
                Util.putIntentInfo(ServiceRule.class, "result", Setting.THIRDPEOPLE_RULE);
                break;
            case R.id.tv_see_terms_4:
                Util.putIntentInfo(ServiceRule.class, "result", Setting.MARKETING_RULE);
                break;
        }
    };


    public void goNextRegister() {
        if (isAgePass.equals("y") && rValue.equals("y")) {
            isAgePass = "";
            rValue = "";
            Intent intent = new Intent(RegisterPage_1.this, RegisterPage_2.class);
            startActivity(intent);

        } else if (isAgePass.equals("n")) {
            Util.showToast("만18세 이상부터 가입이 가능합니다.");
        } else if (rValue.equals("n")) {
            Util.showToast("이미 가입된 회원 정보가 있습니다.\n아이디를 분실한 경우 아이디 패스워드 찾기를 이용해보세요");
        }
    }
}
