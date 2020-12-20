package com.team33.FDMGamification.Model;

import javax.persistence.*;

@Entity
@Table(name = "Rating")
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer rating_id;

    @Column(name = "rating_value")
    private Integer rating_value;

    @ManyToOne
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    public Rating() {
    }

    public Rating(Integer rating_value) {
        this.rating_value = rating_value;
    }

    public Integer getRating_id() {
        return rating_id;
    }

    public void setRating_id(Integer rating_id) {
        this.rating_id = rating_id;
    }

    public Integer getRating_value() {
        return rating_value;
    }

    public void setRating_value(Integer rating_value) {
        this.rating_value = rating_value;
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
                "rating_id=" + rating_id +
                ", rating_value='" + rating_value + '\'' +
                ", challengeId=" + challenge.getId() +
                '}';
    }
}
