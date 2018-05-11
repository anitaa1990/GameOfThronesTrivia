package com.an.got.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.an.got.GOTConstants;
import com.an.got.R;
import com.an.got.databinding.GameFourActivityBinding;
import com.an.got.base.AppActivity;
import com.an.got.model.Answer;
import com.an.got.model.Question;
import com.an.got.utils.AnimationUtils;
import com.an.got.views.CollectionPicker;

public class GameFourActivity extends AppActivity implements CollectionPicker.OnItemClickListener, GOTConstants {

    private GameFourActivityBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_four);
        binding.questionTxt.addTextChangedListener(textWatcher);
        binding.collectionItemPicker.setVisibility(View.INVISIBLE);
        binding.collectionItemPicker.setOnItemClickListener(this);
        initSurvey(getIntent().getExtras().getString("pos"));
    }


    @Override
    public void setUpNextQuestion() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (binding.collectionItemPicker != null) binding.collectionItemPicker.clearItems();
                final Question question =  getCurrentQuestion();
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


    @Override
    public void handleCorrectResponse(int position) {
        super.handleCorrectResponse();

        AnimationUtils.getInstance().slideOutLeft(binding.quizPanel);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                incrementCurrentIndex();
                setUpNextQuestion();
                AnimationUtils.getInstance().slideInRight(binding.quizPanel);
                binding.collectionItemPicker.setVisibility(View.INVISIBLE);
                binding.line.setVisibility(View.INVISIBLE);
            }
        }, getResources().getInteger(R.integer.default_wait_time_before_next_question));
    }


    @Override
    public void handleWrongResponse(int position) {
        super.handleWrongResponse();
    }


    private void displayNextQuestionAnswers() {
        binding.collectionItemPicker.setVisibility(View.VISIBLE);
        binding.line.setVisibility(View.VISIBLE);
        startTimer(GAME_ONE_TIMER);
        binding.collectionItemPicker.drawItemView();
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
                displayNextQuestionAnswers();
            }
        }
    };


    @Override
    public void onClick(Answer answer, int position) {
        if (!answer.isCorrectAnswer()) {
            handleWrongResponse(position);

        } else {
            handleCorrectResponse(position);
        }
    }
}
