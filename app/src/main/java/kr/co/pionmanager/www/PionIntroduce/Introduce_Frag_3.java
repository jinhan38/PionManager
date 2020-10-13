package kr.co.pionmanager.www.PionIntroduce;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.Timer;
import java.util.TimerTask;

import kr.co.pionmanager.www.LoginPage;
import kr.co.pionmanager.www.OnSingleClickListener;
import kr.co.pionmanager.www.R;
import kr.co.pionmanager.www.SharedPreference;
import kr.co.pionmanager.www.Util;

public class Introduce_Frag_3 extends Fragment {
    private int page;
    private Animation slide_up_1;
    private Animation slide_up_2;
    private Animation slide_up_3;
    private Animation slide_up_4;
    private TextView introduce_frag_3_top;
    public ImageView introduce_frag_3_image;
    public TextView introduce_frag_3_bottom;
    public static Introduce_Frag_3 introduce_frag_3;
    public boolean animatedFrag = false;
    public Button introduce_frag_3_next;
    private boolean isFirstChecked = false;
    public static Context introduceContext;

    // newInstance constructor for creating fragment with arguments
    public static Introduce_Frag_3 newInstance(int page) {
        Introduce_Frag_3 fragment = new Introduce_Frag_3();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        fragment.setArguments(args);
        return fragment;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);

    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_introduce_frag_3, container, false);
        Util.SaveCurrentContext(getActivity());
        introduce_frag_3 = this;
        introduceContext = getActivity();

        slide_up_1 = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        slide_up_2 = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        slide_up_3 = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        slide_up_4 = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);

        introduce_frag_3_top = view.findViewById(R.id.introduce_frag_3_top);
        introduce_frag_3_image = view.findViewById(R.id.introduce_frag_3_image);
        introduce_frag_3_bottom = view.findViewById(R.id.introduce_frag_3_bottom);
        introduce_frag_3_next = view.findViewById(R.id.introduce_frag_3_next);

        Display display = getActivity().getWindowManager().getDefaultDisplay();



        introduce_frag_3_next.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                SharedPreference.getSharedPreferences(getActivity());
                SharedPreference.setCloseChecked(getActivity(), true);
                Util.intentNext(LoginPage.class);
                requireActivity().finish();
            }
        });

        if (SharedPreference.getCloseChecked(getActivity())) {
            introduce_frag_3_next.setVisibility(View.GONE);
        }

        resourceGone();

        return view;
    }

    public void animationStart() {
        Log.e("animationStart", "진입");
        if (!animatedFrag) {
            Log.e("animatedFrag_false", animatedFrag + "");
            if (IntroduceParent.ViewPagerNum == 2) {
                Timer timer = new Timer();
                final android.os.Handler handler = new android.os.Handler();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.post(() -> {
                            introduce_frag_3_image.setVisibility(View.VISIBLE);
                            introduce_frag_3_image.startAnimation(slide_up_2);
                        });
                    }
                }, 200);

                Timer timer2 = new Timer();
                final android.os.Handler handler2 = new android.os.Handler();
                timer2.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler2.post(() -> {
                            introduce_frag_3_bottom.startAnimation(slide_up_3);
                            introduce_frag_3_bottom.setVisibility(View.VISIBLE);
                        });
                    }
                }, 700);

                Log.e("introduce_frag_3_next", SharedPreference.getCloseChecked(getActivity()) + "");
                if (SharedPreference.getCloseChecked(getActivity())) {
                    introduce_frag_3_next.setVisibility(View.GONE);
                } else {
                    Timer timer3 = new Timer();
                    final android.os.Handler handler3 = new android.os.Handler();
                    timer3.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            handler3.post(() -> {
                                introduce_frag_3_next.startAnimation(slide_up_4);
                                introduce_frag_3_next.setVisibility(View.VISIBLE);
                            });
                        }
                    }, 1500);
                }
            }
        }
        Log.e("animatedFrag_true", animatedFrag + "");
    }

    public void resourceGone() {
        introduce_frag_3_image.setVisibility(View.GONE);
        introduce_frag_3_bottom.setVisibility(View.GONE);
        introduce_frag_3_next.setVisibility(View.GONE);
    }
}