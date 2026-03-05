package es.codeurjc.web.model;

import java.sql.Blob;

import jakarta.persistence.*;


@Entity
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Lob
    private Blob imageFile;

    //Empty constructor for JPA
    public Image() {
    }

    public Image(Blob imageFile) {
        this.imageFile = imageFile;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Blob getImageFile() {
        return imageFile;
    }

    public void setImageFile(Blob imageFile) {
        this.imageFile = imageFile;
    }

    @Override
    public String toString() {
        return "Image [id=" + id + "]";
    }
}