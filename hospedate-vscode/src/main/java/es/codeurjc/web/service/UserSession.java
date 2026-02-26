package es.codeurjc.web.service;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import es.codeurjc.web.model.User; 

@Component
@SessionScope
public class UserSession {
    
    // el dato principal que guardamos
    private User loggedUser;

    public User getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
    }

    //comprueba si hay alguien en esa session
    public boolean isLogged() {
        return this.loggedUser != null;
    }

   //comprueba si el usuario logueado es admin
    public boolean isAdmin() {
        return this.loggedUser != null && "ADMIN".equals(this.loggedUser.getRole());
    }
}