package kr.co.pionmanager.www;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import kr.co.pionmanager.www.RegInfoDetailFragment.AuctionHouseList;
import kr.co.pionmanager.www.RegInfoDetailFragment.HaveNotAuctionHouse;


public class RegInfoDetail extends AppCompatActivity {

    private ImageButton ibtn_back;
    private LinearLayout ll_reg_detail_info;
    private Handler handler;
    private int loanNum = 0;
    private FrameLayout fm_parent;
    private ProgressBar progressBar;
    private String TAG = "RegInfoDetail";
    private NestedScrollView scrollView;
    public static String regStatus_k;
    private RegAuctionListTask regAuctionListTask;
    private RegistrationInfo_Detail_Task registrationInfo_detail_task;
    private String strUrl_all;
    private ImageButton btn_go_to_top;
    public RecyclerView recyclerView;
    public static RegInfoDetail regInfoDetail;

    private static ArrayList<AuctionListData> auctionListDataArrayList;
    private AuctionListData auctionListData;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        regInfoDetail  = this;
        setContentView(R.layout.activity_reg_info_detail);
        Util.SaveCurrentContext(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        ll_reg_detail_info = findViewById(R.id.ll_reg_detail_info);
        fm_parent = findViewById(R.id.fm_parent);
        progressBar = findViewById(R.id.progressBar);
        ibtn_back = findViewById(R.id.ibtn_back);
        scrollView = findViewById(R.id.scrollView);
        btn_go_to_top = findViewById(R.id.btn_go_to_top);

        Log.e(TAG, "getLoanInfoStatus: recuclerView null check : " + recyclerView);
        handler = new Handler();

        ibtn_back.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                onBackPressed();
            }
        });

        btn_go_to_top.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                TextView tv_reg_detail_info_focus = findViewById(R.id.tv_reg_detail_info_focus);
                scrollToView(tv_reg_detail_info_focus, scrollView, 0);
            }
        });

        pushTaskInit();
    }

    private void pushTaskInit() {


        //object로 받은 푸시론넘 형변환해서 넘겨주면 됨
        int data = 0;
        String str = String.valueOf(UserInfo.getPushUrl());
        Log.e(TAG, "pushTaskInit: getPushUrl : " + UserInfo.getPushUrl() );
        if (str.length() > 0) {
            Log.e(TAG, "init: str 0 이상");
            data = Integer.parseInt(str);
        } else {
            Log.e(TAG, "init: str 없음");
            data = Integer.parseInt(UserInfo.getLoanNum());
        }

        loanNum = data;
        Log.e(TAG, "pushTaskInit: loanNum : " + loanNum);
        if (data > 0) {
            ll_reg_detail_info.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            String strUrl_all = Util.thepion_URL + "AjaxControl/registration_info_detail?loanNum=" + loanNum;
            registrationInfo_detail_task = (RegistrationInfo_Detail_Task) new RegistrationInfo_Detail_Task().execute(strUrl_all);
        } else {
            ll_reg_detail_info.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
        UserInfo.setPushLoanNum("");
        RegistrationListPage.registrationListPage.binding.llRegListSearch.setVisibility(View.VISIBLE);


    }


    /**
     * LoanInfo를 통해 상세 등록정보 받아오기
     */
    @SuppressLint("StaticFieldLeak")
    private class RegistrationInfo_Detail_Task extends AsyncTask<String, String, String> {

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
            Log.e(TAG, "onPostExecute" + result);
            getLoanInfoStatus(result);
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

        private void getLoanInfoStatus(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                String regNum = jsonObject.getString("regNum");
                String regDate = jsonObject.getString("regDate");
                String regStatus = jsonObject.getString("regStatus");
                String isCancled = jsonObject.getString("isCancled");
                String cancelReason = jsonObject.getString("cancelReason");
                String regAddress = jsonObject.getString("regAddress");
                String josoDong = jsonObject.getString("josoDong");
                String josoHo = jsonObject.getString("josoHo");
                String floor = jsonObject.getString("floor");
                String latestDealPrice = jsonObject.getString("latestDealPrice");
                String latestDealDate = jsonObject.getString("latestDealDate");
                String latestDealSize = jsonObject.getString("latestDealSize");
                String landPrice = jsonObject.getString("landPrice");
                String regMemberName = jsonObject.getString("regMemberName");
                String regHopeLoanPrice = jsonObject.getString("regHopeLoanPrice");
                String regPreLoanPrice = jsonObject.getString("regPreLoanPrice");
                String regRenterInfo = jsonObject.getString("regRenterInfo");
                String auctionCompleteDate = jsonObject.getString("auctionCompleteDate");
                String isPrevLoan = jsonObject.getString("isPrevLoan");
                String IsAPTData = jsonObject.getString("IsAPTData");
                String jobType = jsonObject.getString("jobType");
                String platformRate = jsonObject.getString("platformRate");

                setText(regNum, regDate, regStatus, isCancled, cancelReason, regAddress, josoDong, josoHo,
                        floor, latestDealPrice, latestDealDate, latestDealSize, landPrice, regMemberName,
                        regHopeLoanPrice, regPreLoanPrice, regRenterInfo, auctionCompleteDate, IsAPTData, isPrevLoan, jobType, platformRate);

                setAuctionHouseInfoFrag(regStatus);
                regStatus_k = regStatus;
                ll_reg_detail_info.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

                if (!regStatus.equals("등록")) {
                    strUrl_all = Util.thepion_URL + "AjaxControl/registration_info_detail_list?loanNum=" + loanNum;
                    regAuctionListTask = (RegAuctionListTask) new RegAuctionListTask().execute(strUrl_all);
                }
            } catch (JSONException e) {
                Log.e(TAG, "Could not parse JSON. Error: " + e.getMessage());
            }
        }
    }


    /**
     * AuctionHouse List 정보를 받아오는 곳 = 입찰리스트
     */
    @SuppressLint("StaticFieldLeak")
    private class RegAuctionListTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            auctionListData = new AuctionListData();
            auctionListDataArrayList = new ArrayList<AuctionListData>();
