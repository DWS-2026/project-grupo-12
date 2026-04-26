package es.codeurjc.web.controller.rest;

import es.codeurjc.web.dto.HotelDTO;
import es.codeurjc.web.model.Hotel;
import es.codeurjc.web.service.HotelService;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/hotels")
public class HotelRestController {
    
        @Autowired
        private HotelService hotelService;

        //List hotels
        @GetMapping("/")
        public ResponseEntity<Page<HotelDTO>> getHotels(Pageable pageable){
            Page<Hotel> hotels = hotelService.getAllHotels(pageable);
            Page<HotelDTO> dtos = hotels.map(HotelDTO::new);
            return ResponseEntity.ok(dtos); //It returns 200 OK with the JSON
        }

        //Get hotel 
        @GetMapping("/{id}")
        public ResponseEntity<HotelDTO> getHotel(@PathVariable long id){
            Optional<Hotel> hotel = hotelService.getHotelById(id);
            return hotel.map(h -> ResponseEntity.ok(new HotelDTO(h)))
                        .orElseGet(() -> ResponseEntity.notFound().build());
                        //If the hotel is not found, it returns 404 Not Found
        }

        //Create hotel
        @PostMapping("/")
        public ResponseEntity<HotelDTO> createHotel(@RequestBody HotelDTO hotelDto){
            Hotel newHotel = hotelService.createHotelFromDto(hotelDto);
        return new ResponseEntity<>(new HotelDTO(newHotel), HttpStatus.CREATED);
        }

        //Update hotel
        @PutMapping("/{id}")
        public ResponseEntity<HotelDTO> updateHotel(@PathVariable Long id, @RequestBody HotelDTO hotelDto){
            return hotelService.updateHotelFromDto(id, hotelDto)
            .map(h -> ResponseEntity.ok(new HotelDTO(h)))
            .orElse(ResponseEntity.notFound().build());
        }

        //Delete hotel
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteHotel(@PathVariable Long id){
            if (hotelService.getHotelById(id).isPresent()){
                hotelService.deleteHotel(id);
                return ResponseEntity.noContent().build(); //204 No Content
            }
            return ResponseEntity.notFound().build(); //404 Not Found
        }



}
