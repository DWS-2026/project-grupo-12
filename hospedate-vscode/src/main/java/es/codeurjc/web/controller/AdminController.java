package es.codeurjc.web.controller;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
 
import es.codeurjc.web.model.Hotel;
import es.codeurjc.web.model.Reserve;
import es.codeurjc.web.model.Review;
import es.codeurjc.web.model.User;
import es.codeurjc.web.service.HotelService;
import es.codeurjc.web.service.ReserveService;
import es.codeurjc.web.service.ReviewService;
import es.codeurjc.web.service.UserService;
import es.codeurjc.web.service.UserSession;

import java.util.Set;
import java.util.List;
import java.util.Optional;
import java.io.IOException;
import java.nio.file.*;
import java.util.Map;
import java.util.UUID;
 
@Controller
public class AdminController {
 
    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReserveService reserveService;
    
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
            @RequestParam(defaultValue = "false") boolean wifi,
            @RequestParam(defaultValue = "false") boolean tv,
            @RequestParam(defaultValue = "false") boolean airConditioning,
            @RequestParam(defaultValue = "false") boolean family,
            Model model) {
        // Validación: al crear un hotel nuevo es obligatorio subir al menos una imagen
        if (id == null && (galeriaRaw == null || galeriaRaw.trim().isEmpty())) {
            model.addAttribute("errorMessage", "Error: Es obligatorio añadir al menos una imagen para crear un hotel nuevo.");
            return "create_hotel";
        }
        // Si es edición y galeriaRaw viene vacío, el Service mantendrá las fotos existentes
        hotelService.saveOrUpdateHotel(id, name, tipo, city, location, price, description, rating, galeriaRaw, services, wifi, tv, airConditioning, family);
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
             List<Review> userReviews = reviewService.getReviewsByAuthorId(user.get().getId());
        model.addAttribute("reviews", userReviews); 

        //SELECT * FROM reserve WHERE customer_id = ?
        List<Reserve> userReserves = reserveService.getReservesByCustomerId(user.get().getId());
        model.addAttribute("reserves", userReserves);
            return "edit_users";
        }
        return "redirect:/admin/users";
    }
 

    // Saves the changes of an edited user and also processes the deletion of reviews and reserves if any were marked for deletion
    @PostMapping("/admin/users/save")
    public String saveUser(
            @RequestParam Long id,
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam (required = false) String password,
            @RequestParam String role,
            @RequestParam (required = false) List<Long> deleteReviews,
            @RequestParam (required = false) List<Long> deleteReserves) {
 
        // 1. Update the user information in the database
        userService.saveUser(id, name, email, password, role);

        // 2. Check if there are any reviews marked for deletion and process them
        if (deleteReviews != null && !deleteReviews.isEmpty()) {
            for (Long reviewId : deleteReviews) {
                reviewService.deleteReview(reviewId); //remove the review from the database
            }
        }

        // 3. Check if there are any reserves marked for deletion and process them
         if (deleteReserves != null && !deleteReserves.isEmpty()) {
            for (Long reserveId : deleteReserves) {
                reserveService.deleteReserve(reserveId);  //remove the reserve from the database
            }
        }
 
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

    @PostMapping(value = "/admin/upload", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // 1. Obtenemos la ruta absoluta para evitar errores de "Directorio no encontrado"
            Path uploadDir = Paths.get("uploads").toAbsolutePath().normalize();
            
            // 2. Creamos las carpetas si no existen
            Files.createDirectories(uploadDir);

            // 3. Generamos un nombre único y la ruta de destino final
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path destino = uploadDir.resolve(filename);

            // 4. Copiamos el flujo de datos directamente (más fiable que transferTo)
            Files.copy(file.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

            // 5. Devolvemos la URL que el frontend espera
            String url = "/uploads/" + filename;
            return ResponseEntity.ok(Map.of("url", url));

        } catch (IOException e) {
            e.printStackTrace(); // Revisa tu terminal de Java para ver el error exacto
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

}