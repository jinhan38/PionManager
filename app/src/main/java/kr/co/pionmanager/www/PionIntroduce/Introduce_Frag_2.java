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

public class Introduce_Frag_2 extends Fragment {
    private int page;
    public Animation slide_up_1;
    public Animation slide_up_2;
    public Animation slide_up_3;
    public TextView introduce_frag_2_top;
    public ImageView introduce_frag_2_image;
    public TextView introduce_frag_2_bottom;
    public static Introduce_Frag_2 introduce_frag_2;
    public boolean animatedFrag = false;

    // newInstance constructor for creating fragment with arguments
    public static Introduce_Frag_2 newInstance(int page) {
        Introduce_Frag_2 fragment = new Introduce_Frag_2();
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
        View view = inflater.inflate(R.layout.activity_introduce_frag_2, container, false);
        Util.SaveCurrentContext(getActivity());
        introduce_frag_2 = this;

        slide_up_1 = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        slide_up_2 = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        slide_up_3 = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);

        introduce_frag_2_top = view.findViewById(R.id.introduce_frag_2_top);
        introduce_frag_2_image = view.findViewById(R.id.introduce_frag_2_image);
        introduce_frag_2_bottom = view.findViewById(R.id.introduce_frag_2_bottom);

        resourceGone();

        return view;
    }

    public void animationStart() {
        if (!animatedFrag) {
            if (IntroduceParent.ViewPagerNum == 1) {

                Timer timer = new Timer();
                final android.os.Handler handler = new android.os.Handler();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.post(() -> {
                            introduce_frag_2_image.setVisibility(View.VISIBLE);
                            introduce_frag_2_image.startAnimation(slide_up_2);
                        });
                    }
                }, 200);

                Timer timer2 = new Timer();
                final android.os.Handler handler2 = new android.os.Handler();
                timer2.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler2.post(() -> {
                            introduce_frag_2_bottom.startAnimation(slide_up_3);
                            introduce_frag_2_bottom.setVisibility(View.VISIBLE);
                        });
                    }
                }, 700);
            }
        }

    }

    public void resourceGone() {
        introduce_frag_2_image.setVisibility(View.GONE);
        introduce_frag_2_bottom.setVisibility(View.GONE);
    }

}