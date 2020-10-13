package kr.co.pionmanager.www;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * recyclerView의 notifySetDataChanged와 swipeRefreshing을 같이 쓰면 Inconsistency detected. 에러가 발생한다.
 * recyclerView의 포지션 값 부분에서 에러가 발생하는 듯 하다.
 * 그래서 이 클래스 만들어서 넣었음
 */
class WrapLinearLayoutManager extends LinearLayoutManager {

    public WrapLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            Log.e("TAG", "meet a IOOBE in RecyclerView");
        }
    }
}

abstract class SingleClickListener implements View.OnClickListener {

    private static final long MIN_CLICK_INTERVAL = 300;
    private long mLastClickTime;

    public abstract void onSingleClick(View v);

    @Override
    public void onClick(View v) {
        long currentClickTime = SystemClock.uptimeMillis();
        long elapsedTime = currentClickTime - mLastClickTime;
        mLastClickTime = currentClickTime;
        if (elapsedTime <= MIN_CLICK_INTERVAL) {
            return;
        }
        onSingleClick(v);
    }
}