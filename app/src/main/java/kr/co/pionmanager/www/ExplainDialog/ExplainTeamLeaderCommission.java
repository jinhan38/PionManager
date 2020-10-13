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


public class ExplainTeamLeaderCommission extends Dialog {
    private static final String TAG = "ExplainTeamLeaderCommis";
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


    public ExplainTeamLeaderCommission(@NonNull Context context, View.OnClickListener positiveListener) {
        super(context);
        this.positiveListener = positiveListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explain_teamleader_commission);

        initView();
        setUpListener();


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
        popupSetText();

    }

    private void setUpListener(){
        btn_confirm.setOnClickListener(positiveListener);
    }

    private void popupSetText(){
        Log.e(TAG, "popupSetText: " + MypageAboutData.mypageAboutData.bAorB );
        AorB.setText(MypageAboutData.mypageAboutData.bAorB);
        text1.setText(MypageAboutData.mypageAboutData.btext_1);
        text2.setText(MypageAboutData.mypageAboutData.btext_2);
        text3.setText(MypageAboutData.mypageAboutData.btext_3);
        text4.setText(MypageAboutData.mypageAboutData.btext_4);
        sumPrice.setText(MypageAboutData.mypageAboutData.bsumPrice);
        text5.setText(MypageAboutData.mypageAboutData.btext_5);
        text6.setText(MypageAboutData.mypageAboutData.btext_6);
        text7.setText(MypageAboutData.mypageAboutData.btext_7);
        text8.setText(MypageAboutData.mypageAboutData.btext_8);
        sumPayment.setText(MypageAboutData.mypageAboutData.bsumPayment);
        platCommission.setText(MypageAboutData.mypageAboutData.bplatCommission);
        finalPayment.setText(MypageAboutData.mypageAboutData.bfinalPayment);
        Spannable spannable = (Spannable) finalPayment.getText();
        spannable.setSpan(new ForegroundColorSpan(getContext().getResources().getColor(R.color.my_red_light)), 15, 16, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
    }

}
