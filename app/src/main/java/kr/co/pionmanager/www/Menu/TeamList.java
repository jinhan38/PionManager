package kr.co.pionmanager.www.Menu;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import kr.co.pionmanager.www.OnSingleClickListener;
import kr.co.pionmanager.www.R;
import kr.co.pionmanager.www.UserInfo;
import kr.co.pionmanager.www.Util;

public class TeamList extends Activity {

    public static TeamListDetailTaskFront teamListDetailTaskFront;
    public static RecyclerView recyclerView;
    public static String strUrlFront;
    private Timer timer;
    public static ProgressBar progressBar;
    private LinearLayout ll_no_have_teamMember;
    private FrameLayout ll_have_teamMember;
    private Button btn_member_invite_as_kakao;
    private TextView teamMemberIntroduce_1;
    private TextView teamMemberIntroduce_3;
    private TextView teamMemberIntroduce_4;
    private ImageButton ibtn_back;
    public static TeamList teamList;
    private EditText et_list_search;
    private ImageButton ibtn_search;
    private ImageButton ibtn_delete;
    private ProgressBar progressBar_kakao;
    private Button btn_kakao_yellow_box;
    private static final String TAG = "TeamList";
    ArrayList<TeamMemberParent> dataList = new ArrayList<TeamMemberParent>();
    ArrayList<TeamMemberParent> searchNameDataList = new ArrayList<TeamMemberParent>();
    private TextView tv_teamlist;
    private TeamListAdapter teamListAdapter;
    private int currentPage = 1;
    private int totalPageSize = 0;
    private ProgressBar progress_paging;
    private boolean isNameSearch = false;
    private LinearLayout ll_RegListSearch;
    private int y = 0;
    boolean isDragging = true;
    private Animation slideDown;
    private Animation slideUp;
    private boolean isScrolled = false;
    private boolean dataTaskFinish = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teamlist);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Util.SaveCurrentContext(this);
        teamList = this;
        Util.SetTagName("TeamListFragment");
        initView();
        setUpListener();

        putDataQuery("");
        timer = new Timer(true);
        final Handler handler = new Handler();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> {
                    TeamListTaskFrontKill();
                    if (teamListDetailTaskFront != null && teamListDetailTaskFront.getStatus() != TeamListDetailTaskFront.Status.FINISHED) {
                        Util.showToast("인터넷 연결상태가 좋지 않습니다.\n다시 시도해주세요");
                    }
                });
            }
        }, 8000);

    }

    private void initView() {

        ll_no_have_teamMember = findViewById(R.id.ll_no_have_teamMember);
        ll_have_teamMember = findViewById(R.id.ll_have_teamMember);
        ll_have_teamMember.setVisibility(View.GONE);
        ll_no_have_teamMember.setVisibility(View.GONE);

        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.list);
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        ibtn_back = findViewById(R.id.ibtn_back);
        btn_member_invite_as_kakao = findViewById(R.id.btn_member_invite_as_kakao);
        teamMemberIntroduce_1 = findViewById(R.id.teamMemberIntroduce_1);
        teamMemberIntroduce_3 = findViewById(R.id.teamMemberIntroduce_3);
        teamMemberIntroduce_4 = findViewById(R.id.teamMemberIntroduce_4);
        et_list_search = findViewById(R.id.et_list_search);
        ibtn_search = findViewById(R.id.ibtn_search);
        ibtn_delete = findViewById(R.id.ibtn_delete);
        progressBar_kakao = findViewById(R.id.progressBar_kakao);
        progressBar_kakao.setVisibility(View.GONE);
        btn_kakao_yellow_box = findViewById(R.id.btn_kakao_yellow_box);
        btn_kakao_yellow_box.setVisibility(View.GONE);
        progress_paging = findViewById(R.id.progress_paging);
        tv_teamlist = findViewById(R.id.tv_teamlist);
        ll_RegListSearch = findViewById(R.id.ll_RegListSearch);
        TextView tv_TeamMember_top = findViewById(R.id.tv_TeamMember_top);
        TextView tv_back = findViewById(R.id.tv_back);
        slideDown = AnimationUtils.loadAnimation(this, R.anim.scroll_slide_down);
        slideUp = AnimationUtils.loadAnimation(this, R.anim.scroll_slide_up);

        LinearLayout text_2 = findViewById(R.id.text_2);

        if (UserInfo.getUserGrade() == 2) {
            tv_back.setText(R.string.tv_teamInfo);
            tv_TeamMember_top.setText("등록된 팀원이 없습니다");
            Util.setTextColor(Util.CURRENT_CONTEXT, R.string.teamMemberIntroduce_1, teamMemberIntroduce_1, "#56A2F4", 0, 2);
            Util.setTextColor(Util.CURRENT_CONTEXT, R.string.teamMemberIntroduce_3, teamMemberIntroduce_3, "#56A2F4", 0, 6);
            btn_member_invite_as_kakao.setText("카카오톡으로 팀원(매니저) 초대하기");
        } else if (UserInfo.getUserGrade() == 3) {
            tv_teamlist.setText(R.string.tv_teamInfo);
            tv_TeamMember_top.setText("등록된 팀원이 없습니다.");
            teamMemberIntroduce_4.setText(getText(R.string.invite_manager_by_manager_3));
            Util.setTextColor(Util.CURRENT_CONTEXT, R.string.partnerMemberIntroduce_1, teamMemberIntroduce_1, "#56A2F4", 0, 2);
            text_2.setVisibility(View.GONE);
            btn_member_invite_as_kakao.setText("카카오톡으로 팀원(매니저) 초대하기");
        } else if (UserInfo.getUserGrade() == 1) {
            tv_back.setText("추천인");
            TextView btn_member_invite_as_kakao = findViewById(R.id.btn_member_invite_as_kakao);
            tv_teamlist.setText("나를 추천한 매니저");
            tv_TeamMember_top.setText("나를 추천인으로 등록한 매니저가 없습니다.");
            teamMemberIntroduce_1.setText("나를 추천인으로 등록한 매니저가 10명 이상이 된다면, 0.1%의 추가 수당을 받습니다.");
            if (UserInfo.getRefManagerGrade().equals("2")) {
                LinearLayout llEvent = findViewById(R.id.llEvent);
                llEvent.setVisibility(View.GONE);
            }
            text_2.setVisibility(View.GONE);
            Spannable spannable = (Spannable) teamMemberIntroduce_1.getText();
            spannable.setSpan(new RelativeSizeSpan(1.5f), 31, 35, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.pion_basic_soft_blue)), 31, 35, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            btn_member_invite_as_kakao.setText("카카오톡으로 매니저 초대하기");
        }

    }

    private void setUpListener() {

        ibtn_back.setOnClickListener(v -> onBackPressed());
        btn_member_invite_as_kakao.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (UserInfo.getUserGrade() == 1 && UserInfo.getRefManagerGrade().equals("3")) Util.kakaolink(getApplicationContext(), UserInfo.getRefMemberNum());
                else Util.kakaolink(getApplicationContext(), UserInfo.getUserNum());
                progressBar_kakao.setVisibility(View.VISIBLE);
                btn_kakao_yellow_box.setVisibility(View.VISIBLE);
            }
        });

        et_list_search.addTextChangedListener(new TextWatcher() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Stream<TeamMemberParent> filterSearch = dataList.stream().filter(x -> x.getTeamMemberName().contains(et_list_search.getText().toString().trim()));
                setAdapter(getArrayListFromStream(filterSearch));
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Stream<TeamMemberParent> filterSearch = dataList.stream().filter(x -> x.getTeamMemberName().contains(et_list_search.getText().toString().trim()));
                setAdapter(getArrayListFromStream(filterSearch));
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void afterTextChanged(Editable s) {
                Stream<TeamMemberParent> filterSearch = dataList.stream().filter(x -> x.getTeamMemberName().contains(et_list_search.getText().toString().trim()));
                setAdapter(getArrayListFromStream(filterSearch));
                if (et_list_search.getText().toString().length() > 0) {
                    ibtn_delete.setVisibility(View.VISIBLE);
                } else {
//                    et_list_search.setText(null);
                    isNameSearch = false;
                    ibtn_delete.setVisibility(View.INVISIBLE);
                }
            }
        });

        ibtn_search.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Util.hideKeyboard(teamList);
                putDataQuery(et_list_search.getText().toString().trim());
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        et_list_search.setOnEditorActionListener((v, actionId, event) -> {
            Util.hideKeyboard(teamList);
            putDataQuery(et_list_search.getText().toString().trim());
            progressBar.setVisibility(View.VISIBLE);
            return true;
        });

        ibtn_delete.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                et_list_search.setText("");
                ibtn_delete.setVisibility(View.INVISIBLE);
                setAdapter(dataList);
                isNameSearch = false;
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                y = dy;
                int lastVisibleItemPosition = ((LinearLayoutManager) Objects.requireNonNull(recyclerView.getLayoutManager())).findLastCompletelyVisibleItemPosition();
                int itemTotalCount = recyclerView.getAdapter().getItemCount() - 1;
                if (lastVisibleItemPosition >= itemTotalCount - 1) {
                    if (totalPageSize > currentPage && !isNameSearch && dataTaskFinish) {
                        currentPage += 1;
                        progress_paging.setVisibility(View.VISIBLE);
                        putDataQuery("");
                        dataTaskFinish = false;
                    }
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (y > 0) {
                    if (isDragging) {
//                        ll_RegListSearch.startAnimation(slideUp);
                        ll_RegListSearch.setVisibility(View.GONE);
                        isScrolled = true;
                        isDragging = false;
                    }
                } else {
                    if (isScrolled) {
                        ll_RegListSearch.setVisibility(View.VISIBLE);
                        ll_RegListSearch.startAnimation(slideDown);
                        isScrolled = false;
                        isDragging = true;
                    }
                }
            }
        });
    }

    public void putDataQuery(String name) {
        String n = "";
        try {
            n = URLEncoder.encode(name, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (name.length() > 0) {
            isNameSearch = true;
            searchNameDataList.clear();
            strUrlFront = Util.thepion_URL + "AjaxControl/TeamList?userNum=" + UserInfo.getUserNum() + "&currentPage=" + currentPage + "&mName=" + n;
        } else {
            Log.e(TAG, "putDataQuery: name 없음");
            strUrlFront = Util.thepion_URL + "AjaxControl/TeamList?userNum=" + UserInfo.getUserNum() + "&currentPage=" + currentPage;
        }
        teamListDetailTaskFront = (TeamListDetailTaskFront) new TeamListDetailTaskFront().execute(strUrlFront);
    }

    public void setAdapter(ArrayList<TeamMemberParent> data) {
//        ll_no_have_teamMember.setVisibility(View.GONE);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        teamListAdapter = new TeamListAdapter(data);
        recyclerView.setAdapter(teamListAdapter);
        recyclerView.setVisibility(View.VISIBLE);
//        teamListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar_kakao.setVisibility(View.GONE);
        btn_kakao_yellow_box.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {

        if (et_list_search.getText().toString().length() > 0) {
            et_list_search.setText("");
            ibtn_delete.setVisibility(View.INVISIBLE);
            isNameSearch = false;
            setAdapter(dataList);
        } else {
            super.onBackPressed();
            Util.hideKeyboard(this);
        }
    }

    private static <T> ArrayList<T> getArrayListFromStream(Stream<T> stream) {
        List<T> list = stream.collect(Collectors.toList());
        ArrayList<T> arrayList = new ArrayList<T>(list);
        return arrayList;
    }

    @SuppressLint("StaticFieldLeak")
    public class TeamListDetailTaskFront extends AsyncTask<String, String, String> {

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
            Log.e("TeamExpandableListview ", result);
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
            int totalCount = 0;
            try {
                TeamMemberParent teamMemberParent = null;
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = new JSONObject(jsonArray.getString(i));
                    String rValue = jsonObject.getString("rValue");
                    String teamMemberName = jsonObject.getString("teamMemberName");
                    String phone = jsonObject.getString("phone");
                    String registerDate = jsonObject.getString("registerDate");
                    String recommendationDate = jsonObject.getString("recommendationDate");
                    String finishNum = jsonObject.getString("finishNum");
                    String totalPrice = jsonObject.getString("totalPrice");


                    if (isNameSearch) {
                        searchNameDataList.add(new TeamMemberParent(rValue, teamMemberName, phone, registerDate, recommendationDate, finishNum, totalPrice));
                    } else {
                        totalPageSize = jsonObject.getInt("totalPageSize");
                        totalCount = jsonObject.getInt("totalCount");
                        teamMemberParent = new TeamMemberParent(rValue, teamMemberName, phone, registerDate, recommendationDate, finishNum, totalPrice);
                        dataList.add(teamMemberParent);
                    }
                }

                if (isNameSearch) {
                    if (searchNameDataList.get(0).getrValue().equals("y")) {
                        setAdapter(searchNameDataList);
                        Log.e(TAG, "getLoanInfoStatus: rValue = y");
                        ll_have_teamMember.setVisibility(View.VISIBLE);
                        ll_no_have_teamMember.setVisibility(View.GONE);
                    }
                } else if (dataList.get(0).getrValue().equals("n")) {
                    Log.e(TAG, "getLoanInfoStatus: rValue = n");
                    Log.e(TAG, "getLoanInfoStatus: 추천인 없음");
                    ll_no_have_teamMember.setVisibility(View.VISIBLE);
                    ll_have_teamMember.setVisibility(View.GONE);
                } else {
                    if (currentPage == 1) {
                        setAdapter(dataList);
                        ll_have_teamMember.setVisibility(View.VISIBLE);
                        ll_no_have_teamMember.setVisibility(View.GONE);
                    } else {
                        teamListAdapter.notifyDataSetChanged();
                    }
                    switch (UserInfo.getUserGrade()) {
                        case 1:
                            tv_teamlist.setText("추천인(" + totalCount + ")");
                            break;
                        case 2:
                        case 3:
                            tv_teamlist.setText(getString(R.string.tv_teamInfo) + "(" + totalCount + ")");
                            break;
                    }

                }

                dataTaskFinish = true;

                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "getLoanInfoStatus: progressBar.setV");
                progress_paging.setVisibility(View.GONE);
            } catch (JSONException e) {
                Log.e("TAG", "Could not parse JSON. Error: " + e.getMessage());
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                progress_paging.setVisibility(View.GONE);
            }
        }

    }

    private void TeamListTaskFrontKill() {
        if (teamListDetailTaskFront != null && teamListDetailTaskFront.getStatus() != TeamListDetailTaskFront.Status.FINISHED) {
            teamListDetailTaskFront.cancel(true);
            teamListDetailTaskFront = null;
            Log.e("Front task kill", "");
        }
    }
}

