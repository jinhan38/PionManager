package kr.co.pionmanager.www;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class MyViewPager extends ViewPager {
    private static final String TAG = "MyViewPager";
    public static boolean isWebViewScrollPossible = false;
//    private int childId;
//    public boolean canSwipe = false;
//    float actionDownX = -1;


    public MyViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyViewPager(@NonNull Context context) {
        super(context);
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        boolean canChildViewsScroll;

        if (isWebViewScrollPossible) canChildViewsScroll = true;
        else canChildViewsScroll = false;


//        if (canChildViewsScroll){
//            AddAddressPage.addAddressPage. webView_search.setOnScrollChangedCallback((l, t, oldl, oldt) -> {
//                if(t> oldt){
//                    //Do stuff
//                    Log.e(TAG, "onScroll: Swipe UP" );
//                    //Do stuff
//                }
//                else if(t< oldt){
//                    Log.e(TAG, "onScroll: Swipe DOWN" );
//                }
//                Log.d(TAG,"We Scrolled etc...");
//            });
//        }

        return canChildViewsScroll;
    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
////        return super.onTouchEvent(ev);
//        Log.e(TAG, "onTouchEvent: " );
//        return false;
//    }


//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent event) {
//
//        // 자식 View에게 event가 전달되는 것을 가로챈다. // 자식의 touch event보다 우선시 된다.
////            switch (event.getAction()) {
////                case MotionEvent.ACTION_DOWN:
////                    actionDownX = event.getX();
////                    break;
////                case MotionEvent.ACTION_CANCEL:
////                case MotionEvent.ACTION_MOVE:
////                case MotionEvent.ACTION_UP:
////                default:
////                    if (getFocusedChild() instanceof ExtendedWebView) {
////                        if (actionDownX < 200 || actionDownX > (getMeasuredWidth() - 200)) {
////                            return canSwipe ? super.onInterceptTouchEvent(event) : false;
////                        } else {
////                            return false;
////                        }
////
////                    }
////            }
////        return canSwipe ? super.onInterceptTouchEvent(event) : false;
//        Log.e(TAG, "onInterceptTouchEvent: " );
//        return false;
//    }

}
