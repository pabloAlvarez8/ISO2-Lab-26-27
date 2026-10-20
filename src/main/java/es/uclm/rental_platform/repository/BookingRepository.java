package es.uclm.rental_platform.repository;

import es.uclm.rental_platform.domain.Booking;
import es.uclm.rental_platform.domain.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b WHERE b.property.id = :propertyId " +
           "AND b.status = :status " +
           "AND b.checkIn < :checkOut AND b.checkOut > :checkIn")
    List<Booking> findOverlappingBookings(@Param("propertyId") Long propertyId,
                                         @Param("status") BookingStatus status,
                                         @Param("checkIn") LocalDate checkIn,
                                         @Param("checkOut") LocalDate checkOut);

    List<Booking> findByTenantIdOrderByCreatedAtDesc(Long tenantId);

    List<Booking> findByPropertyId(Long propertyId);
}
