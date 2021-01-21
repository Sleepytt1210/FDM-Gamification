package com.team33.FDMGamification.Model;

import com.team33.FDMGamification.Validation.Annotation.EnumNotEquals;
import com.team33.FDMGamification.Validation.Annotation.NullOrNotEqualChallengeID;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;


@Entity(name = "Question")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Integer questionId;

    @NotBlank(message = "Please do not leave this field blank!")
    @Pattern(regexp = "^[^<>]*$", message = "Angle brackets (<, >) are not allowed!")
    @Size(max = 100, message = "Please provide a title not longer than 100 characters!")
    @Column(name = "question_title")
    private String questionTitle = "";

    @NotBlank(message = "Please do not leave this field blank!")
    @Pattern(regexp = "^[^<>]*$", message = "Angle brackets (<, >) are not allowed!")
    @Column(name = "question_text")
    private String questionText = "";

    @Column(name = "question_completion")
    private Integer questionCompletion = 0;

    @EnumNotEquals
    @Column(name = "question_type")
    @Enumerated(EnumType.STRING)
    private QuestionType questionType = QuestionType.NONE;

    @NullOrNotEqualChallengeID(message = "Please select an associate challenge!")
    @ManyToOne
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    @Valid
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "question",
            cascade = {CascadeType.MERGE, CascadeType.REMOVE, CascadeType.REFRESH, CascadeType.DETACH},
            orphanRemoval = true)
    private List<Choice> choices = new ArrayList<>();

    public Question(){}

    public Question(String questionTitle, String questionText, Integer questionCompletion, QuestionType questionType) {
        this.questionTitle = questionTitle;
        this.questionText = questionText;
        this.questionCompletion = questionCompletion;
        this.questionType = questionType;
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

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    @Override
    public String toString() {
        return "Question{" +
                "questionId=" + questionId +
                ", questionTitle='" + questionTitle + '\'' +
                ", questionText='" + questionText + '\'' +
                ", questionCompletion=" + questionCompletion +
                ", questionType=" + questionType +
                ", challengeId=" + (challenge == null ? null : challenge.getId()) +
                '}';
    }
}
