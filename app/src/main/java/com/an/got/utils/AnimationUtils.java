package com.an.got.utils;

import android.os.CountDownTimer;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.View;
import android.widget.TextView;

import com.an.got.R;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

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
        tv.setTextColor(color);
    }

    public void slideFromBottom(View view) {
        YoYo.with(Techniques.SlideInUp)
                .duration(1200)
                .playOn(view);
    }

    public void slideOutLeft(View view) {
        YoYo.with(Techniques.SlideOutLeft)
                .duration(1000)
                .playOn(view.findViewById(R.id.questionTxt));

        YoYo.with(Techniques.SlideOutLeft)
                .duration(1000)
                .playOn(view.findViewById(R.id.app_list));

        YoYo.with(Techniques.SlideOutLeft)
                .duration(1000)
                .playOn(view.findViewById(R.id.line));
    }

    public void slideInRight(View view) {
        YoYo.with(Techniques.SlideInRight)
                .duration(1000)
                .playOn(view.findViewById(R.id.questionTxt));

        YoYo.with(Techniques.SlideInRight)
                .duration(1000)
                .playOn(view.findViewById(R.id.app_list));

        YoYo.with(Techniques.SlideInRight)
                .duration(1000)
                .playOn(view.findViewById(R.id.line));
    }

    public void flipAndSlideOutLeft(View view) {
        YoYo.with(Techniques.FlipOutX)
                .duration(1000)
                .playOn(view.findViewById(R.id.imgBanner));

        YoYo.with(Techniques.SlideOutLeft)
                .duration(1000)
                .playOn(view.findViewById(R.id.app_list));
    }

    public void flipAndSlideInRight(View view) {
        YoYo.with(Techniques.FlipInX)
                .duration(1000)
                .playOn(view.findViewById(R.id.imgBanner));

        YoYo.with(Techniques.SlideInRight)
                .duration(1000)
                .playOn(view.findViewById(R.id.app_list));
    }

    public void flipOVer(View view) {
        YoYo.with(Techniques.FlipOutX)
                .duration(1000)
                .playOn(view);
    }
}
