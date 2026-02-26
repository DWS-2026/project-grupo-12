package es.codeurjc.web.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import es.codeurjc.web.model.Hotel;

@Controller
public class AdminController {

   
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