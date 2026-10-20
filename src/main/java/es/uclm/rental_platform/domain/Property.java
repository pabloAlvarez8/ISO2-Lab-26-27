package es.uclm.rental_platform.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "properties")
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String propertyType;

    @Column(nullable = false)
    private Double pricePerNight;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private Boolean available = true;

    private String imageUrl;

    private Double rating = 5.0;

    private Integer reviewCount = 0;

    public Property() {}

    public Property(String title, String description, String city, String propertyType,
                    Double pricePerNight, Integer capacity, Boolean available, String imageUrl) {
        this(title, description, city, propertyType, pricePerNight, capacity, available, imageUrl, 5.0, 0);
    }

    public Property(String title, String description, String city, String propertyType,
                    Double pricePerNight, Integer capacity, Boolean available, String imageUrl, Double rating) {
        this(title, description, city, propertyType, pricePerNight, capacity, available, imageUrl, rating, 0);
    }

    public Property(String title, String description, String city, String propertyType,
                    Double pricePerNight, Integer capacity, Boolean available, String imageUrl, Double rating, Integer reviewCount) {
        this.title = title;
        this.description = description;
        this.city = city;
        this.propertyType = propertyType;
        this.pricePerNight = pricePerNight;
        this.capacity = capacity;
        this.available = available != null ? available : true;
        this.imageUrl = imageUrl;
        this.rating = rating != null ? rating : 5.0;
        this.reviewCount = reviewCount != null ? reviewCount : 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public Double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(Double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }
}
