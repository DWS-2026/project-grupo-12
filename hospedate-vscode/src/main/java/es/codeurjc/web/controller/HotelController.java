package es.codeurjc.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.web.model.Hotel;
import es.codeurjc.web.model.Review;
import es.codeurjc.web.service.HotelService;
import es.codeurjc.web.service.UserSession;

@Controller
public class HotelController {

    @Autowired
    private HotelService hotelService;

    @Autowired
    private UserSession userSession;


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

        model.addAttribute("hasHotels", !hotelsList.isEmpty());
        
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
            
            // We get the list of the reviews for this exact hotel
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
                // Wer take the first photo for the main image
                model.addAttribute("mainImage", galeria.get(0));
                
                // Wer take the next 3 photos for the side images, if there are enough photos
                if (galeria.size() > 1) {
                    model.addAttribute("sideImages", galeria.subList(1, Math.min(galeria.size(), 3)));
                }
                
                // The rest of the photos to the bottom page
                if (galeria.size() > 3) {
                    model.addAttribute("bottomImages", galeria.subList(3,  Math.min(galeria.size(), 6)));
                }
                
                //We prepare all the photos and tell mustache which one is the first
                List<java.util.Map<String, Object>> carouselImages = new java.util.ArrayList<>();
                for (int i = 0; i < galeria.size(); i++) {
                    java.util.Map<String, Object> imgData = new java.util.HashMap<>();
                    imgData.put("url", galeria.get(i));
                    imgData.put("active", i == 0); // Only the first image will be active
                    carouselImages.add(imgData);
                }
                model.addAttribute("carouselImages", carouselImages);
            }
            
            return "hotel";
        }
        return "redirect:/hotels";
    }
}