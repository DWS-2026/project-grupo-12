package es.codeurjc.web.controller.rest;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import es.codeurjc.web.dto.ReserveDTO;
import es.codeurjc.web.dto.UserDTO;
import es.codeurjc.web.model.Reserve;
import es.codeurjc.web.model.User;
import es.codeurjc.web.service.ReserveService;
import es.codeurjc.web.service.UserService;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private ReserveService reserveService;

    // --- USERS ---

    @GetMapping("/users")
    public ResponseEntity<Page<UserDTO>> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<UserDTO> users = userService.getAllUsersExcludingAdmins(PageRequest.of(page, size))
                .map(UserDTO::new);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new UserDTO(user.get()));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        Optional<User> existing = userService.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = existing.get();

        if (user.isAdmin()) {
            return ResponseEntity.status(403)
                    .body(Map.of("error", "Cannot modify an admin account"));
        }

        if (userDTO.getEmail() != null
                && !userDTO.getEmail().equals(user.getEmail())
                && userService.isEmailTakenByAnother(id, userDTO.getEmail())) {
            return ResponseEntity.status(409)
                    .body(Map.of("error", "Email already in use"));
        }

        if (userDTO.getName() != null
                && !userDTO.getName().equals(user.getName())
                && userService.isUsernameTakenByAnother(id, userDTO.getName())) {
            return ResponseEntity.status(409)
                    .body(Map.of("error", "Name already in use"));
        }

        if (userDTO.getName() != null) user.setName(userDTO.getName());
        if (userDTO.getEmail() != null) user.setEmail(userDTO.getEmail());
        if (userDTO.getRole() != null) user.setRole(userDTO.getRole());

        userService.updateUser(user);
        return ResponseEntity.ok(new UserDTO(user));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (user.get().isAdmin()) {
            return ResponseEntity.status(403)
                    .body(Map.of("error", "Cannot delete an admin account"));
        }
        userService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }

    // --- RESERVES ---

    @GetMapping("/reserves")
    public ResponseEntity<Page<ReserveDTO>> listReserves(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ReserveDTO> reserves = reserveService.getAllReserves(PageRequest.of(page, size))
                .map(ReserveDTO::new);
        return ResponseEntity.ok(reserves);
    }

    @GetMapping("/reserves/{id}")
    public ResponseEntity<ReserveDTO> getReserve(@PathVariable Long id) {
        Optional<Reserve> reserve = reserveService.getReserveById(id);
        if (reserve.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new ReserveDTO(reserve.get()));
    }

    @PutMapping("/reserves/{id}")
    public ResponseEntity<?> updateReserve(@PathVariable Long id, @RequestBody ReserveDTO reserveDTO) {
        Optional<Reserve> existing = reserveService.getReserveById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Reserve reserve = existing.get();

        if (reserveDTO.getEntryDate() != null) reserve.setEntryDate(reserveDTO.getEntryDate());
        if (reserveDTO.getDepartureDate() != null) reserve.setDepartureDate(reserveDTO.getDepartureDate());
        if (reserveDTO.getGuests() > 0) reserve.setGuests(reserveDTO.getGuests());
        if (reserveDTO.getStatus() != null) reserve.setStatus(reserveDTO.getStatus());

        if (reserve.getEntryDate() != null && reserve.getDepartureDate() != null) {
            long nights = java.time.temporal.ChronoUnit.DAYS.between(
                    reserve.getEntryDate(), reserve.getDepartureDate());
            if (nights <= 0) nights = 1;
            reserve.setNights(nights);
            if (reserve.getHotel() != null) {
                reserve.setPrice(nights * reserve.getHotel().getPrice());
            }
        }

        reserveService.saveReserve(reserve);
        return ResponseEntity.ok(new ReserveDTO(reserve));
    }

    @DeleteMapping("/reserves/{id}")
    public ResponseEntity<Map<String, String>> deleteReserve(@PathVariable Long id) {
        Optional<Reserve> reserve = reserveService.getReserveById(id);
        if (reserve.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        reserveService.deleteReserve(id);
        return ResponseEntity.ok(Map.of("message", "Reserve deleted successfully"));
    }
}
