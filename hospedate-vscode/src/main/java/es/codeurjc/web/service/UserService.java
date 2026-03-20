package es.codeurjc.web.service;

import java.util.Optional;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // Importación obligatoria
import org.springframework.stereotype.Service;

import es.codeurjc.web.model.User;
import es.codeurjc.web.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(String name, String email, String password) {
        String hashPassword = passwordEncoder.encode(password);
        
        User newUser = new User(name, email, hashPassword);
        
        return userRepository.save(newUser);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void saveUser(Long id, String name, String email, String password, String role) {
        Optional<User> existing = userRepository.findById(id);

        if (existing.isPresent()) {
            User user = existing.get();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password)); // Encriptar la contraseña
            user.setRole(role);
            userRepository.save(user);
        }
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}