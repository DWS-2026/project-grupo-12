package es.codeurjc.web.controller.rest;

import es.codeurjc.web.dto.UserRegisterDTO;
import es.codeurjc.web.security.jwt.AuthResponse;
import es.codeurjc.web.security.jwt.LoginRequest;
import es.codeurjc.web.security.jwt.UserLoginService;
import es.codeurjc.web.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import es.codeurjc.web.model.User;

@Tag(name = "Auth")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthRestController {

    @Autowired
    private UserLoginService userLoginService;

    @Autowired
    private UserService userService;

    @Operation(summary = "Login and get JWT cookie")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {
        return userLoginService.login(response, loginRequest);
    }

    @Operation(summary = "Register a new user")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Created"),
        @ApiResponse(responseCode = "400", description = "Email already registered or invalid input")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegisterDTO registerDTO) {
        if (userService.findByEmail(registerDTO.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already registered");
        }

        User createdUser = userService.registerUser(
            registerDTO.getUsername(),
            registerDTO.getEmail(),
            registerDTO.getPassword(),
            "USER"
        );

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdUser.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @Operation(summary = "Logout and clear JWT cookie")
    @ApiResponse(responseCode = "200", description = "OK")
    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(HttpServletResponse response) {
        String message = userLoginService.logout(response);
        return ResponseEntity.ok(new AuthResponse(AuthResponse.Status.SUCCESS, message));
    }
}