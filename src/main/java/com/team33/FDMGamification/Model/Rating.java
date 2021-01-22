package com.team33.FDMGamification.Model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "Rating")
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rating_id")
    private Integer ratingId;

    @Column(name = "rating_value")
    private Integer ratingValue = 0;

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

    public void setRatingValue(Integer ratingValue) {
        if(ratingValue != null) this.ratingValue = ratingValue;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Rating rating = (Rating) o;

        if (!Objects.equals(ratingId, rating.ratingId)) return false;
        return ratingValue.equals(rating.ratingValue);
    }

    @Override
    public int hashCode() {
        int result = ratingId != null ? ratingId.hashCode() : 0;
        result = 31 * result + ratingValue.hashCode();
        return result;
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
