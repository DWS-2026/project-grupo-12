package es.codeurjc.web.controller;

import es.codeurjc.web.model.Hotel;
import es.codeurjc.web.model.Review;
import es.codeurjc.web.service.HotelService;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping("/")
    public String index(Model model) {

        List<Hotel> topHoteles = hotelService.getTop3Hotels();
        
        model.addAttribute("hotels", topHoteles);

        return "index"; 
    }

    @GetMapping("/hotels")
    public String showHotels(Model model) {
        model.addAttribute("hotels", hotelService.getAllHotels());
        return "HotelList"; 
    }

    @GetMapping("/hotel/{id}")
    public String showHotel(@PathVariable long id, Model model) {
        Optional<Hotel> hotel = hotelService.getHotelById(id);
        if (hotel.isPresent()) {
            Hotel h = hotel.get();
            model.addAttribute("hotel", h);

            // Dynamic title for the tab
            model.addAttribute("tab_title", h.getName() + " - Hospédate");
            model.addAttribute("metadata_content", "Reserva tu estancia en " + h.getName() + ", situado en " + h.getLocation());
            
            // We get the lisdt of the reviews for this exact hotel
            List<Review> reviewList = h.getReviews(); 
            
            // We add the list of reviews to the model so that we can display them in the hotel page
            model.addAttribute("reviews", reviewList);

            // Create a star list based on the rating
            List<Integer> stars = new ArrayList<>();
            int starsCount = (int) Math.round(h.getRating());
            for (int i = 0; i < starsCount; i++) {
                stars.add(1);
            }
            model.addAttribute("stars", stars);
            
            return "hotel";
        }
        return "redirect:/hotels";
    }
}