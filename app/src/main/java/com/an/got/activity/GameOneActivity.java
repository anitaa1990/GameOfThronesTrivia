package com.an.got.activity;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.an.got.GOTConstants;
import com.an.got.R;
import com.an.got.adapter.AnswerListAdapter;
import com.an.got.base.BaseActivity;
import com.an.got.callbacks.OnSurveyListener;
import com.an.got.model.Answer;
import com.an.got.model.Question;
import com.an.got.model.Survey;
import com.an.got.utils.AnimationUtils;
import com.an.got.utils.Utils;
import com.an.got.views.CustomLinearLayoutManager;
import com.an.got.views.RecyclerItemClickListener;
import com.an.got.views.TypeWriter;
import com.an.got.views.adapter.MyAlphaInAnimationAdapter;

public class GameOneActivity extends BaseActivity implements OnSurveyListener, GOTConstants {

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
                     /* strike out the incorrect answer */
                    TextView tv = (TextView) view.findViewById(R.id.answerTxt);
                    tv.setTextColor(Color.parseColor("#7b0303"));
                    AnimationUtils.getInstance().animateStrikeThrough(tv);

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
        Utils.getSurveyFromFile(getApplicationContext(), R.raw.game_one, GameOneActivity.this);
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
                startTimer(GAME_ONE_TIMER);
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