//            auctionListDataArrayList.clear();
            ll_reg_detail_info.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
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
            Log.e(TAG, "onPostExecute" + result);
            getLoanInfoStatus(result);
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

        private void getLoanInfoStatus(String result) {
            try {
                String rValue = "";
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = new JSONObject(jsonArray.getString(i));
                    rValue = jsonObject.getString("result");
                    String comName = jsonObject.getString("comName");
                    String loanPrice = jsonObject.getString("loanPrice");
                    String loanRate = jsonObject.getString("loanRate");
                    String repayMethod = jsonObject.getString("repayMethod");
                    String repayFeeRate = jsonObject.getString("repayFeeRate");
                    String selectManagerNum = jsonObject.getString("selectManagerNum");
                    String auctionHouseNum = jsonObject.getString("auctionHouseNum");
                    String companyComment = jsonObject.getString("companyComment");
                    String companyLogoUrl = jsonObject.getString("companyLogoUrl");

                    auctionListData = new AuctionListData(rValue, comName, loanPrice, loanRate,
                            repayMethod, repayFeeRate, selectManagerNum, auctionHouseNum, companyComment,
                            companyLogoUrl);
                    auctionListDataArrayList.add(auctionListData);
                }


                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(RegInfoDetail.this));
                AuctionRecyclerViewAdapter adapter = new AuctionRecyclerViewAdapter(auctionListDataArrayList);
//                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);

                LinearLayout ll_have_not_list = findViewById(R.id.ll_have_not_list);
                LinearLayout ll_have_list = findViewById(R.id.ll_have_list);

                switch (rValue) {
                    case "n":
                        ll_have_not_list.setVisibility(View.VISIBLE);
                        ll_have_list.setVisibility(View.GONE);
                        break;
                    case "y":
                        ll_have_not_list.setVisibility(View.GONE);
                        ll_have_list.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }

                ll_reg_detail_info.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);


