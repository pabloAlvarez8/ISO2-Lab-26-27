package es.uclm.rental_platform.web;

import es.uclm.rental_platform.domain.Booking;
import es.uclm.rental_platform.domain.Property;
import es.uclm.rental_platform.persistence.User;
import es.uclm.rental_platform.persistence.UserRepository;
import es.uclm.rental_platform.repository.PropertyRepository;
import es.uclm.rental_platform.service.BookingService;
import es.uclm.rental_platform.service.exception.BookingOverlapException;
import es.uclm.rental_platform.service.exception.ExceededCapacityException;
import es.uclm.rental_platform.service.exception.InvalidBookingDatesException;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
public class BookingController {

    private final BookingService bookingService;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    public BookingController(BookingService bookingService,
                             PropertyRepository propertyRepository,
                             UserRepository userRepository) {
        this.bookingService = bookingService;
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser(HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser != null) {
            return loggedUser;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            return userRepository.findByEmail(auth.getName()).orElse(null);
        }
        return null;
    }

    @GetMapping("/properties/{propertyId}/book")
    public String showBookingForm(@PathVariable Long propertyId,
                                  HttpSession session,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        User user = getAuthenticatedUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        Property property = propertyRepository.findById(propertyId).orElse(null);
        if (property == null) {
            redirectAttributes.addFlashAttribute("error", "El alojamiento seleccionado no existe.");
            return "redirect:/";
        }

        model.addAttribute("property", property);
        model.addAttribute("defaultCheckIn", LocalDate.now().plusDays(1));
        model.addAttribute("defaultCheckOut", LocalDate.now().plusDays(4));
        model.addAttribute("defaultGuests", 2);

        return "booking/book-property";
    }

    @PostMapping("/properties/{propertyId}/book")
    public String processBooking(@PathVariable Long propertyId,
                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
                                 @RequestParam(defaultValue = "1") int guests,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        User user = getAuthenticatedUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        try {
            bookingService.createBooking(propertyId, user.getId(), checkIn, checkOut, guests);
            redirectAttributes.addFlashAttribute("success", "¡Tu reserva ha sido registrada correctamente!");
            return "redirect:/my-bookings";
        } catch (InvalidBookingDatesException | ExceededCapacityException | BookingOverlapException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/properties/" + propertyId + "/book";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error inesperado al procesar la reserva: " + e.getMessage());
            return "redirect:/properties/" + propertyId + "/book";
        }
    }

    @GetMapping("/my-bookings")
    public String showMyBookings(HttpSession session, Model model) {
        User user = getAuthenticatedUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        List<Booking> bookings = bookingService.getBookingsByTenant(user.getId());
        model.addAttribute("bookings", bookings);
        return "booking/my-bookings";
    }

    @PostMapping("/bookings/{bookingId}/cancel")
    public String cancelBooking(@PathVariable Long bookingId,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        User user = getAuthenticatedUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        try {
            bookingService.cancelBooking(bookingId, user.getId());
            redirectAttributes.addFlashAttribute("success", "La reserva ha sido cancelada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/my-bookings";
    }
}
