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

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "challenge",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Thumbnail thumbnail = new Thumbnail(this);

    @Valid
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "challenge",
            cascade = {CascadeType.ALL},
            orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "challenge",
            cascade = {CascadeType.ALL},
            orphanRemoval = true)
    @MapKeyColumn(name = "positive")
    private Map<Boolean, ChallengeFeedback> challengeFeedback = new HashMap<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "challenge",
            cascade = {CascadeType.ALL},
            orphanRemoval = true)
    private List<Rating> ratings = new ArrayList<>();

    public Challenge(){
        this.challengeFeedback.put(true, null);
        this.challengeFeedback.put(false, null);
    }

    public Challenge(String challengeTitle, String description, Stream stream, Integer completion) {
        this.challengeTitle = challengeTitle;
        this.description = description;
        this.stream = stream;
        this.completion = completion;
        this.avgRating = "No rating";
        this.challengeFeedback.put(true, null);
        this.challengeFeedback.put(false, null);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        if(id != null) this.id = id;
    }

    public String getChallengeTitle() {
        return challengeTitle;
    }

    public void setChallengeTitle(String title) {
        if(title != null) this.challengeTitle = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String introduction) {
        if(introduction != null) this.description = introduction;
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
        if(stream != null) this.stream = stream;
    }

    public Integer getCompletion() {
        return completion;
    }

    public void setCompletion(Integer completion) {
        if(completion != null) this.completion = completion;
    }

    public String getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(String rating) {
        this.avgRating = rating;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    private void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    private void setQuestions(List<Question> question) {
        this.questions = question;
    }

    public Map<Boolean, ChallengeFeedback> getChallengeFeedback() {
        return challengeFeedback;
    }

    public ChallengeFeedback getChallengeFeedback(boolean isPositive) {
        return this.challengeFeedback.get(isPositive);
    }

    private void setChallengeFeedback(Map<Boolean, ChallengeFeedback> challengeFeedback) {
        this.challengeFeedback = challengeFeedback;
    }

    /**
     * Add rating to a challenge entity and persist the relationship in database.
     *
     * @param rating    Rating entity to be added.
     */
    public void addRating(Rating rating) {
        if(rating != null) {
            rating.setChallenge(this);
            this.ratings.add(rating);
            this.avgRating = average(this.ratings);
        }
    }

    public void addQuestion(Question question){
        if(question != null) {
            question.setChallenge(this);
            this.questions.add(question);
        }
    }

    public Question getQuestionById(Integer qid){
        return this.questions.stream().filter(question -> question.getQuestionId().equals(qid)).findAny()
                .orElseThrow(() -> new EntityNotFoundException("Question not found!"));
    }

    public Question getQuestionByIndex(int index){
        return this.questions.get(index);
    }

    public void removeQuestion(int index){
        this.questions.remove(index);
    }

    public void removeQuestion(Integer qid){
        this.questions.removeIf(question -> qid.equals(question.getQuestionId()));
    }

    public void removeQuestion(Question question){
        this.questions.remove(question);
    }

    /**
     * Update thumbnail with a thumbnail object with updated properties.
     *
     * @param thumbnail Thumbnail object with updated properties.
     */
    public void updateThumbnailProperties(Thumbnail thumbnail){
        if(thumbnail != null) {
            this.thumbnail.setFileName(thumbnail.getFileName());
            this.thumbnail.setFileType(thumbnail.getFileType());
            this.thumbnail.setBase64String(thumbnail.getBase64String());
        }
    }

    /**
     * Update challenge feedbacks with challenge feedback objects with updated properties.
     *
     * @param positive Positive challenge feedback object with updated properties.
     * @param negative Negative challenge feedback object with updated properties.
     */
    public void updateFeedbacksProperties(ChallengeFeedback positive, ChallengeFeedback negative){
        if(positive != null){
            ChallengeFeedback thisPositive = this.challengeFeedback.get(true);
            if(thisPositive == null) {
                positive.setChallenge(this);
                this.challengeFeedback.put(true, positive);
            }else {
                if (positive.getFeedbackText() != null) thisPositive.setFeedbackText(positive.getFeedbackText());
                if (positive.getFeedbackTitle() != null) thisPositive.setFeedbackText(positive.getFeedbackTitle());
            }
        }
        if(negative != null){
            ChallengeFeedback thisNegative = this.challengeFeedback.get(false);
            if(thisNegative == null) {
                negative.setChallenge(this);
                this.challengeFeedback.put(false, negative);
            }else {
                if (negative.getFeedbackText() != null) thisNegative.setFeedbackText(negative.getFeedbackText());
                if (negative.getFeedbackTitle() != null) thisNegative.setFeedbackText(negative.getFeedbackTitle());
            }
        }
    }

    public Integer completionIncrement(){
        this.completion++;
        return this.completion;
    }

    /**
     * Calculate the average value of ratings.
     *
     * @param ratings Set of ratings to be calculated.
     * @return String: Average value of ratings with one decimal point.
     */
    private String average(List<Rating> ratings) {
        int avg = 0;
        for (Rating cur : ratings) {
            avg += cur.getRatingValue();
        }
        return String.format("%.1f", ((avg * 1.0) / (ratings.size() * 1.0)));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Challenge challenge = (Challenge) o;

        if (!Objects.equals(id, challenge.id)) return false;
        if (!challengeTitle.equals(challenge.challengeTitle)) return false;
        if (!description.equals(challenge.description)) return false;
        if (stream != challenge.stream) return false;
        if (!completion.equals(challenge.completion)) return false;
        return avgRating.equals(challenge.avgRating);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + challengeTitle.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + stream.hashCode();
        result = 31 * result + completion.hashCode();
        result = 31 * result + avgRating.hashCode();
        return result;
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
                '}';
    }

}
