package es.codeurjc.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.web.model.Image;
import es.codeurjc.web.model.Reserve;
import es.codeurjc.web.model.Review;
import es.codeurjc.web.model.User;
import es.codeurjc.web.repository.ReserveRepository;
import es.codeurjc.web.repository.ReviewRepository;
import es.codeurjc.web.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import java.sql.Blob;
import java.sql.SQLException;

@Controller
public class UserController {

    //Path de las imagenes de perfil en el disco duro. En un proyecto real, esto se configuraría por base de datos
    //y no se haría con una carpeta dentro del proyecto.

    @Autowired
    private ReviewRepository reviewRepository; 
    @Autowired
    private ReserveRepository reserveRepository; 
    @Autowired
    private UserRepository userRepository;

    @ModelAttribute
    public void addAttributes(Model model, HttpServletRequest request) {
        /* 
        // --- A) Lógica de sesión (Para saber si está logueado) ---
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            model.addAttribute("logged", true);
            model.addAttribute("userName", principal.getName());
            model.addAttribute("admin", request.isUserInRole("ADMIN"));
        } else {
            model.addAttribute("logged", false);
        }
            */
        // --- B) Lógica de Títulos y Metadatos según la URL --- 
        String currentRoute = request.getRequestURI(); // Ej: "/login" o "/register"

        switch (currentRoute) {
            case "/login":
                model.addAttribute("tab_title", "Iniciar Sesión - Hospédate");
                model.addAttribute("metadata_content", "Accede a tu cuenta para gestionar tus reservas.");
                break;
            case "/register":
                model.addAttribute("tab_title", "Crear Cuenta - Hospédate");
                model.addAttribute("metadata_content", "Regístrate gratis y empieza a viajar.");
                break;
            case "/profile":
                model.addAttribute("tab_title", "Mi Perfil - Hospédate");
                model.addAttribute("metadata_content", "Gestiona tus datos y reservas activas.");
                break;
            default:
                // Por si acaso añades una ruta nueva en el futuro y se te olvida ponerla aquí
                model.addAttribute("tab_title", "Área de Usuario - Hospédate"); 
                model.addAttribute("metadata_content", "Área personal de Hospédate.");
                break;
        }
    }

    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return "redirect:/login"; 
        }

        List<Review> userReviews = reviewRepository.findByUserId(userId);
        List<Reserve> userReserves = reserveRepository.findByUserId(userId);;


        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("reviews", userReviews);
        model.addAttribute("reserves", userReserves);

        
        return "profile"; 
    }



    @PostMapping("/profile/update")
    public String processProfileUpdate(
        @RequestParam String username,
        @RequestParam String phone,
        @RequestParam String email,
        @RequestParam("foto") MultipartFile foto,
        HttpSession session) throws IOException {
    
    User currentUser = userRepository.findById(userId).orElseThrow();
    Long userId = (Long) session.getAttribute("userId");
    if (userId == null) {
        return "redirect:/login";
    }

    if (!foto.isEmpty()) {
    Image newImage = new Image();
    newImage.setImage(foto.getBytes());

    // 3. Establecemos la relación: Le entregamos la imagen al usuario
    currentUser.setProfileImage(newImage);
    }
    User currentUser = userRepository.findById(userId).orElseThrow();


    currentUser.setName(username);
    currentUser.setEmail(email);


    userRepository.save(currentUser);

    session.setAttribute("username", username);

    return "redirect:/profile"; 
        

}

@GetMapping("/profile/avatar")
public ResponseEntity<byte[]> serveAvatar(HttpSession session) throws SQLException {
    
    Long userId = (Long) session.getAttribute("userId");
    User currentUser = userRepository.findById(userId).orElseThrow();

    if (currentUser.getProfileImage() != null) {
        
        // 1. Extraemos el objeto Blob de la base de datos
        Blob fotoBlob = currentUser.getProfileImage().getImageFile();
        
        // 2. Extraemos los píxeles puros del Blob. 
        // El método getBytes pide la posición inicial (1) y la cantidad a leer.
        byte[] pixelesPuros = fotoBlob.getBytes(1, (int) fotoBlob.length());
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                .body(pixelesPuros);
    }
    return ResponseEntity.notFound().build();
}


    @GetMapping("/login")
    public String login() {
        return "login"; 
    }

    @GetMapping("/register")
    public String register() {
        return "register"; 
    }

    //Provisional function
    @PostMapping("/register")
    public String processRegister(@RequestParam String email, 
                                @RequestParam String username, 
                                @RequestParam String password) {
        
        //Create the user using your model's constructor
        User nuevoUsuario = new User(username, email);
        // ***(Acuérdate de añadir la contraseña a tu modelo User)****
        
        //Save to the database
        userRepository.save(nuevoUsuario);
        
        //Redirect to the login page to log in
    return "redirect:/login"; 
}

    @PostMapping("/login")
    public String processLogin(@RequestParam String email, @RequestParam String password, Model model, HttpSession session) {
        System.out.println("Intento de login con email: " + email);
        return "redirect:/"; 
    }

    @GetMapping("/logout")
    public String processLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/"; 
    }

    @GetMapping("/images")
    public String images() {
        return "images";
    }
}