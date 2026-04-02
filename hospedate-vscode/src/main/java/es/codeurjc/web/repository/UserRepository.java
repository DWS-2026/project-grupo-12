package es.codeurjc.web.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import es.codeurjc.web.model.User; 


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    Optional<User> findByName(String name);
    Page<User> findByRoleNot(String role, Pageable pageable);
    Page<User> findByRoleNotAndNameContainingIgnoreCaseOrRoleNotAndEmailContainingIgnoreCase(
                    String role1, String name, String role2, String email, Pageable pageable);

}

