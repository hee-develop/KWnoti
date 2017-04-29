package kr.hee.kwnoti;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/** 웹 브라우저를 위한 웹뷰 클래스 */
public class Browser extends WebView {
    // 스크롤 리스너
    interface ScrollChangedCallback {
        public void onScroll(int cX,int cY,int oX,int oY);
    }
    private ScrollChangedCallback mScrollChangedCallback;

    // 생성자 - 세 개 전부 꼭 필요
    public Browser(final Context context) {
        super(context);
    }
    public Browser(final Context context, final AttributeSet attributeSet) {
        super(context, attributeSet);
    }
    public Browser(final Context context, final AttributeSet attributeSet, final int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
    }

    /** 웹뷰의 onScrollChanged()에 따른 스크롤 리스너 콜 */
    @Override protected void onScrollChanged(int curX, int curY, int oldX, int oldY) {
        super.onScrollChanged(curX, curY, oldX, oldY);

        mScrollChangedCallback.onScroll(curX, curY, oldX, oldY);
    }

    /** 스크롤 리스너 생성 메소드 */
    public void setOnScrollChangedCallback(ScrollChangedCallback callback) {
        mScrollChangedCallback = callback;
    }
}