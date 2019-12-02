package jp.hee.cardwithbackground;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

public class CardWithBackground extends RelativeLayout {
    final String TAG = "CARD_WITH_BACKGROUND";

    TextView titleTextView;
    ImageView backgroundImageView;

    String title = null;
    int titleColor = -1;
    int imgSrc = -1;
    int imgColor = -1;

    public CardWithBackground(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public CardWithBackground(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);
    }

    public CardWithBackground(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, attrs, defStyleAttr);
    }

    // Initialize attribute and layout
    private void initView(Context context, AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater == null) {
            Log.e(TAG, "Cannot find inflater.");
            return;
        }
        inflater.inflate(R.layout.card_with_background, this);

        // call onFinishInflate and make view

        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CardWithBackground);
            title = ta.getString(R.styleable.CardWithBackground_title);
            titleColor = ta.getResourceId(R.styleable.CardWithBackground_title_color, -1);
            imgSrc = ta.getResourceId(R.styleable.CardWithBackground_background, -1);
            imgColor = ta.getResourceId(R.styleable.CardWithBackground_background_color, -1);

            ta.recycle();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // set basic attribute
        this.setBackgroundColor(Color.WHITE);
        this.setClickable(true);
        this.setFocusable(true);


        titleTextView = findViewById(R.id.title);
        backgroundImageView = findViewById(R.id.background);

        if (title == null) title = "";
        titleTextView.setText(title);
        if (titleColor != -1) titleTextView.setTextColor(ContextCompat.getColor(getContext(), titleColor));

        if (imgSrc != -1) backgroundImageView.setImageDrawable(ContextCompat.getDrawable(getContext(), imgSrc));
        if (imgColor != -1) backgroundImageView.setColorFilter(ContextCompat.getColor(getContext(), imgColor));
    }
}
