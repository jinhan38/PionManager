package kr.co.pionmanager.www;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import kr.co.pionmanager.www.Menu.MessageBox;
import kr.co.pionmanager.www.Menu.MypageAboutData;
import kr.co.pionmanager.www.Menu.Setting.Setting;

public class BottomNaviWrap extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG = "BottomNaviWrap";

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentTransaction transaction;
    public BottomNavigationView navView;
    private GetPushSetting getPushSetting;
    private ImageButton setting;
    private ImageButton message;
    private ImageButton btLogout;
    private CustomDialog customDialog;
    public static BottomNaviWrap bottomNaviWrap;
    private ConstraintLayout toolbar;
    private long backBtnTime = 0;
    public MyViewPager vpPager;
    public int prevPageNum = 0;
    private FrameLayout nav_host_fragment_license;
    private PagerAdapter adapterViewPager;
    private ImageView appBarLogo;
    private TextView appBarTextPION;
    private NetworkCheck networkCheck;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navi_wrap);
        bottomNaviWrap = this;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initView();
        setupListener();
        haveNotLicense();
        isGradeZero();
        getToken();

        String strVersion = getString(R.string.version).replaceAll("ver", "").trim();
        if (UserInfo.getUserGrade() > 0) {
            String strUrl = Util.thepion_URL + "AjaxControl/GetPushSetting?userNum=" + UserInfo.getUserNum() + "&strVersion=" + strVersion;
            getPushSetting = (GetPushSetting) new GetPushSetting().execute(strUrl);
        }

    }


    private void initView() {

        networkCheck = new NetworkCheck(this);
        networkCheck.register();
        Util.SaveCurrentContext(bottomNaviWrap);
        setting = findViewById(R.id.setting);
        message = findViewById(R.id.message);
        btLogout = findViewById(R.id.btLogout);
        transaction = fragmentManager.beginTransaction();
        navView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        appBarLogo = findViewById(R.id.appBarLogo);
        appBarTextPION = findViewById(R.id.appBarTextPION);
        vpPager = findViewById(R.id.nav_host_fragment);
        vpPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        vpPager.setOffscreenPageLimit(3);

        nav_host_fragment_license = findViewById(R.id.nav_host_fragment_license);
    }

    private void setupListener() {
        setting.setOnClickListener(bottomNaviWrap);
        message.setOnClickListener(bottomNaviWrap);
        btLogout.setOnClickListener(bottomNaviWrap);
        appBarLogo.setOnClickListener(bottomNaviWrap);
        appBarTextPION.setOnClickListener(bottomNaviWrap);

        navView.setOnNavigationItemSelectedListener(menuItem -> {
            transaction = fragmentManager.beginTransaction();
            switch (menuItem.getItemId()) {
                case R.id.navigation_data:
                    showToolbarIcon(false);
                    prevPageNum = 0;
                    vpPager.setCurrentItem(0);
                    break;
                case R.id.navigation_registration:
                    showToolbarIcon(false);
                    prevPageNum = 1;
                    vpPager.setCurrentItem(1);
                    break;
                case R.id.navigation_list:
                    showToolbarIcon(false);
                    prevPageNum = 2;
                    vpPager.setCurrentItem(2);
                    break;
                case R.id.navigation_menuCollection:
                    showToolbarIcon(true);
                    prevPageNum = 3;
                    vpPager.setCurrentItem(3);
                    break;
            }
            return true;
        });

        vpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                navView.getMenu().getItem(position).setChecked(true);
                if (position == 3) {
                    showToolbarIcon(true);
                } else {
                    showToolbarIcon(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }

    public void showToolbarIcon(boolean show) {
        if (show) {
            setting.setVisibility(View.VISIBLE);
            message.setVisibility(View.VISIBLE);
            btLogout.setVisibility(View.VISIBLE);
        } else {
            setting.setVisibility(View.GONE);
            message.setVisibility(View.GONE);
            btLogout.setVisibility(View.GONE);
        }
    }

    /**
     * 면허 발급에 안됐을 떼 매니저 라이선스 요청 페이지로 넘기기
     * bottom menu gone 시키기
     *
     * @param n
     */
    public void setFrag(int n) {
        RequestingManagerNum requestingManagerNum = new RequestingManagerNum();
        ManagerRegistration managerRegistration = new ManagerRegistration();
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        switch (n) {
            case 0:
                //발급신청 완료하고 등급업 기다리는 중
                transaction.replace(R.id.nav_host_fragment_license, requestingManagerNum);
                transaction.commit();
                nav_host_fragment_license.setVisibility(View.VISIBLE);
                break;
            case 1:
                //매니저 라이센스 신청 안한 상태
                transaction.replace(R.id.nav_host_fragment_license, managerRegistration);
                transaction.commit();
                nav_host_fragment_license.setVisibility(View.VISIBLE);
                break;
//            default:
//                Util.intentNext(LoginPage.class);
//                break;
        }
    }

    /**
     * 라이선스 없을 때 호출하는 함수
     */
    private void haveNotLicense() {
        if (UserInfo.getHasCardImage().equals("n")) {
            setFrag(1);
            toolbar.setVisibility(View.GONE);
            navView.setVisibility(View.GONE);
        } else if (UserInfo.getUserBankNum().length() == 0) {
            setFrag(1);
            toolbar.setVisibility(View.GONE);
            navView.setVisibility(View.GONE);
        } else if (UserInfo.getUserGrade() == 0) {  //등급이 0이고, 라이선스 발급은 신청한 경우(신분증이랑  통장사본 제출한 경우)
            setFrag(0);
            navView.setVisibility(View.GONE);
            toolbar.setVisibility(View.GONE);
        }
    }

    /**
     * grade가 0일 때 자동로그인 설정해도 다시 시작하면 자동로그인 안됨
     * 라이선스 신청 페이지에서는 로그아웃이 없기 때문에 만들었음
     */
    private void isGradeZero() {
        if (UserInfo.getUserGrade() == 0) {
            if (SharedPreference.getCloseChecked(bottomNaviWrap)) {
                SharedPreference.clearAll(bottomNaviWrap);
                SharedPreference.getSharedPreferences(bottomNaviWrap);
                SharedPreference.setCloseChecked(bottomNaviWrap, true);
            }
        }
    }

    private void getToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(bottomNaviWrap, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                Util.TOKEN = newToken;
                Log.e("Util.Token", Util.TOKEN);


                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                String url = Util.thepion_URL + "ajaxcontrol/Login_cert?id=" + UserInfo.getUserID()
                        + "&pw=" + UserInfo.getUserPassword() + "&autoLogin=" + UserInfo.getAutoLogin() + "&token=" + Util.TOKEN + "&needEncrypt=y";

                //json형태로 받음
                final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {

                    Log.e(TAG, "onSuccess: volley task 성공");

                }, error -> {

                    Log.e(TAG, "onErrorResponse: 토큰 전송 실패 : " + error);

                });

                request.setTag(TAG);
                queue.add(request);

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting:
                Util.intentNext(Setting.class);
                break;
            case R.id.message:
                Util.intentNext(MessageBox.class);
                break;
            case R.id.btLogout:
                ShowDialog();
                break;
            case R.id.appBarLogo:
            case R.id.appBarTextPION:
                pageRemove();
                pageAttach();
                break;
        }
    }


    private void ShowDialog() {
        String str = "로그아웃 하시겠습니까?";
        customDialog = new CustomDialog(this, positiveListener_logout, negativeListener, str);
        Objects.requireNonNull(customDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        customDialog.show();
    }

    private View.OnClickListener positiveListener_logout = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            customDialog.dismiss();
            pageRemove();
            UserInfo.LogOut();
            String autoLogin = "n";
            UserInfo.setAutoLogin(autoLogin);
            vpPager = null;
            SharedPreference.getSharedPreferences(bottomNaviWrap);
            SharedPreference.setAutoLogin(bottomNaviWrap, autoLogin);
            Util.intentNext(LoginPage.class);
            finish();

        }
    };

    private View.OnClickListener negativeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            customDialog.dismiss();
        }
    };

    @SuppressLint("StaticFieldLeak")
    private class GetPushSetting extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "다운로드 실패" + e.toString();
            }
        }

        protected void onPostExecute(String result) {
            Log.e(TAG, result);
            getResult(result);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        private String downloadUrl(String myurl) throws IOException {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(myurl);
                Log.e("url", myurl);
                conn = (HttpURLConnection) url.openConnection();
                BufferedInputStream buf = new BufferedInputStream(conn.getInputStream());
                BufferedReader bufreader = new BufferedReader(new InputStreamReader(buf, StandardCharsets.UTF_8));
                String line;
                StringBuilder page = new StringBuilder();
                while ((line = bufreader.readLine()) != null) {
                    page.append(line);
                }
                return page.toString();
            } finally {
                assert conn != null;
                conn.disconnect();
            }
        }

        private void getResult(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                String pushAll = jsonObject.getString("pushAll");
                String pushAuction = jsonObject.getString("pushAuction");
                String pushNotice = jsonObject.getString("pushNotice");
                String pushMarketing = jsonObject.getString("pushMarketing");

                UserInfo.setPushAll(pushAll);
                UserInfo.setPushAuction(pushAuction);
                UserInfo.setPushNotice(pushNotice);
                UserInfo.setPushMarketing(pushMarketing);

            } catch (JSONException e) {
                Log.e("TAG", "Could not parse JSON. Error: " + e.getMessage());
            }
        }

    }

    @Override
    public void onBackPressed() {

        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        if (fragmentList != null) {
            for (Fragment fragment : fragmentList) {
                if (fragment instanceof BackButton) {
                    ((BackButton) fragment).onBackPressed();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        getPushSettingKill();
        super.onPause();
    }


    private void getPushSettingKill() {
        if (getPushSetting != null && getPushSetting.getStatus() != GetPushSetting.Status.FINISHED) {
            getPushSetting.cancel(true);
            getPushSetting = null;
        }
    }

    private void pageRemove() {
        vpPager = null;
        adapterViewPager = null;
        MypageAboutData.mypageAboutData.clearSingleTon();
        AddAddressPage.addAddressPage.clearSingleTon();
        AddAddressPage.addAddressPage.clearSingleTon();
        RegistrationListPage.registrationListPage.clearSingleTon();
        MenuCollection.menuCollection.clearSingleTon();
        MypageAboutData.mypageAboutData.mypageGetDataKill();
        MypageAboutData.mypageAboutData.mypageGetComMemberDataKill();
        RegistrationListPage.registrationListPage.registrationInfo_all_taskKill();

    }

    private void pageAttach() {
        vpPager = findViewById(R.id.nav_host_fragment);
        vpPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        vpPager.setOffscreenPageLimit(3);
        navView.getMenu().getItem(0).setChecked(true);
    }

    @Override
    protected void onDestroy() {
        networkCheck.unregister();
        super.onDestroy();
    }

}

