package es.codeurjc.web.controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ReviewController {

    
    @PostMapping("/hotel/{id}/review")
    public String showHotel(@PathVariable long id,
                            @RequestParam int rating, 
                            @RequestParam String comment, 
                            Model model) {
        // 1. Aquí buscamos el Hotel en la base de datos usando su 'id'
        // 2. Aquí buscamos al Usuario que ha iniciado sesión
        // 3. Creamos la nueva Review: Review r = new Review(rating, comment, autor, hotel);
        // 4. Guardamos la Review en la base de datos
    
    return "redirect:/hotel/" + id; // Recargamos la página del hotel
    }
}