package com.an.got.views;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.an.got.R;


public class CustomTextView extends TextView {

    public CustomTextView(Context context) {
        super(context);
        setTypeFace(1);
    }

    public CustomTextView(Context context, AttributeSet attrs) {

        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.CustomTextView);
        final int fontValue = a.getInt(R.styleable.CustomTextView_font, 0);
        a.recycle();
        setTypeFace(fontValue);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.CustomTextView);
        final int fontValue = a.getInt(R.styleable.CustomTextView_font, 0);
        a.recycle();
        setTypeFace(fontValue);
    }

    public void setTypeFace(int fontValue) {

        Typeface myTypeFace;
        if (fontValue == 1) {
            myTypeFace = Typeface.createFromAsset(this.getContext().getAssets(),"architect_dauhter.ttf");
            this.setTypeface(myTypeFace);
        } else if (fontValue == 2) {
            myTypeFace = Typeface.createFromAsset(this.getContext().getAssets(), "halo_handletter.otf");
            this.setTypeface(myTypeFace);
        } else if (fontValue == 3) {
            myTypeFace = Typeface.createFromAsset(this.getContext().getAssets(), "gt_medium.otf");
            this.setTypeface(myTypeFace);
        } else if (fontValue == 4) {
            myTypeFace = Typeface.createFromAsset(this.getContext().getAssets(), "circular_regular.ttf");
            this.setTypeface(myTypeFace);
        } else if (fontValue == 5) {
            myTypeFace = Typeface.createFromAsset(this.getContext().getAssets(), "lobster-regular.otf");
            this.setTypeface(myTypeFace);
        }
    }
}
