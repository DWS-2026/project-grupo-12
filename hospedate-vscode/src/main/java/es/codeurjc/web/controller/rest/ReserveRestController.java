package es.codeurjc.web.controller.rest;

import es.codeurjc.web.dto.ReserveDTO;
import es.codeurjc.web.model.Hotel;
import es.codeurjc.web.model.Reserve;
import es.codeurjc.web.model.User;
import es.codeurjc.web.service.HotelService;
import es.codeurjc.web.service.ReserveService;
import es.codeurjc.web.service.UserService;
import es.codeurjc.web.service.UserSession;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;

import java.net.URI;
import java.security.Principal;
import java.util.Optional;

@Tag(name = "Reserves")
@RestController
@RequestMapping("/api/v1/reserves")
public class ReserveRestController {
    
    @Autowired
    private ReserveService reserveService;

    @Autowired
    private HotelService hotelService;

    @Autowired 
    private UserService userService;


    @Operation(summary = "Create a reserve")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Created"),
        @ApiResponse(responseCode = "401", description = "Unauthorized – login required"),
        @ApiResponse(responseCode = "404", description = "Hotel not found")
    })
    //Create reserve
    @PostMapping("/")
    public ResponseEntity<ReserveDTO> createReserve(@Valid @RequestBody ReserveDTO dto, Principal principal){
        //Get the hotel
        Optional<Hotel> hotelOpt = hotelService.getHotelById(dto.getHotelId());
        if (hotelOpt.isEmpty()){
            return ResponseEntity.notFound().build(); // 404 not found
        }

        //Get the user
        User user = userService.findByEmail(principal.getName()).orElseThrow();
        //Create pending reserve
        Reserve newReserve = reserveService.createPendingReserve(
            hotelOpt.get(), user, dto.getEntryDate(), dto.getDepartureDate(), dto.getGuests()
        );

        //Generate the header location
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newReserve.getId())
                .toUri();

        return ResponseEntity.created(location).body(new ReserveDTO(newReserve));
    }



    @Operation(summary = "Confirm a reserve")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    //Confirm reserve
    public ResponseEntity<ReserveDTO> confirmReserve(@PathVariable Long id, Principal principal){ // Añadir Principal
        Optional<Reserve> reserveOpt = reserveService.getReserveById(id);
        if(reserveOpt.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        Reserve reserve = reserveOpt.get();
        User currentUser = userService.findByEmail(principal.getName()).orElseThrow();

        //IDOR protection: Only the owner of the reserve can confirm it
        if(!reserve.getCustomer().getId().equals(currentUser.getId())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        reserveService.saveConfirmedReserve(reserve);
        return ResponseEntity.ok(new ReserveDTO(reserve));
    }

    

    @Operation(summary = "Delete a reserve")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Deleted"),
        @ApiResponse(responseCode = "403", description = "Forbidden – not the owner"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReserve(@PathVariable Long id, Principal principal){
        Optional<Reserve> reserveOpt = reserveService.getReserveById(id);
        if(reserveOpt.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        Reserve reserve = reserveOpt.get();
        User currentUser = userService.findByEmail(principal.getName()).orElseThrow();

        // IDOR protection: Only the owner of the reserve can delete it
        if(!reserve.getCustomer().getId().equals(currentUser.getId())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403
        }

        reserveService.deleteReserve(id);
        return ResponseEntity.noContent().build(); 
    }


}