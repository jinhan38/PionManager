package kr.co.pionmanager.www;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

public class ExtendedWebView extends WebView {
    public ExtendedWebView(Context context) {
        super(context);
    }

    public ExtendedWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        final int offset = computeHorizontalScrollOffset();
        final int range = computeHorizontalScrollRange() - computeHorizontalScrollExtent();
        if (range == 0) return false;
        if (direction < 0) {
            return offset > 0;
        } else {
            return offset < range - 1;
        }
    }
}
