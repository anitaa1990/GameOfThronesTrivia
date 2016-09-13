package com.an.got.base;


import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.an.got.R;
import com.an.got.model.Question;
import com.an.got.utils.AnimationUtils;
import com.an.got.utils.Utils;
import com.an.got.views.explosive.ExplosionField;


public class BaseActivity extends AppCompatActivity {

    private TextView timerTxt;
    private TextView scoreTxt;

    private ExplosionField mExplosionField;

    private Question currentQuestion;
    private CountDownTimer timer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setTheme(R.style.Theme_AppCompat_Light_NoActionBar);
    }

    public void setUpTimer() {
        timerTxt = (TextView) findViewById(R.id.timer);
        scoreTxt = (TextView) findViewById(R.id.score);
        mExplosionField = ExplosionField.attach2Window(this);
    }

    public void startTimer() {
        if(timerTxt == null) setUpTimer();

       timer = new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                timerTxt.setText(String.valueOf(millisUntilFinished / 1000));
            }
            public void onFinish() {
                //time's up
//                handleGameOver();
            }
        }.start();
    }


    protected void handleIncorrectResponse(View root, View view) {
           /* update the no of guesses */
            getCurrentQuestion().updateTries();

            /* strike out the incorrect answer */
            TextView tv = (TextView) view.findViewById(R.id.answerTxt);
            tv.setTextColor(Color.parseColor("#7b0303"));
            AnimationUtils.getInstance().animateStrikeThrough(tv);

            /* 3 strikes and he's out! */
            if(getCurrentQuestion().getNumTries() >= getCurrentQuestion().getMaxTries()) {
                handleGameOver(root);
            }
    }

    protected void handleCorrectResponse(View root, View view) {
        TextView tv = (TextView) view.findViewById(R.id.answerTxt);
        AnimationUtils.getInstance().animateCorrectResponse(tv, Color.parseColor("#0e5b02"));

        timer.cancel();
        /* update score */
        updateScore();
        AnimationUtils.getInstance().slideToLeft(root);
    }

    private void handleGameOver(final View root) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mExplosionField.explode(root);
                Toast.makeText(BaseActivity.this, "Oops..Game Over!", Toast.LENGTH_LONG).show();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        overridePendingTransition(R.anim.stay, R.anim.slide_top_to_bottom);
                    }
                }, 2000);
            }
        }, 1000);
    }

    public void updateScore() {
        if(scoreTxt == null) setUpTimer();
        int timeRemaining = Integer.valueOf(timerTxt.getText().toString());
        long score = Utils.getScoreForQuestion(timeRemaining, getCurrentQuestion().getNumTries());
        score = Long.valueOf(scoreTxt.getText().toString()) + score;
        scoreTxt.setText(String.valueOf(score));
    }

    public Question getCurrentQuestion() {
        return currentQuestion;
    }

    public void setCurrentQuestion(Question currentQuestion) {
        this.currentQuestion = currentQuestion;
    }
}
