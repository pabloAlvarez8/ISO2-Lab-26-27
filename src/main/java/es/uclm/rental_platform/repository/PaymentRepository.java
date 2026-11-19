package es.uclm.rental_platform.repository;

import es.uclm.rental_platform.domain.Payment;
import es.uclm.rental_platform.domain.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT p FROM Payment p LEFT JOIN FETCH p.booking LEFT JOIN FETCH p.property ORDER BY p.paymentDate DESC")
    List<Payment> findAllWithDetailsOrderByPaymentDateDesc();

    List<Payment> findAllByOrderByPaymentDateDesc();

    List<Payment> findByStatusOrderByPaymentDateDesc(PaymentStatus status);

    List<Payment> findByBookingIdOrderByPaymentDateDesc(Long bookingId);

    List<Payment> findByPropertyIdOrderByPaymentDateDesc(Long propertyId);
}

