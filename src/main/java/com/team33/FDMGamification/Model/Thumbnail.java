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
    private String base64String;

    @Column(name = "filename")
    private String fileName;

    @Column(name = "alt_text")
    private String altText;

    @Column(name = "file_type")
    private String fileType;

    @OneToOne
    @JoinColumn(name = "challenge_id", referencedColumnName = "challenge_id")
    private Challenge challenge;

    public Thumbnail(){}

    protected Thumbnail(String base64String, String fileName, String altText, String fileType) {
        this.base64String = base64String;
        this.fileName = fileName;
        this.altText = altText;
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

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}
