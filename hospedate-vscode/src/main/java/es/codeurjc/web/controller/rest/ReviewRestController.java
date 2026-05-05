package es.codeurjc.web.controller.rest;

import es.codeurjc.web.dto.ReviewDTO;
import es.codeurjc.web.model.Hotel;
import es.codeurjc.web.model.Review;
import es.codeurjc.web.model.User;
import es.codeurjc.web.service.HotelService;
import es.codeurjc.web.service.ReviewService;
import es.codeurjc.web.service.UserService;
import es.codeurjc.web.service.UserSession;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;

import java.net.URI;
import java.util.Optional;

@Tag(name = "Reviews")
@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewRestController {
    
    @Autowired
    private ReviewService reviewService;

    @Autowired
    private HotelService hotelService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserSession userSession;

    @Operation(summary = "Create a review")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Created"),
        @ApiResponse(responseCode = "401", description = "Unauthorized – login required"),
        @ApiResponse(responseCode = "404", description = "Hotel not found")
    })
    @PostMapping("/")
    public ResponseEntity<ReviewDTO> createReview(@Valid @RequestBody ReviewDTO dto){
        if(!userSession.isLogged()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<Hotel> hotelOpt = hotelService.getHotelById(dto.getHotelId());
        if(hotelOpt.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        User author = userService.findById(userSession.getIdUser()).orElseThrow();

        Review review = reviewService.createReview(
            dto.getRating(),
            dto.getTitle(),
            dto.getComment(),
            author,
            hotelOpt.get()
        );

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(review.getId())
                .toUri();

        return ResponseEntity.created(location).body(new ReviewDTO(review));
    }

    @Operation(summary = "Delete a review")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Deleted"),
        @ApiResponse(responseCode = "401", description = "Unauthorized – login required"),
        @ApiResponse(responseCode = "403", description = "Forbidden – not the author"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id){
        if(!userSession.isLogged()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<Review> reviewOpt = reviewService.getReviewById(id);
        if(reviewOpt.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        Review review = reviewOpt.get();

        if(!review.getAuthor().getId().equals(userSession.getIdUser())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get review by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ReviewDTO> getReview(@PathVariable Long id){
        Optional<Review> reviewOpt = reviewService.getReviewById(id);
        return reviewOpt.map(review -> ResponseEntity.ok(new ReviewDTO(review)))
                        .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
