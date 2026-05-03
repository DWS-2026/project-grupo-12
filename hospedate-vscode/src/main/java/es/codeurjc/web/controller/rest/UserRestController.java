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
import es.codeurjc.web.model.Image;
import es.codeurjc.web.model.User;
import es.codeurjc.web.service.ImageService;
import es.codeurjc.web.service.UserService;

import java.security.Principal;
import java.sql.SQLException;


@RestController
@RequestMapping("/api/v1/users")
public class UserRestController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private ImageService imageService;  

    // PROFILE
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getProfile(Principal principal) {
        User user = userService.findByEmail(principal.getName()).orElseThrow();
        return ResponseEntity.ok(new UserDTO(user));
    }

    // PROFILE PICTURE
    @GetMapping("/me/avatar")
    public ResponseEntity<Object> getProfileAvatar(Principal principal) {
        User user = userService.findByEmail(principal.getName()).orElseThrow();
        
        if (user.getProfileImage() != null) {
            try {
                //read the image file from the disk using the filename stored in the database, if it doesn't exist we return 404
                Resource imageFile = imageService.getImageFile(user.getProfileImage().getId()); 
                MediaType mediaType = MediaTypeFactory.getMediaType(imageFile).orElse(MediaType.IMAGE_JPEG); 
                return ResponseEntity.ok().contentType(mediaType).body(imageFile); 
                
            } catch (Exception e) {
                //If there's any error (like the image is not found in the disk) we return 404 and log the error
                System.err.println("Failed to load avatar: " + e.getMessage());
                return ResponseEntity.notFound().build();
            }
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
                Image newImage = imageService.createAvatarImage(photo); //save the image in the disk and the filename in the database
                user.setProfileImage(newImage);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Solo formato JPG o PNG");
            }
        }

        //update the user information with the new information, if the password is empty, we don't change it (same as in the web controller)
        userService.saveUser(user.getId(), username, email, password, user.getRole()); 
        userService.updateUser(user); 

        //return the updated user information
        return ResponseEntity.ok(new UserDTO(user));
    }
}