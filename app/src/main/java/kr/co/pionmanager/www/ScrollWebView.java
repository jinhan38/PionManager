package kr.co.pionmanager.www;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

public class ScrollWebView extends WebView {

    private OnScrollChangedCallback mOnScrollChangedCallback;

    public ScrollWebView(Context context) {
        super(context);
    }

    public ScrollWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
    }


    public OnScrollChangedCallback getOnScrollChangedCallback()
    {
        return mOnScrollChangedCallback;
    }

    public void setOnScrollChangedCallback(final OnScrollChangedCallback onScrollChangedCallback)
    {
        mOnScrollChangedCallback = onScrollChangedCallback;
    }

    public static interface OnScrollChangedCallback
    {
        public void onScroll(int l, int t, int oldl, int oldt);
    }

}
