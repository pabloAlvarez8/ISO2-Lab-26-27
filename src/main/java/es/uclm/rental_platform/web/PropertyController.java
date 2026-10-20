package es.uclm.rental_platform.web;

import es.uclm.rental_platform.domain.Property;
import es.uclm.rental_platform.domain.Review;
import es.uclm.rental_platform.persistence.User;
import es.uclm.rental_platform.repository.PropertyRepository;
import es.uclm.rental_platform.service.PropertyService;
import es.uclm.rental_platform.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
public class PropertyController {

    private final PropertyService propertyService;
    private final PropertyRepository propertyRepository;
    private final ReviewService reviewService;

    public PropertyController(PropertyService propertyService,
                              PropertyRepository propertyRepository,
                              ReviewService reviewService) {
        this.propertyService = propertyService;
        this.propertyRepository = propertyRepository;
        this.reviewService = reviewService;
    }

    @GetMapping("/inmuebles/alta")
    public String showRegistrationForm(HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || !"LANDLORD".equals(loggedUser.getRole())) {
            return "redirect:/login"; 
        }
        return "properties";
    }

    @PostMapping("/inmuebles/alta")
    public String registerProperty(
            String address, 
            double pricePerNight, 
            String type, 
            String destination, 
            int capacity, 
            @RequestParam(required = false, defaultValue = "false") boolean directBooking,
            RedirectAttributes redirectAttributes) {
        
        try {
            Property newProperty = new Property(address, pricePerNight, type, destination, capacity, directBooking);
            propertyService.registerProperty(newProperty);
            redirectAttributes.addFlashAttribute("success", "¡Inmueble publicado con éxito!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/inmuebles/alta"; 
    }

    @GetMapping("/properties/{propertyId}")
    public String showPropertyDetail(
            @PathVariable Long propertyId,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        Property property = propertyRepository.findById(propertyId).orElse(null);
        if (property == null) {
            redirectAttributes.addFlashAttribute("error", "El alojamiento seleccionado no existe.");
            return "redirect:/";
        }

        List<Review> reviews = reviewService.getReviewsByProperty(propertyId);
        long reviewCount = reviewService.countReviewsByProperty(propertyId);

        model.addAttribute("property", property);
        model.addAttribute("reviews", reviews);
        model.addAttribute("reviewCount", reviewCount);
        model.addAttribute("defaultCheckIn", LocalDate.now().plusDays(1));
        model.addAttribute("defaultCheckOut", LocalDate.now().plusDays(4));
        model.addAttribute("defaultGuests", 2);

        return "property-detail";
    }

    @PostMapping("/properties/{propertyId}/reviews")
    public String addReview(
            @PathVariable Long propertyId,
            @RequestParam double rating,
            @RequestParam String comment,
            @RequestParam(required = false) String authorName,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User loggedUser = (User) session.getAttribute("loggedUser");
        Long userId = loggedUser != null ? loggedUser.getId() : null;
        String effectiveAuthor = (loggedUser != null && loggedUser.getName() != null) ? loggedUser.getName() : authorName;

        try {
            reviewService.addReview(propertyId, userId, effectiveAuthor, rating, comment);
            redirectAttributes.addFlashAttribute("reviewSuccess", "¡Tu valoración y reseña se han publicado correctamente!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("reviewError", e.getMessage());
        }

        return "redirect:/properties/" + propertyId + "#reviews";
    }
}