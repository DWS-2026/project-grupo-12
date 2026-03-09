package es.codeurjc.web.controller;
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
import es.codeurjc.web.repository.ReviewRepository;
import es.codeurjc.web.service.HotelService;
import es.codeurjc.web.service.UserService;
import es.codeurjc.web.service.UserSession;

@Controller
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

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

        //We created the new Review using the constructor
        Review review = new Review(rating, title, comment, author, hotel);

        //We saved the review in the database; the publishDate will be automatically added thanks to @PrePersist.
        reviewRepository.save(review);
        
    return "redirect:/hotel/" + id; // Recargamos la página del hotel
    }

    
}