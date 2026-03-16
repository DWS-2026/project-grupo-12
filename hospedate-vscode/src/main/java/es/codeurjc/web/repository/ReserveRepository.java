package es.codeurjc.web.repository;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.web.model.Reserve;


@Repository
public interface ReserveRepository extends JpaRepository<Reserve,Long>{
    List<Reserve> findByCustomerId(Long userId);
}


