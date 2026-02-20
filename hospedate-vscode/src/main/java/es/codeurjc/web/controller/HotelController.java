package es.codeurjc.web.controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class HotelController {

    @GetMapping("/hotels")
    public String showHotels(Model model) {
        
        return "HotelList"; 
    }

    
    @GetMapping("/hotel/{id}")
    public String showHotel(@PathVariable long id, Model model) {
        return "hotel"; 
    }
}