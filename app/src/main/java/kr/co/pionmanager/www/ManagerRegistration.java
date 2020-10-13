package kr.co.pionmanager.www;

import android.annotation.SuppressLint;
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

import kr.co.pionmanager.www.Registration.GalleryCamera;
import kr.co.pionmanager.www.databinding.ActivityManagerRegistrationBinding;

public class ManagerRegistration extends Fragment {

    int pStatus = 0;
    private Handler handler = new Handler();

    @SuppressLint("ResourceAsColor")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ActivityManagerRegistrationBinding binding = DataBindingUtil.inflate(inflater, R.layout.activity_manager_registration, container, false);

        Util.SaveCurrentContext(getActivity());
        Util.SetTagName("ManagerRegistration");
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


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

        binding.btManagerRegistration.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Util.intentNext(GalleryCamera.class);
//                getActivity().finish();
            }
        });

        return binding.getRoot();
    }


}
