package com.an.got.activity;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import com.an.got.GOTConstants;
import com.an.got.R;
import com.an.got.databinding.GameTwoActivityBinding;
import com.an.got.base.AppActivity;
import com.an.got.model.Question;
import com.an.got.utils.AnimationUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class GameTwoActivity extends AppActivity implements View.OnClickListener, GOTConstants {

    private GameTwoActivityBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_two);

        binding.btnAlive.setOnClickListener(this);
        binding.btnDead.setOnClickListener(this);
        initSurvey(getIntent().getExtras().getString("pos"));
    }


    @Override
    public void setUpNextQuestion() {
        final Question question =  getCurrentQuestion();
        Picasso.get().load(question.getImageUrl())
                .placeholder(R.drawable.progress_drawable)
                .into(binding.imgBanner, new Callback() {
                    @Override
                    public void onSuccess() {
                        startTimer(GAME_TWO_TIMER);
                    }
                    @Override
                    public void onError(Exception e) {}});
    }


    @Override
    public void handleCorrectResponse(int position) {
        super.handleCorrectResponse();

        AnimationUtils.getInstance().flipOut(binding.imgBanner);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                incrementCurrentIndex();
                setUpNextQuestion();
                AnimationUtils.getInstance().flipIn(binding.imgBanner);
            }
        }, getResources().getInteger(R.integer.default_wait_time_before_next_question));
    }


    @Override
    public void handleWrongResponse(int position) {
        super.handleWrongResponse();
    }


    @Override
    public void onClick(View v) {
        Button view = (Button) v;
        String selectedAnswerText = view.getText().toString();

        if(!isCorrectAnswer(selectedAnswerText)) {
            handleWrongResponse(0);

        } else {
            handleCorrectResponse(0);
        }
    }
}