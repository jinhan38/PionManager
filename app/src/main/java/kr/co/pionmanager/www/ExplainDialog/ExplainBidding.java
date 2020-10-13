package kr.co.pionmanager.www.ExplainDialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import kr.co.pionmanager.www.R;

public class ExplainBidding extends Dialog {
    private TextView explain_text_pion;
    private Button btn_confirm;
    private View.OnClickListener positiveListener;

    public ExplainBidding(@NonNull Context context, View.OnClickListener positiveListener) {
        super(context);
        this.positiveListener = positiveListener;
    }



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.explain_bidding);

        initView();
        setUpListener();


//        Util.setTextColor(this, R.string.registrationIntroduce_2, explain_text_pion, "#56A2F4", 0, 4);
//        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();

//        int width = (int) (dm.widthPixels * 0.95); // Display 사이즈의 90%
//        int height = (int) (dm.heightPixels * 0.9); // Display 사이즈의 90%
//        getWindow().getAttributes().width = width;
//        getWindow().getAttributes().height = height;

    }


    private void initView() {
        explain_text_pion = findViewById(R.id.explain_text_pion);
        btn_confirm = findViewById(R.id.btn_confirm);
    }


    private void setUpListener(){
        btn_confirm.setOnClickListener(positiveListener);
    }

}
