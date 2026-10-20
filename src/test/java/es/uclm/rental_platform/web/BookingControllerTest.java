package es.uclm.rental_platform.web;

import es.uclm.rental_platform.domain.Booking;
import es.uclm.rental_platform.domain.BookingStatus;
import es.uclm.rental_platform.domain.Property;
import es.uclm.rental_platform.persistence.User;
import es.uclm.rental_platform.persistence.UserRepository;
import es.uclm.rental_platform.repository.BookingRepository;
import es.uclm.rental_platform.repository.PropertyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
class BookingControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private MockMvc mockMvc;
    private User testUser;
    private Property testProperty;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        bookingRepository.deleteAll();
        propertyRepository.deleteAll();
        userRepository.deleteAll();

        testUser = userRepository.save(new User("Inquilino Web", "inquilino@test.com", "hash12345", "TENANT"));
        testProperty = propertyRepository.save(new Property("Gran Vía 28, Madrid", 120.0, "Ático", "Madrid", 3, true));
    }

    private MockHttpSession createAuthSession() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loggedUser", testUser);
        return session;
    }

    @Test
    void showBookingForm_WhenUnauthenticated_RedirectsToLogin() throws Exception {
        mockMvc.perform(get("/properties/" + testProperty.getId() + "/book"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(username = "inquilino@test.com")
    void showBookingForm_WhenAuthenticated_ReturnsFormView() throws Exception {
        mockMvc.perform(get("/properties/" + testProperty.getId() + "/book").session(createAuthSession()))
                .andExpect(status().isOk())
                .andExpect(view().name("booking/book-property"))
                .andExpect(model().attributeExists("property"))
                .andExpect(model().attributeExists("defaultCheckIn"))
                .andExpect(model().attributeExists("defaultCheckOut"));
    }

    @Test
    @WithMockUser(username = "inquilino@test.com")
    void processBooking_WhenValid_CreatesBookingAndRedirectsToMyBookings() throws Exception {
        LocalDate checkIn = LocalDate.now().plusDays(2);
        LocalDate checkOut = LocalDate.now().plusDays(5);

        mockMvc.perform(post("/properties/" + testProperty.getId() + "/book")
                        .session(createAuthSession())
                        .with(csrf())
                        .param("checkIn", checkIn.toString())
                        .param("checkOut", checkOut.toString())
                        .param("guests", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/my-bookings"))
                .andExpect(flash().attributeExists("success"));

        assertEquals(1, bookingRepository.count());
    }

    @Test
    @WithMockUser(username = "inquilino@test.com")
    void processBooking_WhenDatesOverlap_RedirectsToFormWithError() throws Exception {
        LocalDate checkIn = LocalDate.now().plusDays(3);
        LocalDate checkOut = LocalDate.now().plusDays(6);

        // Crear una reserva existente confirmada
        bookingRepository.save(new Booking(testProperty, testUser, checkIn, checkOut, 2, 360.0, BookingStatus.CONFIRMED));

        // Intento de segunda reserva en fechas solapadas
        mockMvc.perform(post("/properties/" + testProperty.getId() + "/book")
                        .session(createAuthSession())
                        .with(csrf())
                        .param("checkIn", checkIn.plusDays(1).toString())
                        .param("checkOut", checkOut.plusDays(2).toString())
                        .param("guests", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/properties/" + testProperty.getId() + "/book"))
                .andExpect(flash().attributeExists("error"));

        assertEquals(1, bookingRepository.count());
    }

    @Test
    @WithMockUser(username = "inquilino@test.com")
    void showMyBookings_WhenAuthenticated_ReturnsBookingsList() throws Exception {
        bookingRepository.save(new Booking(testProperty, testUser,
                LocalDate.now().plusDays(10), LocalDate.now().plusDays(14),
                2, 480.0, BookingStatus.CONFIRMED));

        mockMvc.perform(get("/my-bookings").session(createAuthSession()))
                .andExpect(status().isOk())
                .andExpect(view().name("booking/my-bookings"))
                .andExpect(model().attribute("bookings", hasSize(1)));
    }

    @Test
    @WithMockUser(username = "inquilino@test.com")
    void cancelBooking_WhenRequestedByTenant_CancelsBookingAndRedirects() throws Exception {
        Booking booking = bookingRepository.save(new Booking(testProperty, testUser,
                LocalDate.now().plusDays(10), LocalDate.now().plusDays(14),
                2, 480.0, BookingStatus.CONFIRMED));

        mockMvc.perform(post("/bookings/" + booking.getId() + "/cancel")
                        .session(createAuthSession())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/my-bookings"))
                .andExpect(flash().attributeExists("success"));

        Booking updated = bookingRepository.findById(booking.getId()).orElseThrow();
        assertEquals(BookingStatus.CANCELLED, updated.getStatus());
    }
}
