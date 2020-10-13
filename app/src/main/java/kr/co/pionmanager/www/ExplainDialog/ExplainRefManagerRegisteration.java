package kr.co.pionmanager.www.ExplainDialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import kr.co.pionmanager.www.R;


public class ExplainRefManagerRegisteration extends Dialog {
    private View.OnClickListener positiveListener;
    private View.OnClickListener negativeListener;
    private Button btn_confirm;
    private Button btn_cancel;

    public ExplainRefManagerRegisteration(@NonNull Context context, View.OnClickListener positiveListener, View.OnClickListener negativeListener) {
        super(context);
        this.positiveListener = positiveListener;
        this.negativeListener = negativeListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explain_ref_manager_reg);

        initView();
        setUpListener();

    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        btn_confirm = findViewById(R.id.btn_confirm);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_confirm.setOnClickListener(positiveListener);
        btn_cancel.setOnClickListener(negativeListener);
    }

    private void setUpListener(){

    }

}
