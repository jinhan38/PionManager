package kr.co.pionmanager.www.Menu;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import kr.co.pionmanager.www.OnSingleClickListener;
import kr.co.pionmanager.www.R;
import kr.co.pionmanager.www.UserInfo;
import kr.co.pionmanager.www.Util;

public class MessageBox extends AppCompatActivity {

    private static final String TAG = "MessageBox";
    private RecyclerView recyclerView;
    private ImageButton ibtn_back;
    private LinearLayoutManager layoutManager;
    private ArrayList<MessageBoxData> dataList = new ArrayList<MessageBoxData>();
    private MessageBoxAdapter messageBoxAdapter;
    private ProgressBar progressBar;
    private String listType = "all";
    private int currentPage = 1;
    private int totalPageSize = 0;
    private ProgressBar progress_paging;
    private MessageBoxTask messageBoxTask;
    private String strUrl;
    private LinearLayout noMessageWrap;
    private LinearLayout recyclerViewWrap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_box);

        initView();
        setupListener();

    }

    @SuppressLint("WrongViewCast")
    private void initView() {

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        ibtn_back = findViewById(R.id.ibtn_back);
        progress_paging = findViewById(R.id.progress_paging);
        noMessageWrap = findViewById(R.id.noMessageWrap);
        recyclerViewWrap = findViewById(R.id.recyclerViewWrap);

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        messageBoxAdapter = new MessageBoxAdapter(dataList);
        recyclerView.setAdapter(messageBoxAdapter);

        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        strUrl = Util.thepion_URL + "AjaxControl/MessageBox?userNum=" + UserInfo.getUserNum() + "&listType=" + listType + "&currentPage=" + currentPage;
        messageBoxTask = (MessageBoxTask) new MessageBoxTask().execute(strUrl);

    }

    private void setupListener(){

        ibtn_back.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                onBackPressed();
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                Log.e(TAG, "onScrolled: 진입" );
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItemPosition = ((LinearLayoutManager) Objects.requireNonNull(recyclerView.getLayoutManager())).findLastCompletelyVisibleItemPosition();
                int itemTotalCount = recyclerView.getAdapter().getItemCount() - 1;
                if (lastVisibleItemPosition >= itemTotalCount - 1) {
                    if (totalPageSize > currentPage) {
                        Log.e(TAG, "onScrolled: totalPageSize > currentPage" );
                        currentPage += 1;
                        progress_paging.setVisibility(View.VISIBLE);
                        strUrl = Util.thepion_URL + "AjaxControl/MessageBox?userNum=" + UserInfo.getUserNum() + "&listType=" + listType + "&currentPage=" + currentPage;
                        messageBoxTask = (MessageBoxTask) new MessageBoxTask().execute(strUrl);
                    }
                }
            }
        });
    }


    @SuppressLint("StaticFieldLeak")
    public class MessageBoxTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "다운로드 실패" + e.toString();
            }
        }

        protected void onPostExecute(String result) {
            Log.e("MessageBoxTask : ", result);
            getLoanInfoStatus(result);
        }

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

        @SuppressLint("SetTextI18n")
        private void getLoanInfoStatus(String result) {

            try {
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = new JSONObject(jsonArray.getString(i));
                    String company = jsonObject.getString("company");
                    String title = jsonObject.getString("content");
                    String date = jsonObject.getString("date");
                    String body = jsonObject.getString("content");
                    String receiveDate = jsonObject.getString("receiveDate");
                    String messageNum = jsonObject.getString("messageNum");
                    totalPageSize = jsonObject.getInt("totalPageSize");

                    dataList.add(new MessageBoxData(company, title, body, date, receiveDate, messageNum, false));
                }

                progressBar.setVisibility(View.GONE);
                progress_paging.setVisibility(View.GONE);

                if (dataList.size() > 0){
                    recyclerViewWrap.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    noMessageWrap.setVisibility(View.GONE);
                }else{
                    noMessageWrap.setVisibility(View.VISIBLE);
                    recyclerViewWrap.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                }

            } catch (JSONException e) {
                Log.e(TAG, "Could not parse JSON. Error: " + e.getMessage());
                progressBar.setVisibility(View.GONE);
                noMessageWrap.setVisibility(View.VISIBLE);
                recyclerViewWrap.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
            }
        }
    }
}


class MessageBoxAdapter extends RecyclerView.Adapter<MessageBoxAdapter.ViewHolder> {
    private static final String TAG = "MessageBoxAdapter";
    public ArrayList<MessageBoxData> data;
    public static Context context;
    // Item의 클릭 상태를 저장할 array 객체
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    // 직전에 클릭됐던 Item의 position
    private int prePosition = -1;
    private ImageView newResult;
    private String checkValue;

    public MessageBoxAdapter(ArrayList<MessageBoxData> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.message_box_item, parent, false);
        context = parent.getContext();
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBind(data.get(position), position);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private LinearLayout ll_click;
        private TextView company;
        private TextView title;
        private TextView date;
        private TextView body;
        private MessageBoxData personalData;
        private int position;
        private ImageView iv_dot;
        public ImageView iv_new;
        public ScrollView scrollView;

