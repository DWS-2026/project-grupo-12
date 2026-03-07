package es.codeurjc.web.service;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;
import es.codeurjc.web.model.User;

@Component
@SessionScope
public class UserSession {

    private Long idUser;
    private String username;
    private String role; // "Admin" or "User"

    public void login(User user) {
        this.idUser = user.getId();
        this.username = user.getName();
        this.role = user.getRole(); 
    }

    public void logout() {
        this.idUser = null;
        this.username = null;
        this.role = null; 
    }

    public boolean isLogged() {
        return this.idUser != null;
    }


    public boolean isAdmin() {
        return "Admin".equals(this.role);
    }

    public Long getIdUser() { 
        return idUser; 
    }

    public String getUsername() { 
        return username; 

    }

    public String getRole() {
        return role; 

    }
}