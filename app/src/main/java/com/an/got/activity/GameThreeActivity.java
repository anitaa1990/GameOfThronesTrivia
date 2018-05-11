package com.an.got.activity;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.view.View;

import com.an.got.GOTConstants;
import com.an.got.R;
import com.an.got.adapter.AnswerListAdapter;
import com.an.got.databinding.GameThreeActivityBinding;
import com.an.got.base.AppActivity;
import com.an.got.model.Answer;
import com.an.got.model.Question;
import com.an.got.utils.AnimationUtils;
import com.an.got.views.CustomLinearLayoutManager;
import com.an.got.views.RecyclerItemClickListener;
import com.an.got.views.adapter.MyAlphaInAnimationAdapter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class GameThreeActivity extends AppActivity implements RecyclerItemClickListener.OnItemClickListener, GOTConstants {

    private AnswerListAdapter adapter;
    private GameThreeActivityBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_three);

        binding.recyclerView.setLayoutManager(new CustomLinearLayoutManager(this));
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), this));
        initSurvey(getIntent().getExtras().getString("pos"));
    }


    @Override
    public void setUpNextQuestion() {
        if(adapter != null) adapter.clear();
        final Question question =  getCurrentQuestion();
        adapter = new AnswerListAdapter(getApplicationContext(), question.getAnswers());

        Picasso.get().load(question.getImageUrl())
                .placeholder(R.drawable.progress_drawable)
                .into(binding.imgBanner, new Callback() {
                    @Override
                    public void onSuccess() {
                        binding.recyclerView.setVisibility(View.VISIBLE);
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
    public void handleCorrectResponse(int position) {
        super.handleCorrectResponse();

        adapter.updateCorrectAnswerResponse(binding.recyclerView, position);
        AnimationUtils.getInstance().flipAndSlideOutLeft(binding.quizPanel);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                incrementCurrentIndex();
                setUpNextQuestion();
                AnimationUtils.getInstance().flipAndSlideInRight(binding.quizPanel);
                binding.recyclerView.setVisibility(View.INVISIBLE);
            }
        }, getResources().getInteger(R.integer.default_wait_time_before_next_question));
    }


    @Override
    public void handleWrongResponse(int position) {
        super.handleWrongResponse();

        /* strike out the incorrect answer */
        adapter.updateWrongAnswerResponse(binding.recyclerView, position);
    }


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
