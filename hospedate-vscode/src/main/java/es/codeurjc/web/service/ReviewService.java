package es.codeurjc.web.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.web.model.Hotel;
import es.codeurjc.web.model.Review;
import es.codeurjc.web.model.User;
import es.codeurjc.web.repository.ReviewRepository;

@Service
public class ReviewService {
    
    @Autowired
    private ReviewRepository reviewRepository;


    public List<Review> getReviewsByAuthorId(Long userId) {
        return reviewRepository.findByAuthorId(userId);
    }

    public Review createReview(int rating, String title, String comment, User author, Hotel hotel) {
        Review review = new Review(rating, title, comment, author, hotel);
        reviewRepository.save(review);
        return review;
    }

    
    public Optional<Review> getReviewById(Long id) {
        return reviewRepository.findById(id);
    }

    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }

    public long countReviews() {
        return reviewRepository.count();
    }
}
