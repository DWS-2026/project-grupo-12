package es.codeurjc.web.service;

import java.util.Optional;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public boolean isEmailTakenByAnother(Long userId, String email) {
        Optional<User> byEmail = userRepository.findByEmail(email);
        return byEmail.isPresent() && !byEmail.get().getId().equals(userId);
    }

    public boolean isUsernameTakenByAnother(Long userId, String username) {
        Optional<User> byUsername = userRepository.findByName(username);
        return byUsername.isPresent() && !byUsername.get().getId().equals(userId);
    }

    public void saveUser(Long id, String name, String email, String password, String role) {
        Optional<User> existing = userRepository.findById(id);

        if (existing.isPresent()) {
            User user = existing.get();
            user.setName(name);
            user.setEmail(email);
            if (password != null && !password.trim().isEmpty()) {
                user.setPassword(passwordEncoder.encode(password));
            }
            user.setRole(role);
            userRepository.save(user);
        }
    }

    public Page<User> getAllUsersExcludingAdmins(Pageable pageable) {
        return userRepository.findByRoleNot("ADMIN", pageable);
    }

    public Page<User> searchUsersExcludingAdmins(String keyword, Pageable pageable) {
        return userRepository.findByRoleNotAndNameContainingIgnoreCaseOrRoleNotAndEmailContainingIgnoreCase(
            "ADMIN", keyword, "ADMIN", keyword, pageable);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

}