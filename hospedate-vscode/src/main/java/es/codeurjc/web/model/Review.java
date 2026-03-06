package es.codeurjc.web.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.time.LocalDate;
@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private int rating; //Rating from 1 to 5
    private String comment; //User review text
    private LocalDate date;

    //Empty constructor for JPA
    public Review(){}
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

    public LocalDate getDate(){
        return date;
    }

    public void setDate(LocalDate date){
        this.date = date;
    }



}
