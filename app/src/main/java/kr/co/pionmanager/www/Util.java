package kr.co.pionmanager.www;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.FeedTemplate;
import com.kakao.message.template.LinkObject;
import com.kakao.message.template.TemplateParams;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.util.helper.log.Logger;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Util extends AppCompatActivity {

    private static final String TAG = "Util";
    public static String NUM = "";
    public static String MID = "";
    public static String MEMBER_LEVEL = "";
    public static boolean showMyBidList = false;

    public static String TOKEN = "";
    public static boolean isLogin = false;

    public static Context CURRENT_CONTEXT = null;

    //파일 다운로드
    public static String DOWNLOAD_FILE_NAME = "";

    public static String NaverClientID = "2GqofgBwRJPy0gKYWGPh";
    public static String ClientSecret = "b3OrozibKE";
    public static Spinner spinner;
    public static DownloadBankListTask mListTask;

    public static int STATUS_BAR_HEIGHT = 0;

    public static ProgressDialog dialog;


    public static String CURRENT_TAG = "";

    private long backBtnTime = 0;
    public String token = "";


    final public static String thepion_URL = "https://www.thepion.co.kr:444/";
//    final public static String thepion_URL = "http://test.thepion.co.kr/";
    final public static String link_address = "http://www.thepion.co.kr/mngr/link/";
    final public static String link_qr = "http://www.thepion.co.kr/mngr/qr/";
    final public static String APIKEY = "AIzaSyDB3F-RJsysFx9StZkCe7NZk8WPjklxmb8";
    final public static String url_link_managerApp = "https://thepion.page.link/6RQi";
    final public static String url_link_app = "https://pionmanager.page.link/qL6j";


    private void changeFragment(int container, Fragment fragment) {
        FragmentTransaction fragmentTransaction;
        fragmentTransaction = getSupportFragmentManager().beginTransaction().add(container, fragment);
        fragmentTransaction.commit();
    }


    public static String getCurrentTime() {

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String currentTime = simpleDateFormat.format(date);
        return currentTime;
    }


    public static boolean isValidEmail(String email) {

        boolean rValue = false;

        String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        if (m.matches()) {
            rValue = true;
        }
        return rValue;
    }

    public static boolean isValidPhone(String phone) {
        String regex = "^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$";
        return phone.matches(regex);
    }

    public static boolean isValidDob(String dob) {
        boolean rValue = false;
        String a = "(19|20)\\\\d{2}(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])";
        String regex = "/^(19[0-9][0-9]|20\\d{2})(0[0-9]|1[0-2])(0[1-9]|[1-2][0-9]|3[0-1])$/";
        String b = "^[0-9][0-9][0-9][0-9]\\\\-[0-9][0-9]\\\\-[0-9][0-9]$";
        if (dob.matches(regex)) {
            rValue = true;
        }
        return rValue;

    }


    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view != null) {
            imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    public static String getStringEncode(String str_utfChange) {
        String strRet = null;
        try {
            strRet = URLEncoder.encode(str_utfChange, "UTF-8");
        } catch (UnsupportedEncodingException e) {

            strRet = "";
        }

        return strRet;
    }


    public static void enableDisableViewGroup(ViewGroup viewGroup, boolean enabled) {
        int childCount = viewGroup.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View view = viewGroup.getChildAt(i);
            //if (view.getId() != R.id.bt_left) {//left slide만 제외하고..
            view.setEnabled(enabled);

            if (view instanceof ViewGroup) {
                enableDisableViewGroup((ViewGroup) view, enabled);
            }
        }
    }


    public static void SaveCurrentContext(Context context) {
        CURRENT_CONTEXT = context;
    }

    public static void SetTagName(String tag) {
        CURRENT_TAG = tag;

    }

    public static void intentNext(Class<?> cls) {
        Intent intent = new Intent(CURRENT_CONTEXT, cls);
        CURRENT_CONTEXT.startActivity(intent);
    }

    //url만 보내고 리턴값이 없는 경우 사용할 수 있는 메소드
    public static void intentUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        Util.CURRENT_CONTEXT.startActivity(intent);
    }

    public static void putIntentInfo(Class<?> cls, String key, int num) {
        Intent intent = new Intent(CURRENT_CONTEXT, cls);
        intent.putExtra(key, num);
        Util.CURRENT_CONTEXT.startActivity(intent);
    }

    public static void putIntentInfo(Class<?> cls, String key, String str) {
        Intent intent = new Intent(CURRENT_CONTEXT, cls);
        intent.putExtra(key, str);
        Util.CURRENT_CONTEXT.startActivity(intent);
    }


    public static void showToast(Context mContext, String message) {

        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(String message) {

        Toast.makeText(CURRENT_CONTEXT, message, Toast.LENGTH_SHORT).show();
    }

    public static void setSnackbar(View view, String str) {
        Snackbar.make(view, str, Snackbar.LENGTH_SHORT).show();
    }


    public static String Datetime() {
        String rValue;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd aa HH:mm:ss");
        rValue = df.format(c.getTime());
        return rValue;
    }

    public static int displaySize(Context context, int int_percent, String flag_widthOrHeight) {
        int rValue = 0;
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        switch (flag_widthOrHeight) {
            case "width":
                rValue = width * int_percent / 100;
                break;
            case "height":
                rValue = height * int_percent / 100;
                break;
            default:
                rValue = 0;
        }
        return rValue;
    }

    public static String replaceTel(String telNum) {
        String reTelNum = "";
        if (telNum.length() == 9) {
            reTelNum = telNum.substring(0, 2) + "-" + telNum.substring(2, 5) + "-" + telNum.substring(5, 9);
        } else if (telNum.length() == 10) {
            reTelNum = telNum.substring(0, 2) + "-" + telNum.substring(2, 6) + "-" + telNum.substring(6, 10);
        } else if (telNum.length() == 11) {
            reTelNum = telNum.substring(0, 3) + "-" + telNum.substring(3, 7) + "-" + telNum.substring(7, 11);
        }
        return reTelNum;
    }

    public void showProgressDialog(Context mcontext) {
        dialog = new ProgressDialog(mcontext);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(getApplicationContext().getResources().getString(R.string.progress));
        dialog.show();
    }


    public static void setSpinnerBankList(Spinner mspinner) {
        String strUrlBankList = Util.thepion_URL + "AjaxControl/getbanklist";
//      DownloadBankListTask는 AsyncTask를 상속받았다. AsyncTask는 execute()명령어로 실행한다.
//      AsyncTask를 상속받은 함수 DownloadBankListTask는 strUrlBankList를 실행한다.
        mListTask = (DownloadBankListTask) new DownloadBankListTask(CURRENT_CONTEXT).execute(strUrlBankList);
        spinner = mspinner;

//        String[] tempArray = {"사과", "바나나"};
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, tempArray);
//        spinner.setAdapter(adapter);
    }


    public static class DownloadBankListTask extends AsyncTask<String, String, String> {

        private static final String TAG = "bankListTask";
        public Context mContext;

        public DownloadBankListTask(Context mContext) {
            super();
            this.mContext = mContext;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                return (String) downloadUrl((String) urls[0]);

            } catch (IOException e) {
                return "다운로드 실패" + e.toString();
            } finally {
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, result);
            getRegistStatus(result);
        }

        private String downloadUrl(String myurl) throws IOException {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(myurl);
                Log.e("url", myurl);
                conn = (HttpURLConnection) url.openConnection();
                BufferedInputStream buf = new BufferedInputStream(conn.getInputStream());
                BufferedReader bufreader = new BufferedReader(new InputStreamReader(buf, "utf-8"));
                String line = null;
                String page = "";
                while ((line = bufreader.readLine()) != null) {
                    page += line;
                }
                return page;
            } finally {
                conn.disconnect();
            }
        }

        private void getRegistStatus(String result) {
            ArrayList<String> bank_list = new ArrayList<String>();
            //JsonParsing
            try {
                // create a new instance from a string
                JSONArray jsonArray = new JSONArray(result);
                bank_list.add("은행선택");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = new JSONObject(jsonArray.getString(i));
                    String bank_name = jsonObject.getString("bankName");
                    bank_list.add(bank_name);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, bank_list);
                spinner.setAdapter(adapter);

            } catch (JSONException e) {
                Log.w(TAG, "Could not parse JSON. Error: " + e.getMessage());
            }
        }
    }

    public static void DownloadBankListTaskill() {
        if (mListTask != null && mListTask.getStatus() != DownloadBankListTask.Status.FINISHED) {
            mListTask.cancel(true);
            mListTask = null;
            Toast.makeText(CURRENT_CONTEXT, "인터넷 연결상태가 좋지 않아서 은행리스트를 받아올 수 없습니다.\n다시 시도해주세요", Toast.LENGTH_SHORT).show();
            //Log.e("task kill", "");
        }
    }

    public static String Phone(String src) {
        if (src == null) {
            return "";
        }
        if (src.length() == 8) {
            return src.replaceFirst("^([0-9]{4})([0-9]{4})$", "$1-$2");
        } else if (src.length() == 12) {
            return src.replaceFirst("(^[0-9]{4})([0-9]{4})([0-9]{4})$", "$1-$2-$3");
        }
        return src.replaceFirst("(^02|[0-9]{3})([0-9]{3,4})([0-9]{4})$", "$1-$2-$3");
    }

    public static String DOB(String src) {
        if (src == null) {
            return "";
        }
        if (src.length() == 8) {
            return src.replaceFirst("^([0-9]{4})([0-9]{4})$", "$1-$2");
        } else if (src.length() == 12) {
            return src.replaceFirst("(^[0-9]{4})([0-9]{4})([0-9]{4})$", "$1-$2-$3");
        }
        return src.replaceFirst("(^02|[0-9]{3})([0-9]{3,4})([0-9]{4})$", "$1-$2-$3");
    }

    public static String StringToDate(String str) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = simple.parse(str);
            String stringDate = simple.format(date);
            return stringDate;
        } catch (Exception e) {

        }
        return null;
    }

    public static String getOneMonthBefore() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        return String.valueOf(simple.format(cal.getTime()));
    }

    public static String getNowDateTime() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
        Date today = new Date();
        return String.valueOf(simple.format(today));
    }

    public static Date getNowDateTimeToDate() {
        Date today = new Date();
        return today;
    }

    public static Date getAuctionCompleteDate(String auctionCompleteDate) throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
        Date toDate = simple.parse(auctionCompleteDate);
        return toDate;
    }

    public static String GetNumFormat(int num) {
        DecimalFormat df = new DecimalFormat("#,###");

        if (num >= 10000) {
            int bilTemp = num / 10000;
            String bil = df.format(bilTemp) + "억";
            int milTemp = (num - (bilTemp * 10000));
            if (milTemp > 0) {
                String mil = df.format(milTemp) + " 만원";
                return bil + " " + mil;
            } else {
                return bil + "원";
            }
        } else {
            return df.format(num) + " 만원";
        }
    }


    public static String GetNumFormatWon(int num) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(num);
    }

    /**
     * 콤마 넣기
     *
     * @param num
     * @return
     */
    public static String GetNumFormat(double num) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(num) + "만원";
    }

    public static String GetNumFormatWon(double num) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(num);
    }

    public static String subStringFormat(String str) {
//        String result = str.substring(str.length()-4, str.length());
        return str.substring(0, str.length() - 4);
    }

    public static double changeIntToString(String str) {
        int sum = Integer.parseInt(str);
        return sum * 0.0001;
    }

    public static double changeIntToDouble(String str) {
        int sum = Integer.parseInt(str);
        return sum * 0.0001;
    }

    public static int changeIntToDouble(int price) {
        double d = price * 0.0001;
        int result = (int) d;
        return result;
    }


    public static void SetLog(String message) {
        Log.e(CURRENT_TAG, message);
    }


    public static void buttonBackgroundChange(EditText editText, Button button) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (count > 3) {
                    button.setBackgroundDrawable(Util.CURRENT_CONTEXT.getResources().getDrawable(R.drawable.btn_selector_blue));
                    button.setTextColor(Util.CURRENT_CONTEXT.getResources().getColor(R.color.white));
                    button.setEnabled(true);
                } else {
                    button.setBackgroundDrawable(Util.CURRENT_CONTEXT.getResources().getDrawable(R.drawable.back_gray_round));
                    button.setTextColor(Util.CURRENT_CONTEXT.getResources().getColor(R.color.basic_gray));
                    button.setEnabled(false);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 3) {
                    button.setBackgroundDrawable(Util.CURRENT_CONTEXT.getResources().getDrawable(R.drawable.btn_selector_blue));
                    button.setTextColor(Util.CURRENT_CONTEXT.getResources().getColor(R.color.white));
                    button.setEnabled(true);
                } else {
                    button.setBackgroundDrawable(Util.CURRENT_CONTEXT.getResources().getDrawable(R.drawable.back_gray_round));
                    button.setTextColor(Util.CURRENT_CONTEXT.getResources().getColor(R.color.basic_gray));
                    button.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (editText.length() > 3) {
                    button.setBackgroundDrawable(Util.CURRENT_CONTEXT.getResources().getDrawable(R.drawable.btn_selector_blue));
                    button.setTextColor(Util.CURRENT_CONTEXT.getResources().getColor(R.color.white));
                    button.setEnabled(true);
                } else {
                    button.setBackgroundDrawable(Util.CURRENT_CONTEXT.getResources().getDrawable(R.drawable.back_gray_round));
                    button.setTextColor(Util.CURRENT_CONTEXT.getResources().getColor(R.color.basic_gray));
                    button.setEnabled(false);
                }
            }
        });
    }

    public static void setStatusBarHeight(LinearLayout linearLayout) {
        ViewGroup.LayoutParams layoutParams = linearLayout.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = Util.STATUS_BAR_HEIGHT;
        linearLayout.setLayoutParams(layoutParams);
    }


    public static String changeTagToText(String str) {
        String decodedXML = StringEscapeUtils.unescapeHtml4(str);

        return decodedXML.replaceAll("\\[PionTagManager_aL\\]", "<").replaceAll("\\[PionTagManager_aR\\]", ">").replaceAll("&amp;", "&")
                .replaceAll("\\[PionTagManager_BMark\\]", "\"").replaceAll("\\[PionTagManager_SMark\\]", "\'")
                .replaceAll("\\[PionTagManager_br\\]", "\n").replaceAll("\\[&lt;\\]", "<").replaceAll("\\[&gt;\\]", ">");

    }


    public static void setTextColor(Context context, int strAddress, TextView textView, String colorString, int start, int end) {
        String a = context.getString(strAddress);
        SpannableStringBuilder ssb = new SpannableStringBuilder(a);
        ssb.setSpan(new ForegroundColorSpan(Color.parseColor(colorString)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(ssb);
    }

    public static void setTextColor(Context context, int strAddress, TextView textView, int colorString, int start, int end) {
        String a = context.getString(strAddress);
        SpannableStringBuilder ssb = new SpannableStringBuilder(a);
        ssb.setSpan(new ForegroundColorSpan(colorString), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(ssb);
    }


    public static void kakaolink(Context context, int refManagerNum) {


        TemplateParams params = FeedTemplate
                .newBuilder(ContentObject.newBuilder("당신을 파이온 매니저로 초대합니다.",
                        "http://www.thepion.co.kr/Images/Advertising/manger_invite5.png",
                        LinkObject.newBuilder()
                                .setWebUrl(Util.thepion_URL + "/Mobile/m_Views/join/membership_individual_m?uniqParam=f8EksasiFiaW8ASDfa2dffaAi28fhnas&mNum=" + refManagerNum)
                                .setMobileWebUrl(Util.thepion_URL + "/Mobile/m_Views/join/membership_individual_m?uniqParam=f8EksasiFiaW8ASDfa2dffaAi28fhnas&mNum=" + refManagerNum)
                                .build())
                        .setDescrption(UserInfo.getUserName() + " 님이 기다리고 있습니다.")
                        .build())
                .addButton(new ButtonObject("자세히보기",
                        LinkObject.newBuilder()
                                .setWebUrl(Util.thepion_URL + "/Mobile/m_Views/join/membership_individual_m?uniqParam=f8EksasiFiaW8ASDfa2dffaAi28fhnas&mNum=" + refManagerNum)
                                .setMobileWebUrl(Util.thepion_URL + "/Mobile/m_Views/join/membership_individual_m?uniqParam=f8EksasiFiaW8ASDfa2dffaAi28fhnas&mNum=" + refManagerNum)
                                .build()))
                .build();


        Map<String, String> templateArgs = new HashMap<>();
        templateArgs.put("template_arg1", "");

        KakaoLinkService.getInstance().sendDefault(context, params, templateArgs, new ResponseCallback<KakaoLinkResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Logger.e(errorResult.toString());
            }

            @Override
            public void onSuccess(KakaoLinkResponse result) {
                // 템플릿 밸리데이션과 쿼터 체크가 성공적으로 끝남. 톡에서 정상적으로 보내졌는지 보장은 할 수 없다. 전송 성공 유무는 서버콜백 기능을 이용하여야 한다.
            }
        });
    }

    public static void kakaolinkCustomer(Context context) {

        TemplateParams params = FeedTemplate
                .newBuilder(ContentObject.newBuilder("파이온 PION",
                        "http://www.thepion.co.kr/Images/Advertising/invitePION5.jpg",
                        LinkObject.newBuilder()
                                .setWebUrl(Util.link_address + UserInfo.getUserNum())
                                .setMobileWebUrl(Util.link_address + UserInfo.getUserNum())
                                .build())
                        .setDescrption("주택담보대출 경쟁입찰 플랫폼")
                        .build()).build();

        Map<String, String> templateArgs = new HashMap<>();
        templateArgs.put("template_arg1", "");

        KakaoLinkService.getInstance().sendDefault(context, params, templateArgs, new ResponseCallback<KakaoLinkResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Logger.e(errorResult.toString());
            }

            @Override
            public void onSuccess(KakaoLinkResponse result) {
                // 템플릿 밸리데이션과 쿼터 체크가 성공적으로 끝남. 톡에서 정상적으로 보내졌는지 보장은 할 수 없다. 전송 성공 유무는 서버콜백 기능을 이용하여야 한다.
            }
        });
    }


    public static void savePhone(String name, String phone) {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        intent.putExtra(ContactsContract.Intents.Insert.NAME, name);
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, phone);
        Util.CURRENT_CONTEXT.startActivity(intent);
    }

    public static void callInWebView(String phone) {
        String tel_number = "tel:" + phone;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tel_number));
        Util.CURRENT_CONTEXT.startActivity(intent);
    }


    public static String phone(String src) {
        if (src == null) {
            return "";
        }
        if (src.length() == 8) {
            return src.replaceFirst("^([0-9]{4})([0-9]{4})$", "$1-$2");
        } else if (src.length() == 12) {
            return src.replaceFirst("(^[0-9]{4})([0-9]{4})([0-9]{4})$", "$1-$2-$3");
        }
        return src.replaceFirst("(^02|[0-9]{3})([0-9]{3,4})([0-9]{4})$", "$1-$2-$3");
    }

    public enum StatusBarColorType {
        SOFTBLUE_STATUS_BAR(R.color.pion_basic_soft_blue);

        private int backgroundColorId;

        StatusBarColorType(int backgroundColorId) {
            this.backgroundColorId = backgroundColorId;
        }

        public int getBackgroundColorId() {
            return backgroundColorId;
        }
    }

    public static void setStatusBarColor(Activity activity, StatusBarColorType colorType) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
            window.setStatusBarColor(ContextCompat.getColor(activity, colorType.getBackgroundColorId()));
        }
    }

    /**
     * 스크롤뷰에서 포커싱 하기
     *
     * @param view
     * @param scrollView
     * @param count
     */
    public static void scrollToView(View view, final ScrollView scrollView, int count) {
        if (view != null && view != scrollView) {
            count += view.getTop();
            scrollToView((View) view.getParent(), scrollView, count);
        } else if (scrollView != null) {
            final int finalCount = count;
            new Handler().postDelayed(() -> scrollView.smoothScrollTo(0, finalCount), 300);
        }
    }


