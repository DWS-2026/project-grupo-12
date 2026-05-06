package es.codeurjc.web.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.web.model.Reserve;
import es.codeurjc.web.model.User;


@Repository
public interface ReserveRepository extends JpaRepository<Reserve,Long>{
    List<Reserve> findByCustomerId(Long userId);
    Page<Reserve> findByHotelNameContainingIgnoreCaseOrCustomerNameContainingIgnoreCaseOrCustomerEmailContainingIgnoreCase(
                        String hotel, String name, String email, Pageable pageable);
    Page<Reserve> findByCustomer(User customer, Pageable pageable);
}


