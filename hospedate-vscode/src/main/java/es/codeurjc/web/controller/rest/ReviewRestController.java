package es.codeurjc.web.controller.rest;

import java.net.URI;
import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import es.codeurjc.web.dto.ReviewDTO;
import es.codeurjc.web.model.Hotel;
import es.codeurjc.web.model.Review;
import es.codeurjc.web.model.User;
import es.codeurjc.web.service.HotelService;
import es.codeurjc.web.service.ReviewService;
import es.codeurjc.web.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

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


    @Operation(summary = "Create a review")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Created"),
        @ApiResponse(responseCode = "401", description = "Unauthorized – login required"),
        @ApiResponse(responseCode = "404", description = "Hotel not found")
    })
    //Create review
    @PostMapping("/")
    public ResponseEntity<ReviewDTO> createReview(@Valid @RequestBody ReviewDTO dto, Principal principal){
        //Get the hotel
        Optional<Hotel> hotelOpt = hotelService.getHotelById(dto.getHotelId());
        if(hotelOpt.isEmpty()){
            return ResponseEntity.notFound().build(); //404
        }

        //Get the author
        User author = userService.findByEmail(principal.getName()).orElseThrow();

        //Create the review
        Review review = reviewService.createReview(
            dto.getRating(),
            dto.getTitle(),
            dto.getComment(),
            author,
            hotelOpt.get()
        );

        //Generate the header location
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(review.getId())
                .toUri();

        return ResponseEntity.created(location).body(new ReviewDTO(review)); //201 Created
    }




    @Operation(summary = "Delete a review")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Deleted"),
        @ApiResponse(responseCode = "401", description = "Unauthorized – login required"),
        @ApiResponse(responseCode = "403", description = "Forbidden – not the author"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    //Delete review
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id, Principal principal){


        Optional<Review> reviewOpt = reviewService.getReviewById(id);
        if(reviewOpt.isEmpty()){
            return ResponseEntity.notFound().build(); //404
        }

        Review review = reviewOpt.get();

        //IDOR: Only the author or an admin can delete the review
        if(!review.getAuthor().getId().equals(userService.findByEmail(principal.getName()).orElseThrow().getId())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); //403
        }

        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build(); //204 No Content
    }





    @Operation(summary = "Get review by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    //Get review
    @GetMapping("/{id}")
    public ResponseEntity<ReviewDTO> getReview(@PathVariable Long id){
        Optional<Review> reviewOpt = reviewService.getReviewById(id);
        return reviewOpt.map(review -> ResponseEntity.ok(new ReviewDTO(review)))
                        .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @Operation(summary = "Update a review")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "401", description = "Unauthorized – login required"),
        @ApiResponse(responseCode = "403", description = "Forbidden – not the author"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })


    
    @PutMapping("/{id}")
    public ResponseEntity<ReviewDTO> updateReview(@PathVariable Long id, @Valid @RequestBody ReviewDTO reviewDto, Principal principal){
        //Find original review
        Optional<Review> reviewOpt = reviewService.getReviewById(id);
        if(reviewOpt.isEmpty()){
            return ResponseEntity.notFound().build(); // 404
        }

        Review review = reviewOpt.get();

        // Verify identity, only the author can update the review
        if(!review.getAuthor().getId().equals(userService.findByEmail(principal.getName()).orElseThrow().getId())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403
        }

        // Update the review and return the updated DTO
        return reviewService.updateReviewFromDto(id, reviewDto)
            .map(r -> ResponseEntity.ok(new ReviewDTO(r)))
            .orElse(ResponseEntity.notFound().build());
    }
}
