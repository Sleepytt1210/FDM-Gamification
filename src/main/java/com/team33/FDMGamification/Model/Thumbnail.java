package com.team33.FDMGamification.Model;

import javax.persistence.*;

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
        this.base64String = base64String;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }
}
