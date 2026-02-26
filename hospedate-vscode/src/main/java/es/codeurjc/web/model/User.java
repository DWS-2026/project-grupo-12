package es.codeurjc.web.model;

public class User {
    private Long id;
    private String name;
    private String email; 
    private String role; // "Admin" or "User"

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

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
}