package com.team33.FDMGamification.Model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Objects;

@Entity
@Table(name = "ChallengeFeedback")
public class ChallengeFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private Integer feedbackId;

    @NotBlank(message = "Please do not leave this field blank!")
    @Pattern(regexp = "^[^<>]*$", message = "Angle brackets (<, >) are not allowed!")
    @Size(max = 100, message = "Please provide a title not longer than 100 characters!")
    @Column(name = "feedback_title")
    private String feedbackTitle = "";

    @Column(name = "feedback_text")
    private String feedbackText = "";

    @Column(name = "positive")
    private boolean positive;

    @ManyToOne
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    public ChallengeFeedback() {
    }

    public ChallengeFeedback(String feedbackTitle, String feedbackText, boolean positive) {
        this.feedbackTitle = feedbackTitle;
        this.feedbackText = feedbackText;
        this.positive = positive;
    }

    public Integer getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(Integer feedbackId) {
        this.feedbackId = feedbackId;
    }

    public String getFeedbackTitle() {
        return feedbackTitle;
    }

    public void setFeedbackTitle(String feedbackTitle) {
        if(feedbackTitle != null) this.feedbackTitle = feedbackTitle;
    }

    public String getFeedbackText() {
        return feedbackText;
    }

    public void setFeedbackText(String feedbackText) {
        if(feedbackText != null) this.feedbackText = feedbackText;
    }

    public boolean isPositive() {
        return positive;
    }

    // The positivity of a challenge feedback should be fixed!
    private void setPositive(boolean positive) {
        this.positive = positive;
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

        ChallengeFeedback that = (ChallengeFeedback) o;

        if (positive != that.positive) return false;
        if (!Objects.equals(feedbackId, that.feedbackId)) return false;
        if (!feedbackTitle.equals(that.feedbackTitle)) return false;
        return feedbackText.equals(that.feedbackText);
    }

    @Override
    public int hashCode() {
        int result = feedbackId != null ? feedbackId.hashCode() : 0;
        result = 31 * result + feedbackTitle.hashCode();
        result = 31 * result + feedbackText.hashCode();
        result = 31 * result + (positive ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ChallengeFeedback{" +
                "feedback_id=" + feedbackId +
                ", feedback_title='" + feedbackTitle + '\'' +
                ", feedback_text='" + feedbackText + '\'' +
                ", positive=" + positive +
                ", challengeId=" + (challenge == null ? null : challenge.getId()) +
                '}';
    }
}
