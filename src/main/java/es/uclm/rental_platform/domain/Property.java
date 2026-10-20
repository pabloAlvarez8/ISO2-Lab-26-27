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
    private String address;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String destination;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String propertyType;

    @Column(nullable = false)
    private double pricePerNight;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    private boolean available = true;

    @Column(nullable = false)
    private boolean directBooking = true;

    private String imageUrl;

    private Double rating = 5.0;

    private Integer reviewCount = 0;

    public Property() {
        // Constructor vacío requerido por JPA
    }

    public Property(String address, double pricePerNight, String type, String destination, int capacity,
            boolean directBooking) {
        this.address = address;
        this.title = address;
        this.pricePerNight = pricePerNight;
        this.type = type;
        this.propertyType = type;
        this.destination = destination;
        this.city = destination;
        this.capacity = capacity;
        this.directBooking = directBooking;
        this.available = true;
        this.rating = 5.0;
        this.reviewCount = 0;
    }

    public Property(String title, String description, String city, String propertyType,
            Double pricePerNight, Integer capacity, Boolean available, String imageUrl) {
        this(title, description, city, propertyType, pricePerNight, capacity, available, imageUrl, 5.0, 0);
    }

    public Property(String title, String description, String city, String propertyType,
            Double pricePerNight, Integer capacity, Boolean available, String imageUrl, Double rating) {
        this(title, description, city, propertyType, pricePerNight, capacity, available, imageUrl, rating, 0);
    }

    public Property(String title, String description, String city, String propertyType,
            Double pricePerNight, Integer capacity, Boolean available, String imageUrl, Double rating,
            Integer reviewCount) {
        this.title = title;
        this.address = title != null ? title : "";
        this.description = description;
        this.city = city;
        this.destination = city != null ? city : "";
        this.propertyType = propertyType;
        this.type = propertyType != null ? propertyType : "";
        this.pricePerNight = pricePerNight != null ? pricePerNight : 0.0;
        this.capacity = capacity != null ? capacity : 1;
        this.available = available != null ? available : true;
        this.directBooking = true;
        this.imageUrl = imageUrl;
        this.rating = rating != null ? rating : 5.0;
        this.reviewCount = reviewCount != null ? reviewCount : 0;
    }

    // Getters y Setters con sincronización defensiva

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return address != null ? address : title;
    }

    public void setAddress(String address) {
        this.address = address;
        if (this.title == null || this.title.isBlank()) {
            this.title = address;
        }
    }

    public String getTitle() {
        return title != null ? title : address;
    }

    public void setTitle(String title) {
        this.title = title;
        if (this.address == null || this.address.isBlank()) {
            this.address = title;
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCity() {
        return city != null ? city : destination;
    }

    public void setCity(String city) {
        this.city = city;
        if (this.destination == null || this.destination.isBlank()) {
            this.destination = city;
        }
    }

    public String getDestination() {
        return destination != null ? destination : city;
    }

    public void setDestination(String destination) {
        this.destination = destination;
        if (this.city == null || this.city.isBlank()) {
            this.city = destination;
        }
    }

    public String getType() {
        return type != null ? type : propertyType;
    }

    public void setType(String type) {
        this.type = type;
        if (this.propertyType == null || this.propertyType.isBlank()) {
            this.propertyType = type;
        }
    }

    public String getPropertyType() {
        return propertyType != null ? propertyType : type;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
        if (this.type == null || this.type.isBlank()) {
            this.type = propertyType;
        }
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public Boolean getAvailable() {
        return available;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available != null ? available : true;
    }

    public boolean getDirectBooking() {
        return directBooking;
    }

    public boolean isDirectBooking() {
        return directBooking;
    }

    public void setDirectBooking(boolean directBooking) {
        this.directBooking = directBooking;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Double getRating() {
        return rating != null ? rating : 5.0;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getReviewCount() {
        return reviewCount != null ? reviewCount : 0;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }
}
