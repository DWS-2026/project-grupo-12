package es.codeurjc.web.controller.rest;

import es.codeurjc.web.dto.ReserveDTO;
import es.codeurjc.web.model.Hotel;
import es.codeurjc.web.model.Reserve;
import es.codeurjc.web.model.User;
import es.codeurjc.web.service.HotelService;
import es.codeurjc.web.service.ReserveService;
import es.codeurjc.web.service.UserService;
import es.codeurjc.web.service.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/reserves")
public class ReserveRestController {
    
    @Autowired
    private ReserveService reserveService;

    @Autowired
    private HotelService hotelService;

    @Autowired 
    private UserService userService;

    @Autowired
    private UserSession userSession;

    //List reserves
    @GetMapping("/")
    public ResponseEntity<Page<ReserveDTO>> getReserves(Pageable pageable){
        Page<Reserve> reserves = reserveService.getAllReserves(pageable);
        return ResponseEntity.ok(reserves.map(ReserveDTO::new));
    }

    //Create reserve
    @PostMapping("/")
    public ResponseEntity<ReserveDTO> createReserve(@RequestBody ReserveDTO dto){

        //Verufy the user is logged in
        if(!userSession.isLogged()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); //401 if not logged
        }

        //Get the hotel
        Optional<Hotel> hotelOpt = hotelService.getHotelById(dto.getHotelId());
        if (hotelOpt.isEmpty()){
            return ResponseEntity.notFound().build(); // 404 not found
        }

        //Get the user
        User user = userService.findById(userSession.getIdUser()).orElseThrow(); 

        //Create pending reserve
        Reserve newReserve =reserveService.createPendingReserve(
            hotelOpt.get(), user, dto.getEntryDate(), dto.getDepartureDate(), dto.getGuests()
            );
        
        return new ResponseEntity<>(new ReserveDTO(newReserve), HttpStatus.CREATED);
    }
    


    //Confirm reserve
    @PostMapping("/{id}/confirm")
    public ResponseEntity<ReserveDTO> confirmReserve(@PathVariable Long id){
        return reserveService.getReserveById(id)
            .map(reserve -> {
                reserveService.saveConfirmedReserve(reserve);
                return ResponseEntity.ok(new ReserveDTO(reserve));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    //Delete reserve
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReserve(@PathVariable Long id){
        if (reserveService.getReserveById(id).isPresent()){
            reserveService.deleteReserve(id);
            return ResponseEntity.noContent().build(); //204 No Content
        }
        return ResponseEntity.notFound().build();
        }


}