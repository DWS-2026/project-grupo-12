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

        // 1. PASAMOS LA SESIÓN A TODA LA WEB (Para que funcione la Navbar siempre)
        model.addAttribute("logged", userSession.isLogged());
        model.addAttribute("isAdmin", userSession.isAdmin()); // Lo traemos del AdminController

        // 2. MANEJO DE TÍTULOS CENTRALIZADO
        if (currentRoute.startsWith("/error")) {
            model.addAttribute("tab_title", "Error - Hospédate");
            model.addAttribute("metadata_content", "Página no encontrada o error en Hospédate.");
        } else if (currentRoute.startsWith("/admin/hotels/new")) {
            model.addAttribute("tab_title", "Crear Hotel - Hospédate");
            model.addAttribute("metadata_content", "Página de creación de hoteles.");
        } else if (currentRoute.startsWith("/admin/hotels/edit")) {
            model.addAttribute("tab_title", "Editar Hotel - Hospédate");
            model.addAttribute("metadata_content", "Página de edición de hoteles.");
        } else if (currentRoute.startsWith("/admin/users")) {
            model.addAttribute("tab_title", "Administración de Usuarios - Hospédate");
            model.addAttribute("metadata_content", "Página de administración de usuarios.");
        } else if (currentRoute.startsWith("/admin")) {
            model.addAttribute("tab_title", "Administración - Hospédate");
            model.addAttribute("metadata_content", "Página de administración.");
        } else {
            // Rutas normales de usuario
            switch (currentRoute) {
                case "/":
                    model.addAttribute("tab_title", "Inicio - Hospédate");
                    model.addAttribute("metadata_content", "Bienvenido a Hospédate, tu plataforma de reservas.");
                    break;
                case "/hotels":
                    model.addAttribute("tab_title", "Lista de Hoteles - Hospédate");
                    model.addAttribute("metadata_content", "Explora nuestra selección de hoteles.");
                    break;
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
                case "/reserve":
                    model.addAttribute("tab_title", "Confirmar Reserva - Hospédate");
                    model.addAttribute("metadata_content", "Reserva tu alojamiento ideal.");
                    break;
                default:
                    model.addAttribute("tab_title", "Hospédate"); 
                    model.addAttribute("metadata_content", "La mejor web de reservas.");
                    break;
            }
        }
        // 3. Pasamos la sesión a Mustache para que funcione el Navbar en todas las páginas
        model.addAttribute("logged", userSession.isLogged());
    }
}
