package com.an.got.activity;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
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
import com.an.got.databinding.GameThreeActivityBinding;
import com.an.got.model.Answer;
import com.an.got.model.Question;
import com.an.got.model.Survey;
import com.an.got.utils.AnimationUtils;
import com.an.got.utils.Utils;
import com.an.got.viewmodel.SurveyViewModel;
import com.an.got.views.CustomLinearLayoutManager;
import com.an.got.views.RecyclerItemClickListener;
import com.an.got.views.adapter.MyAlphaInAnimationAdapter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class GameThreeActivity extends BaseActivity implements RecyclerItemClickListener.OnItemClickListener, GOTConstants {

    private AnswerListAdapter adapter;
    private SurveyViewModel surveyViewModel;
    private GameThreeActivityBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_three);

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
        adapter = new AnswerListAdapter(getApplicationContext(), question.getAnswers());

        Picasso.get().load(question.getImageUrl())
                .placeholder(R.drawable.progress_drawable)
                .into(binding.imgBanner, new Callback() {
                    @Override
                    public void onSuccess() {
                        MyAlphaInAnimationAdapter alphaInAnimationAdapter =  new MyAlphaInAnimationAdapter(adapter);
                        alphaInAnimationAdapter.setRecyclerView(binding.recyclerView);
                        alphaInAnimationAdapter.setDuration(1200);
                        binding.recyclerView.setAdapter(alphaInAnimationAdapter);
                        startTimer(GAME_ONE_TIMER);
                    }

                    @Override
                    public void onError(Exception e) {}});
    }


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
            AnimationUtils.getInstance().flipAndSlideOutLeft(binding.quizPanel);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setCurrentIndex(getCurrentIndex()+1);
                    setUpNextQuestion();
                    AnimationUtils.getInstance().flipAndSlideInRight(binding.quizPanel);
                }
            }, getResources().getInteger(R.integer.default_wait_time_before_next_question));
        }
    }
}
