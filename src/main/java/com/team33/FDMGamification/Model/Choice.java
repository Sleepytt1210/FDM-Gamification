package com.team33.FDMGamification.Model;

import com.team33.FDMGamification.Validation.Annotation.NullOrNotEqualChallengeID;
import com.team33.FDMGamification.Validation.Annotation.NullOrNotEqualQuestionID;

import javax.persistence.*;
import javax.validation.constraints.*;

@Entity(name = "Choice")
public class Choice {

    @Id
    @Column(name = "choice_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer choiceId;

    @NotBlank(message = "Please do not leave this field blank!")
    @Pattern(regexp = "^[^<>]*$", message = "Angle brackets (<, >) are not allowed!")
    @Size(max = 200, message = "Please provide a title not longer than 200 characters!")
    @Column(name = "choice_text")
    private String choiceText = "";

    @Min(value = 0, message = "Must be ≥ 0")
    @Max(value = 2, message = "Must be ≤ 2")
    @Column(name = "choice_weight")
    private int choiceWeight;

    @NotBlank(message = "Please do not leave this field blank!")
    @Pattern(regexp = "^[^<>]*$", message = "Angle brackets (<, >) are not allowed!")
    @Size(max = 400, message = "Please provide a title not longer than 400 characters!")
    @Column(name = "choice_reason")
    private String choiceReason = "";

    @NullOrNotEqualQuestionID(message = "Please select an associate question!")
    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    public Choice() {
    }

    public Choice(String choiceText, int choiceWeight, String choiceReason) {
        this.choiceText = choiceText;
        this.choiceWeight = choiceWeight;
        this.choiceReason = choiceReason;
    }

    public String getChoiceText() {
        return choiceText;
    }

    public void setChoiceText(String choice_text) {
        this.choiceText = choice_text;
    }

    public int getChoiceWeight() {
        return choiceWeight;
    }

    public void setChoiceWeight(int score) {
        this.choiceWeight = score;
    }

    public String getChoiceReason() {
        return choiceReason;
    }

    public void setChoiceReason(String choice_reason) {
        this.choiceReason = choice_reason;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Integer getChoiceId() {
        return choiceId;
    }

    public void setChoiceId(Integer id) {
        this.choiceId = id;
    }

    @Override
    public String toString() {
        return "Choice{" +
                "id=" + choiceId +
                ", choice_text='" + choiceText + '\'' +
                ", weight=" + choiceWeight +
                ", choice_reason'" + choiceReason + '\'' +
                ", questionId=" + (question == null ? null :question.getQuestionId()) +
                '}';
    }
}
