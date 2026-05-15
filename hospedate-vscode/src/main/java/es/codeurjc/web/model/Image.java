package es.codeurjc.web.model;

import jakarta.persistence.*;
import java.sql.Blob;

@Entity
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    // Saves the actual file bytes directly in the database using a large BLOB type
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private Blob imageFile;

    // Used to remember the exact display order of the images in the hotel gallery
    private int position;

    private String name;

    public Image() {}

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public Blob getImageFile() {
        return imageFile;
    }

    public void setImageFile(Blob imageFile) {
        this.imageFile = imageFile;
    }

    public int getPosition() { 
        return position; 
    }


    public void setPosition(int position) { 
        this.position = position; 
    }

    public String getFileName() { 
        return name; 
    }

    public void setFileName(String fileName) { 
        this.name = fileName; 
    }

}