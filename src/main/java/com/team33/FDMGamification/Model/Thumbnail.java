package com.team33.FDMGamification.Model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "Thumbnail")
public class Thumbnail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "thumbnail_id")
    private Integer id;

    @Column(name = "base64String")
    private String base64String="";

    @Column(name = "filename")
    private String fileName="";

    @Column(name = "file_type")
    private String fileType="";

    @OneToOne
    @JoinColumn(name = "challenge_id", referencedColumnName = "challenge_id")
    private Challenge challenge;

    public Thumbnail(){}

    public Thumbnail(Challenge challenge){
        this.challenge = challenge;
    }

    public Thumbnail(String base64String, String fileName, String fileType) {
        this.base64String = base64String;
        this.fileName = fileName;
        this.fileType = fileType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBase64String() {
        return base64String;
    }

    public void setBase64String(String base64String) {
        if(base64String != null) this.base64String = base64String;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        if(fileName != null) this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        if(fileType != null) this.fileType = fileType;
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

        Thumbnail thumbnail = (Thumbnail) o;

        if (!Objects.equals(id, thumbnail.id)) return false;
        if (!base64String.equals(thumbnail.base64String)) return false;
        if (!fileName.equals(thumbnail.fileName)) return false;
        return fileType.equals(thumbnail.fileType);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + base64String.hashCode();
        result = 31 * result + fileName.hashCode();
        result = 31 * result + fileType.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Thumbnail{" +
                "id=" + id +
                ", base64String='" + base64String + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileType='" + fileType + '\'' +
                ", challengeId=" + (challenge == null ? null : challenge.getId()) +
                '}';
    }
}
