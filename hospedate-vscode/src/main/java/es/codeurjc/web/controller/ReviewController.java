package es.codeurjc.web.controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ReviewController {

    
    @GetMapping("/hotel/{id}#reviews")
    public String showHotel(@PathVariable long id, Model model) {
        return "hotel"; 
    }
}