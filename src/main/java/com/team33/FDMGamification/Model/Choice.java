package com.team33.FDMGamification.Model;

import javax.persistence.*;

@Entity(name = "Choice")
public class Choice {

    @Id
    @Column(name = "choice_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String choice_text;
    private int weight;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    public Choice() {
    }

    public Choice(String choice_text, int weight) {
        this.choice_text = choice_text;
        this.weight = weight;
    }

    public String getChoiceText() {
        return choice_text;
    }

    public void setChoiceText(String value) {
        this.choice_text = value;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int score) {
        this.weight = score;
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
                ", question=" + question +
                '}';
    }
}
