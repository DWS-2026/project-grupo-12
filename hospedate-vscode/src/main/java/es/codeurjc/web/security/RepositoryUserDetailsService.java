package es.codeurjc.web.security;

import java.util.ArrayList;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import es.codeurjc.web.model.User;
import es.codeurjc.web.repository.UserRepository;


@Service
public class RepositoryUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;


    //to "translate" our User to the UserDetails interface of SpringSecurity, we will use this method in the authentication provider
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        //Finds the user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        //Create a list of authorities for the user
        List<GrantedAuthority> roles = new ArrayList<>();
        
        //We get the role of the user and add it to the list of authorities (only one role, ADMIN or USER)
        roles.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));

        //Return the user adjusted to the interface of SpringSecurity
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), 
                user.getPassword(), 
                roles);
    }
}