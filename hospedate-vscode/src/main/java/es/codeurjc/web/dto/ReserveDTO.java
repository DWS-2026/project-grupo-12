package es.codeurjc.web.dto;

import es.codeurjc.web.model.Reserve;
import java.time.LocalDate;

public class ReserveDTO {

    private Long id;
    private LocalDate entryDate;
    private LocalDate departureDate;
    private int guests;
    private long nights;
    private double price;
    private String status;

    // Información simplificada de las relaciones para evitar bucles
    private Long hotelId;
    private String hotelName;
    private Long customerId;
    private String customerName;

    // Constructor vacío (necesario para la serialización de JSON)
    public ReserveDTO() {}

    // Constructor para convertir de Entidad a DTO
    public ReserveDTO(Reserve reserve) {
        this.id = reserve.getId();
        this.entryDate = reserve.getEntryDate();
        this.departureDate = reserve.getDepartureDate();
        this.guests = reserve.getGuests();
        this.nights = reserve.getNights();
        this.price = reserve.getPrice();
        this.status = reserve.getStatus();

        // Extraemos datos del Hotel de forma segura
        if (reserve.getHotel() != null) {
            this.hotelId = reserve.getHotel().getId();
            this.hotelName = reserve.getHotel().getName();
        }

        // Extraemos datos del Usuario (cliente) de forma segura
        if (reserve.getCustomer() != null) {
            this.customerId = reserve.getCustomer().getId();
            this.customerName = reserve.getCustomer().getName();
        }
    }

    // Getters y Setters
    public Long getId() { 
        return id; 
    }
    public void setId(Long id) { 
        this.id = id; 
    }

    public LocalDate getEntryDate() { 
        return entryDate; 
    }
    public void setEntryDate(LocalDate entryDate) {
        this.entryDate = entryDate; 
    }

    public LocalDate getDepartureDate() { 
        return departureDate; 
    }
    public void setDepartureDate(LocalDate departureDate) { 
        this.departureDate = departureDate; 
    }

    public int getGuests() { 
        return guests; 
    }
    public void setGuests(int guests) { 
        this.guests = guests; 
    }

    public long getNights() { 
        return nights; 
    }
    public void setNights(long nights) { 
        this.nights = nights; 
    }

    public double getPrice() { 
        return price; 
    }
    public void setPrice(double price) { 
        this.price = price; 
    }

    public String getStatus() { 
        return status; 
    }
    public void setStatus(String status) { 
        this.status = status; 
    }

    public Long getHotelId() { 
        return hotelId; 
    }
    public void setHotelId(Long hotelId) { 
        this.hotelId = hotelId; 
    }

    public String getHotelName() { 
        return hotelName; 
    }
    public void setHotelName(String hotelName) { 
        this.hotelName = hotelName; 
    }

    public Long getCustomerId() { 
        return customerId; 
    }
    public void setCustomerId(Long customerId) { 
        this.customerId = customerId; 
    }

    public String getCustomerName() { 
        return customerName; 
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName; 
    }
}