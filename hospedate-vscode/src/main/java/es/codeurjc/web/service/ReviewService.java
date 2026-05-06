package es.codeurjc.web.service;

import java.util.List;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import es.codeurjc.web.dto.ReviewDTO;
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

    public Page<Review> getUserReviews(User user, Pageable pageable) {
        return reviewRepository.findByAuthorId(user.getId(), pageable);
    }

    public Page<Review> getReviewsByHotelId(Long hotelId, Pageable pageable) {
        return reviewRepository.findByHotelId(hotelId, pageable);
    }

    public Review createReview(int rating, String title, String comment, User author, Hotel hotel) {
        // ANTI-XSS SANITIZATION
        // Safelist.relaxed() allows h1, h2, b, i, u, lists, etc., but blocks scripts and iframes
        String safeComment = Jsoup.clean(comment, Safelist.relaxed());

        Review review = new Review(rating, title, safeComment, author, hotel);
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

    public Optional<Review> updateReviewFromDto(Long id, ReviewDTO reviewDto) {
        return reviewRepository.findById(id).map(review -> {
            review.setRating(reviewDto.getRating());
            review.setTitle(reviewDto.getTitle());
            // ANTI-XSS SANITIZATION
            String safeComment = Jsoup.clean(reviewDto.getComment(), Safelist.relaxed());
            review.setComment(safeComment);
            return reviewRepository.save(review);
        });
    }

}

