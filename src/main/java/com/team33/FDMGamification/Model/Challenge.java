package com.team33.FDMGamification.Model;

import com.team33.FDMGamification.Validation.Annotation.EnumNotEquals;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.*;

@Entity(name="Challenge")
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_id")
    private Integer id;

    @NotBlank(message = "Please do not leave this field blank!")
    @Pattern(regexp = "^[^<>]*$", message = "Angle brackets (<, >) are not allowed!")
    @Size(max = 100, message = "Please provide a title not longer than 100 characters!")
    @Column(name = "challenge_title")
    private String challengeTitle = "";

    @NotBlank(message = "Please do not leave this field blank!")
    @Pattern(regexp = "^[^<>]*$", message = "Angle brackets (<, >) are not allowed!")
    @Column(name = "challenge_description")
    private String description = "";

    @EnumNotEquals
    @Column(name = "challenge_stream")
    @Enumerated(EnumType.STRING)
    private Stream stream = Stream.NONE;

    @Column(name = "challenge_completion")
    private Integer completion = 0;

    @Column(name = "avg_rating")
    private String avgRating = "No rating";

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "challenge")
    private Thumbnail thumbnail = new Thumbnail(this);

    @Valid
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "challenge",
            cascade = {CascadeType.MERGE, CascadeType.REMOVE, CascadeType.REFRESH, CascadeType.DETACH},
            orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "challenge",
            cascade = {CascadeType.MERGE, CascadeType.REMOVE, CascadeType.REFRESH, CascadeType.DETACH},
            orphanRemoval = true)
    @MapKeyColumn(name = "positive")
    private Map<Boolean, ChallengeFeedback> challengeFeedback = new HashMap<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "challenge",
            cascade = {CascadeType.MERGE, CascadeType.REMOVE, CascadeType.REFRESH, CascadeType.DETACH},
            orphanRemoval = true)
    private Set<Rating> ratings = new HashSet<>();

    public Challenge(){}

    public Challenge(String challengeTitle, String description, Stream stream, Integer completion) {
        this.challengeTitle = challengeTitle;
        this.description = description;
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

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> question) {
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
