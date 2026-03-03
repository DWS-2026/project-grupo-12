package es.codeurjc.web.model;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

@Entity
public class Reserve {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String hotelName;
    private String address;
    private double price;
    private int guests;
    private LocalDate entryDate;
    private LocalDate departureDate;
    private List<String> extras = new ArrayList<>();
}
