package es.codeurjc.web.dto;

import es.codeurjc.web.model.Hotel;
import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

public class HotelDTO {

    private Long id;
    
    @NotBlank(message = "El nombre del hotel es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String name;

    @NotBlank(message = "El tipo de alojamiento es obligatorio")
    private String tipo;

    @NotBlank(message = "La ciudad es obligatoria")
    private String city;    

    @NotBlank(message = "La ubicación es obligatoria")
    private String location;

    @Min(value = 0, message = "El precio por noche no puede ser negativo")
    private double price;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Size(max = 2000, message = "La descripción es demasiado larga (máx 2000 caracteres)")
    private String description;

    @Min(value = 0, message = "La puntuación mínima es 0")
    @Max(value = 5, message = "La puntuación máxima es 5")
    private double rating;
    
    // Basic features
    private boolean wifi;
    private boolean tv;
    private boolean airConditioning;
    private boolean family;

    // Dynamic additional services
    private Set<String> services;

    // URL for the main image (we avoid sending the BLOB)
    private String mainImageUrl;

    // Empty constructor required by Jackson
    public HotelDTO() {}

    // Translator constructor (Entity -> DTO)
    public HotelDTO(Hotel hotel) {
        this.id = hotel.getId();
        this.name = hotel.getName();
        this.tipo = hotel.getTipo();
        this.city = hotel.getCity();
        this.location = hotel.getLocation();
        this.price = hotel.getPrice();
        this.description = hotel.getDescription();
        this.rating = hotel.getManualRating(); // We use the manual rating
        
        this.wifi = hotel.isWifi();
        this.tv = hotel.isTv();
        this.airConditioning = hotel.isAirConditioning();
        this.family = hotel.isFamily();
        
        this.services = hotel.getServices(); // Copy the Set of Strings

        // Generate the main image URL using the model method
        this.mainImageUrl = hotel.getMainImage();
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