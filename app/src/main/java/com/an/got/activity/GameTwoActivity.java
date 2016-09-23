package com.an.got.activity;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.an.got.GOTConstants;
import com.an.got.R;
import com.an.got.base.BaseActivity;
import com.an.got.callbacks.OnSurveyListener;
import com.an.got.model.Answer;
import com.an.got.model.Question;
import com.an.got.model.Survey;
import com.an.got.utils.AnimationUtils;
import com.an.got.utils.Utils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class GameTwoActivity extends BaseActivity implements OnSurveyListener, View.OnClickListener, GOTConstants {

    private View quizPanel;
    private ImageView imageView;

    private Button btnAlive;
    private Button btnDead;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_Transparent);
        setContentView(R.layout.activity_two);

        quizPanel = findViewById(R.id.quizPanel);
        imageView = (ImageView) findViewById(R.id.imgBanner);
        btnAlive = (Button) findViewById(R.id.btnAlive);
        btnAlive.setOnClickListener(this);
        btnDead = (Button) findViewById(R.id.btnDead);
        btnDead.setOnClickListener(this);

        fetchQuestions();
        setUpTimer();
    }

    private void fetchQuestions() {
        Utils.getSurveyFromFile(getApplicationContext(), R.raw.game_two, GameTwoActivity.this);
    }

    @Override
    public void onFetchSurvey(final Survey survey) {
        setCurrentSurvey(survey);
        setUpNextQuestion();
    }

    private void setUpNextQuestion() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Question question = getCurrentSurvey().getQuestions().get(getCurrentIndex());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Picasso.with(getApplicationContext())
                                .load(question.getImageUrl())
                                .placeholder(R.mipmap.ic_placeholder)
                                .into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                startTimer(GAME_TWO_TIMER);
                            }
                            @Override
                            public void onError() {}});
                    }
                });
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        Button view = (Button) v;
        String selectedAnswerText = view.getText().toString();
        if(!isCorrectAnswer(selectedAnswerText)) {
            handleIncorrectResponse(quizPanel);
        } else {
            handleCorrectResponse();
            AnimationUtils.getInstance().flipOut(imageView);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setCurrentIndex(getCurrentIndex()+1);
                    setUpNextQuestion();
                    AnimationUtils.getInstance().flipIn(imageView);
                }
            }, 1000);
        }
    }

    private boolean isCorrectAnswer(String text) {
        Question question = getCurrentQuestion();
        for(Answer answer : question.getAnswers()) {
            if(answer.isCorrectAnswer() && text.equalsIgnoreCase(answer.getAnswerDesc())) {
                return true;
            }
        }
        return false;
    }
}
