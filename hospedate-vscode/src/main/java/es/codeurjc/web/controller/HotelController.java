package es.codeurjc.web.controller;

import es.codeurjc.web.model.Hotel;
import es.codeurjc.web.model.Review;
import es.codeurjc.web.service.HotelService;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String showHotels(@RequestParam(required = false) String keyword, Model model) {
List<Hotel> hotelsList;

        if (keyword != null && !keyword.trim().isEmpty()) {
            // Si el usuario ha escrito algo en el buscador, usamos el método nuevo
            hotelsList = hotelService.searchHotels(keyword); // (Crearemos este método en el servicio ahora)
            model.addAttribute("keyword", keyword); // Pasamos la palabra de vuelta para mantenerla en la barra
        } else {
            // Si no hay búsqueda, devolvemos todos como antes
            hotelsList = hotelService.getAllHotels();
        }

        model.addAttribute("hotels", hotelsList);
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
            
            List<String> galeria = h.getGaleria();
            if (galeria != null && !galeria.isEmpty()) {
                // Sacamos la primera foto para el espacio grande
                model.addAttribute("mainImage", galeria.get(0));
                
                // Sacamos de la foto 2 a la 4 para el lateral (si hay suficientes)
                if (galeria.size() > 1) {
                    model.addAttribute("sideImages", galeria.subList(1, Math.min(galeria.size(), 4)));
                }
                
                // Sacamos el resto de fotos para la parte de abajo
                if (galeria.size() > 4) {
                    model.addAttribute("bottomImages", galeria.subList(4, galeria.size()));
                }
            }
            
            return "hotel";
        }
        return "redirect:/hotels";
    }
}