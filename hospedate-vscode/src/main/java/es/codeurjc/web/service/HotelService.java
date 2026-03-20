package es.codeurjc.web.service;

import es.codeurjc.web.model.Hotel;
import es.codeurjc.web.repository.HotelRepository;
import org.springframework.stereotype.Service;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.HashSet;

@Service
public class HotelService {
    
    private final HotelRepository hotelRepository;

    public HotelService(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    public List<Hotel> getAllHotels() {
        return hotelRepository.findAll();
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

    public List<Hotel> searchHotels(String keyword) {
        // We passed the word 3 times (so that it searches in name, city and location)
        return hotelRepository.findByNameContainingIgnoreCaseOrCityContainingIgnoreCaseOrLocationContainingIgnoreCase(keyword, keyword, keyword);
    }

    public void saveHotel(Hotel hotel) {
        hotelRepository.save(hotel);
    }

    public void deleteHotel(Long id) {
        hotelRepository.deleteById(id);
    }
}
