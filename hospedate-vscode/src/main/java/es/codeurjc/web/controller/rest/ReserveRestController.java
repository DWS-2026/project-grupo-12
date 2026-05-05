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

    @Autowired
    private UserSession userSession;

    @Operation(summary = "List all reserves (paginated)")
    @ApiResponse(responseCode = "200", description = "OK")
    @GetMapping("/")
    public ResponseEntity<Page<ReserveDTO>> getReserves(Pageable pageable){
        Page<Reserve> reserves = reserveService.getAllReserves(pageable);
        return ResponseEntity.ok(reserves.map(ReserveDTO::new));
    }

    @Operation(summary = "Create a reserve")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Created"),
        @ApiResponse(responseCode = "401", description = "Unauthorized – login required"),
        @ApiResponse(responseCode = "404", description = "Hotel not found")
    })
    @PostMapping("/")
    public ResponseEntity<ReserveDTO> createReserve(@Valid @RequestBody ReserveDTO dto){
        if(!userSession.isLogged()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<Hotel> hotelOpt = hotelService.getHotelById(dto.getHotelId());
        if (hotelOpt.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        User user = userService.findById(userSession.getIdUser()).orElseThrow();

        Reserve newReserve = reserveService.createPendingReserve(
            hotelOpt.get(), user, dto.getEntryDate(), dto.getDepartureDate(), dto.getGuests()
        );

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
    @PostMapping("/{id}/confirm")
    public ResponseEntity<ReserveDTO> confirmReserve(@PathVariable Long id){
        return reserveService.getReserveById(id)
            .map(reserve -> {
                reserveService.saveConfirmedReserve(reserve);
                return ResponseEntity.ok(new ReserveDTO(reserve));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete a reserve")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Deleted"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReserve(@PathVariable Long id){
        if (reserveService.getReserveById(id).isPresent()){
            reserveService.deleteReserve(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }


}