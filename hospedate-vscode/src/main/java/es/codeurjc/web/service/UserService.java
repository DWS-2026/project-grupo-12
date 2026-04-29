package es.codeurjc.web.service;

import java.util.Optional;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; 
import org.springframework.stereotype.Service;

import es.codeurjc.web.model.User;
import es.codeurjc.web.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(String name, String email, String password, String role) {
        String hashPassword = passwordEncoder.encode(password);
        
        User newUser = new User(name, email, hashPassword, role);
        
        return userRepository.save(newUser);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
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

    public Optional<UserUpdateResult> adminUpdateUser(Long id, String name, String email, String role) {
        Optional<User> existing = userRepository.findById(id);
        if (existing.isEmpty()) return Optional.of(UserUpdateResult.notFound());

        User user = existing.get();
        if (user.isAdmin()) return Optional.of(UserUpdateResult.forbidden("Cannot modify an admin account"));

        if (email != null && !email.equals(user.getEmail()) && isEmailTakenByAnother(id, email))
            return Optional.of(UserUpdateResult.conflict("Email already in use"));

        if (name != null && !name.equals(user.getName()) && isUsernameTakenByAnother(id, name))
            return Optional.of(UserUpdateResult.conflict("Name already in use"));

        if (name != null) user.setName(name);
        if (email != null) user.setEmail(email);
        if (role != null) user.setRole(role);

        userRepository.save(user);
        return Optional.of(UserUpdateResult.ok(user));
    }

    public enum UserUpdateStatus { OK, NOT_FOUND, FORBIDDEN, CONFLICT }

    public static class UserUpdateResult {
        public final UserUpdateStatus status;
        public final User user;
        public final String errorMessage;

        private UserUpdateResult(UserUpdateStatus status, User user, String errorMessage) {
            this.status = status;
            this.user = user;
            this.errorMessage = errorMessage;
        }

        public static UserUpdateResult ok(User user) { return new UserUpdateResult(UserUpdateStatus.OK, user, null); }
        public static UserUpdateResult notFound() { return new UserUpdateResult(UserUpdateStatus.NOT_FOUND, null, "User not found"); }
        public static UserUpdateResult forbidden(String msg) { return new UserUpdateResult(UserUpdateStatus.FORBIDDEN, null, msg); }
        public static UserUpdateResult conflict(String msg) { return new UserUpdateResult(UserUpdateStatus.CONFLICT, null, msg); }
    }
}