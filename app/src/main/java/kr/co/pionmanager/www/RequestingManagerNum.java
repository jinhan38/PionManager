package kr.co.pionmanager.www;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import kr.co.pionmanager.www.databinding.ActivityRequestingManagerNumBinding;

public class RequestingManagerNum extends Fragment {

    private static final String TAG = "RequestingManagerNum";
    int pStatus = 0;
    private Handler handler = new Handler();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ActivityRequestingManagerNumBinding binding = DataBindingUtil.inflate(inflater, R.layout.activity_requesting_manager_num, container, false);

        Util.SaveCurrentContext(getActivity());
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        SharedPreference.getSharedPreferences(getActivity());
        SharedPreference.setRequestingLicense(getActivity());
        binding.logout.setOnClickListener(v -> {
            UserInfo.LogOut();
            Intent intent = new Intent(getActivity(), LoginPage.class);
            startActivity(intent);
        });

        new Thread(() -> {
            // TODO Auto-generated method stub
            while (pStatus < 100) {
                pStatus += 1;
                if (pStatus == 100){
                    binding.tv24.setTextColor(Util.CURRENT_CONTEXT.getResources().getColor(R.color.yellow));
                }
                handler.post(() -> {
                    // TODO Auto-generated method stub
                    binding.circularProgressbar.setProgress(pStatus);
                });
                try {
                    Thread.sleep(15); //thread will take approx 1.5 seconds to finish
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }).start();

        return binding.getRoot();
    }




}
