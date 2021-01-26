package com.team33.FDMGamification.Model;

import com.team33.FDMGamification.Validation.Annotation.NullOrNotEqualQuestionID;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Objects;

@Entity(name = "Choice")
public class Choice {

    @Id
    @Column(name = "choice_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer choiceId;

    @NotBlank(message = "Please do not leave this field blank!")
    @Pattern(regexp = "^[^<>]*$", message = "Angle brackets (<, >) are not allowed!")
    @Size(max = 400, message = "Please provide a title not longer than 400 characters!")
    @Column(name = "choice_text")
    private String choiceText = "";

    @Column(name = "choice_weight")
    private @Min(value = 0, message = "Must be ≥ 0") @Max(value = 2, message = "Must be ≤ 2") Integer choiceWeight;

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

    public void setChoiceText(String choiceText) {
        if(choiceText!= null) this.choiceText = choiceText;
    }

    public Integer getChoiceWeight() {
        return choiceWeight;
    }

    public void setChoiceWeight(Integer choiceWeight) {
        if(choiceWeight != null) this.choiceWeight = choiceWeight;
    }

    public String getChoiceReason() {
        return choiceReason;
    }

    public void setChoiceReason(String choiceReason) {
        if(choiceReason != null) this.choiceReason = choiceReason;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Choice choice = (Choice) o;

        if (!Objects.equals(choiceId, choice.choiceId)) return false;
        if (!choiceText.equals(choice.choiceText)) return false;
        if (!choiceWeight.equals(choice.choiceWeight)) return false;
        return choiceReason.equals(choice.choiceReason);
    }

    @Override
    public int hashCode() {
        int result = choiceId != null ? choiceId.hashCode() : 0;
        result = 31 * result + choiceText.hashCode();
        result = 31 * result + choiceWeight.hashCode();
        result = 31 * result + choiceReason.hashCode();
        return result;
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
