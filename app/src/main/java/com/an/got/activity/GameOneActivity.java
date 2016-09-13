package com.an.got.activity;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.an.got.R;
import com.an.got.adapter.AnswerListAdapter;
import com.an.got.model.Answer;
import com.an.got.model.Question;
import com.an.got.model.Survey;
import com.an.got.utils.Utils;
import com.an.got.views.CustomLinearLayoutManager;
import com.an.got.views.RecyclerItemClickListener;
import com.an.got.views.RevealActivity;
import com.an.got.views.TypeWriter;
import com.an.got.views.adapter.MyAlphaInAnimationAdapter;
import com.google.gson.Gson;

public class GameOneActivity extends RevealActivity {

    private TypeWriter questionTxt;
    private RecyclerView recyclerView;
    private AnswerListAdapter adapter;

    private View quizPanel;

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
                } else handleCorrectResponse(quizPanel, view);
            }
        }));

        setUpTimer();
        setUpUI();
    }


    private void setUpUI() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Survey survey = new Gson().fromJson(Utils.getJSONStringFromRaw(GameOneActivity.this, R.raw.survey), Survey.class);
                Question question = survey.getQuestions().get(0);
                adapter = new AnswerListAdapter(GameOneActivity.this, question.getAnswers());
                setCurrentQuestion(question);
                setUpNextQuestion(question);
            }
        }).start();
        //setup score
    }

    private void setUpNextQuestion(final Question question) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                questionTxt.animateText(question.getQuestionText());
            }
        });
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
}
