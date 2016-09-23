package com.an.got.activity;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
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
import com.an.got.views.adapter.MyAlphaInAnimationAdapter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class GameThreeActivity extends BaseActivity implements OnSurveyListener, GOTConstants {

    private RecyclerView recyclerView;
    private ImageView imageView;
    private View quizPanel;

    private AnswerListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_Transparent);
        setContentView(R.layout.activity_three);

        quizPanel = findViewById(R.id.quizPanel);
        imageView = (ImageView) findViewById(R.id.imgBanner);
        recyclerView = (RecyclerView) findViewById(R.id.app_list);
        recyclerView.setLayoutManager(new CustomLinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Answer answer = adapter.getAnswer(position);
                TextView tv = (TextView) view.findViewById(R.id.answerTxt);
                if(!answer.isCorrectAnswer()) {
                /* strike out the incorrect answer */
                    tv.setTextColor(Color.parseColor("#7b0303"));
                    AnimationUtils.getInstance().animateStrikeThrough(tv);
                    handleIncorrectResponse(quizPanel);
                } else {
                    AnimationUtils.getInstance().animateCorrectResponse(tv, Color.parseColor("#0e5b02"));

                    handleCorrectResponse();
                    AnimationUtils.getInstance().flipAndSlideOutLeft(quizPanel);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setCurrentIndex(getCurrentIndex()+1);
                            setUpNextQuestion();
                            AnimationUtils.getInstance().flipAndSlideInRight(quizPanel);
                        }
                    }, 1000);

                }
            }
        }));

        fetchQuestions();
        setUpTimer();
    }

    private void fetchQuestions() {
        Utils.getSurveyFromFile(getApplicationContext(), R.raw.game_three, GameThreeActivity.this);
    }

    private void setUpNextQuestion() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(adapter != null) adapter.clear();
                final Question question = getCurrentSurvey().getQuestions().get(getCurrentIndex());
                adapter = new AnswerListAdapter(GameThreeActivity.this, question.getAnswers());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Picasso.with(getApplicationContext())
                                .load(question.getImageUrl())
                                .placeholder(R.drawable.progress_drawable)
                                .into(imageView, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        MyAlphaInAnimationAdapter alphaInAnimationAdapter =  new MyAlphaInAnimationAdapter(adapter);
                                        alphaInAnimationAdapter.setRecyclerView(recyclerView);
                                        alphaInAnimationAdapter.setDuration(1200);
                                        recyclerView.setAdapter(alphaInAnimationAdapter);
                                        startTimer(GAME_ONE_TIMER);
                                    }
                                    @Override
                                    public void onError() {}});
                    }
                });
            }
        }).start();
    }

    @Override
    public void onFetchSurvey(Survey survey) {
        setCurrentSurvey(survey);
        setUpNextQuestion();
    }
}
