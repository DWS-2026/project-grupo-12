package es.codeurjc.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.SQLException;
import es.codeurjc.web.model.Image;
import es.codeurjc.web.repository.ImageRepository;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    //to save the image file in the database 
    public Image createImage(MultipartFile imageFile) throws IOException {
        Image image = new Image();
        try {

            image.setImageFile(new SerialBlob(imageFile.getBytes()));
        } catch (Exception e) {
            throw new IOException("Failed to create image", e);
        }
        imageRepository.save(image); 
        return image; 
    }

    //for extrating the image file to serve it 
    public Resource getImageFile(long id) throws SQLException {
        Image image = imageRepository.findById(id).orElseThrow();
        
        if (image.getImageFile() != null) {
            return new InputStreamResource(image.getImageFile().getBinaryStream()); 
        } else {
            throw new RuntimeException("Image file not found"); 
        }
    }
}