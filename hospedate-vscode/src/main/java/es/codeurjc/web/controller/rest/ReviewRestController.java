package es.codeurjc.web.controller.rest;

import es.codeurjc.web.dto.ReviewDTO;
import es.codeurjc.web.model.Hotel;
import es.codeurjc.web.model.Review;
import es.codeurjc.web.model.User;
import es.codeurjc.web.service.HotelService;
import es.codeurjc.web.service.ReviewService;
import es.codeurjc.web.service.UserService;
import es.codeurjc.web.service.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController@RequestMapping("/api/reviews")
public class ReviewRestController {
    
    @Autowired
    private ReviewService reviewService;

    @Autowired
    private HotelService hotelService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserSession userSession;

    //Create review
    @PostMapping("/")
    public ResponseEntity<ReviewDTO> createReview(@RequestBody ReviewDTO dto){
        //Verify the user is logged in
        if(!userSession.isLogged()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); //401
        }   

        //Get the hotel
        Optional<Hotel> hotelOpt = hotelService.getHotelById(dto.getHotelId());
        if(hotelOpt.isEmpty()){
            return ResponseEntity.notFound().build(); //404
        }

        //Get the author
        User author = userService.findById(userSession.getIdUser()).orElseThrow();

        //Create the review
        Review review = reviewService.createReview(
            dto.getRating(),
            dto.getTitle(),
            dto.getComment(),
            author,
            hotelOpt.get()
        );

        return new ResponseEntity<>(new ReviewDTO(review), HttpStatus.CREATED); //201 Created
    }

    //Delete review
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id){
        //Verify the user is logged in
        if(!userSession.isLogged()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); //401
        }

        Optional<Review> reviewOpt = reviewService.getReviewById(id);
        if(reviewOpt.isEmpty()){
            return ResponseEntity.notFound().build(); //404
        }

        Review review = reviewOpt.get();

        //IDOR: Only the author or an admin can delete the review
        if(!review.getAuthor().getId().equals(userSession.getIdUser())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); //403
        }

        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build(); //204 No Content
    }

    //Get review
    @GetMapping("/{id}")
    public ResponseEntity<ReviewDTO> getReview(@PathVariable Long id){
        Optional<Review> reviewOpt = reviewService.getReviewById(id);
        return reviewOpt.map(review -> ResponseEntity.ok(new ReviewDTO(review)))
                        .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
