package es.codeurjc.web.controller;

import es.codeurjc.web.model.Hotel;
import es.codeurjc.web.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class HotelController {

    @Autowired
    private HotelService hotelService;

    @GetMapping("/")
    public String index() {
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
            model.addAttribute("hotel", hotel.get());
            
            // Crear lista de estrellas basada en el rating
            List<Integer> stars = new ArrayList<>();
            int starsCount = (int) Math.round(hotel.get().getRating());
            for (int i = 0; i < starsCount; i++) {
                stars.add(1);
            }
            model.addAttribute("stars", stars);
            
            return "hotel";
        }
        return "redirect:/hotels";
    }
}