//    public static void scrollToViewNested(View view, final NestedScrollView scrollView, int count) {
//        if (view != null && view != scrollView) {
//            count += view.getTop();
//            scrollToView((View) view.getParent(), scrollView, count);
//        } else if (scrollView != null) {
//            final int finalCount = count;
//            new Handler().postDelayed(() -> scrollView.smoothScrollTo(0, finalCount), 300);
//        }
//    }

    public static String removeComma(String str) {
        if (str.contains(",")) {
            str = str.replaceAll(",", "");
            Log.e("콤마제거", str);
        }
        return str;
    }

    public static StringBuilder stringToChangeAnonymusName(String name) {
        StringBuilder myName = new StringBuilder(name);
        if (UserInfo.getUserGrade() != 3) {
            if (name.length() == 3) {
                myName.setCharAt(1, '*');
            } else if (name.length() == 4) {
                myName.setCharAt(1, '*');
                myName.setCharAt(3, '*');
            } else if (name.length() >= 5) {
                myName.setCharAt(1, '*');
                myName.setCharAt(3, '*');
                myName.setCharAt(5, '*');
                myName.setCharAt(7, '*');
            }
        }

        return myName;

    }


    public static String setEncoding(String str) {
        String s = "";
        try {
            s = URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return s;
    }

    public static String setJobType(String jobType) {
        int value = Integer.parseInt(jobType);
        String result = "";
        switch (value) {
            case 0:
                result = "-";
                break;
            case 1:
                result = "직장인";
                break;
            case 2:
                result = "사업자";
                break;
            case 3:
                result = "무직";
                break;
            default:
                break;
        }

        return result;

    }

}



