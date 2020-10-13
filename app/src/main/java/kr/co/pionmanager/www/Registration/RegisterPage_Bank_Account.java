package kr.co.pionmanager.www.Registration;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import java.util.Timer;
import java.util.TimerTask;

import kr.co.pionmanager.www.OnSingleClickListener;
import kr.co.pionmanager.www.R;
import kr.co.pionmanager.www.UserInfo;
import kr.co.pionmanager.www.Util;

public class RegisterPage_Bank_Account extends AppCompatActivity {


    public static Spinner spinner_bank;
    Button btn_next;
    TextView tv_err_account;
    TextView tv_err_bank_owner;
    public static EditText et_account_number;
    public static EditText et_bank_owner;
    String strUrl;
    String TAG = "RegisterPage_Bank_Account";
    Timer timer;
    Timer timer2;
    private DownloadBankAccountTask mTask = null;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page__bank__account);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Util.SaveCurrentContext(this);
        Util.SetTagName("register_Bank_Account");

        tv_err_account = findViewById(R.id.tv_err_account);
        tv_err_bank_owner = findViewById(R.id.tv_err_bank_owner);
        et_bank_owner = findViewById(R.id.et_bank_owner);
        et_account_number = findViewById(R.id.et_account_number);

        spinner_bank = findViewById(R.id.spinner_bank);
        Util.setSpinnerBankList(spinner_bank);

        timer2 = new Timer(true);
        final android.os.Handler handler = new android.os.Handler();

        timer2.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Util::DownloadBankListTaskill);
            }
        }, 8000); // 시작 기동시간, 간격 (ms)


        btn_next = findViewById(R.id.btn_next_bank_account);
        progressBar = findViewById(R.id.progressBar);
        btn_next.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        btn_next.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                tv_err_account.setVisibility(View.GONE);
                tv_err_bank_owner.setVisibility(View.GONE);
                btn_next.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                int errNum = 0;
                String errMsg = "";
                if (spinner_bank.getSelectedItemPosition() == 0) {
                    Util.showToast("계좌를 선택하세요");
                    errNum++;
                    btn_next.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                } else if (et_account_number.length() == 0) {
                    tv_err_account.setVisibility(View.VISIBLE);
                    validation(et_account_number, true);
                    Util.showToast("계좌번호를 입력하세요");
                    errNum++;
                    btn_next.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                } else if (et_bank_owner.length() == 0) {
                    tv_err_bank_owner.setVisibility(View.VISIBLE);
                    Util.showToast("예금주명을 입력해주세요");
                    errNum++;
                    validation(et_bank_owner, true);
                    btn_next.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                } else if (errNum == 0) {//에러가 없다면
                    btn_next.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);



                    strUrl = Util.thepion_URL + "ajaxcontrol/BankRegist?userNum=" + UserInfo.getUserNum() + "&bankName=" + Util.setEncoding(spinner_bank.getSelectedItem().toString()) +
                            "&bankNum=" + et_account_number.getText().toString() + "&bankowner=" + Util.setEncoding(et_bank_owner.getText().toString());
                    mTask = (DownloadBankAccountTask) new DownloadBankAccountTask().execute(strUrl);


                } else {//에러가 있다면
                    validation(et_bank_owner, false);
                    validation(et_account_number, false);
                    timer = new Timer(true);
                    final android.os.Handler handler1 = new android.os.Handler();

                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            handler1.post(() -> {
                                BankAccountDownloadTaskKill();
                                btn_next.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(RegisterPage_Bank_Account.this, "인터넷 연결상태가 좋지 않습니다.\n다시 시도해주세요", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }, 8000); // 시작 기동시간, 간격 (ms)
                }
            }
        });
    }

    private void validation(EditText editText, boolean hasError) {
        if (hasError) {
            editText.setFocusable(true);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadBankAccountTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("result", result);
            getRegistStatus(result);
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
                int BankRegister = jsonObject.getInt("uploadStatus");
                String text = jsonObject.getString("text");

                if (BankRegister == 0) {//계좌 제출 실패한 경우
                    Util.showToast("계좌 등록에 실패했습니다. 다시 시도해주시기 바랍니다.");
                } else {//계좌 제출이 성공한 경우
                    Log.e(TAG, "BankRegister : " + BankRegister + "text : " + text);
                    Intent intent = new Intent(RegisterPage_Bank_Account.this, RegisterManagerApplyComplete.class);
                    startActivity(intent);
                    progressBar.setVisibility(View.GONE);
                    btn_next.setVisibility(View.VISIBLE);
                }
                if (timer != null) {
                    timer.cancel();
                }
            } catch (JSONException e) {
                Log.e(TAG, "Could not parse JSON. Error: " + e.getMessage());
            }
        }
    }

    private void BankAccountDownloadTaskKill() {
        if (mTask != null && mTask.getStatus() != DownloadBankAccountTask.Status.FINISHED) {
            mTask.cancel(true);
            mTask = null;
        }
    }

}
