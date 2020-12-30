package com.team33.FDMGamification.Model;

import javax.persistence.*;

@Entity(name = "Choice")
public class Choice {

    @Id
    @Column(name = "choice_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "choice_text")
    private String choice_text = "";

    @Column(name = "choice_weight")
    private int weight;

    @Column(name = "choice_reason")
    private String choice_reason = "";

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    public Choice() {
    }

    public Choice(String choice_text, int weight, String choice_reason) {
        this.choice_text = choice_text;
        this.weight = weight;
        this.choice_reason = choice_reason;
    }

    public String getChoiceText() {
        return choice_text;
    }

    public void setChoiceText(String choice_text) {
        this.choice_text = choice_text;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int score) {
        this.weight = score;
    }

    public String getChoiceReason() {
        return choice_reason;
    }

    public void setChoiceReason(String choice_reason) {
        this.choice_reason = choice_reason;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Choice{" +
                "id=" + id +
                ", choice_text='" + choice_text + '\'' +
                ", weight=" + weight +
                ", choice_reason'" + choice_reason + '\'' +
                ", questionId=" + question.getQuestionId() +
                '}';
    }
}
