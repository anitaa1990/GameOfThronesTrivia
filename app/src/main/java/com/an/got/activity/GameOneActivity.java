package com.an.got.activity;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.an.got.R;
import com.an.got.adapter.AnswerListAdapter;
import com.an.got.callbacks.OnSurveyListener;
import com.an.got.model.Answer;
import com.an.got.model.Question;
import com.an.got.model.Survey;
import com.an.got.utils.AnimationUtils;
import com.an.got.utils.Utils;
import com.an.got.views.CustomLinearLayoutManager;
import com.an.got.views.RecyclerItemClickListener;
import com.an.got.views.RevealActivity;
import com.an.got.views.TypeWriter;
import com.an.got.views.adapter.MyAlphaInAnimationAdapter;
import com.google.gson.Gson;

import java.util.Collections;

public class GameOneActivity extends RevealActivity implements OnSurveyListener {

    private TypeWriter questionTxt;
    private RecyclerView recyclerView;
    private View quizPanel;

    private AnswerListAdapter adapter;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_Transparent);
        setContentView(R.layout.activity_one);

        View root= findViewById(R.id.overlay);
        showRevealEffect(savedInstanceState, root);

        quizPanel = findViewById(R.id.quizPanel);
        questionTxt = (TypeWriter) findViewById(R.id.questionTxt);
        questionTxt.addTextChangedListener(textWatcher);
        recyclerView = (RecyclerView) findViewById(R.id.app_list);
        recyclerView.setLayoutManager(new CustomLinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Answer answer = adapter.getAnswer(position);
                if(!answer.isCorrectAnswer()) {
                    handleIncorrectResponse(quizPanel, view);
                } else {
                    handleCorrectResponse(quizPanel, view);

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setCurrentIndex(getCurrentIndex()+1);
                            setUpNextQuestion();
                            AnimationUtils.getInstance().slideInRight(quizPanel);
                        }
                    }, 1000);

                }
            }
        }));

        fetchQuestions();
        setUpTimer();
    }

    private void fetchQuestions() {
        Utils.getSurveyFromFile(getApplicationContext(), R.raw.survey, GameOneActivity.this);
    }

    private void setUpNextQuestion() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(adapter != null) adapter.clear();
                final Question question = getCurrentSurvey().getQuestions().get(getCurrentIndex());
                adapter = new AnswerListAdapter(GameOneActivity.this, question.getAnswers());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        questionTxt.animateText(question.getQuestionText());
                    }
                });
            }
        }).start();
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
        @Override
        public void afterTextChanged(Editable s) {
            int numChars = questionTxt.getText().toString().length();
            if(getCurrentQuestion().getQuestionText().length() == numChars) {
                MyAlphaInAnimationAdapter alphaInAnimationAdapter =  new MyAlphaInAnimationAdapter(adapter);
                alphaInAnimationAdapter.setRecyclerView(recyclerView);
                alphaInAnimationAdapter.setDuration(1200);
                recyclerView.setAdapter(alphaInAnimationAdapter);
                startTimer();
            }
        }
    };

    @Override
    public void onFetchSurvey(final Survey survey) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                setCurrentSurvey(survey);
                setUpNextQuestion();

            }
        }).start();
    }
}
