package com.team33.FDMGamification.Model;

import javax.persistence.*;
import java.util.Map;

@Entity(name="Challenge")
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scenario_id")
    private Integer id;

    @Column(name = "introduction")
    private String introduction;

    @Column(name = "completion")
    private Integer completion;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "challenge")
    private Map<Integer, Question> question;

    public Challenge(){}

    public Challenge(String introduction, Integer completion) {
        this.introduction = introduction;
        this.completion = completion;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public Integer getCompletion() {
        return completion;
    }

    public void setCompletion(Integer completion) {
        this.completion = completion;
    }

    public Map<Integer, Question> getQuestion() {
        return question;
    }

    public void setQuestion(Map<Integer, Question> question) {
        this.question = question;
    }
}
