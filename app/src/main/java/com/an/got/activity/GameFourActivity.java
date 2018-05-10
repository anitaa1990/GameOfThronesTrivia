package com.an.got.activity;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.an.got.GOTConstants;
import com.an.got.R;
import com.an.got.base.BaseActivity;
import com.an.got.databinding.GameFourActivityBinding;
import com.an.got.model.Answer;
import com.an.got.model.Question;
import com.an.got.model.Survey;
import com.an.got.utils.AnimationUtils;
import com.an.got.viewmodel.SurveyViewModel;
import com.an.got.views.CollectionPicker;

public class GameFourActivity extends BaseActivity implements CollectionPicker.OnItemClickListener, GOTConstants {

    private GameFourActivityBinding binding;
    private SurveyViewModel surveyViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_four);
        binding.questionTxt.addTextChangedListener(textWatcher);
        binding.collectionItemPicker.setVisibility(View.INVISIBLE);
        binding.collectionItemPicker.setOnItemClickListener(this);

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
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (binding.collectionItemPicker != null) binding.collectionItemPicker.clearItems();
                final Question question = getCurrentSurvey().getQuestions().get(getCurrentIndex());
                binding.collectionItemPicker.setItems(question.getAnswers());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.questionTxt.animateText(question.getQuestionText());
                    }
                });
            }
        }).start();
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            int numChars = binding.questionTxt.getText().toString().length();
            if (getCurrentQuestion().getQuestionText().length() == numChars) {
                binding.collectionItemPicker.setVisibility(View.VISIBLE);
                binding.line.setVisibility(View.VISIBLE);
                startTimer(GAME_ONE_TIMER);
                binding.collectionItemPicker.drawItemView();
            }
        }
    };


    @Override
    public void onClick(Answer answer, int position) {
        if (!answer.isCorrectAnswer()) {

            handleIncorrectResponse(binding.quizPanel);
        } else {
            handleCorrectResponse();
            AnimationUtils.getInstance().slideOutLeft(binding.quizPanel);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setCurrentIndex(getCurrentIndex() + 1);
                    setUpNextQuestion();
                    AnimationUtils.getInstance().slideInRight(binding.quizPanel);
                    binding.collectionItemPicker.setVisibility(View.INVISIBLE);
                    binding.line.setVisibility(View.INVISIBLE);
                }
            }, getResources().getInteger(R.integer.default_wait_time_before_next_question));
        }
    }
}
