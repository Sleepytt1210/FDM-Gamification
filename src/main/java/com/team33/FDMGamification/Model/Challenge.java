package com.team33.FDMGamification.Model;

import javax.persistence.*;
import java.util.*;

@Entity(name="Challenge")
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_id")
    private Integer id;

    @Column(name = "challenge_title")
    private String challengeTitle = "";

    @Column(name = "challenge_description")
    private String description = "";

    @Column(name = "challenge_stream")
    @Enumerated(EnumType.STRING)
    private Stream stream = Stream.NONE;

    @Column(name = "challenge_completion")
    private Integer completion = 0;

    @Column(name = "avg_rating")
    private String avgRating = "No rating";

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "challenge")
    private Thumbnail thumbnail;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "challenge", cascade = CascadeType.ALL, orphanRemoval = true)
    @MapKeyColumn(name = "question_id")
    private Map<Integer, Question> questions = new HashMap<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "challenge", cascade = CascadeType.ALL, orphanRemoval = true)
    @MapKeyColumn(name = "positive")
    private Map<Boolean, ChallengeFeedback> challengeFeedback = new HashMap<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "challenge", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Rating> ratings = new HashSet<>();

    public Challenge(){}

    public Challenge(String challengeTitle, String description, Thumbnail thumbnail, Stream stream, Integer completion) {
        this.challengeTitle = challengeTitle;
        this.description = description;
        this.thumbnail = thumbnail;
        this.stream = stream;
        this.completion = completion;
        this.avgRating = "No rating";
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String introduction) {
        this.description = introduction;
    }

    public Thumbnail getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Thumbnail thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Stream getStream() {
        return stream;
    }

    public void setStream(Stream stream) {
        this.stream = stream;
    }

    public Integer getCompletion() {
        return completion;
    }

    public void setCompletion(Integer completion) {
        this.completion = completion;
    }

    public String getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(String rating) {
        this.avgRating = rating;
    }

    public Set<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(Set<Rating> ratings) {
        this.ratings = ratings;
    }

    public Map<Integer, Question> getQuestions() {
        return questions;
    }

    public void setQuestions(Map<Integer, Question> question) {
        this.questions = question;
    }

    public Map<Boolean, ChallengeFeedback> getChallengeFeedback() {
        return challengeFeedback;
    }

    public void setChallengeFeedback(Map<Boolean, ChallengeFeedback> challengeFeedback) {
        this.challengeFeedback = challengeFeedback;
    }

    @Override
    public String toString() {
        return "Challenge{" +
                "id=" + id +
                ", title='" + challengeTitle + '\'' +
                ", introduction='" + description + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", stream='" + stream + '\'' +
                ", completion=" + completion +
                ", avgRating=" + avgRating +
                ", feedback=" + challengeFeedback +
                '}';
    }

}