//                pushTaskInit();

            } catch (JSONException e) {
                Log.e(TAG, "Could not parse JSON. Error: " + e.getMessage());
            }
        }
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    private void setText(String regNum, String regDate, String regStatus, String isCancled, String cancelReason, String regAddress, String josoDong,
                         String josoHo, String floor, String latestDealPrice, String latestDealDate, String latestDealSize, String landPrice, String regMemberName,
                         String regHopeLoanPrice, String regPreLoanPrice, String regRenterInfo, String auctionCompleteDate, String IsAPTData, String isPrevLoan,
                         String jobType, String platformRate) {

        TextView tv_reg_num = findViewById(R.id.tv_reg_num);
        TextView tv_reg_member_name = findViewById(R.id.tv_reg_member_name);
        TextView tv_reg_date = findViewById(R.id.tv_reg_date);
        TextView tv_type_first = findViewById(R.id.tv_type_first);
        TextView tv_type_second = findViewById(R.id.tv_type_second);

        TextView tv_reg_address = findViewById(R.id.tv_reg_address);
        TextView tv_latestDealPrice = findViewById(R.id.tv_latestDealPrice);
        TextView tv_landPrice = findViewById(R.id.tv_landPrice);

        TextView tv_reg_hope_loan_price = findViewById(R.id.tv_reg_hope_loan_price);
        TextView tv_reg_pre_loan_price = findViewById(R.id.tv_reg_pre_loan_price);
        TextView tv_reg_rent_info = findViewById(R.id.tv_reg_rent_info);
        TextView tv_remaining_time = findViewById(R.id.tv_remaining_time);
        LinearLayout ll_canceled_reason = findViewById(R.id.ll_canceled_reason);
        TextView tv_canceled_reason = findViewById(R.id.tv_canceled_reason);
        TextView tv_jobType = findViewById(R.id.tv_jobType);
        TextView tv_platformRate = findViewById(R.id.tv_platformRate);




        tv_reg_num.setText(regNum);
        tv_reg_date.setText(regDate);

        if (IsAPTData.equals("y")) {
            tv_type_first.setBackgroundDrawable(getResources().getDrawable(R.drawable.type_a));
            tv_type_first.setText("A");
        } else {
            tv_type_first.setBackgroundDrawable(getResources().getDrawable(R.drawable.type_b));
            tv_type_first.setText("B");
        }

        if (isPrevLoan.equals("y")) tv_type_second.setText("2");
        else tv_type_second.setText("1");

        tv_reg_address.setText(Util.changeTagToText(regAddress + ", " + josoDong + "동 " + josoHo + "호 " + floor + "층"));
        if (latestDealPrice.equals("0")) {
            tv_latestDealPrice.setText("-");
        } else {
            tv_latestDealPrice.setText(Util.GetNumFormat(Integer.parseInt(latestDealPrice)) + " (" + latestDealDate + ")" + "\n전용 " + latestDealSize + " m2 기준");
        }

        tv_landPrice.setText(Util.GetNumFormatWon(Integer.parseInt(landPrice)) + " 원");
        tv_reg_member_name.setText(regMemberName);
        tv_reg_hope_loan_price.setText(Util.GetNumFormat(Integer.parseInt(regHopeLoanPrice)));
        tv_jobType.setText(Util.setJobType(jobType));
        tv_platformRate.setText(platformRate + "%");


        FrameLayout fl_status_wrap = findViewById(R.id.fl_status_wrap);
        TextView tv_status_bidding = findViewById(R.id.tv_status_bidding);
        TextView tv_status_select_auction = findViewById(R.id.tv_status_select_auction);
        TextView tv_status_receipt_paper = findViewById(R.id.tv_status_receipt_paper);
        TextView tv_status_check_paper = findViewById(R.id.tv_status_check_paper);
        TextView tv_status_agree_loan = findViewById(R.id.tv_status_agree_loan);
        TextView tv_status_complete = findViewById(R.id.tv_status_complete);

        TextView tv_bidding_explain = findViewById(R.id.tv_bidding_explain);

        ImageView iv_status_circle_bidding = findViewById(R.id.iv_status_circle_bidding);
        ImageView iv_status_circle_select_auction = findViewById(R.id.iv_status_circle_select_auction);
        ImageView iv_status_circle_receipt_paper = findViewById(R.id.iv_status_circle_receipt_paper);
        ImageView iv_status_circle_check_paper = findViewById(R.id.iv_status_circle_check_paper);
        ImageView iv_status_circle_agree_loan = findViewById(R.id.iv_status_circle_agree_loan);
        ImageView iv_status_circle_complete = findViewById(R.id.iv_status_circle_complete);


        switch (regStatus) {
            case "등록":
                setTextRegStatus(tv_remaining_time, regStatus);
                tv_remaining_time.setTextColor(getApplicationContext().getResources().getColor(R.color.pion_basic_soft_blue));
                fl_status_wrap.setVisibility(View.GONE);
                ll_canceled_reason.setVisibility(View.VISIBLE);
                tv_canceled_reason.setText(auctionCompleteDate);
                break;
            case "입찰":
                statusSetting(tv_status_bidding, iv_status_circle_bidding);
                setTextRegStatus(tv_remaining_time, auctionCompleteDate);
                tv_bidding_explain.setVisibility(View.VISIBLE);
                break;
            case "낙찰":
                statusSetting(tv_status_select_auction, iv_status_circle_select_auction);
                break;
            case "서류접수중":
                statusSetting(tv_status_receipt_paper, iv_status_circle_receipt_paper);
                break;
            case "심사중":
                statusSetting(tv_status_check_paper, iv_status_circle_check_paper);
                break;
            case "승인완료":
                statusSetting(tv_status_agree_loan, iv_status_circle_agree_loan);
                break;
            case "대출완료":
                statusSetting(tv_status_complete, iv_status_circle_complete);
                break;
            case "유찰":
                setTextRegStatus(tv_remaining_time, regStatus);
                tv_bidding_explain.setVisibility(View.VISIBLE);
                tv_bidding_explain.setText("입찰 기간 동안 고객이 낙찰하지 않아 유찰되었습니다.");
                ll_canceled_reason.setVisibility(View.GONE);
                fl_status_wrap.setVisibility(View.GONE);
                break;
            case "취소됨":
                fl_status_wrap.setVisibility(View.GONE);
                setTextRegStatus(tv_remaining_time, regStatus);
                if (isCancled.equals("y")) {
                    ll_canceled_reason.setVisibility(View.VISIBLE);
                    tv_canceled_reason.setText(cancelReason);
                }
                break;
            default:
                tv_remaining_time.setVisibility(View.INVISIBLE);
                ll_canceled_reason.setVisibility(View.GONE);
                break;
        }

        if (regPreLoanPrice.length() > 0) tv_reg_pre_loan_price.setText(Util.GetNumFormat(Integer.parseInt(regPreLoanPrice)));
        else tv_reg_pre_loan_price.setText("-");

        if (regRenterInfo.length() > 0) tv_reg_rent_info.setText(Util.GetNumFormat(Integer.parseInt(regRenterInfo)));
        else tv_reg_rent_info.setText("-");
    }

    private void setTextRegStatus(TextView textView, String str) {
        textView.setVisibility(View.VISIBLE);
        textView.setText(str);
    }

    @SuppressLint("ResourceAsColor")
    private void statusSetting(TextView textView, ImageView imageView) {
        textView.setTextColor(getApplicationContext().getResources().getColor(R.color.pion_basic_soft_blue));
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        imageView.setVisibility(View.VISIBLE);

    }

    private void setAuctionHouseInfoFrag(String regStatus) {
        HaveNotAuctionHouse haveNotAuctionHouse = new HaveNotAuctionHouse();
        AuctionHouseList auctionHouseList = new AuctionHouseList();
        FragmentTransaction fragmentTransaction;
        switch (regStatus) {
            case "등록":
                fragmentTransaction = getSupportFragmentManager().beginTransaction().add(R.id.reg_info_detail_fragment_1, haveNotAuctionHouse);
                fragmentTransaction.commit();
                break;
            case "입찰":
            case "유찰":
                fragmentTransaction = getSupportFragmentManager().beginTransaction().add(R.id.reg_info_detail_fragment_2, auctionHouseList);
                fragmentTransaction.commit();
                break;
            default:
                fragmentTransaction = getSupportFragmentManager().beginTransaction().add(R.id.reg_info_detail_fragment_2, auctionHouseList);
                fragmentTransaction.commit();
                break;
        }
    }

    public static void scrollToView(View view, final NestedScrollView scrollView, int count) {
        if (view != null && view != scrollView) {
            count += view.getTop();
            scrollToView((View) view.getParent(), scrollView, count);
        } else if (scrollView != null) {
            final int finalCount = count;
            new Handler().postDelayed(() -> scrollView.smoothScrollTo(0, finalCount), 300);
        }
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause: " );
        super.onPause();
        progressBar.setVisibility(View.GONE);
        RegAuctionListTaskKill();
        RegistrationInfo_Detail_TaskKill();
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy: " );
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        Log.e(TAG, "onResume: " );
        super.onResume();
    }

    private void RegAuctionListTaskKill() {
        if (regAuctionListTask != null && regAuctionListTask.getStatus() != RegAuctionListTask.Status.FINISHED) {
            regAuctionListTask.cancel(true);
            regAuctionListTask = null;
            Log.e("Front task kill", "");
        }
    }

    private void RegistrationInfo_Detail_TaskKill() {
        if (registrationInfo_detail_task != null && registrationInfo_detail_task.getStatus() != RegistrationInfo_Detail_Task.Status.FINISHED) {
            registrationInfo_detail_task.cancel(true);
            registrationInfo_detail_task = null;
            Log.e("Front task kill", "");
        }
    }
}

