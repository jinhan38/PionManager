package kr.co.pionmanager.www.PionIntroduce;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import kr.co.pionmanager.www.R;
import kr.co.pionmanager.www.Util;
import me.relex.circleindicator.CircleIndicator;

public class IntroduceParent extends AppCompatActivity {
    FragmentPagerAdapter adapterViewPager;
    public static int ViewPagerNum = 0;
    public static IntroduceParent introduceParent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduce_parent);
//        Util.setStatusBarColor(this, Util.StatusBarColorType.SOFTBLUE_STATUS_BAR);
        Util.SaveCurrentContext(this);
        introduceParent = this;

        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);
        vpPager.setOffscreenPageLimit(2);

        vpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                ViewPagerNum = position;
                switch (position) {
                    case 0:
//                        Introduce_Frag_1.introduce_frag_1.animationStart();
//                        Introduce_Frag_1.introduce_frag_1.animatedFrag = true;
                        Log.e("onPageSelected", 0 + "");
                        break;
                    case 1:
                        if (!Introduce_Frag_2.introduce_frag_2.animatedFrag){
                            Introduce_Frag_2.introduce_frag_2.animationStart();
                            Introduce_Frag_2.introduce_frag_2.animatedFrag = true;
                        }
                        Log.e("onPageSelected", 1 + "");
                        break;
                    case 2:
                        if (!Introduce_Frag_3.introduce_frag_3.animatedFrag){
                            Introduce_Frag_3.introduce_frag_3.animationStart();
                            Introduce_Frag_3.introduce_frag_3.animatedFrag = true;
                        }

                        Log.e("onPageSelected", 2 + "");
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(vpPager);
    }


    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 3;

        MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return Introduce_Frag_1.newInstance(0);
                case 1:
                    return Introduce_Frag_2.newInstance(1);
                case 2:
                    return Introduce_Frag_3.newInstance(2);
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }


    }


}