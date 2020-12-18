package com.team33.FDMGamification.Model;

import javax.persistence.*;

@Entity
@Table(name = "ChallengeFeedback")
public class ChallengeFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer feedback_id;

    @Column(name = "feedback_title")
    private String feedback_title;

    @Column(name = "feedback_text")
    private String feedback_text;

    @Column(name = "positive")
    private boolean positive;

    @ManyToOne
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    public ChallengeFeedback() {
    }

    public ChallengeFeedback(Integer feedback_id, String feedback_title, String feedback_text, boolean positive) {
        this.feedback_id = feedback_id;
        this.feedback_title = feedback_title;
        this.feedback_text = feedback_text;
        this.positive = positive;
    }

    public Integer getFeedback_id() {
        return feedback_id;
    }

    public void setFeedback_id(Integer feedback_id) {
        this.feedback_id = feedback_id;
    }

    public String getFeedback_title() {
        return feedback_title;
    }

    public void setFeedback_title(String feedback_title) {
        this.feedback_title = feedback_title;
    }

    public String getFeedback_text() {
        return feedback_text;
    }

    public void setFeedback_text(String feedback_text) {
        this.feedback_text = feedback_text;
    }

    public boolean isPositive() {
        return positive;
    }

    public void setPositive(boolean positive) {
        this.positive = positive;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    @Override
    public String toString() {
        return "ChallengeFeedback{" +
                "feedback_id=" + feedback_id +
                ", feedback_title='" + feedback_title + '\'' +
                ", feedback_text='" + feedback_text + '\'' +
                ", positive=" + positive +
                ", challengeId=" + challenge.getId() +
                '}';
    }
}
