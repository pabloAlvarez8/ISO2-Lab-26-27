package es.uclm.rental_platform.service;

import es.uclm.rental_platform.domain.Booking;
import es.uclm.rental_platform.domain.BookingStatus;
import es.uclm.rental_platform.domain.Payment;
import es.uclm.rental_platform.domain.PaymentMethod;
import es.uclm.rental_platform.domain.PaymentStatus;
import es.uclm.rental_platform.domain.Property;
import es.uclm.rental_platform.repository.BookingRepository;
import es.uclm.rental_platform.repository.PaymentRepository;
import es.uclm.rental_platform.repository.PropertyRepository;
import es.uclm.rental_platform.service.exception.InvalidPaymentDataException;
import es.uclm.rental_platform.service.exception.PaymentNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final PropertyRepository propertyRepository;

    public PaymentService(PaymentRepository paymentRepository,
                          BookingRepository bookingRepository,
                          PropertyRepository propertyRepository) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.propertyRepository = propertyRepository;
    }

    @Transactional
    public Payment processPayment(Long bookingId,
                                  Long propertyId,
                                  BigDecimal amount,
                                  String paymentMethodRaw,
                                  String concept,
                                  String holderName,
                                  String cardNumber,
                                  String expiryDate,
                                  String cvv,
                                  String bizumPhone,
                                  String paypalEmail) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidPaymentDataException("El importe del pago debe ser obligatorio y estrictamente mayor que 0 €.");
        }

        if (paymentMethodRaw == null || paymentMethodRaw.isBlank()) {
            throw new InvalidPaymentDataException("Debes seleccionar un método de pago válido.");
        }

        PaymentMethod paymentMethod;
        try {
            paymentMethod = PaymentMethod.valueOf(paymentMethodRaw.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidPaymentDataException("El método de pago seleccionado no es válido.");
        }

        if (holderName == null || holderName.isBlank()) {
            throw new InvalidPaymentDataException("El nombre del titular del pago es obligatorio.");
        }

        String sanitizedReference = validateMethodFieldsAndBuildReference(
                paymentMethod, cardNumber, expiryDate, cvv, bizumPhone, paypalEmail
        );

        Booking booking = null;
        Property property = null;

        if (bookingId != null) {
            booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new InvalidPaymentDataException("La reserva asociada no existe con ID: " + bookingId));
            property = booking.getProperty();
        } else if (propertyId != null) {
            property = propertyRepository.findById(propertyId)
                    .orElseThrow(() -> new InvalidPaymentDataException("El alojamiento asociado no existe con ID: " + propertyId));
        }

        String effectiveConcept = (concept != null && !concept.isBlank())
                ? concept.trim()
                : buildDefaultConcept(booking, property);

        BigDecimal normalizedAmount = amount.setScale(2, RoundingMode.HALF_UP);

        Payment payment = new Payment(
                normalizedAmount,
                paymentMethod,
                PaymentStatus.COMPLETADO,
                effectiveConcept,
                holderName.trim(),
                sanitizedReference,
                booking,
                property
        );

        if (booking != null && booking.getStatus() == BookingStatus.PENDING) {
            booking.setStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);
        }

        return paymentRepository.save(payment);
    }

    private String validateMethodFieldsAndBuildReference(PaymentMethod method,
                                                         String cardNumber,
                                                         String expiryDate,
                                                         String cvv,
                                                         String bizumPhone,
                                                         String paypalEmail) {
        switch (method) {
            case TARJETA -> {
                String cleanCard = cardNumber != null ? cardNumber.replaceAll("\\s+", "") : "";
                if (!cleanCard.matches("\\d{13,19}")) {
                    throw new InvalidPaymentDataException("El número de tarjeta debe contener entre 13 y 19 dígitos numéricos.");
                }
                if (expiryDate == null || !expiryDate.trim().matches("(0[1-9]|1[0-2])/\\d{2}")) {
                    throw new InvalidPaymentDataException("La fecha de caducidad de la tarjeta debe tener el formato MM/AA (ej. 12/28).");
                }
                if (cvv == null || !cvv.trim().matches("\\d{3,4}")) {
                    throw new InvalidPaymentDataException("El código de seguridad CVV debe tener 3 o 4 dígitos.");
                }
                String lastFour = cleanCard.substring(cleanCard.length() - 4);
                return "Tarjeta •••• " + lastFour;
            }
            case BIZUM -> {
                String cleanPhone = bizumPhone != null ? bizumPhone.replaceAll("\\s+", "") : "";
                if (!cleanPhone.matches("\\+?\\d{9,12}")) {
                    throw new InvalidPaymentDataException("El número de teléfono para Bizum debe tener al menos 9 dígitos válidos.");
                }
                String lastThree = cleanPhone.substring(cleanPhone.length() - 3);
                return "Bizum ••• " + lastThree;
            }
            case PAYPAL -> {
                if (paypalEmail == null || !paypalEmail.trim().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
                    throw new InvalidPaymentDataException("Debes indicar un correo electrónico válido para tu cuenta de PayPal.");
                }
                return "PayPal (" + paypalEmail.trim() + ")";
            }
            case TRANSFERENCIA -> {
                return "Transferencia verificada";
            }
        }
        return "Pago verificado";
    }

    private String buildDefaultConcept(Booking booking, Property property) {
        if (booking != null && booking.getProperty() != null) {
            return "Reserva #" + booking.getId() + " - " + booking.getProperty().getTitle();
        }
        if (property != null) {
            return "Estancia en " + property.getTitle();
        }
        return "Reserva de alojamiento en AlohaRent";
    }

    @Transactional(readOnly = true)
    public List<Payment> getAllPayments() {
        return paymentRepository.findAllWithDetailsOrderByPaymentDateDesc();
    }

    @Transactional(readOnly = true)
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Pago no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByBooking(Long bookingId) {
        return paymentRepository.findByBookingIdOrderByPaymentDateDesc(bookingId);
    }

    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByStatusOrderByPaymentDateDesc(status);
    }
}