class TeamMemberParent {
    private String rValue;
    private String teamMemberName;
    private String phone;
    private String registerDate;
    private String recommendationDate;
    private String finishNum;
    private String totalPrice;

    public TeamMemberParent(String rValue, String teamMemberName, String phone, String registerDate, String recommendationDate, String finishNum, String totalPrice) {
        this.rValue = rValue;
        this.teamMemberName = teamMemberName;
        this.phone = phone;
        this.registerDate = registerDate;
        this.recommendationDate = recommendationDate;
        this.finishNum = finishNum;
        this.totalPrice = totalPrice;
    }

    public String getrValue() {
        return rValue;
    }

    public void setrValue(String rValue) {
        this.rValue = rValue;
    }

    public String getTeamMemberName() {
        return teamMemberName;
    }

    public void setTeamMemberName(String teamMemberName) {
        this.teamMemberName = teamMemberName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(String registerDate) {
        this.registerDate = registerDate;
    }

    public String getRecommendationDate() {
        return recommendationDate;
    }

    public void setRecommendationDate(String recommendationDate) {
        this.recommendationDate = recommendationDate;
    }

    public String getFinishNum() {
        return finishNum;
    }

    public void setFinishNum(String finishNum) {
        this.finishNum = finishNum;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }
}

class TeamListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "TeamListAdapter";
    public ArrayList<TeamMemberParent> data;
    public static Context context;
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    private int prePosition = -1;
    private final int TYPE_HEADER = 0;
    private final int TYPE_ITEM = 1;

