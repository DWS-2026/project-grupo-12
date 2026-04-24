package es.codeurjc.web.controller.rest;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import es.codeurjc.web.dto.UserDTO;
import es.codeurjc.web.dto.UserRegisterDTO;
import es.codeurjc.web.model.Image;
import es.codeurjc.web.model.User;
import es.codeurjc.web.service.ImageService;
import es.codeurjc.web.service.UserService;

// TUS IMPORTS DEL JWT
import es.codeurjc.web.security.jwt.LoginRequest;
import es.codeurjc.web.security.jwt.AuthResponse;
import es.codeurjc.web.security.jwt.UserLoginService;

import java.security.Principal;
import java.sql.SQLException;

//for cookies
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private ImageService imageService;  
    
    //JWT
    @Autowired
    private UserLoginService userLoginService;


    
    //REGISTER
    @PostMapping("/")
    public ResponseEntity<?> registerUser(@RequestBody UserRegisterDTO newUser) { 
        if (userService.isEmailTakenByAnother(null, newUser.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El correo ya existe");
        }
        userService.registerUser(newUser.getUsername(), newUser.getEmail(), newUser.getPassword(), "USER"   );
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    //LOGIN 
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest loginRequest, 
            HttpServletResponse response) { 
        
        //return reponse for the login service, which will handle the JWT creation and cookie setting
        //userLoginServie copied from Repo-0
        return userLoginService.login(response, loginRequest);
    }

    //LOGOUT 
    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(HttpServletResponse response) {
        String msg = userLoginService.logout(response);
        return ResponseEntity.ok(new AuthResponse(AuthResponse.Status.SUCCESS, msg));
    }

    // PROFILE
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getProfile(Principal principal) {
        User user = userService.findByEmail(principal.getName()).orElseThrow();
        return ResponseEntity.ok(new UserDTO(user));
    }

    // PROFILE PICTURE
    @GetMapping("/me/avatar")
    public ResponseEntity<Object> getProfileAvatar(Principal principal) throws SQLException {
        User user = userService.findByEmail(principal.getName()).orElseThrow();
        
        if (user.getProfileImage() != null) {
            Resource imageFile = imageService.getImageFile(user.getProfileImage().getId()); 
            MediaType mediaType = MediaTypeFactory.getMediaType(imageFile).orElse(MediaType.IMAGE_JPEG); 
            return ResponseEntity.ok().contentType(mediaType).body(imageFile); 
        }
        return ResponseEntity.notFound().build();
    }

    //UPDATE PROFILE
    @PutMapping("/me") //update with put
    public ResponseEntity<?> updateProfileAPI(
            Principal principal,
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam(required = false) String password,
            @RequestParam(value = "photo", required = false) MultipartFile photo) throws Exception {
        
        //Find the user
        User user = userService.findByEmail(principal.getName()).orElseThrow();

        //same checks as in the web controller, but adapted to the API (we return ResponseEntity with error messages instead of redirecting to the profile page)
        if (photo != null && !photo.isEmpty()) { 
            if ((photo.getContentType().equals("image/jpeg") || photo.getContentType().equals("image/png"))) {
                Image newImage = imageService.createImage(photo);
                user.setProfileImage(newImage);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Solo formato JPG o PNG");
            }
        }

        //update th user information with the new information, if the password is empty, we don't change it (same as in the web controller)
        userService.saveUser(user.getId(), username, email, password, user.getRole()); 
        userService.updateUser(user); 

        //return the updated user information
        return ResponseEntity.ok(new UserDTO(user));
    }
}