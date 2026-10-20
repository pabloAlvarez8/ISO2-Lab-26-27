package es.uclm.rental_platform.repository;

import es.uclm.rental_platform.domain.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    boolean existsByAddress(String address);

    List<Property> findByAvailableTrue();

    @Query("SELECT p FROM Property p WHERE (p.available IS NULL OR p.available = true) " +
           "AND (:destination IS NULL OR :destination = '' OR LOWER(COALESCE(p.city, p.destination, '')) LIKE LOWER(CONCAT('%', :destination, '%')) OR LOWER(COALESCE(p.title, p.address, '')) LIKE LOWER(CONCAT('%', :destination, '%'))) " +
           "AND (:propertyType IS NULL OR :propertyType = '' OR LOWER(COALESCE(p.propertyType, p.type, '')) = LOWER(:propertyType)) " +
           "AND (:guests IS NULL OR p.capacity >= :guests) " +
           "AND (:maxPrice IS NULL OR p.pricePerNight <= :maxPrice) " +
           "AND (:minRating IS NULL OR p.rating >= :minRating) " +
           "AND (:minReviews IS NULL OR p.reviewCount >= :minReviews) " +
           "AND (:q IS NULL OR :q = '' OR LOWER(COALESCE(p.city, p.destination, '')) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(COALESCE(p.title, p.address, '')) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(COALESCE(p.propertyType, p.type, '')) LIKE LOWER(CONCAT('%', :q, '%')))")
    List<Property> searchProperties(
            @Param("destination") String destination,
            @Param("propertyType") String propertyType,
            @Param("guests") Integer guests,
            @Param("maxPrice") Double maxPrice,
            @Param("minRating") Double minRating,
            @Param("minReviews") Integer minReviews,
            @Param("q") String q
    );
}
