package es.codeurjc.web.controller;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.web.model.Hotel;
import es.codeurjc.web.model.User;
import es.codeurjc.web.model.Reserve;
import es.codeurjc.web.service.HotelService;
import es.codeurjc.web.service.ReserveService;
import es.codeurjc.web.service.UserService;
import es.codeurjc.web.service.UserSession;
import jakarta.servlet.http.HttpServletRequest;


@Controller
public class ReserveController {

        @Autowired
    private ReserveService reserveService;

    @Autowired
    private HotelService hotelService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserSession userSession;

 
    // Method to display the reservation and collect the data from the form, 
    // and create the reservation as "PENDING" so that the data cannot be modified and we have greater security
    @PostMapping("/reserve")
    public String showReserve(@RequestParam Long hotelId,
                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate entryDate, 
                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate departureDate,
                            @RequestParam int guests,
                            Model model) {
         // If the user is not logged in, redirect to the login page                    
        if(!userSession.isLogged()){
            return "redirect:/login";
        }

        //Logic for not being able to choose to book on the same day or in the past
        LocalDate today = LocalDate.now();
        // If the entry is in the past, OR the exit is before the entry, OR they are on the same day
        if (entryDate.isBefore(today) || departureDate.isBefore(entryDate) || entryDate.isEqual(departureDate)) {
            // We return the user to the hotel page
            return "redirect:/hotel/" + hotelId; 
        }

        //We search for the Hotel and the User in the database
        Hotel hotel = hotelService.getHotelById(hotelId).orElseThrow();
        User customer = userService.findById(userSession.getIdUser()).orElseThrow();

        //We called the service, which created the pending reservation by calculating the number of nights and the total price.
        Reserve pendingReserve = reserveService.createPendingReserve(hotel, customer, entryDate, departureDate, guests);

        // We passed the data to the view (including the ID of this draft)
        String mImage = hotel.getMainImage();

        //if not starts with slash, we add it
        if (mImage.startsWith("/")) {
            mImage = mImage.substring(1); 
        }
        model.addAttribute("mainImage", "/" + mImage);

        model.addAttribute("reserveId", pendingReserve.getId());
        model.addAttribute("hotel", hotel); 
        model.addAttribute("entryDate", entryDate);
        model.addAttribute("departureDate", departureDate);
        model.addAttribute("guests", guests);
        model.addAttribute("nights", pendingReserve.getNights());
        model.addAttribute("totalPrice", pendingReserve.getPrice());

        return "reserve";
    }
    //Method to save the confirmed reservation in the database and redirect to the profile
    @PostMapping("/reserve/process")
    public String processReserve(@RequestParam Long reserveId) {
        
        // If the user is not logged in, redirect to the login page
        if(!userSession.isLogged()){
            return "redirect:/login";
        }

        // We are looking for the pending reservation using your ID.
        Reserve reserve = reserveService.getReserveById(reserveId).orElseThrow();

        // Protection against IDOR
        // We compare the reservation owner's ID with the logged-in user's ID
        if (!reserve.getCustomer().getId().equals(userSession.getIdUser())) {
            //If you try to modify a reservation that is not yours, we will send you back to the beginning.
            return "redirect:/"; 
        }
        
        // We called the service to change the reservation status to "CONFIRMED" and store it in the database.
        reserveService.saveConfirmedReserve(reserve);

        return "redirect:/profile"; 
    }
   
}