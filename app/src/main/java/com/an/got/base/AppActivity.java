package com.an.got.base;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;

import com.an.got.model.Question;
import com.an.got.model.Survey;
import com.an.got.viewmodel.SurveyViewModel;

public abstract class AppActivity extends BaseActivity {

    private SurveyViewModel surveyViewModel;
    protected void initSurvey(String game) {
        if(surveyViewModel == null) {
            surveyViewModel = ViewModelProviders.of(this).get(SurveyViewModel.class);
            surveyViewModel.setGame(game);
            surveyViewModel.getSurveyMutableLiveData().observe(this, new Observer<Survey>() {
                @Override
                public void onChanged(@Nullable Survey survey) {
                    setUpNextQuestion();
                    setUpTimer();
                }
            });
        }
    }


    protected void incrementCurrentIndex() {
        surveyViewModel.incrementCurrentIndex();
    }

    protected Question getCurrentQuestion() {
        return surveyViewModel.getCurrentQuestion();
    }

    protected boolean isQuestionTypingCompleted(int numChars) {
        return  surveyViewModel.getCurrentQuestion().getQuestionText().length() == numChars;
    }

    protected boolean isCorrectAnswer(String text) {
        return surveyViewModel.isCorrectAnswer(text);
    }

    protected void handleCorrectResponse() {
        cancelTimer();
        surveyViewModel.updateScore(getTimeRemaining());
        displayScore(surveyViewModel.getExistingScore());
    }

    protected void handleWrongResponse() {
        surveyViewModel.updateNumOfTries();
        if(surveyViewModel.isGameOver()) {
            handleGameOver();
        }
    }


    public abstract void setUpNextQuestion();
    public abstract void handleCorrectResponse(int position);
    public abstract void handleWrongResponse(int position);
}
