package es.codeurjc.web.controller.rest;

import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import es.codeurjc.web.dto.HotelDTO;
import es.codeurjc.web.dto.ReviewDTO;
import es.codeurjc.web.model.Hotel;
import es.codeurjc.web.service.HotelService;
import es.codeurjc.web.service.ImageService;
import es.codeurjc.web.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Hotels")
@RestController
@RequestMapping("/api/v1/hotels")
public class HotelRestController {

        @Autowired
        private HotelService hotelService;

        @Autowired
        private ReviewService reviewService; 

        @Autowired
        private ImageService imageService;

        // Retrieves a paginated list of hotels, optionally applying a keyword filter for city or name searches
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
                // Transforms the incoming DTO into a Hotel entity and persists it to the database
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

    // 2. Endpoint to upload new images (POST)
    @Operation(summary = "Upload a new image to a hotel")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Image uploaded"),
        @ApiResponse(responseCode = "400", description = "Invalid or unsafe image file"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Hotel not found")
    })
    @PostMapping("/{id}/images")
    public ResponseEntity<Map<String, Object>> uploadHotelImage(
            @PathVariable Long id,
            @RequestParam MultipartFile file) throws IOException {

        Optional<Hotel> hotelOpt = hotelService.getHotelById(id);
        if (hotelOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Security check using magic bytes (JPG/PNG only)
        if (!imageService.isImageSafe(file)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid or unsafe image file"));
        }

        Hotel hotel = hotelOpt.get();

        // Create image entity and link it to the hotel
        es.codeurjc.web.model.Image image = imageService.createImage(file);
        hotel.getGaleria().add(image);
        hotelService.save(hotel);

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/images/{imageId}")
                .buildAndExpand(image.getId())
                .toUri();

        Map<String, Object> body = Map.of(
                "imageId", image.getId(),
                "hotelId", hotel.getId(),
                "url", "/api/v1/images/" + image.getId()
        );

        return ResponseEntity.created(location).body(body);
    }

    // 3. Endpoint for updating an existing image (PUT)
    @Operation(summary = "Update an existing image of a hotel")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Image updated"),
        @ApiResponse(responseCode = "400", description = "Invalid or unsafe image file"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Hotel or image not found")
    })
    @PutMapping("/{id}/images/{imageId}")
    public ResponseEntity<?> updateHotelImage(
            @PathVariable Long id,
            @PathVariable Long imageId,
            @RequestParam MultipartFile file) throws IOException, SQLException {

        Optional<Hotel> hotelOpt = hotelService.getHotelById(id);
        Optional<es.codeurjc.web.model.Image> imageOpt = imageService.getImageById(imageId);

        if (hotelOpt.isEmpty() || imageOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Hotel hotel = hotelOpt.get();
        es.codeurjc.web.model.Image image = imageOpt.get();

        // The image must belong to this hotel's gallery
        boolean belongsToHotel = hotel.getGaleria().stream()
                .anyMatch(i -> i.getId() == image.getId());
        if (!belongsToHotel) {
            return ResponseEntity.notFound().build();
        }

        if (!imageService.isImageSafe(file)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid or unsafe image file"));
        }

        // Update binary data and filename
        image.setImageFile(new SerialBlob(file.getBytes()));
        image.setFileName(file.getOriginalFilename());

        imageService.saveImage(image);

        return ResponseEntity.noContent().build();
    }

}
