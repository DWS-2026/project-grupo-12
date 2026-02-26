package es.codeurjc.web.model;

public class Hotel{
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