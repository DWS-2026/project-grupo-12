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
import es.codeurjc.web.service.ImageService;
import es.codeurjc.web.service.UserService;
import es.codeurjc.web.service.UserSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Optional;
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
    private ImageService imageService; 
    
    @Autowired
    private UserService userService;

    //User session to not use HttpSession, improves individual sessions access and management 
    @Autowired
    private UserSession userSession;

    //to show bar info and metadata
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
        String currentRoute = request.getRequestURI();

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
                // Just in case you add a new route in the future and forget to put it here
                model.addAttribute("tab_title", "Área de Usuario - Hospédate"); 
                model.addAttribute("metadata_content", "Área personal de Hospédate.");
                break;
        }
    }

    @GetMapping("/profile")
    public String profile(Model model) {

        if (!userSession.isLogged()) {
            return "redirect:/login"; 
        }

        Long userId = userSession.getIdUser();
        User user = userService.findById(userId).orElseThrow();

        //we check the profile image, if null then we show the default icon
        model.addAttribute("hasProfileImage", user.getProfileImage() != null); 

        model.addAttribute("username", userSession.getUsername()); 
        model.addAttribute("email", user.getEmail());       

        //SELECT * FROM review WHERE author_id = ?
        List<Review> userReviews = reviewRepository.findByAuthorId(userId);
        model.addAttribute("reviews", userReviews);

        //SELECT * FROM reserve WHERE customer_id = ?
        List<Reserve> userReserves = reserveRepository.findByCustomerId(userId);
        model.addAttribute("reserves", userReserves);

        return "profile"; 
    }

    //obtain login page
    @GetMapping("/login")
    public String login() {
        return "login"; 
    }

    //obtain register page
    @GetMapping("/register")
    public String register() {
        return "register"; 
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


    //for registering a new user only, need to login after 
    @PostMapping("/register")
    public String processRegister(@RequestParam String email, 
                                @RequestParam String username, 
                                @RequestParam String password) {
        
        userService.registerUser(username, email, password);
        
        
    return "redirect:/"; 
    }

    //service uses the repository to check the database
    @PostMapping("/login")
    public String processLogin(@RequestParam String email, @RequestParam String password, Model model) {
        Optional<User> checkUser = userService.authenticateUser(email, password); 
        model.addAttribute("attemptError", false);

        if (checkUser.isPresent()) { //is present means exists and the password is correct 
            User user = checkUser.get();
            userSession.modifySessionInfo(user);
            return "redirect:/profile";
        } else {
            model.addAttribute("attemptError", "Credenciales inválidas"); //if else in mustache
            return "login";
        }
    }

   

    //to update the user's profile information
    @PostMapping("/profile/update")
    public String updateProfile(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam("photo") MultipartFile photo) throws Exception {
        
        //to prevent not logged users from accessing profile page    
        if (!userSession.isLogged()) {
            return "redirect:/login";
        }

        Long userId = userSession.getIdUser(); //is logged, so we obtain session id
        Optional<User> userOp = userService.findById(userId); //recover user from database

        if (!userOp.isPresent()) {//if user doesn't exist, something went wrong
            return "redirect:/login";
        }

        User user = userOp.get(); //get the logged user 

        if (!photo.isEmpty()) { //if the user is uploading a photo, we change it 
            Image newImage = imageService.createImage(photo);
            user.setProfileImage(newImage);
        }
        //modify the logged container with the new information
        user.setName(username);
        user.setEmail(email);
        userService.updateUser(user);

        userSession.modifySessionInfo(user); //update session info with new username and profile picture
        return "redirect:/profile";
    }


    //adapted from Repo-0, makes a GET request to the server to get the profile picture of the user, and returns it as a Resource to be displayed in the profile page
    @GetMapping("/profile/avatar")
    public ResponseEntity<Object> getProfileAvatar() throws SQLException {
        
        if (!userSession.isLogged()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); //if the user is not logged, we return 401 Unauthorized (ReponseEntity)
        }  

        Long userId = userSession.getIdUser();
        Optional<User> userOp = userService.findById(userId);

        if (!userOp.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOp.get();
        if (user.getProfileImage() != null) {
            Resource imageFile = imageService.getImageFile(user.getProfileImage().getId()); 
            MediaType mediaType = MediaTypeFactory.getMediaType(imageFile).orElse(MediaType.IMAGE_JPEG); 
            return ResponseEntity.ok().contentType(mediaType).body(imageFile); 
        }

        return ResponseEntity.notFound().build();
    }

}