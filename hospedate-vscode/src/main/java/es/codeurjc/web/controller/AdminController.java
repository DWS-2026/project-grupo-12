package es.codeurjc.web.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;

import es.codeurjc.web.model.Hotel;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class AdminController {


    @ModelAttribute
    public void addAttributes(Model model, HttpServletRequest request) {

        // --- B) Lógica de Títulos y Metadatos según la URL --- 
        String currentRoute = request.getRequestURI();

        switch (currentRoute) {
            case "/admin/hotels":
                model.addAttribute("tab_title", "Administración de Hoteles - Hospédate");
                model.addAttribute("metadata_content", "Página de administración de hoteles.");
                break;
            case "/admin/hotels/new/{id}":
                model.addAttribute("tab_title", "Creación de Hoteles - Hospédate");
                model.addAttribute("metadata_content", "Página de creación de hoteles.");
                break;
            case "/admin/hotels/edit/{id}":
                model.addAttribute("tab_title", "Edición de Hoteles - Hospédate");
                model.addAttribute("metadata_content", "Página de edición de hoteles.");
                break;
            case "/admin/users/{id}":
                model.addAttribute("tab_title", "Administración de Usuarios - Hospédate");
                model.addAttribute("metadata_content", "Página de administración de usuarios.");
                break;
            case "/admin/users/edit/{id}":
                model.addAttribute("tab_title", "Edición de Usuarios - Hospédate");
                model.addAttribute("metadata_content", "Página de edición de usuarios.");
                break;
            default:
                // Por si acaso añades una ruta nueva en el futuro y se te olvida ponerla aquí
                model.addAttribute("tab_title", "Administración - Hospédate"); 
                model.addAttribute("metadata_content", "Página de administración.");
                break;
        }
    }
   
    @GetMapping("/admin/hotels")
    public String adminHotels() {
        return "admin_hotel"; 
    }

    @GetMapping("/admin/hotels/new/{id}")
    public String newHotel() {
        return "create_hotel"; 
    }

    @GetMapping("/admin/hotels/edit/{id}") 
    public String editHotel() {
        return "edit_hotel"; 
    }

    @PostMapping("/admin/hotels/save")
    public String saveHotel(Hotel hotel) {
        return "redirect:/admin/hotels"; //To save the changes on the hotels or the new hotels
    }
   
    @GetMapping("/admin/users/{id}")
    public String adminUsers() {
        return "admin_users"; 
    }

    @GetMapping("/admin/users/edit/{id}") 
    public String editUser() {
        return "edit_users"; 
    }
}