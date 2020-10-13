package kr.co.pionmanager.www.Menu;

import android.os.Bundle;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import kr.co.pionmanager.www.OnSingleClickListener;
import kr.co.pionmanager.www.R;
import kr.co.pionmanager.www.UserInfo;
import kr.co.pionmanager.www.Util;
import kr.co.pionmanager.www.databinding.ActivityInviteKaKaoBinding;

public class InviteKaKao extends AppCompatActivity {
    private ActivityInviteKaKaoBinding binding;
    public static InviteKaKao inviteKaKao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inviteKaKao = this;
        binding = DataBindingUtil.setContentView(this, R.layout.activity_invite_ka_kao);

        binding.progressBarKakao.setVisibility(View.GONE);
        binding.btnKakaoYellowBox.setVisibility(View.GONE);

        binding.ibtnBack.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                onBackPressed();
            }
        });

        if (UserInfo.getUserGrade() == 1 && UserInfo.getRefManagerGrade().equals("3")) {
            binding.teamMemberIntroduce1.setText(R.string.bizPartnerMember_invite);
            binding.teamMemberIntroduce2.setVisibility(View.GONE);
            binding.tvDot2.setVisibility(View.GONE);
        } else if (UserInfo.getUserGrade() == 1) {
            binding.teamMemberIntroduce1.setText(R.string.invite_manager_by_manager_1);
            binding.teamMemberIntroduce2.setText(R.string.invite_manager_by_manager_2);
            Spannable spannable = (Spannable) binding.teamMemberIntroduce1.getText();
            spannable.setSpan(new RelativeSizeSpan(1.5f), 45, 49, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.pion_basic_soft_blue)), 45, 49, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

            if (UserInfo.getRefManagerGrade().equals("2")){
                binding.llEvent.setVisibility(View.GONE);
            }

        } else if (UserInfo.getUserGrade() == 2) {

            Util.setTextColor(Util.CURRENT_CONTEXT, R.string.teamMemberIntroduce_1, binding.teamMemberIntroduce1, "#56A2F4", 0, 2);
            Util.setTextColor(Util.CURRENT_CONTEXT, R.string.teamMemberIntroduce_3, binding.teamMemberIntroduce2, "#56A2F4", 0, 6);

        } else if (UserInfo.getUserGrade() == 3){
            binding.teamMemberIntroduce1.setText(R.string.partnerMemberIntroduce_1);
            Util.setTextColor(Util.CURRENT_CONTEXT, R.string.partnerMemberIntroduce_1, binding.teamMemberIntroduce1, "#56A2F4", 0, 2);
            binding.teamMemberIntroduce2.setText(R.string.invite_manager_by_manager_3);
        }


        binding.btnMemberInvite.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                binding.progressBarKakao.setVisibility(View.VISIBLE);
                binding.btnKakaoYellowBox.setVisibility(View.VISIBLE);
                if (UserInfo.getUserGrade() == 1 && UserInfo.getRefManagerGrade().equals("3")) Util.kakaolink(getApplicationContext(), UserInfo.getRefMemberNum());
                else Util.kakaolink(getApplicationContext(), UserInfo.getUserNum());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.progressBarKakao.setVisibility(View.GONE);
        binding.btnKakaoYellowBox.setVisibility(View.GONE);
    }

}
