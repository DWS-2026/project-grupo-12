package es.codeurjc.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

//para el manejo de la sesión
import jakarta.servlet.http.HttpSession; 

@Controller
public class UserController {

    @GetMapping("/profile")
    public String profile() {
        return "profile"; 
    }

    @GetMapping("/login")
    public String login() {
        return "login"; 
    }

    @GetMapping("/register")
    public String register() {
        return "register"; 
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String email, @RequestParam String password, Model model, HttpSession session) {
        
        System.out.println("Intento de login con email: " + email);
        // logica de autenticacion por implementar
        return "redirect:/"; 
    }

    @GetMapping("/logout")
    public String processLogout(HttpSession session) {
        //para cerrar la sesión, simplemente la invalidamos        
        session.invalidate();
        return "redirect:/"; 
    }
    
}