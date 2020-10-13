package kr.co.pionmanager.www.Menu.Payment;

import android.os.Bundle;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import kr.co.pionmanager.www.R;
import kr.co.pionmanager.www.databinding.ActivityBizPartnerPaymentBinding;

public class BizPartnerPayment extends Fragment {
    private static final String TAG = "ManagerPayment";
    private ActivityBizPartnerPaymentBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_biz_partner_payment, container, false);

        changeText();
        return binding.getRoot();
    }


    private void changeText() {
        Spannable span = (Spannable) binding.tvTopExplain.getText();
        span.setSpan(new RelativeSizeSpan(1.2f), 2, 13, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        span.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.pion_basic_soft_blue)), 2, 13, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

    }

}
