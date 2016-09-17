package com.an.got.base;


import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.an.got.R;
import com.an.got.model.Question;
import com.an.got.model.Survey;
import com.an.got.utils.AnimationUtils;
import com.an.got.utils.Utils;
import com.an.got.views.explosive.ExplosionField;

import java.util.Collections;


public class BaseActivity extends AppCompatActivity {

    private TextView timerTxt;
    private TextView scoreTxt;

    private ExplosionField mExplosionField;

    private Survey currentSurvey;
    private int currentIndex;

    private CountDownTimer timer;
    private int totalTime;

    protected Handler handler = new Handler(Looper.getMainLooper());

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

    public void startTimer(int totalTime) {
        this.totalTime = totalTime;
        if(timerTxt == null) setUpTimer();
        if(timer != null) timer.cancel();
       timer = new CountDownTimer(totalTime, 1000) {
            public void onTick(long millisUntilFinished) {
                timerTxt.setText(String.valueOf(millisUntilFinished / 1000));
            }
            public void onFinish() {
                //time's up
                //TODO : need to add a callback to each of the activities once the time is up!!
                handleGameOver(null);
            }
        }.start();
    }


    protected void handleIncorrectResponse(View root) {
           /* update the no of guesses */
            getCurrentQuestion().updateTries();

            /* 3 strikes and he's out! */
            if(getCurrentQuestion().getNumTries() >= getCurrentQuestion().getMaxTries()) {
                handleGameOver(root);
            }
    }

    protected void handleCorrectResponse() {
        /* cancel timer
        /* update the no of guesses
        /* update score
        /* animation this set of question to the left */
        timer.cancel();
        getCurrentQuestion().updateTries();
        updateScore();

    }

    private void handleGameOver(final View root) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(root != null) mExplosionField.explode(root);
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
        timerTxt.setText(String.valueOf((int)(totalTime / 1000)));
    }


    protected Survey getCurrentSurvey() {
        return currentSurvey;
    }

    protected void setCurrentSurvey(Survey currentSurvey) {
        Collections.shuffle(currentSurvey.getQuestions());
        this.currentSurvey = currentSurvey;
    }

    protected int getCurrentIndex() {
        return currentIndex;
    }

    protected void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    protected Question getCurrentQuestion() {
        return getCurrentSurvey().getQuestions().get(currentIndex);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
