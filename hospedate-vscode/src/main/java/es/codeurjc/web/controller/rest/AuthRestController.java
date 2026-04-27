package es.codeurjc.web.controller.rest;

import es.codeurjc.web.dto.UserRegisterDTO;
import es.codeurjc.web.security.jwt.AuthResponse;
import es.codeurjc.web.security.jwt.LoginRequest;
import es.codeurjc.web.security.jwt.UserLoginService;
import es.codeurjc.web.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import es.codeurjc.web.model.User;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthRestController {

    @Autowired
    private UserLoginService userLoginService;

    @Autowired
    private UserService userService;

    //LOGIN: Generate the JWT token and store it in a cookie.
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest loginRequest, 
            HttpServletResponse response) {
        
        return userLoginService.login(response, loginRequest);
    }

    //REGISTER: Creates the user and returns the Location header
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegisterDTO registerDTO) {
        
        // We check if the email already exists
        if (userService.findByEmail(registerDTO.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email ya registrado");
        }

        // We register the user (by default as USER)
        User createdUser = userService.registerUser(
            registerDTO.getUsername(), 
            registerDTO.getEmail(), 
            registerDTO.getPassword(), 
            "USER"
        );

        // We add the Location header
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdUser.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    // 3. LOGOUT: Delete security cookies
    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(HttpServletResponse response) {
        String message = userLoginService.logout(response);
        return ResponseEntity.ok(new AuthResponse(AuthResponse.Status.SUCCESS, message));
    }
}