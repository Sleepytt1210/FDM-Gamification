package com.team33.FDMGamification.Model;

import javax.persistence.*;

@Entity
@Table(name = "Rating")
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rating_id")
    private Integer ratingId;

    @Column(name = "rating_value")
    private Integer ratingValue;

    @ManyToOne
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    public Rating() {
    }

    public Rating(Integer ratingValue) {
        this.ratingValue = ratingValue;
    }

    public Integer getRatingId() {
        return ratingId;
    }

    public void setRatingId(Integer ratingId) {
        this.ratingId = ratingId;
    }

    public Integer getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(Integer rating_value) {
        this.ratingValue = rating_value;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    @Override
    public String toString() {
        return "Rating{" +
                "ratingId=" + ratingId +
                ", ratingValue='" + ratingValue + '\'' +
                ", challengeId=" + (challenge == null ? null : challenge.getId()) +
                '}';
    }
}
