package kr.co.pionmanager.www.Menu.MyInfo;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
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


public class ChangeBankInfo extends AppCompatActivity {


    private ImageButton ibtn_back;
    private EditText et_change_bank_num;
    private EditText et_change_bank_owner;
    private Button btn_change_bank;
    private CustomDialog customDialog;
    private TextView tv_bank_num_error;
    private TextView tv_bank_owner_error;

    private BankChangeTask bankChangeTask;
    private String strUrl;
    public static Spinner spinner;

    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_bank);
        Util.SaveCurrentContext(this);
        Util.SetTagName("ChangeBankInfo");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        ibtn_back = findViewById(R.id.ibtn_back);
        et_change_bank_num = findViewById(R.id.et_change_bank_num);
        et_change_bank_owner = findViewById(R.id.et_change_bank_owner);
        btn_change_bank = findViewById(R.id.btn_change_bank);
        tv_bank_num_error = findViewById(R.id.tv_bank_num_error);
        tv_bank_owner_error = findViewById(R.id.tv_bank_owner_error);
        tv_bank_owner_error.setVisibility(View.GONE);
        tv_bank_num_error.setVisibility(View.GONE);

        buttonColorChange();

        spinner = findViewById(R.id.spinner_bank);
        Util.setSpinnerBankList(spinner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) buttonColorChange();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ibtn_back.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                onBackPressed();
            }
        });

        btn_change_bank.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (spinner.getSelectedItemPosition() == 0) {
                    Util.showToast("은행을 선택해주세요");
                } else if (0 >= et_change_bank_num.getText().toString().trim().length() || et_change_bank_num.getText().toString().trim().length() <= 8) {
                    tv_bank_num_error.setVisibility(View.VISIBLE);
                    tv_bank_owner_error.setVisibility(View.GONE);
                } else if (et_change_bank_owner.getText().toString().trim().length() == 0) {
                    tv_bank_num_error.setVisibility(View.GONE);
                    tv_bank_owner_error.setVisibility(View.VISIBLE);
                } else {
                    ShowDialog();
                }
            }
        });

    }


    private void ShowDialog() {
        String str = "계좌정보를 변경하시겠습니까?";
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
                    Util.thepion_URL + "AjaxControl/Privacy_Info_Change?bankName=" + Util.setEncoding(spinner.getSelectedItem().toString().trim())
                            + "&bankNum=" + et_change_bank_num.getText().toString().trim()
                            + "&bankOwner=" + Util.setEncoding(et_change_bank_owner.getText().toString().trim()) + "&userNum=" + UserInfo.getUserNum() + "&changeNum=" + MyInfo.BANK_CHANGE;
            bankChangeTask = (BankChangeTask) new BankChangeTask().execute(strUrl);
        }
    };

    private View.OnClickListener negativeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            customDialog.dismiss();
            tv_bank_owner_error.setVisibility(View.GONE);
            tv_bank_num_error.setVisibility(View.GONE);
        }
    };

    @SuppressLint("StaticFieldLeak")
    private class BankChangeTask extends AsyncTask<String, Void, String> {
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
                    UserInfo.setUserBankName(spinner.getSelectedItem().toString().trim());
                    UserInfo.setUserBankNum(et_change_bank_num.getText().toString().trim());
                    UserInfo.setUserBankOwner(et_change_bank_owner.getText().toString().trim());
                    CustomDialog.progressBar.setVisibility(View.GONE);
                    CustomDialog.ll_customDialog_button.setVisibility(View.VISIBLE);
                    customDialog.dismiss();
                    onBackPressed();
                    Util.showToast("계좌정보 변경 완료");
                    finish();
                } else if (rValue.equals("n")) {
                    CustomDialog.progressBar.setVisibility(View.GONE);
                    CustomDialog.ll_customDialog_button.setVisibility(View.VISIBLE);
                    customDialog.dismiss();
                    onBackPressed();
                    Util.showToast("현재 계좌정보와 동일합니다");
                }
            } catch (JSONException e) {
                Log.e(Util.CURRENT_TAG, "Could not parse JSON. Error: " + e.getMessage());
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ChangeBankTaskKill();
    }

    private void ChangeBankTaskKill() {
        if (bankChangeTask != null && bankChangeTask.getStatus() != BankChangeTask.Status.FINISHED) {
            bankChangeTask.cancel(true);
            bankChangeTask = null;
            Util.showToast("인터넷 연결상태가 좋지 않습니다.\n다시 시도해주세요");
        }
    }



    public void buttonColorChange() {
        btn_change_bank.setBackgroundDrawable(Util.CURRENT_CONTEXT.getResources().getDrawable(R.drawable.back_gray_round));
        btn_change_bank.setEnabled(false);
        et_change_bank_owner.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (et_change_bank_num.length() > 0 && et_change_bank_owner.length() > 0 && spinner.getSelectedItemPosition() > 0) {
                    btn_change_bank.setBackgroundDrawable(Util.CURRENT_CONTEXT.getResources().getDrawable(R.drawable.btn_selector_blue));
                    btn_change_bank.setTextColor(Util.CURRENT_CONTEXT.getResources().getColor(R.color.white));
                    btn_change_bank.setEnabled(true);
                } else {
                    btn_change_bank.setBackgroundDrawable(Util.CURRENT_CONTEXT.getResources().getDrawable(R.drawable.back_gray_round));
                    btn_change_bank.setTextColor(Util.CURRENT_CONTEXT.getResources().getColor(R.color.basic_gray));
                    btn_change_bank.setEnabled(false);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et_change_bank_num.length() > 0 && et_change_bank_owner.length() > 0 && spinner.getSelectedItemPosition() > 0) {
                    btn_change_bank.setBackgroundDrawable(Util.CURRENT_CONTEXT.getResources().getDrawable(R.drawable.btn_selector_blue));
                    btn_change_bank.setTextColor(Util.CURRENT_CONTEXT.getResources().getColor(R.color.white));
                    btn_change_bank.setEnabled(true);
                } else {
                    btn_change_bank.setBackgroundDrawable(Util.CURRENT_CONTEXT.getResources().getDrawable(R.drawable.back_gray_round));
                    btn_change_bank.setTextColor(Util.CURRENT_CONTEXT.getResources().getColor(R.color.basic_gray));
                    btn_change_bank.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (et_change_bank_num.length() > 0 && et_change_bank_owner.length() > 0 && spinner.getSelectedItemPosition() > 0) {
                    btn_change_bank.setBackgroundDrawable(Util.CURRENT_CONTEXT.getResources().getDrawable(R.drawable.btn_selector_blue));
                    btn_change_bank.setTextColor(Util.CURRENT_CONTEXT.getResources().getColor(R.color.white));
                    btn_change_bank.setEnabled(true);
                } else {
                    btn_change_bank.setBackgroundDrawable(Util.CURRENT_CONTEXT.getResources().getDrawable(R.drawable.back_gray_round));
                    btn_change_bank.setTextColor(Util.CURRENT_CONTEXT.getResources().getColor(R.color.basic_gray));
                    btn_change_bank.setEnabled(false);
                }
            }
        });
    }
}
