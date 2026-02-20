package es.codeurjc.web.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

   
    @GetMapping("/admin/hotels")
    public String adminHotels() {
        return "admin_hotel"; 
    }

    @GetMapping("/admin/hotels/new")
    public String newHotel() {
        return "create_hotel"; 
    }

    @GetMapping("/admin/hotels/edit") 
    public String editHotel() {
        return "edit_hotel"; 
    }

   
    @GetMapping("/admin/users")
    public String adminUsers() {
        return "admin_users"; 
    }

    @GetMapping("/admin/users/edit") 
    public String editUser() {
        return "edit_users"; 
    }
}