class AuctionListData {
    private String rValue;
    private String comName;
    private String loanPrice;
    private String loanRate;
    private String repayMethod;
    private String repayFeeRate;
    private String selectManagerNum;
    private String auctionHouseNum;
    private String companyComment;
    private String companyLogoUrl;

    public AuctionListData() {
    }

    public AuctionListData(String rValue, String comName, String loanPrice, String loanRate, String repayMethod,
                           String repayFeeRate, String selectManagerNum, String auctionHouseNum, String companyComment, String companyLogoUrl) {
        this.rValue = rValue;
        this.comName = comName;
        this.loanPrice = loanPrice;
        this.loanRate = loanRate;
        this.repayMethod = repayMethod;
        this.repayFeeRate = repayFeeRate;
        this.selectManagerNum = selectManagerNum;
        this.auctionHouseNum = auctionHouseNum;
        this.companyComment = companyComment;
        this.companyLogoUrl = companyLogoUrl;
    }

    public String getrValue() {
        return rValue;
    }

    public void setrValue(String rValue) {
        this.rValue = rValue;
    }

    public String getComName() {
        return comName;
    }

    public String getLoanPrice() {
        return loanPrice;
    }

    public String getLoanRate() {
        return loanRate;
    }

    public String getRepayMethod() {
        return repayMethod;
    }

    public String getRepayFeeRate() {
        return repayFeeRate;
    }

    public String getSelectManagerNum() {
        return selectManagerNum;
    }

    public String getCompanyComment() {
        return companyComment;
    }

    public String getCompanyLogoUrl() {
        return companyLogoUrl;
    }

}
