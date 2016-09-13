package com.an.got.model;

import java.io.Serializable;
import java.util.List;

public class Survey implements Serializable {


    private long id;
    private List<Question> questions;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}
