package kr.co.pionmanager.www;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import kr.co.pionmanager.www.databinding.ActivityRegistrationListPageBinding;

public class RegistrationListPage extends Fragment implements BackButton, SwipeRefreshLayout.OnRefreshListener {


    //필터 걸고 스크롤 바닥에 닿으면 ajax 불러오기 에러 발생


    private static final String TAG = "RegistrationListPage";
    public ActivityRegistrationListPageBinding binding;
    private boolean isNameSearch = false;
    public ArrayList<RegistrationData> regInfoList = new ArrayList<RegistrationData>();
    public ArrayList<RegistrationData> searchRegInfoList = new ArrayList<RegistrationData>();
    private String strUrl_all;
    public RegistrationInfo_All_Task registrationInfo_all_task;
    private int currentPage = 1;
    private int totalPageSize = 0;
    private boolean isSwipeRefresh = false;
    private RegInfoAdapter adapter;
    private boolean isSwipeRefreshing = false;
    private Animation slideDown;
    public static boolean isFabOpen = false;
    private Animation fab_open, fab_close;
    public static RegistrationListPage registrationListPage;
    private int y = 0;
    boolean isDragging = true;
    private boolean isScrolled = false;
    private long backBtnTime = 0;
    public static RegistrationListPage singlton;
    private boolean dataTaskFinish = false;


    // newInstance constructor for creating fragment with arguments
    public static RegistrationListPage newInstance() {
        if (singlton == null) {
            singlton = new RegistrationListPage();
        }
        return singlton;
    }

    public void clearSingleTon() {
        this.singlton = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        registrationListPage = this;
        currentPage = 1;
        Log.e(TAG, "onCreateView: currentPage = " + currentPage);

        binding = DataBindingUtil.inflate(inflater, R.layout.activity_registration_list_page, container, false);

        slideDown = AnimationUtils.loadAnimation(Util.CURRENT_CONTEXT, R.anim.scroll_slide_down);
        fab_open = AnimationUtils.loadAnimation(Util.CURRENT_CONTEXT, R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(Util.CURRENT_CONTEXT, R.anim.fab_close);
        regInfoList.clear();
        putDataQuery("");
        setupListener();

        return binding.getRoot();
    }


    private void setupListener() {
        binding.swipeRefresh.setOnRefreshListener(this);
        fabFilter(binding.fabListViewFilterFirst);
        fabFilter(binding.fabFilterFinished);
        fabFilter(binding.fabFilterIng);
        fabFilter(binding.fabFilterCanceled);
        fabFilter(binding.fabFilterAll);
        fabFilter(binding.flFab);

        binding.etListSearch.setOnEditorActionListener((v, actionId, event) -> {
            Util.hideKeyboard(getActivity());
            binding.progressBar.setVisibility(View.VISIBLE);
            putDataQuery(binding.etListSearch.getText().toString().trim());
            return true;
        });

        binding.ibtnSearch.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Util.hideKeyboard(getActivity());
                binding.progressBar.setVisibility(View.VISIBLE);
                putDataQuery(binding.etListSearch.getText().toString().trim());
            }
        });

        binding.ibtnDelete.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                binding.etListSearch.setText("");
                binding.ibtnDelete.setVisibility(View.INVISIBLE);
                isNameSearch = false;
                searchRegInfoList.clear();
                setAdapter(regInfoList);
            }
        });

        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                y = dy;
                Log.e(TAG, "onScrolled: 괜히 한번 더 부름");
                int lastVisibleItemPosition = ((LinearLayoutManager) Objects.requireNonNull(recyclerView.getLayoutManager())).findLastCompletelyVisibleItemPosition();
                int itemTotalCount = recyclerView.getAdapter().getItemCount() - 1;
                if (lastVisibleItemPosition >= itemTotalCount - 1 && !isNameSearch && binding.etListSearch.getText().length() == 0 && !isSwipeRefreshing) {
                    if (totalPageSize > currentPage && dataTaskFinish && currentPage > 1) {
                        currentPage += 1;
                        binding.progressPaging.setVisibility(View.VISIBLE);
                        putDataQuery("");
                        dataTaskFinish = false;
                    }
                }
            }

