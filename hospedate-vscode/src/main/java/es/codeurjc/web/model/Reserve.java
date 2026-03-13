package es.codeurjc.web.model;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

@Entity
public class Reserve {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Hotel hotel;    //Many reservations belong to a single hotel

    @ManyToOne
    private User customer;  //Many reservations belong to a single customer

    private double price;
    private int guests;
    private LocalDate entryDate;
    private LocalDate departureDate;
    private long nights;

    //We have a status to save reservation data in the database
    //such as "Pending_confirmation" or "CONFIRMED" so that no one can modify the data.
    private String status; 

    

    @ElementCollection
    private List<String> extras = new ArrayList<>();

    //Reserve constructor
    public Reserve(Hotel hotel, User customer, double price, int guests, LocalDate entryDate, LocalDate departureDate, long nights) {
        this.hotel = hotel;
        this.customer = customer;
        this.price = price;
        this.guests = guests;
        this.entryDate = entryDate;
        this.departureDate = departureDate;
        this.nights = nights;
    }

    //Empty constructor for JPA
    public Reserve(){}

    //Getters and Setters

    public Hotel getHotel(){
        return hotel;
    }

    public void setHotel(Hotel hotel){
        this.hotel = hotel;
    }

    public User getCustomer(){
        return customer;
    }

    public void setCustomer(User customer){
        this.customer = customer;
    }

    public double getPrice(){
        return price;
    }

    public void setPrice(double price){
        this.price = price;
    }

    public int getGuests(){
        return guests;
    }

    public void setGuests(int guests){
        this.guests = guests;
    }

    public LocalDate getEntryDate(){
        return entryDate;
    }

    public void setEntryDate(LocalDate entryDate){
        this.entryDate = entryDate;
    }
    public LocalDate getDepartureDate(){
        return departureDate;
    }

    public void setDepartureDate(LocalDate departureDate){
        this.departureDate = departureDate;
    }

    public Long getId(){
        return id;
    }

    public List<String> getExtras(){
        return extras;
    }

    public void setExtras(List<String> extras){
        this.extras = extras;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getNights(){
        return nights;
    }

    public void setNights(long nights){
        this.nights = nights;
    }

}
