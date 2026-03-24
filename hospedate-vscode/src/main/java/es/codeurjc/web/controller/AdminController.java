package es.codeurjc.web.controller;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
 
import es.codeurjc.web.model.Hotel;
import es.codeurjc.web.model.User;
import es.codeurjc.web.service.HotelService;
import es.codeurjc.web.service.UserService;
import es.codeurjc.web.service.UserSession;

import java.util.Set;
import java.util.List;
import java.util.Optional;
 
@Controller
public class AdminController {
 
    @Autowired
    private HotelService hotelService;

    @Autowired
    private UserService userService;
 
    @Autowired
    private UserSession userSession;
 
    // List all the hotels
    @GetMapping("/admin/hotels")
    public String adminHotels(Model model) {
        model.addAttribute("hotels", hotelService.getAllHotels());
        return "admin_hotels";
    }
 
    // Shows a form to create a new hotel (without ID yet)
    @GetMapping("/admin/hotels/new")
    public String newHotelForm(Model model) {
        return "create_hotel";
    }
 
    // Shows an edit form with the hotel data loaded
    @GetMapping("/admin/hotels/edit/{id}")
    public String editHotelForm(@PathVariable Long id, Model model) {
        Optional<Hotel> hotel = hotelService.getHotelById(id);
 
        if (hotel.isPresent()) {
            model.addAttribute("hotel", hotel.get());
            return "edit_hotel";
        }
 
        // If hotel doesn't exist, redirect to the list
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
            @RequestParam(required = false) Set<String> services,
            Model model) {
<<<<<<< HEAD
=======
            
>>>>>>> ec805d9f51822ad6c0a5f0a8187efc6f9ee82f47
        // Image upload validation in hotel creation
        if (galeriaRaw == null || galeriaRaw.trim().isEmpty()) {
            
            // We created the appropriate error message
            model.addAttribute("errorMessage", "Error: Es obligatorio añadir al menos una imagen para crear el hotel.");
            
            // We return the correct view without saving anything to the database.
            if (id == null) {
                return "create_hotel"; // If it was a new hotel
            } else {
                // If we were editing, we retrieved the data so we wouldn't leave it blank.
                Optional<Hotel> hotel = hotelService.getHotelById(id);
                if (hotel.isPresent()) {
                    model.addAttribute("hotel", hotel.get());
                }
                return "edit_hotel";
            }
        }
<<<<<<< HEAD
=======

>>>>>>> ec805d9f51822ad6c0a5f0a8187efc6f9ee82f47
        hotelService.saveOrUpdateHotel(id, name, tipo, city, location, price, description, rating, galeriaRaw, services);
        return "redirect:/admin/hotels";
    }
 
    @PostMapping("/admin/hotels/delete/{id}")
    public String deleteHotel(@PathVariable Long id) {
        hotelService.deleteHotel(id);
        return "redirect:/admin/hotels";
    }
 
    // List all the users
    @GetMapping("/admin/users")
    public String adminUsers(Model model) {
        // 1. Pedimos todos los usuarios a la base de datos
        List<User> users = userService.getAllUsers();
        users.removeIf(user -> "ADMIN".equals(user.getRole()));  //filter the admin user out of the list
        model.addAttribute("users", users);
        return "admin_users"; 
    }
 
    // Shows the form to edit a single user
    @GetMapping("/admin/users/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        Optional<User> user = userService.findById(id);
 
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            return "edit_users";
        }
        return "redirect:/admin/users";
    }
 
    // Saves the changes of an edited user
    @PostMapping("/admin/users/save")
    public String saveUser(
            @RequestParam Long id,
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String role) {
 
        userService.saveUser(id, name, email, password, role);
        return "redirect:/admin/users";
    }
 
    
    @PostMapping("/admin/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        
        // look for user in database
        Optional<User> userToDelete = userService.findById(id);
        
        if (userToDelete.isPresent()) {
            //not allowed to delete admin users
            if (!"ADMIN".equals(userToDelete.get().getRole())) {
                userService.deleteUser(id);
            } else {
                System.out.println("Tried to delete admin user");
            }
        }
        return "redirect:/admin/users";
    }

}