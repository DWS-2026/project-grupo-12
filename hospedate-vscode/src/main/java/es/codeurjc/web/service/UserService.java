package es.codeurjc.web.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.web.model.User;
import es.codeurjc.web.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    public Optional<User> authenticateUser(String email, String password) {
        Optional<User> checkUser = userRepository.findByEmail(email);

        if (checkUser.isPresent() && checkUser.get().getPassword().equals(password)) {
            return checkUser; 
        }

        return Optional.empty(); //return optional if does not match or with the user if exists and password is correct 
    }

    public User registerUser(String name, String email, String password) {
        User newUser = new User(name, email, password);
        return userRepository.save(newUser);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }
}
