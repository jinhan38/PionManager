package kr.co.pionmanager.www.Menu.Payment;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import kr.co.pionmanager.www.R;
import kr.co.pionmanager.www.UserInfo;


public class MenuCommissionExplanation extends AppCompatActivity {
    private TextView top_back_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_commission_explanation);

        ImageButton ibtn_back = findViewById(R.id.ibtn_back);
        ibtn_back.setOnClickListener(v -> onBackPressed());

        top_back_text = findViewById(R.id.top_back_text);

        setAuctionHouseInfoFrag();

    }

    private void setAuctionHouseInfoFrag() {

        ManagerPayment managerPayment = new ManagerPayment();
        BizManagerPayment bizManagerPayment = new BizManagerPayment();
        BizPartnerPayment bizPartnerPayment = new BizPartnerPayment();
        FragmentTransaction fragmentTransaction;
        if (UserInfo.getUserGrade() == 1) {
            top_back_text.setText(R.string.commision_for_manager);
            fragmentTransaction = getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, managerPayment);
            fragmentTransaction.commit();

        } else if (UserInfo.getUserGrade() == 2) {
            top_back_text.setText(R.string.commision_for_biz_manager);
            fragmentTransaction = getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, bizManagerPayment);
            fragmentTransaction.commit();

        } else if (UserInfo.getUserGrade() == 3) {
            top_back_text.setText(R.string.commision_for_biz_partner);
            fragmentTransaction = getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, bizPartnerPayment);
            fragmentTransaction.commit();
        }

    }
}
