package com.an.got.activity;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import com.an.got.GOTConstants;
import com.an.got.R;
import com.an.got.base.BaseActivity;
import com.an.got.databinding.GameTwoActivityBinding;
import com.an.got.model.Question;
import com.an.got.model.Survey;
import com.an.got.utils.AnimationUtils;
import com.an.got.viewmodel.SurveyViewModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class GameTwoActivity extends BaseActivity implements View.OnClickListener, GOTConstants {

    private GameTwoActivityBinding binding;
    private SurveyViewModel surveyViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_two);

        binding.btnAlive.setOnClickListener(this);
        binding.btnDead.setOnClickListener(this);
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
        final Question question = getCurrentSurvey().getQuestions().get(getCurrentIndex());
        Picasso.get().load(question.getImageUrl())
                .placeholder(R.drawable.progress_drawable)
                .into(binding.imgBanner, new Callback() {
                    @Override
                    public void onSuccess() {
                        startTimer(GAME_TWO_TIMER);
                    }
                    @Override
                    public void onError(Exception e) {}});
    }


    @Override
    public void onClick(View v) {
        Button view = (Button) v;

        String selectedAnswerText = view.getText().toString();

        if(!surveyViewModel.isCorrectAnswer(selectedAnswerText)) {
            handleIncorrectResponse(binding.quizPanel);

        } else {
            handleCorrectResponse();
            AnimationUtils.getInstance().flipOut(binding.imgBanner);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    surveyViewModel.incrementCurrentIndex();
                    setCurrentIndex(getCurrentIndex()+1);
                    setUpNextQuestion();
                    AnimationUtils.getInstance().flipIn(binding.imgBanner);
                }
            }, getResources().getInteger(R.integer.default_wait_time_before_next_question));
        }
    }
}