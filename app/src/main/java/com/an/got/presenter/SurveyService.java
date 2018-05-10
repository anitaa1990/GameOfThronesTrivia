package com.an.got.presenter;


import com.an.got.model.Answer;

public interface SurveyService {

    void fetchQuestions(String game);
    void verifyAnswer(int position, Answer answer);

}
