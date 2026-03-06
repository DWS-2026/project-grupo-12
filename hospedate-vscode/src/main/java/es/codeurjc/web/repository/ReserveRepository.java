package es.codeurjc.web.repository;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.web.model.Reserve;
import es.codeurjc.web.model.User;

@Repository
public interface ReserveRepository extends JpaRepository<Reserve,Long>{
    List<Reserve> findByCustomer(User user);
    List<Reserve> findByCustomerId(Long userId);
}


