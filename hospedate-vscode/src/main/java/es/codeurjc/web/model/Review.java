package es.codeurjc.web.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private int rating; //Rating from 1 to 5

    private String title; //Review title

    @Column(columnDefinition = "TEXT")
    private String comment; //User review text

    private LocalDate publishDate;

    //Empty constructor for JPA
    public Review(){}

    public Review(int rating,String title, String comment, User author, Hotel hotel){
        this.rating = rating;
        this.title = title;
        this.comment = comment;
        this.author = author;
        this.hotel = hotel;
    }

    @PrePersist //It is executed before the .save, and saves the date it is done
    protected void initializePublishDate() {
        this.publishDate = LocalDate.now();
    }


    //Relationships with other entities
    @ManyToOne
    private User author;    //Many reviews belong to one author

    @ManyToOne
    private Hotel hotel;    //Many reviews belong to a hotel

    //Getters and Setters

    public Hotel getHotel(){
        return hotel;
    }

    public void setHotel(Hotel hotel){
        this.hotel = hotel;
    }

    public User getAuthor(){
        return author;
    }

    public void setAuthor(User author){
        this.author = author;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getRating(){
        return rating;
    }

    public void setRating(int rating){
        this.rating = rating;
    }

    public String getComment(){
        return comment;
    }

    public void setComment(String comment){
        this.comment = comment;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public LocalDate getPublishDate(){
        return publishDate;
    }

    // Formatted date getter
    public String getFormattedDate() {
        if (this.publishDate == null) {
            return "Fecha desconocida";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return this.publishDate.format(formatter);
    }


}
