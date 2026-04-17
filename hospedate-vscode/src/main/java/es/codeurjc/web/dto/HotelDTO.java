package es.codeurjc.web.dto;

import es.codeurjc.web.model.Hotel;
import java.util.Set;

public class HotelDTO {

    private Long id;
    private String name;
    private String tipo;
    private String city;    
    private String location;
    private double price;
    private String description;
    private double rating;
    
    // Características básicas
    private boolean wifi;
    private boolean tv;
    private boolean airConditioning;
    private boolean family;

    // Servicios adicionales dinámicos
    private Set<String> services;

    // URL para la imagen principal (evitamos enviar el BLOB)
    private String mainImageUrl;

    // Constructor vacío para Jackson
    public HotelDTO() {}

    // Constructor traductor (Entidad -> DTO)
    public HotelDTO(Hotel hotel) {
        this.id = hotel.getId();
        this.name = hotel.getName();
        this.tipo = hotel.getTipo();
        this.city = hotel.getCity();
        this.location = hotel.getLocation();
        this.price = hotel.getPrice();
        this.description = hotel.getDescription();
        this.rating = hotel.getManualRating(); // Usamos el rating manual o el calculado según prefieras
        
        this.wifi = hotel.isWifi();
        this.tv = hotel.isTv();
        this.airConditioning = hotel.isAirConditioning();
        this.family = hotel.isFamily();
        
        this.services = hotel.getServices(); // Copiamos el Set de Strings

        // Generamos la URL de la imagen principal usando el método que ya tienes en el modelo
        this.mainImageUrl = hotel.getMainImage();
    }

    // Getters y Setters
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

    public double getRating() { 
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

    public Set<String> getServices() { 
        return services; 
    }
    public void setServices(Set<String> services) { 
        this.services = services; 
    }

    public String getMainImageUrl() { 
        return mainImageUrl; 
    }
    public void setMainImageUrl(String mainImageUrl) { 
        this.mainImageUrl = mainImageUrl; 
    }
}