    public TeamListAdapter(ArrayList<TeamMemberParent> data) {
        this.data = data;
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {

        public View header_blank;

        HeaderViewHolder(View headerView) {
            super(headerView);
            header_blank = headerView.findViewById(R.id.header_blank);

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v;
        RecyclerView.ViewHolder vh;
        if (viewType == TYPE_HEADER) {
            Log.e(TAG, "onCreateViewHolder: header");
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_header, parent, false);
            vh = new HeaderViewHolder(v);
            return vh;
        } else if (viewType == TYPE_ITEM) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.teamlist_parent, parent, false);
            vh = new ViewHolder(v);
            return vh;
        } else {
            return null;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            Log.e(TAG, "onBindViewHolder: header");
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.header_blank.setVisibility(View.VISIBLE);
        } else if (holder instanceof ViewHolder) {
            int itemPosition = position - 1;
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.onBind(data.get(itemPosition), position);
//            viewHolder.onBind(data.get(position), position);
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public int getItemCount() {
        return data.size() + 1;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private LinearLayout ll_teamList_parent;
        private TextView list_parent_name;
        private ImageButton arrow_top;
        private ImageButton arrow_bottom;
        private LinearLayout ll_body;
        private ImageView iv_dot;
        private TextView teamList_child_totalPrice;
        private TextView teamList_child_finishNum;
        private TextView teamList_child_registerDate;
        private TextView teamList_child_recommendationDate;
        private TeamMemberParent personalData;
        private int position;

        @SuppressLint("CutPasteId")
        ViewHolder(View view) {
            super(view);
            this.ll_teamList_parent = view.findViewById(R.id.ll_teamList_parent);
            this.list_parent_name = view.findViewById(R.id.list_parent_name);
            this.arrow_top = view.findViewById(R.id.arrow_top);
            this.arrow_bottom = view.findViewById(R.id.arrow_bottom);
            this.ll_body = view.findViewById(R.id.ll_body);
            this.iv_dot = view.findViewById(R.id.iv_dot);
            this.teamList_child_totalPrice = view.findViewById(R.id.teamList_child_totalPrice);
            this.teamList_child_finishNum = view.findViewById(R.id.teamList_child_finishNum);
            this.teamList_child_registerDate = view.findViewById(R.id.teamList_child_registerDate);
            this.teamList_child_recommendationDate = view.findViewById(R.id.teamList_child_recommendationDate);
        }

        void onBind(TeamMemberParent personalData, int position) {
            this.personalData = personalData;
            this.position = position;

            switch (UserInfo.getUserGrade()) {
                case 1:
                    ll_body.setVisibility(View.GONE);
                    arrow_top.setVisibility(View.GONE);
                    arrow_bottom.setVisibility(View.GONE);
                case 2:
                    list_parent_name.setText(Util.stringToChangeAnonymusName(personalData.getTeamMemberName()));
                    settingTextByGrade2And3();
                    break;
                case 3:
                    list_parent_name.setText(personalData.getTeamMemberName());
                    settingTextByGrade2And3();
                    break;
            }
        }

        private void settingTextByGrade2And3() {
            int price = (int) (Long.parseLong(personalData.getTotalPrice()) / 10000);
            teamList_child_totalPrice.setText(Util.GetNumFormat(price));
            teamList_child_finishNum.setText(personalData.getFinishNum() + "건");
            teamList_child_registerDate.setText(personalData.getRegisterDate());
            teamList_child_recommendationDate.setText(personalData.getRecommendationDate());
            changeVisibility(selectedItems.get(position));
            ll_teamList_parent.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.e(TAG, "onClick: " );
            if (selectedItems.get(position)) {
                Log.e(TAG, "onClick: selectedItems true" );
                selectedItems.delete(position);
            } else {
                Log.e(TAG, "onClick: selectedItems false" );
                selectedItems.delete(prePosition);
                selectedItems.put(position, true);
            }
            if (prePosition != -1) notifyItemChanged(prePosition);
            notifyItemChanged(position);
            prePosition = position;
            v.requestFocus();
        }


        private void changeVisibility(final boolean isExpanded) {
            int dpValue = 184;
            float d = context.getResources().getDisplayMetrics().density;
            int height = (int) (dpValue * d);

            ValueAnimator va = isExpanded ? ValueAnimator.ofInt(0, height) : ValueAnimator.ofInt(height, 0);
            va.setDuration(300);
            va.addUpdateListener(animation -> {
                int value = (int) animation.getAnimatedValue();
                ll_body.getLayoutParams().height = value;
                ll_body.requestLayout(); //layout을 갱신함
                ll_body.requestFocus();
                arrow_top.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                arrow_bottom.setVisibility(isExpanded ? View.GONE : View.VISIBLE);
                iv_dot.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                ll_body.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            });
            // Animation start
            va.start();

        }
    }

}



