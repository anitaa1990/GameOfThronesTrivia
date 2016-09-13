package com.an.got.utils;

import android.os.CountDownTimer;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.View;
import android.widget.TextView;
import com.github.florent37.viewanimator.ViewAnimator;

public class AnimationUtils {

    private static AnimationUtils instance;
    public static AnimationUtils getInstance() {
        if(instance == null)
            instance = new AnimationUtils();
        return instance;
    }

    public void animateStrikeThrough(final TextView tv) {
        final int ANIM_DURATION = 1000;              //duration of animation in millis
        final int length = tv.getText().length();
        new CountDownTimer(ANIM_DURATION, ANIM_DURATION/length) {
            Spannable span = new SpannableString(tv.getText());
            StrikethroughSpan strikethroughSpan = new StrikethroughSpan();

            @Override
            public void onTick(long millisUntilFinished) {
                //calculate end position of strikethrough in textview
                int endPosition = (int) (((millisUntilFinished-ANIM_DURATION)*-1)/(ANIM_DURATION/length));
                endPosition = endPosition > length ?
                        length : endPosition;
                span.setSpan(strikethroughSpan, 0, endPosition,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv.setText(span);
            }

            @Override
            public void onFinish() {}
        }.start();
    }

    public void animateCorrectResponse(final TextView tv, int color) {
        ViewAnimator.animate(tv).pulse().duration(500).textColor(color)
                .start();
    }

    public void slideFromBottom(View view) {
        ViewAnimator.animate(view).slideBottom().repeatCount(0)
                .start();
    }

    public void slideToLeft(View view) {
        ViewAnimator.animate(view).slideRight().repeatCount(0)
                .duration(1000)
                .start();
    }

    public void slideInFromRight(View view) {
        ViewAnimator.animate(view).slideRight().repeatCount(0)
                .start();
    }
}
