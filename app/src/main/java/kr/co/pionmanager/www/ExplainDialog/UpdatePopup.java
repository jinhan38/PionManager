package kr.co.pionmanager.www.ExplainDialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import kr.co.pionmanager.www.Loading;
import kr.co.pionmanager.www.R;
import kr.co.pionmanager.www.Util;

public class UpdatePopup extends Dialog {
    private Button btn_playStore;
    private View.OnClickListener positiveListener;
    private long backBtnTime = 0;

    public UpdatePopup(@NonNull Context context, View.OnClickListener positiveListener) {
        super(context);
        this.positiveListener = positiveListener;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        long curTime = System.currentTimeMillis();
        long gapTime = curTime - backBtnTime;
        if (0 <= gapTime && 2000 >= gapTime) {
            Loading.loading.finish();
        } else {
            backBtnTime = curTime;
            Toast.makeText(Util.CURRENT_CONTEXT, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_popup);

        initView();
        setUpListener();
        setCancelable(false);


//        Util.setTextColor(this, R.string.registrationIntroduce_2, explain_text_pion, "#56A2F4", 0, 4);
//        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();

//        int width = (int) (dm.widthPixels * 0.95); // Display 사이즈의 90%
//        int height = (int) (dm.heightPixels * 0.9); // Display 사이즈의 90%
//        getWindow().getAttributes().width = width;
//        getWindow().getAttributes().height = height;


    }


    private void initView() {
        btn_playStore = findViewById(R.id.btn_playStore);
    }


    private void setUpListener(){
        btn_playStore.setOnClickListener(positiveListener);
    }

}
