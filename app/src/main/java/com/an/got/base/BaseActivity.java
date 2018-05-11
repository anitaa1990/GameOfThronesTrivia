package com.an.got.base;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.TextView;

import com.an.got.R;
import com.an.got.views.explosive.ExplosionField;


public class BaseActivity extends AppCompatActivity {

    private TextView timerTxt;
    private TextView scoreTxt;

    private MediaPlayer mediaPlayer;
    private ExplosionField mExplosionField;

    private CountDownTimer timer;
    private int totalTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setTheme(R.style.Theme_AppCompat_Light_NoActionBar);
    }

    protected void playBgm() {
        if(mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.got);
        }
        if(!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
        }
    }

    protected void setUpTimer() {
        timerTxt = findViewById(R.id.timer);
        scoreTxt = findViewById(R.id.score);
        mExplosionField = ExplosionField.attach2Window(this);
    }

    protected void startTimer(int totalTime) {
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
                handleGameOver();
            }
        }.start();
    }

    protected void cancelTimer() {
        if(timer != null) {
            timer.cancel();
        }
    }


    protected void handleGameOver() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mExplosionField.explode(findViewById(R.id.quizPanel));
                closeScreen();

            }
        }, getResources().getInteger(R.integer.default_wait_time_before_explode_anim));
    }


    protected void closeScreen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                overridePendingTransition(R.anim.stay, R.anim.slide_top_to_bottom);
            }
        }, getResources().getInteger(R.integer.default_wait_time_before_game_finishes));
    }


    public void displayScore(long score) {
        if(scoreTxt == null) setUpTimer();
        scoreTxt.setText(String.valueOf(score));
        timerTxt.setText(String.valueOf((int)(totalTime / 1000)));
    }


    public int getTimeRemaining() {
        if(scoreTxt == null) setUpTimer();
        int timeRemaining = Integer.valueOf(timerTxt.getText().toString());
        return timeRemaining;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(timer != null) {
            cancelTimer();
            timer = null;
        }
        if(mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}
