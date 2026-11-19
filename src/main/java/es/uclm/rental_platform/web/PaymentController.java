package es.uclm.rental_platform.web;

import es.uclm.rental_platform.domain.Booking;
import es.uclm.rental_platform.domain.Payment;
import es.uclm.rental_platform.domain.PaymentMethod;
import es.uclm.rental_platform.domain.Property;
import es.uclm.rental_platform.persistence.User;
import es.uclm.rental_platform.repository.BookingRepository;
import es.uclm.rental_platform.repository.PropertyRepository;
import es.uclm.rental_platform.service.PaymentService;
import es.uclm.rental_platform.service.exception.InvalidPaymentDataException;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Controller
public class PaymentController {

    private final PaymentService paymentService;
    private final BookingRepository bookingRepository;
    private final PropertyRepository propertyRepository;

    public PaymentController(PaymentService paymentService,
                             BookingRepository bookingRepository,
                             PropertyRepository propertyRepository) {
        this.paymentService = paymentService;
        this.bookingRepository = bookingRepository;
        this.propertyRepository = propertyRepository;
    }

    @GetMapping({"/pagos/nuevo", "/payments/new"})
    public String showPaymentForm(@RequestParam(required = false) Long bookingId,
                                  @RequestParam(required = false) Long propertyId,
                                  @RequestParam(required = false) BigDecimal amount,
                                  @RequestParam(required = false) String concept,
                                  HttpSession session,
                                  Model model) {

        Booking booking = null;
        Property property = null;
        BigDecimal effectiveAmount = amount;
        String effectiveConcept = concept;

        if (bookingId != null) {
            booking = bookingRepository.findById(bookingId).orElse(null);
            if (booking != null) {
                property = booking.getProperty();
                if (effectiveAmount == null) {
                    effectiveAmount = BigDecimal.valueOf(booking.getTotalPrice()).setScale(2, RoundingMode.HALF_UP);
                }
                if (effectiveConcept == null || effectiveConcept.isBlank()) {
                    effectiveConcept = "Reserva #" + booking.getId() + " - " + property.getTitle();
                }
            }
        } else if (propertyId != null) {
            property = propertyRepository.findById(propertyId).orElse(null);
            if (property != null) {
                if (effectiveAmount == null) {
                    effectiveAmount = BigDecimal.valueOf(property.getPricePerNight()).setScale(2, RoundingMode.HALF_UP);
                }
                if (effectiveConcept == null || effectiveConcept.isBlank()) {
                    effectiveConcept = "Estancia en " + property.getTitle();
                }
            }
        }

        if (effectiveAmount == null) {
            effectiveAmount = new BigDecimal("180.00");
        }
        if (effectiveConcept == null || effectiveConcept.isBlank()) {
            effectiveConcept = "Reserva de alojamiento en AlohaRent";
        }

        User loggedUser = (User) session.getAttribute("loggedUser");
        if (!model.containsAttribute("holderName") && loggedUser != null && loggedUser.getName() != null) {
            model.addAttribute("holderName", loggedUser.getName());
        }

        model.addAttribute("booking", booking);
        model.addAttribute("property", property);
        model.addAttribute("bookingId", booking != null ? booking.getId() : bookingId);
        model.addAttribute("propertyId", property != null ? property.getId() : propertyId);
        model.addAttribute("amount", effectiveAmount);
        model.addAttribute("concept", effectiveConcept);
        model.addAttribute("paymentMethods", PaymentMethod.values());
        model.addAttribute("properties", propertyRepository.findAll());

        return "payment/payment-form";
    }

    @PostMapping({"/pagos", "/payments"})
    public String processPayment(@RequestParam(required = false) Long bookingId,
                                 @RequestParam(required = false) Long propertyId,
                                 @RequestParam(required = false) BigDecimal amount,
                                 @RequestParam(required = false) String paymentMethod,
                                 @RequestParam(required = false) String concept,
                                 @RequestParam(required = false) String holderName,
                                 @RequestParam(required = false) String cardNumber,
                                 @RequestParam(required = false) String expiryDate,
                                 @RequestParam(required = false) String cvv,
                                 @RequestParam(required = false) String bizumPhone,
                                 @RequestParam(required = false) String paypalEmail,
                                 RedirectAttributes redirectAttributes) {
        try {
            Payment savedPayment = paymentService.processPayment(
                    bookingId, propertyId, amount, paymentMethod, concept,
                    holderName, cardNumber, expiryDate, cvv, bizumPhone, paypalEmail
            );
            redirectAttributes.addFlashAttribute(
                    "success",
                    "¡Pago #" + savedPayment.getId() + " por importe de " + savedPayment.getAmount() + " € procesado y completado con éxito!"
            );
            return "redirect:/pagos";
        } catch (InvalidPaymentDataException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("holderName", holderName);
            redirectAttributes.addFlashAttribute("selectedMethod", paymentMethod);
            return buildRetryRedirectUrl(bookingId, propertyId, amount);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo procesar el pago: " + e.getMessage());
            return buildRetryRedirectUrl(bookingId, propertyId, amount);
        }
    }

    @GetMapping({"/pagos", "/payments"})
    public String listPayments(Model model) {
        List<Payment> payments = paymentService.getAllPayments();
        model.addAttribute("payments", payments);
        return "payment/payment-list";
    }

    private String buildRetryRedirectUrl(Long bookingId, Long propertyId, BigDecimal amount) {
        StringBuilder url = new StringBuilder("redirect:/pagos/nuevo");
        boolean hasParam = false;
        if (bookingId != null) {
            url.append("?bookingId=").append(bookingId);
            hasParam = true;
        } else if (propertyId != null) {
            url.append("?propertyId=").append(propertyId);
            hasParam = true;
        }
        if (amount != null) {
            url.append(hasParam ? "&" : "?").append("amount=").append(amount);
        }
        return url.toString();
    }
}

