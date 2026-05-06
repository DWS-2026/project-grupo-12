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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

@Tag(name = "Admin")
@RestController
@RequestMapping("/api/v1/admin")
public class AdminRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private ReserveService reserveService;

    // --- USERS ---

    @Operation(summary = "List all non-admin users (paginated)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "403", description = "Forbidden – admin only")
    })
    @GetMapping("/users")
    public ResponseEntity<Page<UserDTO>> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<UserDTO> users = userService.getAllUsersExcludingAdmins(PageRequest.of(page, size))
                .map(UserDTO::new);
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Get user by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "403", description = "Forbidden – admin only"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new UserDTO(user.get()));
    }

    @Operation(summary = "Update a user")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Not found"),
        @ApiResponse(responseCode = "409", description = "Email conflict")
    })
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        // adminUpdateUser returns a result object with a status enum instead of throwing exceptions
        // so we can map each case to the correct HTTP response
        UserService.UserUpdateResult result = userService.adminUpdateUser(
                id, userDTO.getName(), userDTO.getEmail(), userDTO.getRole());

        return switch (result.status) {
            case NOT_FOUND  -> ResponseEntity.notFound().build();
            case FORBIDDEN  -> ResponseEntity.status(403).body(Map.of("error", result.errorMessage));
            case CONFLICT   -> ResponseEntity.status(409).body(Map.of("error", result.errorMessage));
            case OK         -> ResponseEntity.ok(new UserDTO(result.user));
        };
    }

    @Operation(summary = "Delete a user")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Deleted"),
        @ApiResponse(responseCode = "403", description = "Cannot delete admin"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
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

    @Operation(summary = "List all reserves (paginated)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "403", description = "Forbidden – admin only")
    })
    @GetMapping("/reserves")
    public ResponseEntity<Page<ReserveDTO>> listReserves(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ReserveDTO> reserves = reserveService.getAllReserves(PageRequest.of(page, size))
                .map(ReserveDTO::new);
        return ResponseEntity.ok(reserves);
    }

    @Operation(summary = "Get reserve by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "403", description = "Forbidden – admin only"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    @GetMapping("/reserves/{id}")
    public ResponseEntity<ReserveDTO> getReserve(@PathVariable Long id) {
        Optional<Reserve> reserve = reserveService.getReserveById(id);
        if (reserve.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new ReserveDTO(reserve.get()));
    }

    @Operation(summary = "Update a reserve")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PutMapping("/reserves/{id}")
    public ResponseEntity<?> updateReserve(@PathVariable Long id, @Valid @RequestBody ReserveDTO reserveDTO) {
        // adminUpdateReserve returns empty Optional if the reserve does not exist
        Optional<Reserve> updated = reserveService.adminUpdateReserve(
                id, reserveDTO.getEntryDate(), reserveDTO.getDepartureDate(),
                reserveDTO.getGuests(), reserveDTO.getStatus());

        return updated.isEmpty()
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(new ReserveDTO(updated.get()));
    }

    @Operation(summary = "Delete a reserve")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Deleted"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
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
