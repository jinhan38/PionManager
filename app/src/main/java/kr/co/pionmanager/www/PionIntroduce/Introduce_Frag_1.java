package kr.co.pionmanager.www.PionIntroduce;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.Timer;
import java.util.TimerTask;

import kr.co.pionmanager.www.R;
import kr.co.pionmanager.www.Util;

public class Introduce_Frag_1 extends Fragment {
    private int page;
    private Animation slide_up_1;
    private Animation slide_up_2;
    private Animation slide_up_3;
    private TextView introduce_frag_1_top;
    public ImageView introduce_frag_1_image;
    public TextView introduce_frag_1_bottom;

    public static Introduce_Frag_1 introduce_frag_1;
    public boolean animatedFrag = false;


    // newInstance constructor for creating fragment with arguments
    public static Introduce_Frag_1 newInstance(int page) {
        Introduce_Frag_1 fragment = new Introduce_Frag_1();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_introduce_frag_1, container, false);
        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        Util.SaveCurrentContext(getActivity());

//        ImageView transition_backgroud = view.findViewById(R.id.transition_backgroud);
//        TransitionDrawable td = (TransitionDrawable) getResources().getDrawable( R.drawable.transition_drawable );
//        transition_backgroud.setImageDrawable( td );
//        td.startTransition( 1000 );


        introduce_frag_1 = this;
        slide_up_1 = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        slide_up_2 = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        slide_up_3 = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);

        introduce_frag_1_top = view.findViewById(R.id.introduce_frag_1_top);
        introduce_frag_1_image = view.findViewById(R.id.introduce_frag_1_image);
        introduce_frag_1_bottom = view.findViewById(R.id.introduce_frag_1_bottom);



        slide_up_1.setDuration(1000);
        introduce_frag_1_top.startAnimation(slide_up_1);

        IntroduceParent.ViewPagerNum = 0;

        if (!animatedFrag) {
            resourceGone();
            animationStart();
            animatedFrag = true;
        }

        return view;
    }


    public void animationStart() {

        if (!animatedFrag) {
            if (IntroduceParent.ViewPagerNum == 0) {
                Timer timer = new Timer();
                final android.os.Handler handler = new android.os.Handler();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.post(() -> {
                            introduce_frag_1_image.setVisibility(View.VISIBLE);
                            introduce_frag_1_image.startAnimation(slide_up_2);
                        });
                    }
                }, 500);

                Timer timer2 = new Timer();
                final android.os.Handler handler2 = new android.os.Handler();
                timer2.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler2.post(() -> {
                            introduce_frag_1_bottom.startAnimation(slide_up_3);
                            introduce_frag_1_bottom.setVisibility(View.VISIBLE);
                        });
                    }
                }, 1200);
            }
            animatedFrag = true;
        }
    }

    public void resourceGone() {
        introduce_frag_1_image.setVisibility(View.GONE);
        introduce_frag_1_bottom.setVisibility(View.GONE);
    }


}