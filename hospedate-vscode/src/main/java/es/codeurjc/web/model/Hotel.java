package es.codeurjc.web.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Hotel{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String tipo;
    private String city;    
    private String location;
    private double price;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> galeria;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> services;

    private double rating;
    private boolean wifi;
    private boolean tv;
    private boolean airConditioning;
    private boolean family;

    // A hotel has MANY reviews.
    // mappedBy = "hotel" means that the Review class will contain a variable called "hotel".
    // cascade = CascadeType.ALL means that if you delete the hotel, its reviews will be automatically deleted.
    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    //Empty constructor for JPA
    public Hotel() {}

    public Hotel(String name, String tipo, String city, String location, 
                 double price, String description, List<String> galeria, 
                 Set<String> services, double rating, boolean wifi, boolean tv,
                 boolean airConditioning, boolean family) {
        this.name = name;
        this.tipo = tipo;
        this.city = city;
        this.location = location;
        this.price = price;
        this.description = description;
        this.galeria = galeria;
        this.services = services;
        this.rating = rating;
        this.wifi = wifi;
        this.tv = tv;
        this.airConditioning = airConditioning;
        this.family = family;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }
    

    public void setId(Long id) {
        this.id = id;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getGaleria() {
        return galeria;
    }

    public void setGaleria(List<String> galeria) {
        this.galeria = galeria;
    }

    public Set<String> getServices() {
        return services;
    }

    public void setServices(Set<String> services) {
        this.services = services;
    }

    public double getRating() {
        if (reviews != null && !reviews.isEmpty()) {
            double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
            return Math.round(avg * 10.0) / 10.0;
        }
        return rating;
    }

    public double getManualRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public boolean isWifi() {
        return wifi;
    } 

    public void setWifi(boolean wifi) {
        this.wifi = wifi;
    }

    public boolean isTv() {
        return tv;
    }

    public void setTv(boolean tv) {
        this.tv = tv;
    }

    public boolean isAirConditioning() {
        return airConditioning;
    }   

    public void setAirConditioning(boolean airConditioning) {
        this.airConditioning = airConditioning;
    }  

    public boolean isFamily() {
        return family;
    }

    public void setFamily(boolean family) {
        this.family = family;
    }   

    // Method to return the first photo in the gallery list
    public String getMainImage() {
        if (this.galeria != null && !this.galeria.isEmpty()) {
            return this.galeria.get(0); // Return the first photo on the list
        }
        // It returns a default image in case a hotel doesn't have photos.
        return "/assets/img/portfolio/hoteles/default.jpg"; 
    }
}