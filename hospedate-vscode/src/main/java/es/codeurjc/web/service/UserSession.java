package es.codeurjc.web.service;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;
import es.codeurjc.web.model.User;

@Component
@SessionScope
public class UserSession {

    private Long idUser=null;
    private String username=null;
    private String role=null; // "ADMIN" or "USER"

    //saves user info in the actual session, we save id, name and role 
    public void modifySessionInfo(User user) {
        this.idUser = user.getId();
        this.username = user.getName();
        this.role = user.getRole(); 
    }

    //restart session info of the current user 
    public void logout() {
        this.idUser = null;
        this.username = null;
        this.role = null; 
    }

    //check if querying user is logged in or not
    public boolean isLogged() {
        return this.idUser != null;
    }

    //check only role field of the user
    public boolean isAdmin() {
        return "ADMIN".equals(this.role);
    }

    //getters 
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