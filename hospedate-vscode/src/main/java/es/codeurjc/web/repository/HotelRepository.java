package es.codeurjc.web.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import es.codeurjc.web.model.Hotel; 


@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    
    //the name of the hotel is unique, so we return an Optional<Hotel> 
    Optional<Hotel> findByName(String name); 
    
    //we can search for hotels by city, type, location, price and rating, so we return a List<Hotel> for each of these attributes
    List<Hotel> findByCity(String city);
    List<Hotel> findByTipo(String tipo);
    List<Hotel> findByLocation(String location);
    List<Hotel> findByPrice(double price);
    List<Hotel> findByRating(double rating);
    // Search the first 3 (Top3), ordered by rating (OrderByRating), from highest to lowest (Desc)
    List<Hotel> findTop3ByOrderByRatingDesc();

    List<Hotel> findByNameContainingIgnoreCaseOrCityContainingIgnoreCaseOrLocationContainingIgnoreCase(
        String name, String city, String location);
}
