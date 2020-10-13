package kr.co.pionmanager.www.Menu.Payment;

import android.os.Bundle;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import kr.co.pionmanager.www.R;
import kr.co.pionmanager.www.SharedPreference;
import kr.co.pionmanager.www.UserInfo;
import kr.co.pionmanager.www.databinding.ActivityManagerPaymentBinding;

public class ManagerPayment extends Fragment {
    private static final String TAG = "ManagerPayment";
    private ActivityManagerPaymentBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_manager_payment, container, false);
        changeText();

        return binding.getRoot();
    }


    private void changeText() {

        Log.e(TAG, "changeText: 추천인 수 : " + UserInfo.getRefManagerCount() );
        Log.e(TAG, "changeText: 추천인 등급 : " + SharedPreference.getRefManagerGrade(getActivity()));
        if (Integer.parseInt(UserInfo.getRefManagerCount()) >= 10 || UserInfo.getRefManagerGrade().equals("2")) {
            binding.tvA2.setText("1.4%");
            binding.tvA1.setText("0.9%");
            binding.tvB2.setText("0.8%");
            binding.tvB1.setText("0.5%");
        }

        Spannable span = (Spannable) binding.tvTopExplain.getText();
        span.setSpan(new RelativeSizeSpan(1.2f), 2, 13, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        span.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.pion_basic_soft_blue)), 2, 13, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        if (UserInfo.getRefManagerGrade().equals("2") && UserInfo.getUserGrade() == 1) {
            Log.e(TAG, "changeText: 비즈매니저 추천" );
            binding.PlusManagerPay.setVisibility(View.VISIBLE);
            Spannable spannable = (Spannable) binding.payUp.getText();
            spannable.setSpan(new RelativeSizeSpan(1.2f), 35, 39, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.pion_basic_soft_blue)), 35, 39, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        } else if (Integer.parseInt(UserInfo.getRefManagerCount()) >= 10) {
            Log.e(TAG, "changeText: 추천인 10명" );
            binding.PlusManagerPay.setVisibility(View.VISIBLE);
            binding.payUp.setText(R.string.missionPassed);
            Spannable spannable = (Spannable) binding.payUp.getText();
            spannable.setSpan(new RelativeSizeSpan(1.2f), 18, 22, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.pion_basic_soft_blue)), 18, 22, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
    }


}
