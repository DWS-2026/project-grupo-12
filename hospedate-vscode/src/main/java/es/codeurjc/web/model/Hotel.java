package es.codeurjc.web.model;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Hotel{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String city;
    private double price;
    private String description;

    public Hotel() {}

    public Hotel(Long id, String name, String city, double price, String description) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.price = price;
        this.description = description;
    }

}