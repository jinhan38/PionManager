package kr.co.pionmanager.www.ExplainDialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import kr.co.pionmanager.www.R;

public class ExplainAuctionPick extends Dialog {
    private View.OnClickListener positiveListener;
    private Button btn_confirm;

    public ExplainAuctionPick(@NonNull Context context, View.OnClickListener positiveListener) {
        super(context);
        this.positiveListener = positiveListener;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_explain_auction_pick);

        initView();
        setUpListener();


    }

    private void initView() {
        btn_confirm = findViewById(R.id.btn_confirm);
    }

    private void setUpListener() {
        btn_confirm.setOnClickListener(positiveListener);
    }

}
