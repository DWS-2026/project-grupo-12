package es.codeurjc.web.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    
    // Save avatar image in the disk and save the filename in the database
    public Image createAvatarImage(MultipartFile file) throws IOException {
        Path path = Paths.get(uploadDir).toAbsolutePath().normalize(); 
        
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        // We keep the original name (required in the rubric)
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.isEmpty()) {
            originalFileName = "unnamed_file.jpg";
        }

        // We sanitize to prevent a path traversal
        // We extract ONLY the final filename, deleting any attempts at "../"
        String sanitizedFileName = Paths.get(originalFileName).getFileName().toString();
        
        // We solve the final route
        Path destination = path.resolve(sanitizedFileName).normalize();

        //We mathematically verified that the file will not be saved outside of our allowed 'uploads' folder.
        if (!destination.startsWith(path)) {
            throw new SecurityException("¡Ataque de Path Traversal detectado! Archivo bloqueado.");
        }

        // Save bytes of the uploaded file in the destination path
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        // save only filename in the database, the file is saved in the disk
        Image image = new Image();
        image.setFileName(sanitizedFileName); 
        return imageRepository.save(image);
    }

    //get the image file from the disk using the filename stored in the database
    public Resource getImageFile(long id) throws SQLException, IOException {
        Image image = imageRepository.findById(id).orElseThrow();
        
        //if the image has a filename, we read it from the disk
        if (image.getFileName() != null) {
            Path basePath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path filePath = basePath.resolve(image.getFileName()).normalize();
            
            // PATH TRAVERSAL COUNTERMEASURE IN READING
            // Make sure that nobody reads a file outside of the uploads folder
            if (!filePath.startsWith(basePath)) {
                throw new SecurityException("¡Ataque de Path Traversal detectado en lectura!");
            }

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
        else {
            throw new RuntimeException("Image file not found"); 
        }
    }

    public void deleteImage(long id) {
        imageRepository.deleteById(id);
    }
    
    public boolean isImageSafe(MultipartFile file) {
    try {
        if (file == null || file.isEmpty()) return false;

        // We read the first 4 bytes (Magic Bytes)
        byte[] header = new byte[4];
        try (java.io.InputStream is = file.getInputStream()) {
            if (is.read(header) < 3) return false; 
        }

        // We checked JPEG (FF D8 FF)
        boolean isJpeg = header[0] == (byte) 0xFF && 
                         header[1] == (byte) 0xD8 && 
                         header[2] == (byte) 0xFF;

        // We checked PNG (89 50 4E 47)
        boolean isPng = header[0] == (byte) 0x89 && 
                        header[1] == (byte) 0x50 && 
                        header[2] == (byte) 0x4E && 
                        header[3] == (byte) 0x47;

        return isJpeg || isPng;
    } catch (Exception e) {
        return false;
    }
}

    public void saveImage(Image image) {
        imageRepository.save(image);
    }
}