package kr.co.pionmanager.www.ExplainDialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import kr.co.pionmanager.www.Menu.MypageAboutData;
import kr.co.pionmanager.www.R;
import kr.co.pionmanager.www.UserInfo;

public class ExplainManagerCommission extends Dialog {
    private static final String TAG = "ExplainManagerCommissio";
    private TextView AorB;
    private TextView text1;
    private TextView text2;
    private TextView text3;
    private TextView text4;
    private TextView sumPrice;
    private TextView text5;
    private TextView text6;
    private TextView text7;
    private TextView text8;
    private TextView sumPayment;
    private TextView platCommission;
    private TextView finalPayment;
    private Button btn_confirm;
    private View.OnClickListener positiveListener;


    public ExplainManagerCommission(@NonNull Context context, View.OnClickListener positiveListener) {
        super(context);
        this.positiveListener = positiveListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explain_manager_commission);


        initView();
        setUpListener();

        popupSetText();
    }


    @SuppressLint("SetTextI18n")
    private void initView() {
        btn_confirm = findViewById(R.id.btn_confirm);
        AorB = findViewById(R.id.AorB);
        text1 = findViewById(R.id.text1);
        text2 = findViewById(R.id.text2);
        text3 = findViewById(R.id.text3);
        text4 = findViewById(R.id.text4);
        sumPrice = findViewById(R.id.sumPrice);
        text5 = findViewById(R.id.text5);
        text6 = findViewById(R.id.text6);
        text7 = findViewById(R.id.text7);
        text8 = findViewById(R.id.text8);
        sumPayment = findViewById(R.id.sumPayment);
        platCommission = findViewById(R.id.platCommission);
        finalPayment = findViewById(R.id.finalPayment);

    }

    private void setUpListener() {
        btn_confirm.setOnClickListener(positiveListener);
    }

    private void popupSetText() {
        Log.e(TAG, "popupSetText: " + MypageAboutData.mypageAboutData.AorB);

        if (UserInfo.getUserGrade() == 2){
            TextView tvTitle = findViewById(R.id.tvTitle);
            TextView text = findViewById(R.id.text);
            tvTitle.setText("비즈매니저 지급 수당");
            text.setText("*이번달 비즈매니저 지급 수당 계산");
        }
        AorB.setText(MypageAboutData.mypageAboutData.AorB);
        text1.setText(MypageAboutData.mypageAboutData.text1);
        text2.setText(MypageAboutData.mypageAboutData.text2);
        text3.setText(MypageAboutData.mypageAboutData.text3);
        text4.setText(MypageAboutData.mypageAboutData.text4);
        sumPrice.setText(MypageAboutData.mypageAboutData.sumPrice);
        text5.setText(MypageAboutData.mypageAboutData.text5);
        text6.setText(MypageAboutData.mypageAboutData.text6);
        text7.setText(MypageAboutData.mypageAboutData.text7);
        text8.setText(MypageAboutData.mypageAboutData.text8);
        sumPayment.setText(MypageAboutData.mypageAboutData.sumPayment);
        platCommission.setText(MypageAboutData.mypageAboutData.platCommission);
        finalPayment.setText(MypageAboutData.mypageAboutData.finalPayment);
        Spannable spannable = (Spannable) finalPayment.getText();
        spannable.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.my_red_light)), 15, 16, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
    }

}
