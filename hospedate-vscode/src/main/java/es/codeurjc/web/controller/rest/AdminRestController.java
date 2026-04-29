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
import es.codeurjc.web.service.UserService.UserUpdateResult;

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
        UserService.UserUpdateResult result = userService.adminUpdateUser(
                id, userDTO.getName(), userDTO.getEmail(), userDTO.getRole());

        return switch (result.status) {
            case NOT_FOUND  -> ResponseEntity.notFound().build();
            case FORBIDDEN  -> ResponseEntity.status(403).body(Map.of("error", result.errorMessage));
            case CONFLICT   -> ResponseEntity.status(409).body(Map.of("error", result.errorMessage));
            case OK         -> ResponseEntity.ok(new UserDTO(result.user));
        };
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
        Optional<Reserve> updated = reserveService.adminUpdateReserve(
                id, reserveDTO.getEntryDate(), reserveDTO.getDepartureDate(),
                reserveDTO.getGuests(), reserveDTO.getStatus());

        return updated.isEmpty()
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(new ReserveDTO(updated.get()));
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
