package es.uclm.rental_platform.web;

import es.uclm.rental_platform.domain.Property;
import es.uclm.rental_platform.service.PropertySearchResult;
import es.uclm.rental_platform.service.PropertyService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
public class HomeController {

    private final PropertyService propertyService;

    public HomeController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/search")
    public String search(
            @RequestParam(name = "destination", required = false) String destination,
            @RequestParam(name = "checkin", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkin,
            @RequestParam(name = "checkout", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkout,
            @RequestParam(name = "guests", required = false) Integer guests,
            @RequestParam(name = "propertyType", required = false) String propertyType,
            @RequestParam(name = "maxPrice", required = false) Double maxPrice,
            @RequestParam(name = "minRating", required = false) Double minRating,
            @RequestParam(name = "minReviews", required = false) Integer minReviews,
            @RequestParam(name = "q", required = false) String q,
            Model model
    ) {
        String effectiveDestination = destination;
        if ((effectiveDestination == null || effectiveDestination.isBlank()) && q != null && !q.isBlank()) {
            effectiveDestination = q;
        }

        PropertySearchResult result = propertyService.search(
                effectiveDestination,
                checkin,
                checkout,
                guests,
                propertyType,
                maxPrice,
                minRating,
                minReviews,
                q
        );

        List<Property> allProperties = propertyService.getAllAvailableProperties();

        model.addAttribute("properties", allProperties);
        model.addAttribute("filteredProperties", result.getProperties());
        model.addAttribute("searchError", result.getErrorMessage());
        model.addAttribute("destination", effectiveDestination != null ? effectiveDestination.trim() : "");
        model.addAttribute("checkin", checkin);
        model.addAttribute("checkout", checkout);
        model.addAttribute("guests", guests);
        model.addAttribute("propertyType", propertyType != null ? propertyType.trim() : "");
        model.addAttribute("maxPrice", maxPrice != null ? maxPrice : 400.0);
        model.addAttribute("minRating", minRating != null ? minRating : 0.0);
        model.addAttribute("minReviews", minReviews != null ? minReviews : 0);
        model.addAttribute("q", q != null ? q.trim() : "");

        return "search";
    }
}
