package es.codeurjc.web.controller.rest;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;

import es.codeurjc.web.service.ImageService;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/images")
public class ImageRestController {

    @Autowired
    private ImageService imageService;

    //return a Resource with the image file
    @GetMapping("/{id}")
    public ResponseEntity<Resource> downloadImage(@PathVariable Long id) {
        try {
            //Get the resource of the image file from the service
            Resource imageFile = imageService.getImageFile(id); 
            
            // Check the image file's media type (MIME type) based on its content or filename
            MediaType mediaType = MediaTypeFactory.getMediaType(imageFile).orElse(MediaType.IMAGE_JPEG); 
            
            //Return the bytes of the image file with the correct media type so the browser can render it
            return ResponseEntity.ok()
                    .contentType(mediaType) //for rendering the image in the browser
                    .body(imageFile);
                    
        } catch (Exception e) {
            return ResponseEntity.notFound().build(); 
        }
    }
}