package com.an.got.activity;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.an.got.GOTConstants;
import com.an.got.R;
import com.an.got.adapter.AnswerListAdapter;
import com.an.got.base.BaseActivity;
import com.an.got.databinding.GameOneActivityBinding;
import com.an.got.model.Answer;
import com.an.got.model.Question;
import com.an.got.model.Survey;
import com.an.got.utils.AnimationUtils;
import com.an.got.viewmodel.SurveyViewModel;
import com.an.got.views.CustomLinearLayoutManager;
import com.an.got.views.RecyclerItemClickListener;
import com.an.got.views.adapter.MyAlphaInAnimationAdapter;

public class GameOneActivity extends BaseActivity implements RecyclerItemClickListener.OnItemClickListener, GOTConstants {

    private AnswerListAdapter adapter;
    private GameOneActivityBinding binding;
    private SurveyViewModel surveyViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_one);

        binding.questionTxt.addTextChangedListener(textWatcher);
        binding.recyclerView.setLayoutManager(new CustomLinearLayoutManager(this));
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), this));

        surveyViewModel = ViewModelProviders.of(this).get(SurveyViewModel.class);
        surveyViewModel.setGame(getIntent().getExtras().getString("pos"));
        surveyViewModel.getSurveyMutableLiveData().observe(this, new Observer<Survey>() {
            @Override
            public void onChanged(@Nullable Survey survey) {
                setCurrentSurvey(survey);
                setUpNextQuestion();
                setUpTimer();
            }
        });
    }


    private void setUpNextQuestion() {
        if(adapter != null) adapter.clear();
        final Question question = getCurrentSurvey().getQuestions().get(getCurrentIndex());
        adapter = new AnswerListAdapter(GameOneActivity.this, question.getAnswers());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.questionTxt.animateText(question.getQuestionText());
            }
        });
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
            if(getCurrentQuestion().getQuestionText().length() == numChars) {
                displayNextQuestionAnswers();
            }
        }
    };


    @Override
    public void onItemClick(View view, int position) {
        Answer answer = adapter.getAnswer(position);

        if(!answer.isCorrectAnswer()) {
            /* strike out the incorrect answer */
            adapter.updateWrongAnswerResponse(binding.recyclerView, position);
            handleIncorrectResponse(binding.quizPanel);

        } else {

            adapter.updateCorrectAnswerResponse(binding.recyclerView, position);

            handleCorrectResponse();
            AnimationUtils.getInstance().slideOutLeft(binding.quizPanel);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setCurrentIndex(getCurrentIndex()+1);
                    setUpNextQuestion();
                    AnimationUtils.getInstance().slideInRight(binding.quizPanel);
                    binding.recyclerView.setVisibility(View.INVISIBLE);
                    binding.line.setVisibility(View.INVISIBLE);
                }
            }, getResources().getInteger(R.integer.default_wait_time_before_next_question));
        }
    }
}
