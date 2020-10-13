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
import kr.co.pionmanager.www.databinding.ActivityBizManagerPaymentBinding;

public class BizManagerPayment extends Fragment {
    private static final String TAG = "ManagerPayment";
    private ActivityBizManagerPaymentBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         binding = DataBindingUtil.inflate(inflater, R.layout.activity_biz_manager_payment, container, false);

        changeText();

        return binding.getRoot();
    }

    private void changeText(){
        Spannable span = (Spannable)binding.tvTopExplain.getText();
        span.setSpan(new RelativeSizeSpan(1.2f), 2, 13, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        span.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.pion_basic_soft_blue)), 2, 13, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        Spannable span2 = (Spannable)binding.tvMiddleExplain.getText();
        span2.setSpan(new RelativeSizeSpan(1.2f), 2, 13, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        span2.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.pion_basic_soft_blue)), 2, 13, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
    }


}