        @SuppressLint("CutPasteId")
        ViewHolder(View view) {
            super(view);
            this.ll_click = view.findViewById(R.id.ll_click);
            this.company = view.findViewById(R.id.company);
            this.title = view.findViewById(R.id.title);
            this.date = view.findViewById(R.id.date);
            this.body = view.findViewById(R.id.body);
            this.iv_dot = view.findViewById(R.id.iv_dot);
            this.iv_new = view.findViewById(R.id.iv_new);
            this.scrollView = view.findViewById(R.id.scrollView);
            newResult = view.findViewById(R.id.iv_new);
        }

        void onBind(MessageBoxData personalData, int position) {
            this.personalData = personalData;
            this.position = position;

            company.setText(personalData.getCompany());
            title.setText(Util.changeTagToText(personalData.getTitle()));
            date.setText(personalData.getDate());
            body.setText(Util.changeTagToText(personalData.getBody()));

            //View의 아래쪽으로 포커스가 잡히도록 함
//            company.requestFocus(View.FOCUS_DOWN);
            if (data.get(position).getReceiveDate().length() > 1) {
                data.get(position).setReadData(true);
                iv_new.setVisibility(View.INVISIBLE);
            } else {
                data.get(position).setReadData(false);
                iv_new.setVisibility(View.VISIBLE);
            }

            changeVisibility(selectedItems.get(position));

            ll_click.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (selectedItems.get(position)) {
                // 펼쳐진 Item을 클릭 시
                selectedItems.delete(position);
            } else {
                // 직전의 클릭됐던 Item의 클릭상태를 지움
                selectedItems.delete(prePosition);
                // 클릭한 Item의 position을 저장
                selectedItems.put(position, true);
            }
            // 해당 포지션의 변화를 알림
            if (prePosition != -1) notifyItemChanged(prePosition);
            notifyItemChanged(position);
            // 클릭된 position 저장
            prePosition = position;
            v.requestFocus();

            //receiveDate는 메세지를 읽은 날짜
            if (personalData.getReceiveDate().length() < 1) {
                String strUrl = Util.thepion_URL + "AjaxControl/MessageBoxCheck?messageNum=" + personalData.getMessageNum();
                MessageBoxCheck messageBoxCheck = (MessageBoxCheck) new MessageBoxCheck().execute(strUrl);
            }

            //new 이미지뷰 수정
            newResult.setVisibility(View.INVISIBLE);
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date time = new Date();
            String nowTime = format1.format(time);
            data.get(position).setReadData(true);
            data.get(position).setReceiveDate(nowTime);
            newIconInvisible(true);
        }

        private void newIconInvisible(boolean isChecked) {
            if (isChecked) {
                iv_new.setVisibility(View.INVISIBLE);
                Log.d(TAG, "newIconInvisible: isChecked");

            }
        }

        private void changeVisibility(final boolean isExpanded) {
            // height 값을 dp로 지정해서 넣고싶으면 아래 소스를 이용
            int dpValue = 120;
            float d = context.getResources().getDisplayMetrics().density;
            int height = (int) (dpValue * d);

            //ValueAnimator는 수치값으로 animation를 넣을 수 있는 클래스
            // ValueAnimator.ofInt(int... values)는 View가 변할 값을 지정, 인자는 int 배열
            ValueAnimator va = isExpanded ? ValueAnimator.ofInt(0, height) : ValueAnimator.ofInt(height, 0);
            va.setDuration(300);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    // value는 height 값
                    int value = (int) animation.getAnimatedValue();
                    // imageView의 높이 변경
                    body.getLayoutParams().height = value;
                    body.requestLayout(); //layout을 갱신함
                    body.requestFocus();

                    // imageView가 실제로 사라지게하는 부분
                    body.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                    iv_dot.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                }
            });
            // Animation start
            va.start();

        }
    }


    @SuppressLint("StaticFieldLeak")
    class MessageBoxCheck extends AsyncTask<String, String, String> {
        private static final String TAG = "MessageBoxCheck";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "다운로드 실패" + e.toString();
            }
        }

        protected void onPostExecute(String result) {
            Log.d(TAG, "onPostExecute: " + result);
            getCheckedStatus(result);
        }

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

        @SuppressLint("SetTextI18n")
        private void getCheckedStatus(String result) {
            try {

                JSONObject jsonObject = new JSONObject(result);
                checkValue = jsonObject.getString("checkValue");


                Log.d(TAG, "getCheckedStatus: " + checkValue);

            } catch (JSONException e) {
                Log.e(TAG, "Could not parse JSON. Error: " + e.getMessage());
            }
        }
    }

}


class MessageBoxData {

    private String company;
    private String title;
    private String body;
    private String date;
    private String receiveDate;
    private String messageNum;
    private boolean isReadData = false;

    public MessageBoxData(String company, String title, String body, String date, String receiveDate, String messageNum, boolean isReadData) {
        this.company = company;
        this.title = title;
        this.body = body;
        this.date = date;
        this.receiveDate = receiveDate;
        this.messageNum = messageNum;
        this.isReadData = isReadData;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(String receiveDate) {
        this.receiveDate = receiveDate;
    }

    public String getMessageNum() {
        return messageNum;
    }

    public void setMessageNum(String messageNum) {
        this.messageNum = messageNum;
    }

    public boolean isReadData() {
        return isReadData;
    }

    public void setReadData(boolean readData) {
        isReadData = readData;
    }
}


