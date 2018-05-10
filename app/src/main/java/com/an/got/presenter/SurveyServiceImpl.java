package com.an.got.presenter;

import android.content.Context;
import com.an.got.callbacks.OnSurveyListener;
import com.an.got.model.Answer;
import com.an.got.utils.Utils;

public class SurveyServiceImpl implements SurveyService {

    private Context context;
    private OnSurveyListener onSurveyListener;
    public SurveyServiceImpl(Context context, OnSurveyListener onSurveyListener) {
        this.context = context;
        this.onSurveyListener = onSurveyListener;
    }

    @Override
    public void fetchQuestions(String game) {
        Utils.getSurveyFromFile(context, game, onSurveyListener);
    }

    @Override
    public void verifyAnswer(int position, Answer answer) {
//        if(answer.isCorrectAnswer()) {
//            onSurveyListener.handleCorrectResponse(position);
//        } else {
//            onSurveyListener.handleIncorrectResponse(position);
//        }
    }
}
