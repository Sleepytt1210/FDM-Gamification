package com.team33.FDMGamification.Model;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;


@Entity(name = "Question")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Integer questionId;

    @Column(name = "question_title")
    private String questionTitle = "";

    @Column(name = "question_text")
    private String questionText = "";

    @Column(name = "question_completion")
    private Integer questionCompletion = 0;

    @ManyToOne
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @MapKeyColumn(name = "choice_id")
    private Map<Integer, Choice> choices = new HashMap<>();

    public Question(){}

    public Question(String questionTitle, String questionText, Integer questionCompletion) {
        this.questionTitle = questionTitle;
        this.questionText = questionText;
        this.questionCompletion = questionCompletion;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String question) {
        this.questionText = question;
    }

    public Integer getQuestionCompletion() {
        return questionCompletion;
    }

    public void setQuestionCompletion(Integer questionCompletion) {
        this.questionCompletion = questionCompletion;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    public Map<Integer, Choice> getChoices() {
        return choices;
    }

    public void setChoices(Map<Integer, Choice> choices) {
        this.choices = choices;
    }

    @Override
    public String toString() {
        return "Question{" +
                "questionId=" + questionId +
                ", questionTitle='" + questionTitle + '\'' +
                ", questionText='" + questionText + '\'' +
                ", questionCompletion=" + questionCompletion +
                ", challengeId=" + challenge.getId() +
                '}';
    }
}
