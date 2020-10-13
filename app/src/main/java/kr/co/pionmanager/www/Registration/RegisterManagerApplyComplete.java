package kr.co.pionmanager.www.Registration;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import kr.co.pionmanager.www.LoginPage;
import kr.co.pionmanager.www.OnSingleClickListener;
import kr.co.pionmanager.www.R;
import kr.co.pionmanager.www.UserInfo;
import kr.co.pionmanager.www.Util;

public class RegisterManagerApplyComplete extends AppCompatActivity {

    private Button bt_next;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_manager_apply_complete);
        Util.SaveCurrentContext(this);
        Util.SetTagName("registerComplete");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        bt_next = findViewById(R.id.btNext);

        bt_next.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                LoginPage.completeLicenseApply = true;
                Intent intent = new Intent(getApplicationContext(), LoginPage.class);
                intent.putExtra("id", UserInfo.getUserID());
                intent.putExtra("pw", UserInfo.getPwNotSecret());
                startActivity(intent);
                finish();
            }
        });
    }


}
