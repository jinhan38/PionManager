package kr.co.pionmanager.www.Menu;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuView;
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


public class CustomerList extends AppCompatActivity {

    private static final String TAG = "CustomerList";
    private LinearLayout ll_no_have_customer;
    private FrameLayout ll_have_customer;
    private CustomerTaskParent customerTaskParent;
    private String strUrlParent;
    private ArrayList<CustomerParent> customerListGroups = new ArrayList<CustomerParent>();
    private ArrayList<CustomerParent> searchCustomerName = new ArrayList<CustomerParent>();
    private Timer timer;
    private ProgressBar progressBar;
    private ProgressBar progress_paging;
    private TextView customerIntroduce_1;
    public static CustomerList customerList;
    private ImageButton ibtn_back;
    private EditText et_list_search;
    private ImageButton ibtn_search;
    private ImageButton ibtn_delete;
    private RecyclerView recyclerView;
    private LinearLayout ll_recyclerview_wrap;
    private CustomerAdapter customerAdapter;
    private int currentPage = 1;
    private int totalPageSize = 0;
    private TextView tv_customerList;
    private boolean isNameSearch = false;
    private LinearLayout ll_RegListSearch;
    private int y = 0;
    boolean isDragging = true;
    private Animation slideDown;
    private Animation slideUp;
    private boolean isScrolled = false;
    private Button customerInvite;
    private boolean dataTaskFinish = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customerlist_fragment);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Util.SaveCurrentContext(this);

        initView();
        setUpListener();

        customerIntroduce_1 = findViewById(R.id.customerIntroduce_1);
        Util.setTextColor(Util.CURRENT_CONTEXT, R.string.customerIntroduce_1, customerIntroduce_1, "#56A2F4", 0, 2);

        putDataQuery("");

        timer = new Timer(true);
        final Handler handler = new Handler();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> {
//                    CustomerListTaskKill();
                    if (customerTaskParent != null && customerTaskParent.getStatus() != CustomerTaskParent.Status.FINISHED) {
                        Util.showToast("인터넷 연결상태가 좋지 않습니다.\n다시 시도해주세요");
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                });
            }
        }, 8000);
    }

    private void initView() {
        customerList = this;
        progressBar = findViewById(R.id.progressBar);
        customerInvite = findViewById(R.id.customerInvite);
        ll_no_have_customer = findViewById(R.id.ll_no_have_customer);
        ll_have_customer = findViewById(R.id.ll_have_customer);
        progressBar.setVisibility(View.VISIBLE);
        ibtn_back = findViewById(R.id.ibtn_back);
        et_list_search = findViewById(R.id.et_list_search);
        ibtn_search = findViewById(R.id.ibtn_search);
        ibtn_delete = findViewById(R.id.ibtn_delete);
        recyclerView = findViewById(R.id.list);
        recyclerView.setVisibility(View.INVISIBLE);
        ll_recyclerview_wrap = findViewById(R.id.ll_recyclerview_wrap);
        progress_paging = findViewById(R.id.progress_paging);
        tv_customerList = findViewById(R.id.tv_customerList);
        ll_RegListSearch = findViewById(R.id.ll_RegListSearch);
        slideDown = AnimationUtils.loadAnimation(this, R.anim.scroll_slide_down);
        slideUp = AnimationUtils.loadAnimation(this, R.anim.scroll_slide_up);
    }

    private void setUpListener() {

        ibtn_back.setOnClickListener(v -> onBackPressed());

        customerInvite.setOnClickListener(v -> Util.intentNext(CustomerInvite.class));

        et_list_search.addTextChangedListener(new TextWatcher() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Stream<CustomerParent> filterSearch = customerListGroups.stream().filter(x -> x.getName().contains(et_list_search.getText().toString().trim()));
                setAdapter(getArrayListFromStream(filterSearch));

            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Stream<CustomerParent> filterSearch = customerListGroups.stream().filter(x -> x.getName().contains(et_list_search.getText().toString().trim()));
                setAdapter(getArrayListFromStream(filterSearch));
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void afterTextChanged(Editable s) {
                Stream<CustomerParent> filterSearch = customerListGroups.stream().filter(x -> x.getName().contains(et_list_search.getText().toString().trim()));
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
                Util.hideKeyboard(customerList);
                putDataQuery(et_list_search.getText().toString().trim());
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        et_list_search.setOnEditorActionListener((v, actionId, event) -> {
            Util.hideKeyboard(customerList);
            putDataQuery(et_list_search.getText().toString().trim());
            progressBar.setVisibility(View.VISIBLE);
            return true;
        });

        ibtn_delete.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                et_list_search.setText("");
                ibtn_delete.setVisibility(View.INVISIBLE);
                setAdapter(customerListGroups);
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
                if (y > 0 ) {
                    if (isDragging){
//                        ll_RegListSearch.startAnimation(slideUp);
                        ll_RegListSearch.setVisibility(View.GONE);
                        isScrolled = true;
                        isDragging = false;
                    }
                } else {
                    if (isScrolled) {
                        ll_RegListSearch.startAnimation(slideDown);
                        ll_RegListSearch.setVisibility(View.VISIBLE);
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
            searchCustomerName.clear();
            strUrlParent = Util.thepion_URL + "AjaxControl/CustomerList?userNum=" + UserInfo.getUserNum() + "&currentPage=" + currentPage + "&mName=" + n;
        } else {
            Log.e(TAG, "putDataQuery: name 없음");
            strUrlParent = Util.thepion_URL + "AjaxControl/CustomerList?userNum=" + UserInfo.getUserNum() + "&currentPage=" + currentPage;
        }
        customerTaskParent = (CustomerTaskParent) new CustomerTaskParent().execute(strUrlParent);
    }

    private void setAdapter(ArrayList<CustomerParent> data) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        customerAdapter = new CustomerAdapter(data);
        recyclerView.setAdapter(customerAdapter);
        recyclerView.setVisibility(View.VISIBLE);

    }

    @Override
    public void onBackPressed() {

        if (et_list_search.getText().toString().length() > 0) {
            et_list_search.setText("");
            ibtn_delete.setVisibility(View.INVISIBLE);
            isNameSearch = false;
            setAdapter(customerListGroups);
        } else {
            super.onBackPressed();
            Util.hideKeyboard(CustomerList.this);
        }
    }

    private static <T> ArrayList<T> getArrayListFromStream(Stream<T> stream) {
        List<T> list = stream.collect(Collectors.toList());
        ArrayList<T> arrayList = new ArrayList<T>(list);
        return arrayList;
    }

    @SuppressLint("StaticFieldLeak")
    public class CustomerTaskParent extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            customerListGroups.clear();
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "다운로드 실패" + e.toString();
            }
        }

        @SuppressLint("LongLogTag")
        protected void onPostExecute(String result) {
            Log.d(TAG, "onPostExecute: " + result);
            getLoanInfoStatus(result);
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

        @SuppressLint("SetTextI18n")
        private void getLoanInfoStatus(String result) {
            int totalCount = 0;
            try {
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = new JSONObject(jsonArray.getString(i));
                    String rValue = jsonObject.getString("rValue");
                    String memberNum = jsonObject.getString("memberNum");
                    String memberName = jsonObject.getString("memberName");
                    String phone = jsonObject.getString("phone");

                    if (isNameSearch) {
                        searchCustomerName.add(new CustomerParent(rValue, memberNum, memberName, phone));
                    } else {
                        totalPageSize = jsonObject.getInt("totalPageSize");
                        totalCount = jsonObject.getInt("totalCount");
                        customerListGroups.add(new CustomerParent(rValue, memberNum, memberName, phone));
                    }
                }


                if (isNameSearch) {
                    if (searchCustomerName.get(0).getrValue().equals("y")) {
                        setAdapter(searchCustomerName);
                    }
                } else if (customerListGroups.get(0).getrValue().equals("n")) {
                    ll_no_have_customer.setVisibility(View.VISIBLE);
                    ll_have_customer.setVisibility(View.GONE);
                } else {
                    if (currentPage == 1) {
                        setAdapter(customerListGroups);
                    } else {
                        customerAdapter.notifyDataSetChanged();
                    }
                    tv_customerList.setText("고객리스트" + "(" + totalCount + ")");
                }

                dataTaskFinish = true;
                progressBar.setVisibility(View.GONE);
                progress_paging.setVisibility(View.GONE);
            } catch (JSONException e) {
                Log.e("TAG", "Could not parse JSON. Error: " + e.getMessage());
                progressBar.setVisibility(View.GONE);
                ll_have_customer.setVisibility(View.VISIBLE);
                progress_paging.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        CustomerListTaskKill();
        if (timer != null) {
            timer.cancel();
        }
    }

    private void CustomerListTaskKill() {
        if (customerTaskParent != null && customerTaskParent.getStatus() != CustomerTaskParent.Status.FINISHED) {
            customerTaskParent.cancel(true);
            customerTaskParent = null;
            Log.e("Front task kill", "");
        }
    }
}


class CustomerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<CustomerParent> mDataset;
    private final int TYPE_HEADER = 0;
    private final int TYPE_ITEM = 1;


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView list_parent_name;
        public TextView tv_phone;
        public ImageButton btn_call;
        public ImageButton btn_save;
        public LinearLayout ll_teamList_parent;
//        public View header_blank;

        public MyViewHolder(View v) {
            super(v);
            list_parent_name = v.findViewById(R.id.list_parent_name);
            tv_phone = v.findViewById(R.id.tv_phone);
            btn_call = v.findViewById(R.id.btn_call);
            btn_save = v.findViewById(R.id.btn_save);
            ll_teamList_parent = v.findViewById(R.id.ll_teamList_parent);
//            header_blank = v.findViewById(R.id.header_blank);
        }
    }


    public static class HeaderViewHolder extends RecyclerView.ViewHolder {

        public View header_blank;

        HeaderViewHolder(View headerView) {
            super(headerView);
            header_blank = headerView.findViewById(R.id.header_blank);

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public CustomerAdapter(ArrayList<CustomerParent> myDataset) {
        mDataset = myDataset;
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        RecyclerView.ViewHolder vh;
        if (viewType == TYPE_HEADER) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_header, parent, false);
            vh = new HeaderViewHolder(v);
            return vh;
        } else if (viewType == TYPE_ITEM) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_parent, parent, false);
            vh = new MyViewHolder(v);
            return vh;
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.header_blank.setVisibility(View.VISIBLE);
        } else if (holder instanceof MyViewHolder) {
            int itemPosition = position - 1;
            MyViewHolder itemViewHolder = (MyViewHolder) holder;
            itemViewHolder.list_parent_name.setText(mDataset.get(itemPosition).getName());

            itemViewHolder.tv_phone.setText(Util.phone(mDataset.get(itemPosition).getPhone()));
            itemViewHolder.btn_call.setFocusable(false);
            itemViewHolder.btn_call.setOnClickListener(n -> Util.callInWebView(mDataset.get(itemPosition).getPhone()));

            itemViewHolder.btn_save.setFocusable(false);
            itemViewHolder.btn_save.setOnClickListener(n -> Util.savePhone(mDataset.get(itemPosition).getName() + "(파이온 고객)", mDataset.get(itemPosition).getPhone()));
        }
    }


//            LinearLayout.LayoutParams plControl = (LinearLayout.LayoutParams) holder.ll_teamList_parent.getLayoutParams();
//            plControl.topMargin = 85;
//            holder.list_parent_name.setLayoutParams(plControl);


    @Override
    public int getItemCount() {
        return mDataset.size() + 1;
    }
}


class CustomerParent {

    public String rValue = "";
    public String num = "";
    public String name = "";
    public String phone = "";
    public String status = "";

    public CustomerParent(String rValue, String num, String name, String phone) {
        this.rValue = rValue;
        this.num = num;
        this.name = name;
        this.phone = phone;
    }


    public String getrValue() {
        return rValue;
    }

    public void setrValue(String rValue) {
        this.rValue = rValue;
    }

    public String getName() {
        return name;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}


