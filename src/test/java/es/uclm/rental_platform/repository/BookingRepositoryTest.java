package es.uclm.rental_platform.repository;

import es.uclm.rental_platform.domain.Booking;
import es.uclm.rental_platform.domain.BookingStatus;
import es.uclm.rental_platform.domain.Property;
import es.uclm.rental_platform.persistence.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User sampleTenant;
    private Property sampleProperty;

    @BeforeEach
    void setUp() {
        sampleTenant = new User("Inquilino Prueba", "tenant@example.com", "hash12345", "TENANT");
        entityManager.persistAndFlush(sampleTenant);

        sampleProperty = new Property("Calle Mayor 10, Madrid", 85.0, "Apartamento", "Madrid", 4, true);
        entityManager.persistAndFlush(sampleProperty);
    }

    @Test
    void save_PersistsBookingAndGeneratesId() {
        LocalDate checkIn = LocalDate.of(2026, 12, 1);
        LocalDate checkOut = LocalDate.of(2026, 12, 5);
        Booking booking = new Booking(sampleProperty, sampleTenant, checkIn, checkOut, 2, 340.0, BookingStatus.CONFIRMED);

        Booking saved = bookingRepository.save(booking);

        assertNotNull(saved.getId());
        assertEquals(sampleProperty.getId(), saved.getProperty().getId());
        assertEquals(sampleTenant.getId(), saved.getTenant().getId());
        assertEquals(checkIn, saved.getCheckIn());
        assertEquals(checkOut, saved.getCheckOut());
        assertEquals(2, saved.getGuests());
        assertEquals(340.0, saved.getTotalPrice());
        assertEquals(BookingStatus.CONFIRMED, saved.getStatus());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void findOverlappingBookings_WhenDatesOverlap_ReturnsOverlappingBooking() {
        Booking existing = new Booking(sampleProperty, sampleTenant,
                LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 15),
                2, 425.0, BookingStatus.CONFIRMED);
        entityManager.persistAndFlush(existing);

        // Intento de reserva que se solapa parcialmente (del 12 al 17)
        List<Booking> overlaps = bookingRepository.findOverlappingBookings(
                sampleProperty.getId(),
                BookingStatus.CONFIRMED,
                LocalDate.of(2026, 11, 12),
                LocalDate.of(2026, 11, 17)
        );

        assertEquals(1, overlaps.size());
        assertEquals(existing.getId(), overlaps.get(0).getId());
    }

    @Test
    void findOverlappingBookings_WhenCheckInMatchesExistingCheckOut_ReturnsEmpty() {
        Booking existing = new Booking(sampleProperty, sampleTenant,
                LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 15),
                2, 425.0, BookingStatus.CONFIRMED);
        entityManager.persistAndFlush(existing);

        // Reserva que entra el mismo día que la anterior sale (día 15) -> no debe solapar
        List<Booking> overlaps = bookingRepository.findOverlappingBookings(
                sampleProperty.getId(),
                BookingStatus.CONFIRMED,
                LocalDate.of(2026, 11, 15),
                LocalDate.of(2026, 11, 20)
        );

        assertTrue(overlaps.isEmpty());
    }

    @Test
    void findOverlappingBookings_WhenCheckOutMatchesExistingCheckIn_ReturnsEmpty() {
        Booking existing = new Booking(sampleProperty, sampleTenant,
                LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 15),
                2, 425.0, BookingStatus.CONFIRMED);
        entityManager.persistAndFlush(existing);

        // Reserva que sale el mismo día que la siguiente entra (día 10) -> no debe solapar
        List<Booking> overlaps = bookingRepository.findOverlappingBookings(
                sampleProperty.getId(),
                BookingStatus.CONFIRMED,
                LocalDate.of(2026, 11, 5),
                LocalDate.of(2026, 11, 10)
        );

        assertTrue(overlaps.isEmpty());
    }

    @Test
    void findOverlappingBookings_WhenExistingBookingIsCancelled_ReturnsEmpty() {
        Booking cancelled = new Booking(sampleProperty, sampleTenant,
                LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 15),
                2, 425.0, BookingStatus.CANCELLED);
        entityManager.persistAndFlush(cancelled);

        // Si la reserva previa está CANCELLED, el intervalo debe estar disponible
        List<Booking> overlaps = bookingRepository.findOverlappingBookings(
                sampleProperty.getId(),
                BookingStatus.CONFIRMED,
                LocalDate.of(2026, 11, 11),
                LocalDate.of(2026, 11, 14)
        );

        assertTrue(overlaps.isEmpty());
    }

    @Test
    void findByTenantIdOrderByCreatedAtDesc_ReturnsTenantBookings() {
        Booking b1 = new Booking(sampleProperty, sampleTenant,
                LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 4),
                1, 255.0, BookingStatus.CONFIRMED);
        Booking b2 = new Booking(sampleProperty, sampleTenant,
                LocalDate.of(2026, 11, 20), LocalDate.of(2026, 11, 25),
                2, 425.0, BookingStatus.CONFIRMED);
        entityManager.persistAndFlush(b1);
        entityManager.persistAndFlush(b2);

        List<Booking> list = bookingRepository.findByTenantIdOrderByCreatedAtDesc(sampleTenant.getId());

        assertEquals(2, list.size());
    }
}
