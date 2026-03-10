package es.codeurjc.web.controller;

import es.codeurjc.web.service.UserSession;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private UserSession userSession;

    @ModelAttribute
    public void addGlobalAttributes(Model model, HttpServletRequest request) {
        String currentRoute = request.getRequestURI(); 

        // 1. Manejo especial para la ruta de errores interna de Spring
        if (currentRoute.startsWith("/error")) {
            model.addAttribute("tab_title", "Error - Hospédate");
            model.addAttribute("metadata_content", "Página no encontrada o error en Hospédate.");
        } else {
            // 2. Tu switch original para las demás rutas
            switch (currentRoute) {
                case "/":
                    model.addAttribute("tab_title", "Inicio - Hospédate");
                    model.addAttribute("metadata_content", "Bienvenido a Hospédate, tu plataforma de reservas de alojamiento.");
                    break;
                case "/hotels":
                    model.addAttribute("tab_title", "Lista de Hoteles - Hospédate");
                    model.addAttribute("metadata_content", "Explora nuestra selección de hoteles.");
                    break;
                default:
                    model.addAttribute("tab_title", "Hoteles - Hospédate"); 
                    model.addAttribute("metadata_content", "Hoteles de Hospédate.");
                    break;
            }
        }

        // 3. Pasamos la sesión a Mustache para que funcione el Navbar en todas las páginas
        model.addAttribute("logged", userSession.isLogged());
    }
}
