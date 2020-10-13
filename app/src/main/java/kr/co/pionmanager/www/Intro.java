package kr.co.pionmanager.www;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;

import kr.co.pionmanager.www.PionIntroduce.IntroduceParent;

public class Intro extends AppCompatActivity {

    private CheckBox ch_forever_close;
    private Button bt_close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        Util.SaveCurrentContext(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        ch_forever_close = findViewById(R.id.ch_forever_close);
        bt_close = findViewById(R.id.bt_close);

        bt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreference.getSharedPreferences(Intro.this);
                if (ch_forever_close.isChecked()) {
                    SharedPreference.setCloseChecked(Intro.this, true);
                } else {
                    SharedPreference.setCloseChecked(Intro.this, false);
                }
                bt_close.setEnabled(false);

                Util.intentNext(IntroduceParent.class);
                finish();
            }
        });

    }


}
