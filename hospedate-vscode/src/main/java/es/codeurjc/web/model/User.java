package es.codeurjc.web.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity(name = "UserTable")
public class User {
    //1 to 1 relation with image 
    @OneToOne(cascade=CascadeType.ALL)
    private Image image;    
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    private String name;
    private String email; 
    private String role; // "Admin" or "User"

    
    

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
    
    //Empty constructor for JPA 
    public User(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Image getProfileImage() {
        return image;
    }

    public void setProfileImage(Image image) {
        this.image = image;
    }
}