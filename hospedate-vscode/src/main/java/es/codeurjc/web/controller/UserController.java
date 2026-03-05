package es.codeurjc.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.web.model.User;
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
import java.security.Principal;


@Controller
public class UserController {

    //Path de las imagenes de perfil en el disco duro. En un proyecto real, esto se configuraría por base de datos
    //y no se haría con una carpeta dentro del proyecto.
    private static final Path RUTAS_FOTOS = Paths.get("data/imagenes/perfiles");


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
        /* 
       String username = (String) session.getAttribute("username");
        
        String phone = (String) session.getAttribute("phone");
        String email = (String) session.getAttribute("email");
        String profileImage = (String) session.getAttribute("profileImage");
        
        // 2. Si la sesión está vacía (acaba de entrar), ponemos valores por defecto
        if (username == null) {
            username = "Usuario_Nuevo";
            phone = "+34 000 000 000";
            email = "nuevo@hospedate.com";
        }
        
        // 3. Inyectamos los datos obligatoriamente en el Model para que Mustache pueda pintar el HTML
        model.addAttribute("username", username);
        model.addAttribute("phone", phone);
        model.addAttribute("email", email);
        
        if (profileImage != null) {
            model.addAttribute("profileImageUrl", "/profile/avatar");
        }   else {
            model.addAttribute("profileImageUrl", "/images/default-avatar.jpg");
        }
        */
        return "profile"; 
    }



    @PostMapping("/profile/update")
    public String processProfileUpdate() {
        /* 
            @RequestParam String username,
            @RequestParam String phone,
            @RequestParam String email,
            @RequestParam("foto") MultipartFile foto,
            HttpSession session) throws IOException {

        // 1. Guardamos los nuevos datos de texto en la memoria del usuario
        session.setAttribute("username", username);
        session.setAttribute("phone", phone);
        session.setAttribute("email", email);

        // 2. Procesamiento del fichero si el usuario ha adjuntado uno
        if (!foto.isEmpty()) {
            // Creamos la carpeta en el disco duro si no existía previamente
            Files.createDirectories(RUTAS_FOTOS);

            // Generamos un nombre único para evitar que se sobreescriban fotos
            String nombreArchivo = "avatar_" + username + "_" + System.currentTimeMillis() + ".jpg";
            
            // Construimos la ruta exacta y guardamos los bytes físicamente
            Path rutaFisica = RUTAS_FOTOS.resolve(nombreArchivo);
            foto.transferTo(rutaFisica);

            // Le decimos a la sesión cómo se llama la foto de este usuario
            session.setAttribute("profileImage", nombreArchivo);
        }
            */

        // Redirigimos al GET de arriba para que vuelva a cargar la página con los datos actualizados
        return "redirect:/profile"; 
    }


    @GetMapping("/profile/avatar")
    public ResponseEntity<Resource> serveAvatar(HttpSession session) throws MalformedURLException {
        /* 
        // Leemos qué foto le pertenece a este usuario concreto
        String profileImage = (String) session.getAttribute("profileImage");

        if (profileImage != null) {
            Path rutaFisica = RUTAS_FOTOS.resolve(profileImage);
            Resource recurso = new UrlResource(rutaFisica.toUri());

            // Si el archivo sigue existiendo en el disco duro, lo enviamos al HTML
            if (recurso.exists() && recurso.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                        .body(recurso);
            }
        }
    */
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