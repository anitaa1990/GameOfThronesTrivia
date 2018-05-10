package com.an.got.activity;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.an.got.GOTConstants;
import com.an.got.R;
import com.an.got.adapter.AnswerListAdapter;
import com.an.got.base.AppActivity;
import com.an.got.databinding.GameOneActivityBinding;
import com.an.got.model.Answer;
import com.an.got.model.Question;
import com.an.got.utils.AnimationUtils;
import com.an.got.views.CustomLinearLayoutManager;
import com.an.got.views.RecyclerItemClickListener;
import com.an.got.views.adapter.MyAlphaInAnimationAdapter;

public class GameOneActivity extends AppActivity implements RecyclerItemClickListener.OnItemClickListener, GOTConstants {

    private AnswerListAdapter adapter;
    private GameOneActivityBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_one);

        binding.questionTxt.addTextChangedListener(textWatcher);
        binding.recyclerView.setLayoutManager(new CustomLinearLayoutManager(this));
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), this));

        initSurvey(getIntent().getExtras().getString("pos"));
    }


    @Override
    public void setUpNextQuestion() {
        if(adapter != null) adapter.clear();
        final Question question = getCurrentQuestion();
        adapter = new AnswerListAdapter(GameOneActivity.this, question.getAnswers());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.questionTxt.animateText(question.getQuestionText());
            }
        });
    }

    @Override
    public void handleCorrectResponse(int position) {
        super.handleCorrectResponse();

        adapter.updateCorrectAnswerResponse(binding.recyclerView, position);

        AnimationUtils.getInstance().slideOutLeft(binding.quizPanel);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                incrementCurrentIndex();
                setUpNextQuestion();
                AnimationUtils.getInstance().slideInRight(binding.quizPanel);
                binding.recyclerView.setVisibility(View.INVISIBLE);
                binding.line.setVisibility(View.INVISIBLE);
            }
        }, getResources().getInteger(R.integer.default_wait_time_before_next_question));
    }


    @Override
    public void handleWrongResponse(int position) {
        super.handleWrongResponse();

        /* strike out the incorrect answer */
        adapter.updateWrongAnswerResponse(binding.recyclerView, position);
    }


    private void displayNextQuestionAnswers() {
        binding.recyclerView.setVisibility(View.VISIBLE);
        MyAlphaInAnimationAdapter alphaInAnimationAdapter =  new MyAlphaInAnimationAdapter(adapter);
        alphaInAnimationAdapter.setRecyclerView(binding.recyclerView);
        alphaInAnimationAdapter.setDuration(1200);
        binding.recyclerView.setAdapter(alphaInAnimationAdapter);
        binding.line.setVisibility(View.VISIBLE);
        startTimer(GAME_ONE_TIMER);
    }


    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
        @Override
        public void afterTextChanged(Editable s) {
            int numChars = binding.questionTxt.getText().toString().length();
            if(isQuestionTypingCompleted(numChars)) {
                displayNextQuestionAnswers();
            }
        }
    };


    @Override
    public void onItemClick(View view, int position) {
        Answer answer = adapter.getAnswer(position);
        if(!answer.isCorrectAnswer()) {
            handleWrongResponse(position);
        } else {
            handleCorrectResponse(position);
        }
    }
}
