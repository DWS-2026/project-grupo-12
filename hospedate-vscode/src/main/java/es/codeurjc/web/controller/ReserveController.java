package es.codeurjc.web.controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.web.model.Reserve;
import jakarta.servlet.http.HttpServletRequest;


@Controller
public class ReserveController {


// Comprobar si hace falta saber s esta logueado ya que sino, nos ahorramos este code y añadimos el tab_title 
// y metadata_content directamente en el método showReserve() y no hace falta el @ModelAttribute
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
            case "/reserve":
                model.addAttribute("tab_title", "Confirmar Reserva - Hospédate");
                model.addAttribute("metadata_content", "Reserva tu alojamiento ideal.");
                break;
            default:
                // Por si acaso añades una ruta nueva en el futuro y se te olvida ponerla aquí
                model.addAttribute("tab_title", "Área de Usuario - Hospédate"); 
                model.addAttribute("metadata_content", "Área personal de Hospédate.");
                break;
        }
    }
    
    // Method for painting the reservation data
    @PostMapping("/reserve")
    public String showReserve(@RequestParam Long hotelId,
                            @RequestParam String entryDate, 
                            @RequestParam String departureDate, 
                            @RequestParam int guests,
                            Model model) {
        
        model.addAttribute("entryDate", entryDate);
        model.addAttribute("departureDate", departureDate);
        model.addAttribute("guests", guests);
        model.addAttribute("hotelId", hotelId);

        return "reserve"; 
    }

    //Method to save the reservation in the database and redirect to the profile
    @PostMapping("/reserve/process")
    public String processReserve(@RequestParam Long hotelId,
                                @RequestParam String entryDate, 
                                @RequestParam String departureDate, 
                                @RequestParam int guests) {
        // 1. Buscamos el Hotel en la BD con el hotelId
        // 2. Creamos una nueva Reserva con las fechas y los invitados
        // 3. Calculamos el precio total (noches * precio del hotel)
        // 4. Guardamos en la base de datos
        
        /*Como sería
            // 1. Nace la reserva (el cliente es null)
            Reserve nuevaReserva = new Reserve(); 
            
            // 2. Buscas quién es el usuario que está navegando
            User usuarioLogueado = userRepository.findByEmail("pepe@gmail.com").get();
            
            // 3. ¡AQUÍ USAS EL SETTER! Le asignas el dueño por primera vez
            nuevaReserva.setCustomer(usuarioLogueado); 
            
            // 4. Le asignas el hotel
            nuevaReserva.setHotel(hotelSeleccionado);
            
            // 5. Ahora sí, la guardas en la base de datos
            reserveRepository.save(nuevaReserva);*/

    return "redirect:/profile"; // Mandamos al usuario a su perfil para ver sus reservas
}
   
}