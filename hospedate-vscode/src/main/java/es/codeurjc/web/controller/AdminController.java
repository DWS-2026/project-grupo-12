package es.codeurjc.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import es.codeurjc.web.model.Hotel;
import es.codeurjc.web.model.User;
import es.codeurjc.web.repository.UserRepository;
import es.codeurjc.web.service.HotelService;
import es.codeurjc.web.service.UserSession;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
public class AdminController {

    @Autowired
    private HotelService hotelService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSession userSession;


    @ModelAttribute
    public void addAttributes(Model model, HttpServletRequest request) {

        // --- B) Lógica de Títulos y Metadatos según la URL --- 
        String currentRoute = request.getRequestURI();

        //Prottection to secure that only admins can access /admin
        model.addAttribute("isAdmin", userSession.isAdmin());
        model.addAttribute("isLogged", userSession.isLogged());

        if (currentRoute.equals("/admin/hotels")) {
            model.addAttribute("tab_title", "Administración de Hoteles - Hospédate");
            model.addAttribute("metadata_content", "Página de administración de hoteles.");
        } else if (currentRoute.startsWith("/admin/hotels/new")) {
            model.addAttribute("tab_title", "Crear Hotel - Hospédate");
            model.addAttribute("metadata_content", "Página de creación de hoteles.");
        } else if (currentRoute.startsWith("/admin/hotels/edit")) {
            model.addAttribute("tab_title", "Editar Hotel - Hospédate");
            model.addAttribute("metadata_content", "Página de edición de hoteles.");
        } else if (currentRoute.startsWith("/admin/users")) {
            model.addAttribute("tab_title", "Administración de Usuarios - Hospédate");
            model.addAttribute("metadata_content", "Página de administración de usuarios.");
        } else {
            model.addAttribute("tab_title", "Administración - Hospédate");
            model.addAttribute("metadata_content", "Página de administración.");
        }
    }
    //List all the hotels
    @GetMapping("/admin/hotels")
    public String adminHotels(Model model) {
        model.addAttribute("hotels", hotelService.getAllHotels());
        return "admin_hotel"; 
    }

    // Shows a form to create a new hotel (without ID yet)
    @GetMapping("/admin/hotels/new")
    public String newHotelForm(Model model) {
        model.addAttribute("hotel", new Hotel());
        model.addAttribute("editing", false);
        return "hotel_form"; 
    }

    // Shows a edit form with the hotel data charged
    @GetMapping("/admin/hotels/edit/{id}") 
    public String editHotelForm(@PathVariable Long id, Model model) {
        Optional<Hotel> hotel = hotelService.getHotelById(id);

        if(hotel.isPresent()) {
            model.addAttribute("hotel", hotel.get());
            model.addAttribute("editing", true);
            return "hotel_form";
        } 

        // If hotel doesn't exit, we send the user to the list
        return "redirect:/admin/hotels";
    }


    @PostMapping("/admin/hotels/save")
    public String saveHotel(
            @RequestParam(required = false) Long id,
            @RequestParam String name,
            @RequestParam String tipo,
            @RequestParam String city,
            @RequestParam String location,
            @RequestParam double price,
            @RequestParam String description,
            @RequestParam double rating,
            @RequestParam(required = false, defaultValue = "") String galeriaRaw,
            @RequestParam(required = false) List <String> services) {

        Hotel hotel = new Hotel();
        hotel.setId(id); // If its null we create hotel, if not we edit the hotel
        hotel.setName(name);
        hotel.setTipo(tipo);
        hotel.setCity(city);
        hotel.setLocation(location);
        hotel.setPrice(price);
        hotel.setDescription(description);
        hotel.setRating(rating);

        if (!galeriaRaw.isBlank()){
            List<String> galeria = Arrays.asList(galeriaRaw.split(","));
            galeria.replaceAll(String::trim);
            hotel.setGaleria(galeria);
        }

        if(services != null) {
            hotel.setServices(services);
        }

        hotelService.saveHotel(hotel);
        return "redirect:/admin/hotels";
}
    @PostMapping("/admin/hotels/delete/{id}")
    public String deleteHotel(@PathVariable Long id){
        hotelService.deleteHotel(id);
        return "redirect:/admin/hotels";
    }


    //List all the users
    @GetMapping("/admin/users")
    public String adminUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin_users"; 
    }

    // Shows the form to edit a single user
    @GetMapping("/admin/users/edit/{id}") 
    public String editUserForm(@PathVariable Long id, Model model) {
        Optional<User> user = userRepository.findById(id);
        
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            return "edit_users";
        }
        return "redirect:/admin/users";
    }

    // Saves the changes of a edit user
    @PostMapping("/admin/users/save")
    public String saveUser(
            @RequestParam Long id,
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String role) {

        Optional<User> existing = userRepository.findById(id);

        if (existing.isPresent()) {
            User user = existing.get();
            user.setName(name);
            user.setEmail(email);
            user.setRole(role);
            userRepository.save(user);
        }
    
        return "redirect:/admin/users";
    }

    @PostMapping("/admin/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "redirect:/admin/users";
    }
}