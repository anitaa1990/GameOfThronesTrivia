package com.an.got.activity;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import com.an.got.GOTConstants;
import com.an.got.R;
import com.an.got.base.BaseActivity;
import com.an.got.callbacks.OnSurveyListener;
import com.an.got.model.Answer;
import com.an.got.model.Question;
import com.an.got.model.Survey;
import com.an.got.utils.AnimationUtils;
import com.an.got.utils.Utils;
import com.an.got.views.CollectionPicker;
import com.an.got.views.TypeWriter;

public class GameFourActivity extends BaseActivity implements OnSurveyListener, CollectionPicker.OnItemClickListener, GOTConstants {

    private TypeWriter questionTxt;
    private CollectionPicker picker;
    private View quizPanel;
    private View lineSeparator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_Transparent);
        setContentView(R.layout.activity_four);

        quizPanel = findViewById(R.id.quizPanel);
        lineSeparator = findViewById(R.id.line);
        questionTxt = (TypeWriter) findViewById(R.id.questionTxt);
        questionTxt.addTextChangedListener(textWatcher);

        picker = (CollectionPicker) findViewById(R.id.collection_item_picker);
        picker.setVisibility(View.INVISIBLE);
        picker.setOnItemClickListener(this);

        fetchQuestions();
        setUpTimer();
    }

    private void fetchQuestions() {
        String raw = getIntent().getExtras().getString("pos");
        Utils.getSurveyFromFile(getApplicationContext(), raw, GameFourActivity.this);
    }

    private void setUpNextQuestion() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(picker != null) picker.clearItems();
                final Question question = getCurrentSurvey().getQuestions().get(getCurrentIndex());
                picker.setItems(question.getAnswers());
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
                picker.setVisibility(View.VISIBLE);
                lineSeparator.setVisibility(View.VISIBLE);
                startTimer(GAME_ONE_TIMER);
                picker.drawItemView();
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

    @Override
    public void onClick(Answer answer, int position) {
                if(!answer.isCorrectAnswer()) {

                    handleIncorrectResponse(quizPanel);
                } else {
                    handleCorrectResponse();
                    AnimationUtils.getInstance().slideOutLeft(quizPanel);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setCurrentIndex(getCurrentIndex()+1);
                            setUpNextQuestion();
                            AnimationUtils.getInstance().slideInRight(quizPanel);
                            picker.setVisibility(View.INVISIBLE);
                            lineSeparator.setVisibility(View.INVISIBLE);
                        }
                    }, 1000);
                }
    }
}
