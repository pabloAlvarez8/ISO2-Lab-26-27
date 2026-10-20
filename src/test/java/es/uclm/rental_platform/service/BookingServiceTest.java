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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingService bookingService;

    private Property sampleProperty;
    private User sampleTenant;

    @BeforeEach
    void setUp() {
        sampleProperty = new Property("Calle Luna 5, Toledo", 100.0, "Piso", "Toledo", 4, true);
        sampleProperty.setAddress("Calle Luna 5, Toledo");
        // Establecer id simulado mediante reflection o fixture
        try {
            var idField = Property.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(sampleProperty, 10L);
        } catch (Exception ignored) {}

        sampleTenant = new User("María López", "maria@example.com", "hashPassword", "TENANT");
        sampleTenant.setId(1L);
    }

    @Test
    void createBooking_Success_WhenDirectBookingTrue_ConfirmsAndCalculatesPrice() {
        LocalDate checkIn = LocalDate.now().plusDays(5);
        LocalDate checkOut = LocalDate.now().plusDays(8); // 3 noches

        when(propertyRepository.findById(10L)).thenReturn(Optional.of(sampleProperty));
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleTenant));
        when(bookingRepository.findOverlappingBookings(10L, BookingStatus.CONFIRMED, checkIn, checkOut))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Booking result = bookingService.createBooking(10L, 1L, checkIn, checkOut, 2);

        assertNotNull(result);
        assertEquals(300.0, result.getTotalPrice(), 0.001);
        assertEquals(BookingStatus.CONFIRMED, result.getStatus());
        assertEquals(2, result.getGuests());
        assertEquals(sampleProperty, result.getProperty());
        assertEquals(sampleTenant, result.getTenant());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void createBooking_Success_WhenDirectBookingFalse_SetsPendingStatus() {
        sampleProperty.setDirectBooking(false);
        LocalDate checkIn = LocalDate.now().plusDays(2);
        LocalDate checkOut = LocalDate.now().plusDays(4); // 2 noches

        when(propertyRepository.findById(10L)).thenReturn(Optional.of(sampleProperty));
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleTenant));
        when(bookingRepository.findOverlappingBookings(10L, BookingStatus.CONFIRMED, checkIn, checkOut))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Booking result = bookingService.createBooking(10L, 1L, checkIn, checkOut, 3);

        assertNotNull(result);
        assertEquals(200.0, result.getTotalPrice(), 0.001);
        assertEquals(BookingStatus.PENDING, result.getStatus());
    }

    @Test
    void createBooking_ThrowsInvalidBookingDatesException_WhenDatesAreNull() {
        assertThrows(InvalidBookingDatesException.class, () ->
                bookingService.createBooking(10L, 1L, null, LocalDate.now().plusDays(2), 2));
        assertThrows(InvalidBookingDatesException.class, () ->
                bookingService.createBooking(10L, 1L, LocalDate.now().plusDays(1), null, 2));
    }

    @Test
    void createBooking_ThrowsInvalidBookingDatesException_WhenCheckInNotBeforeCheckOut() {
        LocalDate date = LocalDate.now().plusDays(2);
        assertThrows(InvalidBookingDatesException.class, () ->
                bookingService.createBooking(10L, 1L, date, date, 2));

        assertThrows(InvalidBookingDatesException.class, () ->
                bookingService.createBooking(10L, 1L, date, date.minusDays(1), 2));
    }

    @Test
    void createBooking_ThrowsInvalidBookingDatesException_WhenCheckInInThePast() {
        LocalDate pastCheckIn = LocalDate.now().minusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(2);

        assertThrows(InvalidBookingDatesException.class, () ->
                bookingService.createBooking(10L, 1L, pastCheckIn, checkOut, 2));
    }

    @Test
    void createBooking_ThrowsExceededCapacityException_WhenGuestsZeroOrNegative() {
        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(3);

        when(propertyRepository.findById(10L)).thenReturn(Optional.of(sampleProperty));

        assertThrows(ExceededCapacityException.class, () ->
                bookingService.createBooking(10L, 1L, checkIn, checkOut, 0));
        assertThrows(ExceededCapacityException.class, () ->
                bookingService.createBooking(10L, 1L, checkIn, checkOut, -1));
    }

    @Test
    void createBooking_ThrowsExceededCapacityException_WhenGuestsExceedsCapacity() {
        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(3);

        when(propertyRepository.findById(10L)).thenReturn(Optional.of(sampleProperty)); // capacity: 4

        assertThrows(ExceededCapacityException.class, () ->
                bookingService.createBooking(10L, 1L, checkIn, checkOut, 5));
    }

    @Test
    void createBooking_ThrowsBookingOverlapException_WhenOverlappingBookingExists() {
        LocalDate checkIn = LocalDate.now().plusDays(2);
        LocalDate checkOut = LocalDate.now().plusDays(5);

        when(propertyRepository.findById(10L)).thenReturn(Optional.of(sampleProperty));
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleTenant));

        Booking existingBooking = new Booking(sampleProperty, sampleTenant, checkIn, checkOut, 2, 300.0, BookingStatus.CONFIRMED);
        when(bookingRepository.findOverlappingBookings(10L, BookingStatus.CONFIRMED, checkIn, checkOut))
                .thenReturn(List.of(existingBooking));

        assertThrows(BookingOverlapException.class, () ->
                bookingService.createBooking(10L, 1L, checkIn, checkOut, 2));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void cancelBooking_Success_WhenRequestedByTenant() {
        LocalDate checkIn = LocalDate.now().plusDays(2);
        LocalDate checkOut = LocalDate.now().plusDays(5);
        Booking booking = new Booking(sampleProperty, sampleTenant, checkIn, checkOut, 2, 300.0, BookingStatus.CONFIRMED);
        booking.setId(99L);

        when(bookingRepository.findById(99L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Booking cancelled = bookingService.cancelBooking(99L, 1L);

        assertEquals(BookingStatus.CANCELLED, cancelled.getStatus());
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    void cancelBooking_ThrowsUnauthorized_WhenUserIsNotTheTenant() {
        LocalDate checkIn = LocalDate.now().plusDays(2);
        LocalDate checkOut = LocalDate.now().plusDays(5);
        Booking booking = new Booking(sampleProperty, sampleTenant, checkIn, checkOut, 2, 300.0, BookingStatus.CONFIRMED);
        booking.setId(99L);

        when(bookingRepository.findById(99L)).thenReturn(Optional.of(booking));

        assertThrows(UnauthorizedBookingActionException.class, () ->
                bookingService.cancelBooking(99L, 999L)); // Otro usuario no autorizado

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void getBookingById_ThrowsBookingNotFoundException_WhenNotFound() {
        when(bookingRepository.findById(123L)).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class, () ->
                bookingService.getBookingById(123L));
    }
}
