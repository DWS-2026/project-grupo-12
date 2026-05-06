package es.codeurjc.web.controller.rest;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import es.codeurjc.web.dto.ReserveDTO;
import es.codeurjc.web.dto.ReviewDTO;
import es.codeurjc.web.dto.UserDTO;
import es.codeurjc.web.model.Image;
import es.codeurjc.web.model.Reserve;
import es.codeurjc.web.model.Review;
import es.codeurjc.web.model.User;
import es.codeurjc.web.service.ImageService;
import es.codeurjc.web.service.ReserveService;
import es.codeurjc.web.service.ReviewService;
import es.codeurjc.web.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.security.Principal;
import java.sql.SQLException;


@Tag(name = "Users")
@RestController
@RequestMapping("/api/v1/users")
public class UserRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private ReserveService reserveService;
    
    @Autowired
    private ReviewService reviewService;
    
    @Autowired
    private ImageService imageService;  

    @Operation(summary = "Get current user profile")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })




    //Own profile
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getProfile(Principal principal) {
        User user = userService.findByEmail(principal.getName()).orElseThrow();
        return ResponseEntity.ok(new UserDTO(user));
    }


    
    @Operation(summary = "Get current user avatar")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "No avatar set")
    })
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



    @Operation(summary = "Update current user profile")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Invalid file format"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
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
                if (user.getProfileImage() != null) {
                    //delete the old image file from the disk, if it exists, we ignore any error that may occur during deletion
                    try {
                        Resource oldImageFile = imageService.getImageFile(user.getProfileImage().getId());
                        oldImageFile.getFile().delete();
                    } catch (Exception e) {
                        System.err.println("Failed to delete old avatar: " + e.getMessage());
                    }
                }
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




    @Operation(summary = "Get current user's reserves (paginated)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/me/reserves")
    public ResponseEntity<Page<ReserveDTO>> getMyReserves(Principal principal, Pageable pageable) {
        User user = userService.findByEmail(principal.getName()).orElseThrow();
        //Find reserves of the user and convert them to DTOs
        Page<Reserve> reserves = reserveService.getUserReserves(user, pageable);
        return ResponseEntity.ok(reserves.map(ReserveDTO::new));
    }




    @Operation(summary = "Get current user's reviews (paginated)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/me/reviews")
    public ResponseEntity<Page<ReviewDTO>> getMyReviews(Principal principal, Pageable pageable) {
        User user = userService.findByEmail(principal.getName()).orElseThrow();
        //Find reviews of the user and convert them to DTOs
        Page<Review> reviews = reviewService.getUserReviews(user, pageable);
        return ResponseEntity.ok(reviews.map(ReviewDTO::new));
    }

    
}