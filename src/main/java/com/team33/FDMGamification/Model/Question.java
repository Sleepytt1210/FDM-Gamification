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
import java.util.Objects;
import java.util.Optional;


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
            cascade = {CascadeType.ALL},
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
        if(questionTitle != null) this.questionTitle = questionTitle;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        if(questionText != null) this.questionText = questionText;
    }

    public Integer getQuestionCompletion() {
        return questionCompletion;
    }

    public void setQuestionCompletion(Integer questionCompletion) {
        if(questionCompletion != null) this.questionCompletion = questionCompletion;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        if(questionType != null) this.questionType = questionType;
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

    public Optional<Choice> getChoiceById(Integer choiceId) {
        return this.choices.stream().filter(choice -> choice.getChoiceId().equals(choiceId)).findAny();
    }

    private void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    public void addChoice(Choice choice){
        if(choice != null) {
            choice.setQuestion(this);
            this.choices.add(choice);
        }
    }

    public void removeChoice(Integer choiceId){
        this.choices.removeIf(choice -> choiceId.equals(choice.getChoiceId()));
    }

    public void removeChoice(int index){
        this.choices.remove(index);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Question question = (Question) o;

        if (!Objects.equals(questionId, question.questionId)) return false;
        if (!questionTitle.equals(question.questionTitle)) return false;
        if (!questionText.equals(question.questionText)) return false;
        if (!questionCompletion.equals(question.questionCompletion)) return false;
        return questionType == question.questionType;
    }

    @Override
    public int hashCode() {
        int result = questionId != null ? questionId.hashCode() : 0;
        result = 31 * result + questionTitle.hashCode();
        result = 31 * result + questionText.hashCode();
        result = 31 * result + questionCompletion.hashCode();
        result = 31 * result + questionType.hashCode();
        return result;
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
