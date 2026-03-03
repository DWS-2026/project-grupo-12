package es.codeurjc.web.model;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class Hotel{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    
    private Long id;

    private String name;
    private String tipo;
    private String city;
    private String location;
    private double price;
    private String description;
    private List<String> galeria;
    private List<String> services;
    private double rating;

    public Hotel() {}

    public Hotel(Long id, String name, String tipo, String city, String location, 
                 double price, String description, List<String> galeria, 
                 List<String> services, double rating) {
        this.id = id;
        this.name = name;
        this.tipo = tipo;
        this.city = city;
        this.location = location;
        this.price = price;
        this.description = description;
        this.galeria = galeria;
        this.services = services;
        this.rating = rating;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<String> getServices() {
        return services;
    }

    public void setServices(List<String> services) {
        this.services = services;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}