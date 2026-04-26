package es.codeurjc.web.dto;

import java.time.LocalDate;
import es.codeurjc.web.model.Review;

public class ReviewDTO {
    private Long id;
    private String title;
    private String comment;
    private int rating;
    private LocalDate publishDate;
    
    //En lugar de objetos, usamos textos/números
    private String authorName;
    private Long hotelId;

    // Constructor vacío (Obligatorio para Spring/Jackson)
    public ReviewDTO() {}

    // Constructor traductor (Entidad -> DTO)
    public ReviewDTO(Review review) {
        this.id = review.getId();
        this.title = review.getTitle();
        this.comment = review.getComment();
        this.rating = review.getRating();
        this.publishDate = review.getPublishDate();
        
        // Extraemos solo el nombre del autor de forma segura
        if (review.getAuthor() != null) {
            this.authorName = review.getAuthor().getName();
        }
        
        // Extraemos solo el ID del hotel de forma segura
        if (review.getHotel() != null) {
            this.hotelId = review.getHotel().getId();
        }
    }
    // Getters y Setters 
    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getRating() {
        return rating;
    }


    public void setPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
    }
    public LocalDate getPublishDate() {
        return publishDate;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }
}
