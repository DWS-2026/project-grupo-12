package es.codeurjc.web.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users") // "user" is a reserved word in SQL, so we use "users" as the table name
public class User {
    //1 to 1 relation with image
    @OneToOne(cascade=CascadeType.ALL)
    private Image image;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // A user can have many reviews; cascade delete so removing a user removes their reviews
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    // A user can have many reserves; cascade delete so removing a user removes their reserves
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reserve> reserves = new ArrayList<>();


    private String name;
    private String email; 
    private String role; // "Admin" or "User"
    private String password; // Add this field for the password    

    
    

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = "USER";//always create a user with role User if not specified, only admin can create another admin
    }

    //Constructor for ADMIN ONLY
    public User(String name, String email, String password, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return "ADMIN".equals(this.role);
    }
}
