package es.uclm.rental_platform.service;

import es.uclm.rental_platform.domain.Property;
import es.uclm.rental_platform.repository.PropertyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class PropertyService {

    private final PropertyRepository propertyRepository;

    public PropertyService(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    public List<Property> getAllAvailableProperties() {
        return propertyRepository.findByAvailableTrue();
    }

    public PropertySearchResult search(
            String destination,
            LocalDate checkin,
            LocalDate checkout,
            Integer guests,
            String propertyType,
            Double maxPrice,
            String q
    ) {
        return search(destination, checkin, checkout, guests, propertyType, maxPrice, null, null, q);
    }

    public PropertySearchResult search(
            String destination,
            LocalDate checkin,
            LocalDate checkout,
            Integer guests,
            String propertyType,
            Double maxPrice,
            Double minRating,
            String q
    ) {
        return search(destination, checkin, checkout, guests, propertyType, maxPrice, minRating, null, q);
    }

    public PropertySearchResult search(
            String destination,
            LocalDate checkin,
            LocalDate checkout,
            Integer guests,
            String propertyType,
            Double maxPrice,
            Double minRating,
            Integer minReviews,
            String q
    ) {
        // Validación de fechas (opcionales)
        if (checkin != null && checkout != null) {
            if (checkin.isBefore(LocalDate.now())) {
                return PropertySearchResult.error("La fecha de llegada no puede ser anterior a hoy.");
            }
            if (!checkout.isAfter(checkin)) {
                return PropertySearchResult.error("La fecha de salida debe ser posterior a la fecha de llegada.");
            }
        } else if (checkin != null && checkin.isBefore(LocalDate.now())) {
            return PropertySearchResult.error("La fecha de llegada no puede ser anterior a hoy.");
        }

        // Validación de huéspedes
        if (guests != null && guests < 1) {
            return PropertySearchResult.error("El número de viajeros debe ser al menos 1.");
        }

        // Validación de precio
        if (maxPrice != null && maxPrice < 0) {
            return PropertySearchResult.error("El precio máximo no puede ser negativo.");
        }

        // Validación de valoración
        if (minRating != null && (minRating < 0.0 || minRating > 5.0)) {
            return PropertySearchResult.error("La valoración debe estar comprendida entre 0 y 5 estrellas.");
        }

        // Validación de número de reseñas
        if (minReviews != null && minReviews < 0) {
            return PropertySearchResult.error("El número de reseñas no puede ser negativo.");
        }

        // Normalización de cadenas de texto y valores numéricos
        String normalizedDestination = normalizeString(destination);
        String normalizedPropertyType = normalizeString(propertyType);
        String normalizedQ = normalizeString(q);
        Double normalizedMinRating = (minRating != null && minRating > 0.0) ? minRating : null;
        Integer normalizedMinReviews = (minReviews != null && minReviews > 0) ? minReviews : null;

        List<Property> results = propertyRepository.searchProperties(
                normalizedDestination,
                normalizedPropertyType,
                guests,
                maxPrice,
                normalizedMinRating,
                normalizedMinReviews,
                normalizedQ
        );

        return PropertySearchResult.success(results);
    }

    public PropertySearchResult search(
            String destination,
            String checkin,
            String checkout,
            Integer guests,
            String propertyType,
            Double maxPrice,
            Double minRating,
            Integer minReviews,
            String q
    ) {
        return search(
                destination,
                parseDate(checkin),
                parseDate(checkout),
                guests,
                propertyType,
                maxPrice,
                minRating,
                minReviews,
                q
        );
    }

    public LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank() || "null".equalsIgnoreCase(dateStr.trim()) || "undefined".equalsIgnoreCase(dateStr.trim())) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr.trim());
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private String normalizeString(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
