package es.codeurjc.web.model;

import jakarta.persistence.*;
import java.sql.Blob;

@Entity
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private Blob imageFile;

    private int position;

    public Image() {}

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public Blob getImageFile() {
        return imageFile;
    }

    public void setImageFile(Blob imageFile) {
        this.imageFile = imageFile;
    }

    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }
}