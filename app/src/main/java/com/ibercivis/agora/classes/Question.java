package com.ibercivis.agora.classes;

import java.util.List;

public class Question {
    private int id;
    private String questionText;
    private List<String> answers;
    private int correctAnswer;

    // Constructor, getters y setters

    public Question(int id, String questionText, List<String> answers, int correctAnswer) {
        this.id = id;
        this.questionText = questionText;
        this.answers = answers;
        this.correctAnswer = correctAnswer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(int correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}
