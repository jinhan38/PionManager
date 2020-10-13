package kr.co.pionmanager.www.Menu;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.widget.FrameLayout;

import androidx.core.content.ContextCompat;

public class FullscreenableChromeClient extends WebChromeClient {
    private static final String TAG = "FullscreenableChromeCli";
    private Activity mActivity = null;
    private View mCustomView;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    private int mOriginalOrientation;
    private FrameLayout mFullscreenContainer;
    private static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


    public FullscreenableChromeClient(Activity activity) {
        this.mActivity = activity;
    }

    //현재 페이지가 풀스크린 모드에 접속했다는 것을 알려줌
    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {

        //callback = 현재 페이지한테 custom View가 해제되었다는것을 알려주는 인터페이스

        if (mCustomView != null) {
            //onCustomViewHidden = 호스트 어플리케이션이 custom view를 닫을 때 호출된다.
            callback.onCustomViewHidden();
            return;
        }

        //현재 activit의 Orientation(방향)을 가져온다ㅏ.
        mOriginalOrientation = mActivity.getRequestedOrientation();
        //표준 윈도우 frame/decorations과 이 안에 있는 클라이언트 컨텐츠를 포함하고 있는 최상위 decor view를 검색한다.
        FrameLayout decor = (FrameLayout) mActivity.getWindow().getDecorView();


        //Framelayout을 상속받은 FullscreenHolder를 생성
        mFullscreenContainer = new FullscreenHolder(mActivity);
        //FullscreenHolderd에 풀스크린 모드인 view와 사이즈(COVER_SCREEN_PARAMS)를 addView해준다.
        mFullscreenContainer.addView(view, COVER_SCREEN_PARAMS);
        //
        decor.addView(mFullscreenContainer, COVER_SCREEN_PARAMS);
        mCustomView = view;
        setFullscreen(true);
        mCustomViewCallback = callback;
//          mActivity.setRequestedOrientation(requestedOrientation);

        super.onShowCustomView(view, callback);
    }


    @Override
    public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
        super.onShowCustomView(view, requestedOrientation, callback);
    }

    @Override
    public void onHideCustomView() {
        if (mCustomView == null) {
            return;
        }

        setFullscreen(false);
        FrameLayout decor = (FrameLayout) mActivity.getWindow().getDecorView();
        decor.removeView(mFullscreenContainer);
        mFullscreenContainer = null;
        mCustomView = null;
        mCustomViewCallback.onCustomViewHidden();
        mActivity.setRequestedOrientation(mOriginalOrientation);

    }

    private void setFullscreen(boolean enabled) {

        Window win = mActivity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
//        win.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        final int bits = WindowManager.LayoutParams.FLAG_FULLSCREEN ;
        if (enabled) {
            winParams.flags |= bits;
            mCustomView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE  );
            Log.e(TAG, "setFullscreen: enabled : " + enabled);
        } else {
            winParams.flags &= ~bits;
            Log.e(TAG, "setFullscreen: enabled : " + enabled);
            if (mCustomView != null) {
                mCustomView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            }
        }
        win.setAttributes(winParams);
    }

    private static class FullscreenHolder extends FrameLayout {
        public FullscreenHolder(Context ctx) {
            // super = 자식 클래스가 자신을 생성할 때 부모 클래스의 생성자를 불러 초기화 할때 사용된다.
            // FullscreenHolder가 부모 뷰인 FullscreenableChromeClient를 호출할 때 FullscreenableChromeClient의 내용을 상속받기 위해서 super 사용한다.
            super(ctx);
            setBackgroundColor(ContextCompat.getColor(ctx, android.R.color.black));
        }

        @Override
        public boolean onTouchEvent(MotionEvent evt) {
            return true;
        }
    }
}
