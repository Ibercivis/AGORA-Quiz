package com.ibercivis.agora.classes;

import java.util.List;

public class Game {
    private int id;
    private List<Question> questions;
    private int score;
    private String status;

    private int timeLeft;

    // Constructor, getters y setters

    public Game(int id, List<Question> questions, int score, String status) {
        this.id = id;
        this.questions = questions;
        this.score = score;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
    }
}
