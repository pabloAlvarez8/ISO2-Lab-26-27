package es.uclm.rental_platform.service;

import es.uclm.rental_platform.domain.Property;
import es.uclm.rental_platform.domain.Review;
import es.uclm.rental_platform.persistence.User;
import es.uclm.rental_platform.persistence.UserRepository;
import es.uclm.rental_platform.repository.PropertyRepository;
import es.uclm.rental_platform.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         PropertyRepository propertyRepository,
                         UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Review addReview(Long propertyId, Long userId, String authorName, double rating, String comment) {
        if (propertyId == null) {
            throw new IllegalArgumentException("El identificador del alojamiento es obligatorio.");
        }

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Alojamiento no encontrado con id: " + propertyId));

        // Validación de rango de puntuación (0.5 a 5.0)
        if (rating < 0.5 || rating > 5.0) {
            throw new IllegalArgumentException("La puntuación debe estar comprendida entre 0.5 y 5.0 estrellas.");
        }

        // Validación de saltos de 0.5 estrellas
        double doubled = rating * 2.0;
        if (Math.abs(doubled - Math.round(doubled)) > 0.001) {
            throw new IllegalArgumentException("La puntuación debe incrementarse en intervalos de 0.5 estrellas.");
        }

        // Validación del comentario
        if (comment == null || comment.trim().isBlank()) {
            throw new IllegalArgumentException("Debes incluir un comentario con tu valoración.");
        }
        if (comment.trim().length() > 1000) {
            throw new IllegalArgumentException("El comentario no puede superar los 1000 caracteres.");
        }

        User author = null;
        String effectiveAuthorName = authorName;
        if (userId != null) {
            author = userRepository.findById(userId).orElse(null);
            if (author != null && (effectiveAuthorName == null || effectiveAuthorName.isBlank())) {
                effectiveAuthorName = author.getName();
            }
        }
        if (effectiveAuthorName == null || effectiveAuthorName.isBlank()) {
            effectiveAuthorName = "Huésped verificado";
        }

        Review review = new Review(property, author, effectiveAuthorName, rating, comment);
        Review savedReview = reviewRepository.save(review);

        // Recálculo atómico de valoración media y total de reseñas en la entidad Property
        List<Review> allReviews = reviewRepository.findByPropertyIdOrderByCreatedAtDesc(propertyId);
        double totalSum = 0.0;
        for (Review r : allReviews) {
            totalSum += r.getRating();
        }
        double average = allReviews.isEmpty() ? 5.0 : (totalSum / allReviews.size());
        double roundedAverage = Math.round(average * 10.0) / 10.0;

        property.setRating(roundedAverage);
        property.setReviewCount(allReviews.size());
        propertyRepository.save(property);

        return savedReview;
    }

    public List<Review> getReviewsByProperty(Long propertyId) {
        return reviewRepository.findByPropertyIdOrderByCreatedAtDesc(propertyId);
    }

    public long countReviewsByProperty(Long propertyId) {
        return reviewRepository.countByPropertyId(propertyId);
    }
}
