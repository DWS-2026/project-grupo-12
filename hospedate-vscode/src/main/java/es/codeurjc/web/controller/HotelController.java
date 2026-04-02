package es.codeurjc.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import es.codeurjc.web.model.Hotel;
import es.codeurjc.web.model.Review;
import es.codeurjc.web.service.HotelService;
import es.codeurjc.web.service.ImageService;
import es.codeurjc.web.service.UserSession;
import es.codeurjc.web.model.Image;

@Controller
public class HotelController {

    @Autowired
    private HotelService hotelService;

    @Autowired
    private UserSession userSession;

    @Autowired
    private ImageService imageService;


    @GetMapping("/")
    public String index(Model model) {

        List<Hotel> topHoteles = hotelService.getTop3Hotels();
        
        model.addAttribute("hotels", topHoteles);

        return "index"; 
    }

    @GetMapping("/hotels")
    public String showHotels(
            @RequestParam(required = false) String keyword, 
            @RequestParam(defaultValue = "0") int page, //By default we start on page 0
            Model model) {
        
        // We created a pagination request: Current page, and 9 hotels per page
        Pageable pageable = PageRequest.of(page, 9); 
        Page<Hotel> hotelsPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            hotelsPage = hotelService.searchHotels(keyword, pageable); 
            model.addAttribute("keyword", keyword); 
        } else {
            hotelsPage = hotelService.getAllHotels(pageable);
        }

        // hotelsPage.getContent() extracts only the list of hotels from that specific page
        model.addAttribute("hotels", hotelsPage.getContent());
        model.addAttribute("hasHotels", !hotelsPage.isEmpty());
        
        model.addAttribute("hasNext", hotelsPage.hasNext());
        model.addAttribute("hasPrev", hotelsPage.hasPrevious());
        model.addAttribute("nextPage", page + 1);
        model.addAttribute("prevPage", page - 1);
        
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
            
            List<Image> galeriaObj = h.getGaleria();
            if (galeriaObj != null && !galeriaObj.isEmpty()) {
                
                // 1. Traducimos los objetos Image a URLs de texto
                List<String> galeria = new ArrayList<>();
                for (Image img : galeriaObj) {
                    galeria.add("/hotel/image/" + img.getId());
                }

                // We take the first photo for the main image
                model.addAttribute("mainImage", galeria.get(0));
                
                // We take the next 3 photos for the side images
                if (galeria.size() > 1) {
                    model.addAttribute("sideImages", galeria.subList(1, Math.min(galeria.size(), 3)));
                }
                
                // The rest of the photos to the bottom page
                if (galeria.size() > 4) {
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

    @GetMapping("/hotel/image/{id}")
    public ResponseEntity<Object> getHotelImage(@PathVariable Long id) {
        try {
            org.springframework.core.io.Resource imageFile = imageService.getImageFile(id); 
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(imageFile);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}