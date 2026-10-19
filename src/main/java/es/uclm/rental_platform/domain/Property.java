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

    // From UML diagram:
    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private double pricePerNight;

    // From domain description:
    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String destination;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    private boolean directBooking; // true para reserva inmediata, false para solicitud

    protected Property() {
        // Empty constructor required by JPA
    }

    public Property(String address, double pricePerNight, String type, String destination, int capacity, boolean directBooking) {
        this.address = address;
        this.pricePerNight = pricePerNight;
        this.type = type;
        this.destination = destination;
        this.capacity = capacity;
        this.directBooking = directBooking;
    }

    // Getters
    public Long getId() { return id; }
    public String getAddress() { return address; }
    public double getPricePerNight() { return pricePerNight; }
    public String getType() { return type; }
    public String getDestination() { return destination; }
    public int getCapacity() { return capacity; }
    public boolean getDirectBooking() { return directBooking; }


    // --- SETTERS (Necesarios para el Update del CRUD) ---
    public void setAddress(String address) { this.address = address; }
    public void setPricePerNight(double pricePerNight) { this.pricePerNight = pricePerNight; }
    public void setType(String type) { this.type = type; }
    public void setDestination(String destination) { this.destination = destination; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public void setDirectBooking(boolean directBooking) { this.directBooking = directBooking; }
}