package com.team33.FDMGamification.Model;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@Entity(name = "Question")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Integer questionId;

    @Column(name = "question_text")
    private String questionText;

    @ManyToOne
    @JoinColumn(name = "scenario_id")
    private Challenge challenge;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "question", cascade = CascadeType.ALL)
    @MapKeyColumn(name = "choice_id")
    private Map<Integer, Choice> choices = new HashMap<>();

    public Question(){}

    public Question(String questionText) {
        this.questionText = questionText;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String question) {
        this.questionText = question;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
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
                ", questionText='" + questionText + '\'' +
                ", challengeId=" + challenge.getId() +
                ", choices=" + choices +
                '}';
    }
}
