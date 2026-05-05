package es.codeurjc.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.Optional;

import es.codeurjc.web.model.Image;
import es.codeurjc.web.repository.ImageRepository;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    //route for saving the image avatar in the disk
    @Value("${image.upload.dir:uploads/images}")
    private String uploadDir;

    public Optional<Image> getImageById(Long id) {
        return imageRepository.findById(id);
    }

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

    // save avatar image in the disk and save the filename in the database
    public Image createAvatarImage(MultipartFile file) throws IOException {
        Path path = Paths.get(uploadDir); //path for uploading images
        
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        // Generate a unique filename to avoid conflicts, the user has no efect on the filename 
        String fileName = java.util.UUID.randomUUID().toString() + ".jpg";        
        Path destination = path.resolve(fileName);
        
        //Save bytes of the uploaded file in the destination path
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        //save only filename in the database, the file is saved in the disk
        Image image = new Image();
        image.setFileName(fileName); 
        return imageRepository.save(image);
    }
    

    //get the image file from the disk using the filename stored in the database, if it doesn't exist we return 404
    public Resource getImageFile(long id) throws SQLException, IOException {
        Image image = imageRepository.findById(id).orElseThrow();
        
        //if the image has a filename, we read it from the disk, if it doesn't exist we return 404
        if (image.getFileName() != null) {
            java.nio.file.Path filePath = java.nio.file.Paths.get(uploadDir).resolve(image.getFileName()).normalize();
            Resource resource = new org.springframework.core.io.UrlResource(filePath.toUri());
            
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("El archivo de la imagen no se encuentra en el disco duro");
            }
        } 
        //if the image has a BLOB, we read it from the database
        else if (image.getImageFile() != null) {
            return new InputStreamResource(image.getImageFile().getBinaryStream()); 
        } 
        //if the image is empty completely
        else {
            throw new RuntimeException("Image file not found"); 
        }
    }

    public void deleteImage(long id) {
        imageRepository.deleteById(id);
    }
    
}