package kr.hee.kwnoti;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

public class Browser extends WebView {
    private ScrollChangedCallback mScrollChangedCallback;

    public Browser(final Context context) {
        super(context);
    }
    public Browser(final Context context, final AttributeSet attributeSet) {
        super(context, attributeSet);
    }
    public Browser(final Context context, final AttributeSet attributeSet, final int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
    }

    public interface ScrollChangedCallback {
        public void onScroll(int cX,int cY,int oX,int oY);
    }

    @Override protected void onScrollChanged(int curX, int curY, int oldX, int oldY) {
        super.onScrollChanged(curX, curY, oldX, oldY);

        mScrollChangedCallback.onScroll(curX, curY, oldX, oldY);
    }

    public void setOnScrollChangedCallback(ScrollChangedCallback callback) {
        mScrollChangedCallback = callback;
    }
}