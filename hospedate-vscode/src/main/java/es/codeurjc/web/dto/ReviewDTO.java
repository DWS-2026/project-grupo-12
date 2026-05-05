package es.codeurjc.web.dto;

import java.time.LocalDate;
import es.codeurjc.web.model.Review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ReviewDTO {
    private Long id;

    @NotBlank(message = "Review title is required")
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    private String title;

    @NotBlank(message = "Comment cannot be empty")
    @Size(max = 2000, message = "Comment is too long (max 2000 characters)")
    private String comment;

    @Min(value = 1, message = "Minimum rating is 1")
    @Max(value = 5, message = "Maximum rating is 5")
    private int rating;

    private boolean boldTitle;
    
    private LocalDate publishDate;

    // Use plain strings/numbers instead of nested objects
    private String authorName;

    @NotNull(message = "Hotel is required")
    private Long hotelId;

    // Empty Constructor (Mandatory for Spring)
    public ReviewDTO() {}

    // Constructor translator (Entity -> DTO)
    public ReviewDTO(Review review) {
        this.id = review.getId();
        this.title = review.getTitle();
        this.comment = review.getComment();
        this.rating = review.getRating();
        this.publishDate = review.getPublishDate();
        this.boldTitle = review.isBoldTitle();
        
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

    public boolean isBoldTitle() { 
        return boldTitle; 
    }
    
    public void setBoldTitle(boolean boldTitle) { 
        this.boldTitle = boldTitle; 
    }
}
