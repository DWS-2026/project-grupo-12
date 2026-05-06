package es.codeurjc.web.service;

import es.codeurjc.web.dto.HotelDTO;
import es.codeurjc.web.model.Hotel;
import es.codeurjc.web.repository.HotelRepository;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.HashSet;
import java.util.Set;

@Service
public class HotelService {
    
    private final HotelRepository hotelRepository;

    private final ImageService imageService;

    public HotelService(HotelRepository hotelRepository, ImageService imageService) {
        this.hotelRepository = hotelRepository;
        this.imageService = imageService;
    }
    // Original method for the Admin panel
    public List<Hotel> getAllHotels() {
        return hotelRepository.findAll();
    }
    // New method for pagination of the public web
    public Page<Hotel> getAllHotels(Pageable pageable) {
        return hotelRepository.findAll(pageable);
    }

    public Page<Hotel> searchHotels(String keyword, Pageable pageable) {
        return hotelRepository.findByNameContainingIgnoreCaseOrCityContainingIgnoreCaseOrLocationContainingIgnoreCase(keyword, keyword, keyword, pageable);
    }

    public Optional<Hotel> getHotelById(Long id) {
        return hotelRepository.findById(id);
    }

    public void addHotel(Hotel hotel) {
        hotelRepository.save(hotel);
    }

    public List<Hotel> getTop3Hotels() {
        return hotelRepository.findTop3ByOrderByRatingDesc();
    }


    public void saveHotel(Hotel hotel) {
        hotelRepository.save(hotel);
    }

    public void deleteHotel(Long id) {
        hotelRepository.deleteById(id);
    }

    // Used by the admin web panel to create or update a hotel from form data (not from DTO)
    public void saveOrUpdateHotel(Long id, String name, String tipo,
                                String city, String location, double price,
                                String description, double rating, String galeriaRaw,
                                Set<String> services, boolean wifi, boolean tv,
                                boolean airConditioning, boolean family) {
        if (price < 0) {
            throw new IllegalArgumentException("El precio del hotel no puede ser negativo");
        }
        // If id is null we create a new hotel, otherwise we load the existing one
        Hotel hotel;
        if (id == null) {
            hotel = new Hotel();
        } else {
            Optional<Hotel> existing = hotelRepository.findById(id);
            if (existing.isPresent()) {
                hotel = existing.get();
            } else {
                hotel = new Hotel();
            }
        }

        hotel.setName(name);
        hotel.setTipo(tipo);
        hotel.setCity(city);
        hotel.setLocation(location);
        hotel.setPrice(price);
        hotel.setDescription(description);
        hotel.setRating(rating);
        hotel.setWifi(wifi);
        hotel.setTv(tv);
        hotel.setAirConditioning(airConditioning);
        hotel.setFamily(family);

        // galeriaRaw is a comma-separated list of image URLs sent from the form
        // We parse each URL to extract the image ID and load the Image entity from the database
        if (galeriaRaw != null && !galeriaRaw.trim().isEmpty()) {
            List<String> urls = Arrays.asList(galeriaRaw.split(","));
            List<es.codeurjc.web.model.Image> newGallery = new java.util.ArrayList<>();

            for (String url : urls) {
                url = url.trim();
                // Only process URLs that point to our image endpoint
                if (url.contains("/hotel/image/")) {
                    try {
                        Long imgId = Long.parseLong(url.substring(url.lastIndexOf("/") + 1));
                        imageService.getImageById(imgId).ifPresent(newGallery::add);
                    } catch (NumberFormatException e) { }
                }
            }
            // Assign positions so the gallery order is preserved
            for (int i = 0; i < newGallery.size(); i++) {
                newGallery.get(i).setPosition(i);
            }
            hotel.setGaleria(newGallery);
        }
        // If it's an edit and galeriaRaw is empty, we keep the existing gallery

        if (services != null) {
            hotel.setServices(services);
        } else {
            // If null is found (all have been unchecked), we empty the database list.
            hotel.setServices(new HashSet<>());
        }

        hotelRepository.save(hotel);
    }

    // Creates a new hotel from a REST API request (DTO)
    public Hotel createHotelFromDto(HotelDTO dto) {
        Hotel hotel = new Hotel();
        mapDtoToEntity(dto, hotel);
        return hotelRepository.save(hotel);
    }

    // Updates an existing hotel from a REST API request (DTO), returns empty if not found
    public Optional<Hotel> updateHotelFromDto(Long id, HotelDTO dto) {
        return hotelRepository.findById(id).map(hotel -> {
            mapDtoToEntity(dto, hotel);
            return hotelRepository.save(hotel);
        });
    }

    // Internal mapping method, only used within the service
    private void mapDtoToEntity(HotelDTO dto, Hotel hotel) {
        if (dto.getPrice() < 0) {
            throw new IllegalArgumentException("El precio del hotel no puede ser negativo");
        }
        hotel.setName(dto.getName());
        hotel.setTipo(dto.getTipo());
        hotel.setCity(dto.getCity());
        hotel.setLocation(dto.getLocation());
        hotel.setPrice(dto.getPrice());
        hotel.setDescription(dto.getDescription());
        hotel.setWifi(dto.isWifi());
        hotel.setTv(dto.isTv());
        hotel.setAirConditioning(dto.isAirConditioning());
        hotel.setFamily(dto.isFamily());
        hotel.setServices(dto.getServices());
    }
}
