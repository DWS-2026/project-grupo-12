package es.codeurjc.web.controller.rest;

import es.codeurjc.web.dto.HotelDTO;
import es.codeurjc.web.dto.ReviewDTO;
import es.codeurjc.web.model.Hotel;
import es.codeurjc.web.service.HotelService;
import es.codeurjc.web.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;

import java.util.Optional;

@Tag(name = "Hotels")
@RestController
@RequestMapping("/api/v1/hotels")
public class HotelRestController {

        @Autowired
        private HotelService hotelService;

        @Autowired
        private ReviewService reviewService; 

        @Operation(summary = "List all hotels (paginated)")
        @ApiResponse(responseCode = "200", description = "OK")
        //List hotels
        @GetMapping("/")
        public ResponseEntity<Page<HotelDTO>> getHotels(Pageable pageable){
            Page<Hotel> hotels = hotelService.getAllHotels(pageable);
            Page<HotelDTO> dtos = hotels.map(HotelDTO::new);
            return ResponseEntity.ok(dtos); //It returns 200 OK with the JSON
        }



        @Operation(summary = "Get hotel by ID")
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Not found")
        })
        //Get hotel
        @GetMapping("/{id}")
        public ResponseEntity<HotelDTO> getHotel(@PathVariable long id){
            Optional<Hotel> hotel = hotelService.getHotelById(id);
            return hotel.map(h -> ResponseEntity.ok(new HotelDTO(h)))
                        .orElseGet(() -> ResponseEntity.notFound().build()); //If the hotel is not found, it returns 404 Not Found
        }




        @Operation(summary = "Create a hotel")
        @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
        })
        //Create hotel
        @PostMapping("/")
        public ResponseEntity<?> createHotel(@Valid @RequestBody HotelDTO hotelDto){
            try {
                Hotel newHotel = hotelService.createHotelFromDto(hotelDto);
                //Generate the header location
                URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(newHotel.getId())
                        .toUri();
                return ResponseEntity.created(location).body(new HotelDTO(newHotel));
            } catch (IllegalArgumentException e) {
                // if the price is negative, it returns 400 Bad Request with the error message
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }



        @Operation(summary = "Update a hotel")
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Not found")
        })
        //Update hotel
        @PutMapping("/{id}")
        public ResponseEntity<HotelDTO> updateHotel(@PathVariable Long id, @Valid @RequestBody HotelDTO hotelDto){
            return hotelService.updateHotelFromDto(id, hotelDto)
            .map(h -> ResponseEntity.ok(new HotelDTO(h)))
            .orElse(ResponseEntity.notFound().build());
        }



        @Operation(summary = "Delete a hotel")
        @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Deleted"),
            @ApiResponse(responseCode = "404", description = "Not found")
        })
        //Delete hotel
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteHotel(@PathVariable Long id){
            if (hotelService.getHotelById(id).isPresent()){
                hotelService.deleteHotel(id);
                return ResponseEntity.noContent().build(); //204 No Content
            }
            return ResponseEntity.notFound().build(); //404 Not Found
        }
        



    @Operation(summary = "Get all reviews for a specific hotel")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "404", description = "Hotel not found")
    })
    @GetMapping("/{hotelId}/reviews") 
    public ResponseEntity<Page<ReviewDTO>> getHotelReviews(
            @PathVariable Long hotelId, 
            Pageable pageable) {

        // check if the hotel exists, if not return 404 Not Found
        if (hotelService.getHotelById(hotelId).isEmpty()) {
            return ResponseEntity.notFound().build(); // 404
        }

        //find reviews by hotel id and convert to DTOs
        Page<ReviewDTO> reviews = reviewService
                .getReviewsByHotelId(hotelId, pageable)
                .map(ReviewDTO::new);

        return ResponseEntity.ok(reviews);
    }



}
