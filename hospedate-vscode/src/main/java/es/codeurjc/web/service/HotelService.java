package es.codeurjc.web.service;

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

    public void saveOrUpdateHotel(Long id, String name, String tipo, 
                                String city, String location, double price,
                                String description, double rating, String galeriaRaw,  
                                Set<String> services, boolean wifi, boolean tv, 
                                boolean airConditioning, boolean family) {
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

        if (galeriaRaw != null && !galeriaRaw.trim().isEmpty()) {
            List<String> urls = Arrays.asList(galeriaRaw.split(","));
            List<es.codeurjc.web.model.Image> newGallery = new java.util.ArrayList<>();
            
            for (String url : urls) {
                url = url.trim();
                if (url.contains("/hotel/image/")) {
                    try {
                        Long imgId = Long.parseLong(url.substring(url.lastIndexOf("/") + 1));
                        imageService.getImageById(imgId).ifPresent(newGallery::add);
                    } catch (NumberFormatException e) { }
                }
            }
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
}
