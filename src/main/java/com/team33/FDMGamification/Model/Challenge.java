package com.team33.FDMGamification.Model;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity(name="Challenge")
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_id")
    private Integer id;

    @Column(name = "challenge_title")
    private String challengeTitle;

    @Column(name = "challenge_introduction")
    private String introduction;

    @Column(name = "challenge_completion")
    private Integer completion;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "challenge", cascade = CascadeType.ALL, orphanRemoval = true)
    @MapKeyColumn(name = "question_id")
    private Map<Integer, Question> question = new HashMap<>();

    public Challenge(){}

    public Challenge(String challengeTitle, String introduction, Integer completion) {
        this.challengeTitle = challengeTitle;
        this.introduction = introduction;
        this.completion = completion;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getChallengeTitle() {
        return challengeTitle;
    }

    public void setChallengeTitle(String title) {
        this.challengeTitle = title;
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

    @Override
    public String toString() {
        return "Challenge{" +
                "id=" + id +
                ", title='" + challengeTitle + '\'' +
                ", introduction='" + introduction + '\'' +
                ", completion=" + completion +
                ", question=" + question +
                '}';
    }

}
