package es.codeurjc.web.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import es.codeurjc.web.model.Review;
import es.codeurjc.web.model.User;

import java.util.List;
 


@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByAuthorId(Long userId);
    Page<Review> findByAuthorId(Long userId, Pageable pageable);
    Page<Review> findByHotelId(Long hotelId, Pageable pageable);
}

