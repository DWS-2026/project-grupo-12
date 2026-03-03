package es.codeurjc.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.codeurjc.web.model.Image;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
}