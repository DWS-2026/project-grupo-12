package es.codeurjc.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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
import java.util.stream.Collectors;
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

    // ==================== DASHBOARD ====================

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        List<Hotel> allHotels = hotelService.getAllHotels();
        List<User> allUsers = userService.getAllUsers();
        List<Reserve> allReserves = reserveService.getAllReserves();
        long totalReviews = reviewService.countReviews();

        model.addAttribute("totalHotels", allHotels.size());
        model.addAttribute("totalUsers", allUsers.size());
        model.addAttribute("totalReserves", allReserves.size());
        model.addAttribute("totalReviews", totalReviews);

        // Recent hotels (last 5)
        List<Hotel> recentHotels = allHotels.stream()
            .limit(5)
            .collect(Collectors.toList());
        model.addAttribute("recentHotels", recentHotels);

        // Recent reserves (last 5)
        List<Reserve> recentReserves = allReserves.stream()
            .limit(5)
            .collect(Collectors.toList());
        model.addAttribute("recentReserves", recentReserves);

        return "admin_dashboard";
    }

    // ==================== HOTELS ====================

    @GetMapping("/admin/hotels")
    public String adminHotels(@RequestParam(required = false) String search, Model model) {
        List<Hotel> hotels;
        if (search != null && !search.trim().isEmpty()) {
            hotels = hotelService.getAllHotels().stream()
                .filter(h -> h.getName().toLowerCase().contains(search.toLowerCase())
                    || h.getCity().toLowerCase().contains(search.toLowerCase()))
                .collect(Collectors.toList());
        } else {
            hotels = hotelService.getAllHotels();
        }
        model.addAttribute("search", search != null ? search : "");
        model.addAttribute("hotels", hotels);
        return "admin_hotels";
    }

    @GetMapping("/admin/hotels/new")
    public String newHotelForm(Model model) {
        return "create_hotel";
    }

    @GetMapping("/admin/hotels/edit/{id}")
    public String editHotelForm(@PathVariable Long id, Model model) {
        Optional<Hotel> hotel = hotelService.getHotelById(id);

        if (hotel.isPresent()) {
            model.addAttribute("hotel", hotel.get());
            return "edit_hotel";
        }

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
            Model model,
            RedirectAttributes redirectAttributes) {
        // Validacion: al crear un hotel nuevo es obligatorio subir al menos una imagen
        if (id == null && (galeriaRaw == null || galeriaRaw.trim().isEmpty())) {
            model.addAttribute("errorMessage", "Error: Es obligatorio anadir al menos una imagen para crear un hotel nuevo.");
            return "create_hotel";
        }
        hotelService.saveOrUpdateHotel(id, name, tipo, city, location, price, description, rating, galeriaRaw, services, wifi, tv, airConditioning, family);

        if (id == null) {
            redirectAttributes.addFlashAttribute("successMessage", "Hotel creado correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("successMessage", "Hotel actualizado correctamente.");
        }
        return "redirect:/admin/hotels";
    }

    @PostMapping("/admin/hotels/delete/{id}")
    public String deleteHotel(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Hotel> hotel = hotelService.getHotelById(id);
        if (hotel.isPresent()) {
            hotelService.deleteHotel(id);
            redirectAttributes.addFlashAttribute("successMessage", "Hotel '" + hotel.get().getName() + "' eliminado correctamente.");
        }
        return "redirect:/admin/hotels";
    }

    // ==================== USERS ====================

    @GetMapping("/admin/users")
    public String adminUsers(@RequestParam(required = false) String search, Model model) {
        List<User> users = userService.getAllUsers();
        users.removeIf(user -> "ADMIN".equals(user.getRole()));

        if (search != null && !search.trim().isEmpty()) {
            users = users.stream()
                .filter(u -> u.getName().toLowerCase().contains(search.toLowerCase())
                    || u.getEmail().toLowerCase().contains(search.toLowerCase()))
                .collect(Collectors.toList());
        }

        model.addAttribute("search", search != null ? search : "");
        model.addAttribute("users", users);
        return "admin_users";
    }

    @GetMapping("/admin/users/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        Optional<User> user = userService.findById(id);

        if (user.isPresent()) {
            User u = user.get();
            model.addAttribute("user", u);
            model.addAttribute("isAdmin", "ADMIN".equals(u.getRole()));
            model.addAttribute("isUser", "USER".equals(u.getRole()));

            List<Review> userReviews = reviewService.getReviewsByAuthorId(u.getId());
            model.addAttribute("reviews", userReviews);

            List<Reserve> userReserves = reserveService.getReservesByCustomerId(u.getId());
            model.addAttribute("reserves", userReserves);
            return "edit_users";
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/admin/users/save")
    public String saveUser(
            @RequestParam Long id,
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam(required = false) String password,
            @RequestParam String role,
            @RequestParam(required = false) List<Long> deleteReviews,
            @RequestParam(required = false) List<Long> deleteReserves,
            RedirectAttributes redirectAttributes) {

        if (userService.isEmailTakenByAnother(id, email)) {
            redirectAttributes.addFlashAttribute("errorMessage", "El email '" + email + "' ya está en uso por otro usuario.");
            return "redirect:/admin/users/edit/" + id;
        }

        userService.saveUser(id, name, email, password, role);

        if (deleteReviews != null && !deleteReviews.isEmpty()) {
            for (Long reviewId : deleteReviews) {
                reviewService.deleteReview(reviewId);
            }
        }

        if (deleteReserves != null && !deleteReserves.isEmpty()) {
            for (Long reserveId : deleteReserves) {
                reserveService.deleteReserve(reserveId);
            }
        }

        redirectAttributes.addFlashAttribute("successMessage", "Usuario actualizado correctamente.");
        return "redirect:/admin/users";
    }

    @PostMapping("/admin/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<User> userToDelete = userService.findById(id);

        if (userToDelete.isPresent()) {
            if (!"ADMIN".equals(userToDelete.get().getRole())) {
                userService.deleteUser(id);
                redirectAttributes.addFlashAttribute("successMessage", "Usuario '" + userToDelete.get().getName() + "' eliminado correctamente.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "No se puede eliminar un usuario administrador.");
            }
        }
        return "redirect:/admin/users";
    }

    // ==================== RESERVES ====================

    @GetMapping("/admin/reserves")
    public String adminReserves(@RequestParam(required = false) String search, Model model) {
        List<Reserve> reserves;
        if (search != null && !search.trim().isEmpty()) {
            reserves = reserveService.getAllReserves().stream()
                .filter(r -> r.getHotel().getName().toLowerCase().contains(search.toLowerCase())
                    || r.getCustomer().getName().toLowerCase().contains(search.toLowerCase())
                    || r.getCustomer().getEmail().toLowerCase().contains(search.toLowerCase()))
                .collect(Collectors.toList());
        } else {
            reserves = reserveService.getAllReserves();
        }
        model.addAttribute("search", search != null ? search : "");
        model.addAttribute("reserves", reserves);
        return "admin_reserves";
    }

    @PostMapping("/admin/reserves/delete/{id}")
    public String deleteReserve(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Reserve> reserve = reserveService.getReserveById(id);
        if (reserve.isPresent()) {
            reserveService.deleteReserve(id);
            redirectAttributes.addFlashAttribute("successMessage", "Reserva eliminada correctamente.");
        }
        return "redirect:/admin/reserves";
    }

    // ==================== UPLOAD ====================

    @PostMapping(value = "/admin/upload", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            Path uploadDir = Paths.get("uploads").toAbsolutePath().normalize();
            Files.createDirectories(uploadDir);

            // Sanitize filename: keep only the file extension, use UUID as name
            String originalName = file.getOriginalFilename();
            String extension = "";
            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
            }
            if (!extension.matches("\\.(jpg|jpeg|png|gif|webp)")) {
                return ResponseEntity.badRequest().body(Map.of("error", "Tipo de archivo no permitido."));
            }
            String filename = UUID.randomUUID() + extension;
            Path destino = uploadDir.resolve(filename).normalize();

            // Prevent path traversal
            if (!destino.startsWith(uploadDir)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Nombre de archivo no valido."));
            }

            Files.copy(file.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

            String url = "/uploads/" + filename;
            return ResponseEntity.ok(Map.of("url", url));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

}
