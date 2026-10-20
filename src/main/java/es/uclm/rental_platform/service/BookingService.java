package es.uclm.rental_platform.service;

import es.uclm.rental_platform.domain.Booking;
import es.uclm.rental_platform.domain.BookingStatus;
import es.uclm.rental_platform.domain.Property;
import es.uclm.rental_platform.persistence.User;
import es.uclm.rental_platform.persistence.UserRepository;
import es.uclm.rental_platform.repository.BookingRepository;
import es.uclm.rental_platform.repository.PropertyRepository;
import es.uclm.rental_platform.service.exception.BookingNotFoundException;
import es.uclm.rental_platform.service.exception.BookingOverlapException;
import es.uclm.rental_platform.service.exception.ExceededCapacityException;
import es.uclm.rental_platform.service.exception.InvalidBookingDatesException;
import es.uclm.rental_platform.service.exception.UnauthorizedBookingActionException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    public BookingService(BookingRepository bookingRepository,
                          PropertyRepository propertyRepository,
                          UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Booking createBooking(Long propertyId, Long tenantId, LocalDate checkIn, LocalDate checkOut, int guests) {
        // 1. Validar fechas de entrada y salida
        if (checkIn == null || checkOut == null) {
            throw new InvalidBookingDatesException("Las fechas de entrada y salida son obligatorias.");
        }
        if (!checkIn.isBefore(checkOut)) {
            throw new InvalidBookingDatesException("La fecha de entrada debe ser estrictamente anterior a la de salida.");
        }
        if (checkIn.isBefore(LocalDate.now())) {
            throw new InvalidBookingDatesException("No se permiten reservas con fecha de entrada en el pasado.");
        }

        // 2. Buscar y verificar el alojamiento
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Alojamiento no encontrado con id: " + propertyId));

        // 3. Validar aforo y capacidad
        if (guests <= 0) {
            throw new ExceededCapacityException("El número de huéspedes debe ser mayor que 0.");
        }
        if (guests > property.getCapacity()) {
            throw new ExceededCapacityException("El número de huéspedes (" + guests + ") supera la capacidad máxima de " + property.getCapacity() + " personas.");
        }

        // 4. Buscar y verificar el inquilino
        User tenant = userRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario inquilino no encontrado con id: " + tenantId));

        // 5. Invariante de dominio: Prevención estricta de solapamiento de fechas
        List<Booking> overlaps = bookingRepository.findOverlappingBookings(propertyId, BookingStatus.CONFIRMED, checkIn, checkOut);
        if (!overlaps.isEmpty()) {
            throw new BookingOverlapException("El alojamiento no está disponible en las fechas seleccionadas.");
        }

        // 6. Cálculo exacto del importe total
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        double totalPrice = nights * property.getPricePerNight();

        // 7. Determinar estado según modalidad de reserva directa
        BookingStatus status = property.getDirectBooking() ? BookingStatus.CONFIRMED : BookingStatus.PENDING;

        Booking booking = new Booking(property, tenant, checkIn, checkOut, guests, totalPrice, status);
        return bookingRepository.save(booking);
    }

    @Transactional(readOnly = true)
    public List<Booking> getBookingsByTenant(Long tenantId) {
        return bookingRepository.findByTenantIdOrderByCreatedAtDesc(tenantId);
    }

    @Transactional(readOnly = true)
    public List<Booking> getBookingsByProperty(Long propertyId) {
        return bookingRepository.findByPropertyId(propertyId);
    }

    @Transactional(readOnly = true)
    public Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Reserva no encontrada con id: " + bookingId));
    }

    @Transactional
    public Booking cancelBooking(Long bookingId, Long userId) {
        Booking booking = getBookingById(bookingId);

        if (!booking.getTenant().getId().equals(userId)) {
            throw new UnauthorizedBookingActionException("No tienes permiso para cancelar esta reserva.");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        return bookingRepository.save(booking);
    }
}
