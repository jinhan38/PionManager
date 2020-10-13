package kr.co.pionmanager.www.Menu;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Objects;

import kr.co.pionmanager.www.BackButton;
import kr.co.pionmanager.www.BottomNaviWrap;
import kr.co.pionmanager.www.ExplainDialog.ExplainManagerCommission;
import kr.co.pionmanager.www.ExplainDialog.ExplainPartnerCommission;
import kr.co.pionmanager.www.ExplainDialog.ExplainTeamLeaderCommission;
import kr.co.pionmanager.www.OnSingleClickListener;
import kr.co.pionmanager.www.R;
import kr.co.pionmanager.www.RegInfoDetail;
import kr.co.pionmanager.www.RegistrationListPage;
import kr.co.pionmanager.www.UserInfo;
import kr.co.pionmanager.www.Util;

public class MypageAboutData extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "MypageAboutData";
    private ImageView iv_ex_manager_commission;
    private ImageView iv_ex_teamleader_commission;
    private ExplainManagerCommission explainManagerCommission;
    private ExplainTeamLeaderCommission explainTeamLeaderCommission;
    private ExplainPartnerCommission explainPartnerCommission;
    public static MypageAboutData mypageAboutData;
    private long backBtnTime = 0;


    private TextView tv_commission_num;
    private TextView tv_register;
    private TextView tv_ing;
    private TextView tv_finished;
    private TextView biz_register;
    private TextView biz_ing;
    private TextView biz_finished;
    private TextView tv_teamLeaderCommission;
    private TextView tv_teamLeaderCommission_num;
    private TextView biz_won;
    private TextView tv_cMember_price_num;
    private TextView tv_com_register;
    private TextView tv_com_ing;
    private TextView tv_com_finished;
    private TextView tv_personal_price;
    private TextView tv_date;

    private TextView notice_1;
    private TextView notice_2;
    private TextView notice_3;
    private TextView see_board;

    private MypageGetData mypageGetData;

    public int rValue = 0;
    public String monthPayment = "";
    public String nowRegister;
    public String nowIng;
    public String monthFinished;
    public String bizManagerRegister;
    public String bizManagerIng;
    public String bizManagerFinish;
    public String AorB;
    public String text1;
    public String text2;
    public String text3;
    public String text4;
    public String sumPrice;
    public String text5;
    public String text6;
    public String text7;
    public String text8;
    public String sumPayment;
    public String incentivePay;
    public String platCommission;
    public String finalPayment;

    public String monthTeamLeaderPayment;
    public String bAorB;
    public String btext_1;
    public String btext_2;
    public String btext_3;
    public String btext_4;
    public String bsumPrice;
    public String btext_5;
    public String btext_6;
    public String btext_7;
    public String btext_8;
    public String bsumPayment;
    public String bplatCommission;
    public String bfinalPayment;

    public TextView bizSection1;
    public TextView bizSection2;
    public TextView bizSection3;
    public TextView bizSection4;

    public String personalPrice;

    private Button btn_seeDetail;

    private ConstraintLayout cont_manager;
    private ConstraintLayout cont_company;
    private ImageView iv_ex_biz_partner;
    private ProgressBar progressRange;

    private boolean isOpenShowManager = false;
    private boolean isOpenShowBizManager = false;
    private boolean isOpenShowBizPartner = false;
    private View v;
    private FrameLayout fm_content_all;
    private ProgressBar progressBar;
    private TextView cMember_text;
    private MypageGetComMemberData mypageGetComMemberData;
    private LinearLayout ll_biz_status;
    private Handler handler = new Handler();
    private int pStatus = 0;
    private FrameLayout fm_rangeProgress;
    public static MypageAboutData singlton;
    private SwipeRefreshLayout swipeRefresh;
    private boolean isSwipeOpen = false;
    private boolean dataTaskFinish = false;

    // newInstance constructor for creating fragment with arguments
    public static MypageAboutData newInstance() {
        if (singlton == null) {
            singlton = new MypageAboutData();
        }
        return singlton;
    }

    public void clearSingleTon() {
        this.singlton = null;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_mypage_about_data, container, false);
        Log.e(TAG, "onCreateView: ");
        Util.SaveCurrentContext(getActivity());
        initView();
        setupListener();
        mypageAboutData = this;
        return v;
    }


    private void initView() {
        swipeRefresh = v.findViewById(R.id.swipeRefresh);
        biz_register = v.findViewById(R.id.biz_register);
        biz_ing = v.findViewById(R.id.biz_ing);
        biz_finished = v.findViewById(R.id.biz_finished);
        iv_ex_manager_commission = v.findViewById(R.id.iv_ex_manager_commission);
        tv_commission_num = v.findViewById(R.id.tv_commission_num);
        tv_register = v.findViewById(R.id.tv_register);
        tv_ing = v.findViewById(R.id.tv_ing);
        tv_finished = v.findViewById(R.id.tv_finished);
        tv_cMember_price_num = v.findViewById(R.id.tv_cMember_price_num);
        tv_teamLeaderCommission = v.findViewById(R.id.tv_teamLeaderCommission);
        tv_teamLeaderCommission_num = v.findViewById(R.id.tv_teamLeaderCommission_num);
        iv_ex_teamleader_commission = v.findViewById(R.id.iv_ex_teamleader_commission);
        biz_won = v.findViewById(R.id.biz_won);
        tv_teamLeaderCommission.setVisibility(View.GONE);
        tv_teamLeaderCommission_num.setVisibility(View.GONE);
        biz_won.setVisibility(View.GONE);
        iv_ex_teamleader_commission.setVisibility(View.GONE);
        cont_manager = v.findViewById(R.id.cont_manager);
        cont_company = v.findViewById(R.id.cont_company);
        cont_manager.setVisibility(View.VISIBLE);
        cont_company.setVisibility(View.GONE);
        cMember_text = v.findViewById(R.id.cMember_text);
        cMember_text.setVisibility(View.GONE);
        Calendar cal = Calendar.getInstance();
        int month = cal.get(cal.MONTH) + 1;
        int year = cal.get(cal.YEAR);
        tv_date = v.findViewById(R.id.tv_date);
        tv_date.setText(year + "년 " + month + "월");
        progressRange = v.findViewById(R.id.progressRange);
        tv_com_register = v.findViewById(R.id.tv_com_register);
        tv_com_ing = v.findViewById(R.id.tv_com_ing);
        tv_com_finished = v.findViewById(R.id.tv_com_finished);
        iv_ex_biz_partner = v.findViewById(R.id.iv_ex_biz_partner);
        tv_personal_price = v.findViewById(R.id.tv_personal_price);
        btn_seeDetail = v.findViewById(R.id.btn_seeDetail);
        fm_content_all = v.findViewById(R.id.fm_content_all);
        progressBar = v.findViewById(R.id.progressBar);
        ll_biz_status = v.findViewById(R.id.ll_biz_status);
        ll_biz_status.setVisibility(View.GONE);
        fm_rangeProgress = v.findViewById(R.id.fm_rangeProgress);

        notice_1 = v.findViewById(R.id.notice_1);
        notice_2 = v.findViewById(R.id.notice_2);
        notice_3 = v.findViewById(R.id.notice_3);
        see_board = v.findViewById(R.id.see_board);

        getDataTask();

    }


    private void setupListener() {
        iv_ex_manager_commission.setOnClickListener(v -> showExplainPopup(1));
        iv_ex_teamleader_commission.setOnClickListener(v -> showExplainPopup(2));
        iv_ex_biz_partner.setOnClickListener(v -> showExplainPopup(3));
        btn_seeDetail.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Analystic.class);
            startActivity(intent);
        });

        notice_1.setOnClickListener(this);
        notice_2.setOnClickListener(this);
        notice_3.setOnClickListener(this);
        see_board.setOnClickListener(this);
        swipeRefresh.setOnRefreshListener(this);
    }

    private void getDataTask() {

        Log.e(TAG, "getDataTask: getDataTask" );
        Log.e(TAG, "getDataTask: userGrade : " + UserInfo.getUserGrade() );
        if (getActivity() != null) {

            fm_content_all.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            if (UserInfo.getUserGrade() == 1 && UserInfo.getRefManagerGrade().equals("3")) {
                String strUrl = Util.thepion_URL + "AjaxControl/Mypage_3_member?userNum=" + UserInfo.getUserNum();
                mypageGetComMemberData = (MypageGetComMemberData) new MypageGetComMemberData().execute(strUrl);
                Log.e(TAG, "grade1: manager of bizPatener : " + strUrl);
                cont_manager.setVisibility(View.GONE);
                cont_company.setVisibility(View.VISIBLE);
                iv_ex_biz_partner.setVisibility(View.GONE);
                cMember_text.setVisibility(View.VISIBLE);

            } else if (UserInfo.getUserGrade() == 1) {
                String strUrl = Util.thepion_URL + "AjaxControl/Mypage?userNum=" + UserInfo.getUserNum();
                mypageGetData = (MypageGetData) new MypageGetData().execute(strUrl);
                Log.e(TAG, "grade1: Manager : " + strUrl);

            } else if (UserInfo.getUserGrade() == 2) {
                String strUrl = Util.thepion_URL + "AjaxControl/Mypage_2?userNum=" + UserInfo.getUserNum();
                mypageGetData = (MypageGetData) new MypageGetData().execute(strUrl);
                Log.e(TAG, "grade2: bizManager : " + strUrl);
                tv_teamLeaderCommission.setVisibility(View.VISIBLE);
                tv_teamLeaderCommission_num.setVisibility(View.VISIBLE);
                biz_won.setVisibility(View.VISIBLE);
                iv_ex_teamleader_commission.setVisibility(View.VISIBLE);
                ll_biz_status.setVisibility(View.VISIBLE);

            } else if (UserInfo.getUserGrade() == 3) {
                String strUrl = Util.thepion_URL + "AjaxControl/Mypage_3?userNum=" + UserInfo.getUserNum();
                mypageGetComMemberData = (MypageGetComMemberData) new MypageGetComMemberData().execute(strUrl);
                Log.e(TAG, "grade3: bizpartner : " + strUrl);
                cont_manager.setVisibility(View.GONE);
                cont_company.setVisibility(View.VISIBLE);
                tv_personal_price.setText("비즈파트너 수당");
                iv_ex_biz_partner.setVisibility(View.VISIBLE);
                fm_rangeProgress.setVisibility(View.VISIBLE);
            }
        }
    }


    private void showExplainPopup(int gradeNum) {
        Window window = null;
        switch (gradeNum) {
            case 1:
                explainManagerCommission = new ExplainManagerCommission(getActivity(), positiveListener);
                Objects.requireNonNull(explainManagerCommission.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                explainManagerCommission.show();
                isOpenShowManager = true;
                window = explainManagerCommission.getWindow();
                break;
            case 2:
                explainTeamLeaderCommission = new ExplainTeamLeaderCommission(getActivity(), positiveListener);
                Objects.requireNonNull(explainTeamLeaderCommission.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                explainTeamLeaderCommission.show();
                isOpenShowBizManager = true;
                window = explainTeamLeaderCommission.getWindow();
                break;
            case 3:
                explainPartnerCommission = new ExplainPartnerCommission(getActivity(), positiveListener);
                Objects.requireNonNull(explainPartnerCommission.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                explainPartnerCommission.show();
                isOpenShowBizPartner = true;
                window = explainPartnerCommission.getWindow();
                break;
        }

        //디스플레이 해상도 가져오기
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int x = (int) (size.x * 0.95f);
        int y = (int) (size.y * 0.8f);
        window.setLayout(x, y);
    }


    private OnSingleClickListener positiveListener = new OnSingleClickListener() {
        @Override
        public void onSingleClick(View v) {

            if (isOpenShowManager) {
                explainManagerCommission.dismiss();
                isOpenShowManager = false;
            } else if (isOpenShowBizManager) {
                explainTeamLeaderCommission.dismiss();
                isOpenShowBizManager = false;
            } else if (isOpenShowBizPartner) {
                explainPartnerCommission.dismiss();
                isOpenShowBizPartner = false;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.notice_1:
                Util.putIntentInfo(BoardPager.class, "MypageNotice", "1");
                break;
            case R.id.notice_2:
                Util.putIntentInfo(BoardPager.class, "MypageNotice", "2");
                break;
            case R.id.notice_3:
                Util.putIntentInfo(BoardPager.class, "MypageNotice", "3");
                break;
            case R.id.see_board:
                Util.putIntentInfo(BoardPager.class, "page", "board");
                break;

        }
    }

    @Override
    public void onRefresh() {
        swipeRefresh.setRefreshing(true);
        isSwipeOpen = true;
        if (isSwipeOpen) {
            new Handler().postDelayed(() -> {
                getDataTask();
                isSwipeOpen = false;
                swipeRefresh.setRefreshing(false);
            }, 1500);
        }
    }

//    @Override
//    public void onBackPressed() {
//        long curTime = System.currentTimeMillis();
//        long gapTime = curTime - backBtnTime;
//        if (0 <= gapTime && 2000 >= gapTime) {
//            getActivity().finish();
//        } else {
//            backBtnTime = curTime;
//            Toast.makeText(Util.CURRENT_CONTEXT, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
//        }
//    }


    @SuppressLint("StaticFieldLeak")
    private class MypageGetData extends AsyncTask<String, Void, String> {
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
                rValue = jsonObject.getInt("rValue");
                monthPayment = jsonObject.getString("monthPayment");
                nowRegister = jsonObject.getString("nowRegister");
                nowIng = jsonObject.getString("nowIng");
                monthFinished = jsonObject.getString("monthFinished");
                AorB = jsonObject.getString("AorB");
                text1 = jsonObject.getString("text_1");
                text2 = jsonObject.getString("text_2");
                text3 = jsonObject.getString("text_3");
                text4 = jsonObject.getString("text_4");
                sumPrice = jsonObject.getString("sumPrice");

                text5 = jsonObject.getString("text_5");
                text6 = jsonObject.getString("text_6");
                text7 = jsonObject.getString("text_7");
                text8 = jsonObject.getString("text_8");
                sumPayment = jsonObject.getString("sumPayment");
                platCommission = jsonObject.getString("platCommission");
                finalPayment = jsonObject.getString("finalPayment");

                if (UserInfo.getUserGrade() == 1) {
                    tv_commission_num.setText(monthPayment);
                    tv_register.setText("등록\n" + nowRegister);
                    tv_ing.setText("진행\n" + nowIng);
                    tv_finished.setText("완료\n" + monthFinished);
                    numberCountUp(tv_commission_num, monthPayment, 1500);

                    String refManagerCount = jsonObject.getString("refManagerCount");
                    Log.e(TAG, "getRegistStatus: refManagerCount : " + refManagerCount );
                    if (refManagerCount.length() < 1) refManagerCount = "0";
                    UserInfo.setRefManagerCount(refManagerCount);

                } else if (UserInfo.getUserGrade() == 2) {

                    monthTeamLeaderPayment = jsonObject.getString("monthTeamLeaderPayment");
                    nowRegister = jsonObject.getString("nowRegister");
                    nowIng = jsonObject.getString("nowIng");
                    monthFinished = jsonObject.getString("monthFinished");
                    bizManagerRegister = jsonObject.getString("bizManagerRegister");
                    bizManagerIng = jsonObject.getString("bizManagerIng");
                    bizManagerFinish = jsonObject.getString("bizManagerFinish");
                    bAorB = jsonObject.getString("bAorB");
                    btext_1 = jsonObject.getString("btext_1");
                    btext_2 = jsonObject.getString("btext_2");
                    btext_3 = jsonObject.getString("btext_3");
                    btext_4 = jsonObject.getString("btext_4");
                    bsumPrice = jsonObject.getString("bsumPrice");
                    btext_5 = jsonObject.getString("btext_5");
                    btext_6 = jsonObject.getString("btext_6");
                    btext_7 = jsonObject.getString("btext_7");
                    btext_8 = jsonObject.getString("btext_8");
                    bsumPayment = jsonObject.getString("bsumPayment");
                    bplatCommission = jsonObject.getString("bplatCommission");
                    bfinalPayment = jsonObject.getString("bfinalPayment");

                    tv_commission_num.setText(monthPayment);
                    tv_teamLeaderCommission_num.setText(monthTeamLeaderPayment);
                    tv_register.setText("등록\n" + nowRegister);
                    tv_ing.setText("진행\n" + nowIng);
                    tv_finished.setText("완료\n" + monthFinished);
                    biz_register.setText("등록\n" + bizManagerRegister);
                    biz_ing.setText("진행\n" + bizManagerIng);
                    biz_finished.setText("완료\n" + bizManagerFinish);
                    TextView tv_status = v.findViewById(R.id.tv_status);
                    tv_status.setText("팀원 경쟁입찰 현황");
                    numberCountUp(tv_commission_num, monthPayment, 1500);
                    numberCountUp(tv_teamLeaderCommission_num, monthTeamLeaderPayment, 1500);
                }

                String notice1 = jsonObject.getString("notice_1");
                int notice_1_num = jsonObject.getInt("notice_1_num");
                String notice2 = jsonObject.getString("notice_2");
                int notice_2_num = jsonObject.getInt("notice_2_num");
                String notice3 = jsonObject.getString("notice_3");
                int notice_3_num = jsonObject.getInt("notice_3_num");

                notice_1.setText(notice1);
                notice_2.setText(notice2);
                notice_3.setText(notice3);

                UserInfo.setNotice_1_num(notice_1_num);
                UserInfo.setNotice_2_num(notice_2_num);
                UserInfo.setNotice_3_num(notice_3_num);


                textColorPoint();
                fm_content_all.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                pushTask();

            } catch (JSONException e) {
                Log.e(TAG, "Could not parse JSON. Error: " + e.getMessage());
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class MypageGetComMemberData extends AsyncTask<String, Void, String> {
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

                String notice1 = jsonObject.getString("notice_1");
                String notice2 = jsonObject.getString("notice_2");
                String notice3 = jsonObject.getString("notice_3");
                Log.e(TAG, "getRegistStatus: " + notice1);

                Log.e(TAG, "getRegistStatus: notice1" + Util.changeTagToText(notice1));
                notice_1.setText(notice1);
                notice_2.setText(notice2);
                notice_3.setText(notice3);

                Log.e(TAG, "getRegistStatus: ");

                if (UserInfo.getUserGrade() == 3) {
                    rValue = jsonObject.getInt("rValue");
                    monthPayment = jsonObject.getString("monthPayment");
                    nowRegister = jsonObject.getString("nowRegister");
                    nowIng = jsonObject.getString("nowIng");
                    monthFinished = jsonObject.getString("monthFinished");
                    AorB = jsonObject.getString("AorB");
                    text1 = jsonObject.getString("text_1");
                    text2 = jsonObject.getString("text_2");
                    text3 = jsonObject.getString("text_3");
                    text4 = jsonObject.getString("text_4");
                    sumPrice = jsonObject.getString("sumPrice");

                    text5 = jsonObject.getString("text_5");
                    text6 = jsonObject.getString("text_6");
                    text7 = jsonObject.getString("text_7");
                    text8 = jsonObject.getString("text_8");
                    sumPayment = jsonObject.getString("sumPayment");
                    incentivePay = jsonObject.getString("incentivePay");
                    platCommission = jsonObject.getString("platCommission");
                    finalPayment = jsonObject.getString("finalPayment");
                    int rangePer = jsonObject.getInt("rangePer");
                    String Section1 = jsonObject.getString("bizSection1");
                    String Section2 = jsonObject.getString("bizSection2");
                    String Section3 = jsonObject.getString("bizSection3");
                    String Section4 = jsonObject.getString("bizSection4");


                    bizSection1 = v.findViewById(R.id.bizSection1);
                    bizSection2 = v.findViewById(R.id.bizSection2);
                    bizSection3 = v.findViewById(R.id.bizSection3);
                    bizSection4 = v.findViewById(R.id.bizSection4);

                    bizSection1.setText(Section1);
                    bizSection2.setText(Section2);
                    bizSection3.setText(Section3);
                    bizSection4.setText(Section4);

                    getRangePer(rangePer);

                    tv_cMember_price_num.setText(monthPayment);
                    tv_com_register.setText("등록\n" + nowRegister);
                    tv_com_ing.setText("진행\n" + nowIng);
                    tv_com_finished.setText("완료\n" + monthFinished);
                    numberCountUp(tv_cMember_price_num, monthPayment, 1500);

                } else {

                    rValue = jsonObject.getInt("rValue");
                    personalPrice = jsonObject.getString("personalPrice");
                    nowRegister = jsonObject.getString("nowRegister");
                    nowIng = jsonObject.getString("nowIng");
                    monthFinished = jsonObject.getString("monthFinished");
                    String text = jsonObject.getString("text");
                    tv_cMember_price_num.setText(personalPrice);
                    tv_com_register.setText("등록\n" + nowRegister);
                    tv_com_ing.setText("진행\n" + nowIng);
                    tv_com_finished.setText("완료\n" + monthFinished);
                    cMember_text.setText(text);
                    numberCountUp(tv_cMember_price_num, personalPrice, 1500);
                }

                textColorPoint();

                fm_content_all.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                pushTask();



            } catch (JSONException e) {
                Log.e(Util.CURRENT_TAG, "Could not parse JSON. Error: " + e.getMessage());
            }
        }
    }

    /**
     * 푸시 클릭 시 이동하는 부분
     */
    private void pushTask(){
        //FCM 데이터 보낸 데이터 받는 코드
        //LoanInfo Num값 받아서 보내주기만 하면 됨
        if (UserInfo.getPushOpen()) {
            BottomNaviWrap.bottomNaviWrap.showToolbarIcon(false);
            BottomNaviWrap.bottomNaviWrap.prevPageNum = 2;
            BottomNaviWrap.bottomNaviWrap.vpPager.setCurrentItem(2);
            BottomNaviWrap.bottomNaviWrap.navView.getMenu().getItem(2).setChecked(true);
            UserInfo.setPushOpen(false);
            startActivity(new Intent(getActivity(), RegInfoDetail.class));
            RegistrationListPage.registrationListPage.binding.llRegListSearch.setVisibility(View.GONE);
        }

        dataTaskFinish = true;

    }


    /**
     * 등록, 진행, 완료 건수 텍스트 컬러 변화
     * 글자수에 맞춰서 조건문 작성
     * 등급별로 다르게 진행
     */
    private void textColorPoint() {
        Log.d(TAG, "textColorPoint: 추천인 " + UserInfo.getRefManagerGrade());
        if (UserInfo.getRefManagerGrade().equals("3") && UserInfo.getUserGrade() == 1 || UserInfo.getUserGrade() == 3) {
            spannableTask(nowRegister.length(), tv_com_register);
            spannableTask(nowIng.length(), tv_com_ing);
            spannableTask(monthFinished.length(), tv_com_finished);
        } else if (UserInfo.getUserGrade() == 2) {
            spannableTask(nowRegister.length(), tv_register);
            spannableTask(nowIng.length(), tv_ing);
            spannableTask(monthFinished.length(), tv_finished);
            spannableTask(bizManagerRegister.length(), biz_register);
            spannableTask(bizManagerIng.length(), biz_ing);
            spannableTask(bizManagerFinish.length(), biz_finished);
        } else if (UserInfo.getUserGrade() == 1) {
            spannableTask(nowRegister.length(), tv_register);
            spannableTask(nowIng.length(), tv_ing);
            spannableTask(monthFinished.length(), tv_finished);

        }
    }

    private void spannableTask(int length, TextView textview) {

        switch (length) {
            case 1:
                Spannable spannable = (Spannable) textview.getText();
                spannable.setSpan(new RelativeSizeSpan(2f), 3, 4, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.pion_basic_soft_blue)), 3, 4, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                break;
            case 2:
                Spannable spannable2 = (Spannable) textview.getText();
                spannable2.setSpan(new RelativeSizeSpan(2f), 3, 5, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                spannable2.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.pion_basic_soft_blue)), 3, 5, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                break;
            case 3:
                Spannable spannable3 = (Spannable) textview.getText();
                spannable3.setSpan(new RelativeSizeSpan(2f), 3, 6, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                spannable3.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.pion_basic_soft_blue)), 3, 6, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                break;
            default:
                break;
        }
    }

    /**
     * 텍스트 숫자 올라가는 애니메이션 구현
     *
     * @param textView
     * @param strData
     * @param duration
     */
    private void numberCountUp(TextView textView, String strData, long duration) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                long maxNum = Long.parseLong(strData.replaceAll(",", "").replaceAll(" ", "").replaceAll("원", ""));
                boolean loopEnd = false;
                long increaseMount = maxNum / duration;
                long status = 0;
                while (!loopEnd) {
                    status += increaseMount;
                    if (status > maxNum) {
                        status = maxNum;
                        loopEnd = true;
                    }

                    long finalStatus = status;
                    handler.post(() -> {
                        textView.setText(Util.GetNumFormatWon(finalStatus));
                    });

                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    /**
     * 비즈파트너
     */
    private void getRangePer(int rangePer) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (pStatus < rangePer) {
                    pStatus += 1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressRange.setProgress(pStatus);
                        }
                    });
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    public void mypageGetComMemberDataKill() {
        if (mypageGetComMemberData != null && mypageGetComMemberData.getStatus() != MypageGetComMemberData.Status.FINISHED) {
            mypageGetComMemberData.cancel(true);
            mypageGetComMemberData = null;
            Log.e("Front task kill", "");
        }
    }

    public void mypageGetDataKill() {
        if (mypageGetData != null && mypageGetData.getStatus() != MypageGetData.Status.FINISHED) {
            mypageGetData.cancel(true);
            mypageGetData = null;
            Log.e("Front task kill", "");
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        mypageGetDataKill();
        mypageGetComMemberDataKill();
    }

    @Override
    public void onResume() {
        if (!dataTaskFinish) {
            getDataTask();
        }
        super.onResume();
    }
}
