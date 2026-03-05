package es.codeurjc.web.controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.web.model.Reserve;


@Controller
public class ReserveController {

    @GetMapping("/reserve")
    public String showReserve(Model model) {
        
        return "reserve"; 
    }

    //Comprobar esto ya que el form para rellenar los datos esta en hotel
    @PostMapping("/reserve/process")
    public String processReserve(@RequestParam Long hotelId,
                                @RequestParam String entryDate, 
                                @RequestParam String departureDate, 
                                @RequestParam int guest) {
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
            nuevaReserva.setClient(usuarioLogueado); 
            
            // 4. Le asignas el hotel
            nuevaReserva.setHotel(hotelSeleccionado);
            
            // 5. Ahora sí, la guardas en la base de datos
            reserveRepository.save(nuevaReserva);*/
            
    return "redirect:/profile"; // Mandamos al usuario a su perfil para ver sus reservas
}
    
    
}