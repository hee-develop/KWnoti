package jp.hee.tileview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TileView extends LinearLayout {
    private String mTitle;
    private int mBackGround;

    private TextView mTitleView;

    public TileView(Context context, AttributeSet attrs) {
        super(context);

        // get xml attribute
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TileView);

        if (array != null) {
            mTitle = array.getString(R.styleable.TileView_titleName);
            mBackGround = array.getColor(R.styleable.TileView_backgroundColor, Color.WHITE);

            // inflate layout
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.view_tile, this);

            //// draw
            // title
            mTitleView = findViewById(R.id.titleView);
            mTitleView.setText(mTitle);
            // background
            this.setBackgroundColor(mBackGround);

            array.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // get width&height of this view
        int viewWidth = getWidth();

        // control height
        ViewGroup.LayoutParams params = this.getLayoutParams();
        params.height = viewWidth;
//        this.setLayoutParams(params);

        super.onDraw(canvas);
    }
}
