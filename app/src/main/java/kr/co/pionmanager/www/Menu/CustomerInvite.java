package kr.co.pionmanager.www.Menu;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import kr.co.pionmanager.www.OnSingleClickListener;
import kr.co.pionmanager.www.R;
import kr.co.pionmanager.www.UserInfo;
import kr.co.pionmanager.www.Util;

public class CustomerInvite extends AppCompatActivity {
    private ImageButton ibtn_back;
    private ImageButton icon_copy;
    private ImageButton icon_kakao;
    private TextView tv_link_address;
    private String link_address;
    private String qr_address;
    private ImageView iv_qr;
    private LinearLayout ll_link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_customer);
        Util.SaveCurrentContext(this);

        initView();
        setUpListener();
        ibtn_back = findViewById(R.id.ibtn_back);
        ibtn_back.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                onBackPressed();
            }
        });

        Log.e("link Address", Util.link_address + UserInfo.getUserNum());
        iv_qr = findViewById(R.id.iv_qr);
        qr_address = Util.link_qr + UserInfo.getUserNum();

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(qr_address, BarcodeFormat.QR_CODE, 800, 800);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            iv_qr.setImageBitmap(bitmap);
        } catch (Exception e) {
        }


    }

    private void initView() {
        ll_link = findViewById(R.id.ll_link);
        icon_copy = findViewById(R.id.icon_copy);
        icon_kakao = findViewById(R.id.icon_kakao);
        tv_link_address = findViewById(R.id.tv_link_address);
        link_address = Util.link_address + UserInfo.getUserNum();
        tv_link_address.setText(link_address);

    }

    private void setUpListener() {

        icon_copy.setOnClickListener(v -> clipBoard());
        icon_kakao.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Util.kakaolinkCustomer(Util.CURRENT_CONTEXT);
            }
        });

        tv_link_address.setOnClickListener(v -> clipBoard());
    }

    private void clipBoard() {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("ID", link_address); //클립보드에 ID라는 이름표로 id 값을 복사하여 저장
        clipboardManager.setPrimaryClip(clipData);
        Snackbar.make(ll_link, "링크가 복사되었습니다.", Snackbar.LENGTH_SHORT).show();
//        Util.showToast("복사되었습니다.");
    }
}
