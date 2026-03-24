package es.codeurjc.web.service;

import es.codeurjc.web.model.Hotel;
import es.codeurjc.web.repository.HotelRepository;
import org.springframework.stereotype.Service;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

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

    public HotelService(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
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

    public void saveOrUpdateHotel(Long id, String name, String tipo, String city, String location, double price, String description, double rating, String galeriaRaw, Set<String> services) {
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

        if (!galeriaRaw.isBlank()) {
            List<String> galeria = Arrays.asList(galeriaRaw.split(","));
            galeria.replaceAll(String::trim);
            hotel.setGaleria(galeria);
        }

        if (services != null) {
            hotel.setServices(services);
        }

        hotelRepository.save(hotel);
    }
}