//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if (y > 0) {
//                    if (isDragging) {
//                        binding.llRegListSearch.setVisibility(View.GONE);
//                        isDragging = false;
//                        isScrolled = true;
//                    }
//                } else {
//                    if (isScrolled) {
//                        binding.llRegListSearch.setVisibility(View.VISIBLE);
//                        binding.llRegListSearch.startAnimation(slideDown);
//                        isScrolled = false;
//                        isDragging = true;
//                    }
//                }
//            }
        });
    }

    public void putDataQuery(String name) {
        Log.e(TAG, "putDataQuery:");
        String n = Util.setEncoding(name);
        if (name.length() > 0) {
            Log.e(TAG, "putDataQuery: name 있음");
            isNameSearch = true;
            searchRegInfoList.clear();
            strUrl_all = Util.thepion_URL + "AjaxControl/registration_info_all?userNum=" + UserInfo.getUserNum() + "&currentPage=" + currentPage + "&mName=" + n;
        } else {
            Log.e(TAG, "putDataQuery: name 없음");
            strUrl_all = Util.thepion_URL + "AjaxControl/registration_info_all?userNum=" + UserInfo.getUserNum() + "&currentPage=" + currentPage;
        }
        registrationInfo_all_task = (RegistrationInfo_All_Task) new RegistrationInfo_All_Task().execute(strUrl_all);
    }

    @Override
    public void onBackPressed() {
        if (isFabOpen) {
            anim();
        } else if (binding.etListSearch.getText().toString().length() > 0) {
            binding.etListSearch.setText("");
            binding.ibtnDelete.setVisibility(View.INVISIBLE);
            searchRegInfoList.clear();
            isNameSearch = false;
            setAdapter(regInfoList);
        } else {
            long curTime = System.currentTimeMillis();
            long gapTime = curTime - backBtnTime;
            if (0 <= gapTime && 2000 >= gapTime) {
                getActivity().finish();
            } else {
                backBtnTime = curTime;
                Toast.makeText(Util.CURRENT_CONTEXT, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @SuppressLint("StaticFieldLeak")
    public class RegistrationInfo_All_Task extends AsyncTask<String, String, String> {

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
            getLoanInfoStatus(result);
            Log.e("result", result);
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
            int completeCount = 0;
            int canceledCount = 0;
            int ingCount = 0;
            int totalCount = 0;

            Log.e(TAG, "getLoanInfoStatus: ");

            if (isSwipeRefresh) {
                regInfoList.clear();
                searchRegInfoList.clear();
                isSwipeRefresh = false;
            }

            try {
                JSONArray jsonArray = new JSONArray(result);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = new JSONObject(jsonArray.getString(i));
                    String loanNum = jsonObject.getString("loanNum");
                    String auctionNum = jsonObject.getString("auctionNum");
                    String status = jsonObject.getString("status");
                    String name = jsonObject.getString("name");
                    String auctionCompleteDate = jsonObject.getString("auctionCompleteDate");
                    String color = jsonObject.getString("color");
                    String hopeLoanPrice = jsonObject.getString("hopeLoanPrice");
                    String maxPrice = jsonObject.getString("maxPrice");
                    int auctionHouseCount = jsonObject.getInt("auctionHouseCount");

                    if (isNameSearch) {
                        searchRegInfoList.add(new RegistrationData(loanNum, auctionNum, status, name, auctionCompleteDate, color, hopeLoanPrice, maxPrice, auctionHouseCount));
                    } else {
                        totalPageSize = jsonObject.getInt("totalPageSize");
                        completeCount = jsonObject.getInt("completeCount");
                        canceledCount = jsonObject.getInt("canceledCount");
                        ingCount = jsonObject.getInt("ingCount");
                        totalCount = jsonObject.getInt("totalCount");
                        regInfoList.add(new RegistrationData(loanNum, auctionNum, status, name, auctionCompleteDate, color, hopeLoanPrice, maxPrice, auctionHouseCount));
                    }
                }

                Log.e(TAG, "getLoanInfoStatus: regInfoListSize = " + regInfoList.size());

                if (isNameSearch) {
                    if (searchRegInfoList.get(0).getLoanNum().length() > 0) {
                        setAdapter(searchRegInfoList);
                    }
                } else if (regInfoList.get(0).getLoanNum().length() > 0) {
                    searchRegInfoList.clear();
                    if (currentPage == 1) {
                        //첫번째 페이지일 경우 recyclerView를 생성하는 시점이니 setAdapter를 해준다.
                        setAdapter(regInfoList);
                    } else {
                        //첫번째 페이지가 아닌경우에는 다시 setAdapter를 하는게 아니고, 데이터를 추가만 해주면 된다.
                        adapter.notifyDataSetChanged();
                    }

                    binding.noHaveRegistration.setVisibility(View.GONE);
                    binding.llRecyclerviewWrap.setVisibility(View.VISIBLE);
                    binding.fabListViewFilterFirst.setClickable(true);
                    binding.tvRegStatus.setText("등록현황(" + totalCount + ")");


                    binding.tvFabAll.setText("전체(" + totalCount + ")");
                    binding.tvFabIng.setText("진행(" + ingCount + ")");
                    binding.tvFabFinish.setText("완료(" + completeCount + ")");
                    binding.tvFabCancel.setText("취소(" + canceledCount + ")");
                    binding.llRegListSearch.setVisibility(View.VISIBLE);

                } else {
                    binding.llRegListSearch.setVisibility(View.GONE);
                    binding.noHaveRegistration.setVisibility(View.VISIBLE);

                    binding.fabListViewFilterFirst.setVisibility(View.GONE);
                    Util.setTextColor(getActivity(), R.string.registrationIntroduce_2, binding.registrationIntroduce2, "#56A2F4", 0, 4);
                    binding.llRecyclerviewWrap.setVisibility(View.GONE);
                    binding.progressBar.setVisibility(View.GONE);
                    binding.fabListViewFilterFirst.setClickable(false);
                }


                dataTaskFinish = true;

                //FCM 데이터 보낸 데이터 받는 코드
                //LoanInfo Num값 받아서 보내주기만 하면 됨


                binding.progressBar.setVisibility(View.GONE);
                binding.progressPaging.setVisibility(View.GONE);

                if (UserInfo.getPushOpen()) {
                    UserInfo.setPushOpen(false);
                    startActivity(new Intent(getActivity(), RegInfoDetail.class));
//                    binding.llRegListSearch.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                Log.e(Util.CURRENT_TAG, "Could not parse JSON. Error: " + e.getMessage());
            }
        }
    }

    private void setAdapter(ArrayList<RegistrationData> regInfoList) {
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new WrapLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        adapter = new RegInfoAdapter(regInfoList);
        binding.recyclerView.setAdapter(adapter);
    }

    @Override
    public void onRefresh() {

        isSwipeRefresh = true;

        binding.llRegListSearch.setVisibility(View.GONE);
        binding.swipeRefresh.setRefreshing(true);

//        regInfoList.clear();
        binding.etListSearch.setText(null);
        isNameSearch = false;
        isSwipeRefreshing = true;

        binding.FLNoTouch.setVisibility(View.VISIBLE);
        binding.FLNoTouch.setClickable(true);
        currentPage = 1;
        putDataQuery("");
        new Handler().postDelayed(() -> {
            Log.e(TAG, "onRefresh: ");
            binding.swipeRefresh.setRefreshing(false);
            binding.FLNoTouch.setVisibility(View.GONE);
            binding.FLNoTouch.setClickable(false);
            binding.llRegListSearch.setVisibility(View.VISIBLE);
            binding.llRegListSearch.startAnimation(slideDown);
            isSwipeRefreshing = false;
        }, 1500);

    }

    /**
     * 등록현황 필터 메소드
     *
     * @param v
     */
    private void fabFilter(View v) {
        v.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                switch (v.getId()) {
                    case R.id.fabListViewFilterFirst:
                        binding.etListSearch.setText(null);
                        isNameSearch = false;
                        anim();
                        break;
                    case R.id.fabFilterFinished:
                        setFilterView("g");
                        break;
                    case R.id.fabFilterIng:
                        setFilterView("b");
                        break;
                    case R.id.fabFilterCanceled:
                        setFilterView("r");
                        break;
                    case R.id.fabFilterAll:
                        setFilterView("all");
                        break;
                    case R.id.flFab:
                        anim();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void setFilterView(String statusColor) {
        Stream<RegistrationData> filter = null;
        if (statusColor.equals("all")) {
            filter = regInfoList.stream();
        } else {
            filter = regInfoList.stream().filter(x -> x.getColor().equals(statusColor));
        }
        setAdapter(getArrayListFromStream(filter));
        binding.progressBar.setVisibility(View.GONE);
        binding.llRecyclerviewWrap.setVisibility(View.VISIBLE);
        anim();
    }


    private static <T> ArrayList<T> getArrayListFromStream(Stream<T> stream) {
        List<T> list = stream.collect(Collectors.toList());
        ArrayList<T> arrayList = new ArrayList<T>(list);
        return arrayList;
    }


    /**
     * 등록현황 필터 애니메이션 열기 닫기 메소드
     */
    public void anim() {
        if (isFabOpen) {
            hideFAB();
            binding.tvFabAll.startAnimation(fab_close);
            binding.tvFabCancel.startAnimation(fab_close);
            binding.tvFabIng.startAnimation(fab_close);
            binding.tvFabFinish.startAnimation(fab_close);
            binding.tvFabAll.setVisibility(View.GONE);
            binding.tvFabCancel.setVisibility(View.GONE);
            binding.tvFabIng.setVisibility(View.GONE);
            binding.tvFabFinish.setVisibility(View.GONE);

            binding.fabFilterAll.startAnimation(fab_close);
            binding.fabFilterFinished.startAnimation(fab_close);
            binding.fabFilterIng.startAnimation(fab_close);
            binding.fabFilterCanceled.startAnimation(fab_close);

            binding.fabFilterAll.setVisibility(View.GONE);
            binding.fabFilterFinished.setVisibility(View.GONE);
            binding.fabFilterCanceled.setVisibility(View.GONE);
            binding.fabFilterIng.setVisibility(View.GONE);
            binding.fabFilterAll.setClickable(false);
            binding.fabFilterFinished.setClickable(false);
            binding.fabFilterCanceled.setClickable(false);
            binding.fabFilterIng.setClickable(false);
            isFabOpen = false;
        } else {
            revealFAB();
            binding.flFab.setVisibility(View.VISIBLE);
            binding.tvFabAll.startAnimation(fab_open);
            binding.tvFabCancel.startAnimation(fab_open);
            binding.tvFabIng.startAnimation(fab_open);
            binding.tvFabFinish.startAnimation(fab_open);

            binding.tvFabAll.setVisibility(View.VISIBLE);
            binding.tvFabCancel.setVisibility(View.VISIBLE);
            binding.tvFabIng.setVisibility(View.VISIBLE);
            binding.tvFabFinish.setVisibility(View.VISIBLE);

            binding.fabFilterAll.setVisibility(View.VISIBLE);
            binding.fabFilterFinished.setVisibility(View.VISIBLE);
            binding.fabFilterCanceled.setVisibility(View.VISIBLE);
            binding.fabFilterIng.setVisibility(View.VISIBLE);
            binding.fabFilterAll.startAnimation(fab_open);
            binding.fabFilterFinished.startAnimation(fab_open);
            binding.fabFilterIng.startAnimation(fab_open);
            binding.fabFilterCanceled.startAnimation(fab_open);
            binding.fabFilterAll.setClickable(true);
            binding.fabFilterFinished.setClickable(true);
            binding.fabFilterCanceled.setClickable(true);
            binding.fabFilterIng.setClickable(true);
            isFabOpen = true;
        }
    }

    /**
     * 등록현황 필터 클릭 시 물결 퍼지게하는 메소드
     */
    private void revealFAB() {
        Rect r = new Rect();
        binding.fabListViewFilterFirst.getGlobalVisibleRect(r);
        int cx = ((int) binding.fabListViewFilterFirst.getRight() - 55);
        int cy = ((int) binding.fabListViewFilterFirst.getY() + 68);

        float finalRadius = (float) Math.hypot(cx, cy);
        Animator revealAnimator = ViewAnimationUtils.createCircularReveal(binding.flFab, cx, cy, 0, finalRadius);
        binding.flFab.setVisibility(View.VISIBLE);
        revealAnimator.start();

        Log.e(TAG, "revealFAB: " + finalRadius);
    }

    /**
     * 등록현활 필터 클릭 시 물결 닫는 메소드
     */
    private void hideFAB() {
        int cx = ((int) binding.fabListViewFilterFirst.getRight() - 55);
        int cy = ((int) binding.fabListViewFilterFirst.getY() + 68);

        float finalRadius = (float) Math.hypot(cx, cy);
        Animator hideAnimator = ViewAnimationUtils.createCircularReveal(binding.flFab, cx, cy, finalRadius, 0);
        hideAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                binding.flFab.setVisibility(View.GONE);
            }
        });
        hideAnimator.start();
        Log.e(TAG, "hideFAB: " + finalRadius);
    }

    public void registrationInfo_all_taskKill() {
        if (registrationInfo_all_task != null && registrationInfo_all_task.getStatus() != RegistrationInfo_All_Task.Status.FINISHED) {
            registrationInfo_all_task.cancel(true);
            registrationInfo_all_task = null;
            binding.progressPaging.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        registrationInfo_all_taskKill();
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.e(TAG, "onResume: ");
        if (!dataTaskFinish) {
            Log.e(TAG, "onResume: datatTaskFinish : " + dataTaskFinish);
//            putDataQuery("");
            dataTaskFinish = true;
        }
        super.onResume();
    }
}
