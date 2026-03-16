package es.codeurjc.web.controller;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.web.model.Hotel;
import es.codeurjc.web.model.Review;
import es.codeurjc.web.model.User;
import es.codeurjc.web.service.ReviewService;
import es.codeurjc.web.service.HotelService;
import es.codeurjc.web.service.UserService;
import es.codeurjc.web.service.UserSession;

@Controller
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private HotelService hotelService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserSession userSession;
    
    @PostMapping("/hotel/{id}/review")
    public String showHotel(@PathVariable long id,
                            @RequestParam int rating, 
                            @RequestParam String title,
                            @RequestParam String comment, 
                            Model model) {
        
        // If the user is not logged in, redirect to the login page
        if(!userSession.isLogged()){
            return "redirect:/login"; 
        }
        //We search for the Hotel and the User in the database
        Hotel hotel = hotelService.getHotelById(id).orElseThrow();
        User author = userService.findById(userSession.getIdUser()).orElseThrow();

        //We created and saved the review in the database
        reviewService.createReview(rating, title, comment, author, hotel);
        
    return "redirect:/hotel/" + id; // We reloaded the hotel page
    }

    @PostMapping("/review/delete/{id}")
    public String deleteReview(@PathVariable long id){
        // If the user is not logged in, redirect to the login page
        if(!userSession.isLogged()){
            return "redirect:/login"; 
        }

        Review review = reviewService.getReviewById(id).orElseThrow();

        // Protection against IDOR
        // We compare the review owner's ID with the logged-in user's ID
        if (!review.getAuthor().getId().equals(userSession.getIdUser())) {
                return "redirect:/";
            }
        
            reviewService.deleteReview(id);

        return "redirect:/profile";
    }
    
}