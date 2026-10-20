package es.uclm.rental_platform.web;

import es.uclm.rental_platform.domain.Property;
import es.uclm.rental_platform.service.PropertySearchResult;
import es.uclm.rental_platform.service.PropertyService;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
public class HomeController {

    private final PropertyService propertyService;

    public HomeController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @GetMapping("/")
    public String home(Model model) {
        List<Property> properties = propertyService.getAllProperties();
        model.addAttribute("inmuebles", properties);
        model.addAttribute("properties", properties);
        return "home";
    }

    @GetMapping("/search")
    public String search(
            @RequestParam(name = "destination", required = false) String destination,
            @RequestParam(name = "checkin", required = false) String checkin,
            @RequestParam(name = "fechaEntrada", required = false) String fechaEntrada,
            @RequestParam(name = "checkout", required = false) String checkout,
            @RequestParam(name = "fechaSalida", required = false) String fechaSalida,
            @RequestParam(name = "guests", required = false) Integer guests,
            @RequestParam(name = "propertyType", required = false) String propertyType,
            @RequestParam(name = "maxPrice", required = false) Double maxPrice,
            @RequestParam(name = "minRating", required = false) Double minRating,
            @RequestParam(name = "minReviews", required = false) Integer minReviews,
            @RequestParam(name = "q", required = false) String q,
            Model model
    ) {
        String effectiveDestination = (destination != null && !destination.isBlank()) ? destination : q;
        String rawCheckin = (checkin != null && !checkin.isBlank()) ? checkin : fechaEntrada;
        String rawCheckout = (checkout != null && !checkout.isBlank()) ? checkout : fechaSalida;

        LocalDate parsedCheckin = propertyService.parseDate(rawCheckin);
        LocalDate parsedCheckout = propertyService.parseDate(rawCheckout);

        PropertySearchResult result = propertyService.search(
                effectiveDestination,
                parsedCheckin,
                parsedCheckout,
                guests,
                propertyType,
                maxPrice,
                minRating,
                minReviews,
                q
        );

        List<Property> allProperties = propertyService.getAllAvailableProperties();

        model.addAttribute("properties", allProperties);
        model.addAttribute("inmuebles", allProperties);
        model.addAttribute("filteredProperties", result.getProperties());
        model.addAttribute("searchError", result.getErrorMessage());
        model.addAttribute("destination", effectiveDestination != null ? effectiveDestination.trim() : "");
        model.addAttribute("checkin", parsedCheckin != null ? parsedCheckin.toString() : "");
        model.addAttribute("checkout", parsedCheckout != null ? parsedCheckout.toString() : "");
        model.addAttribute("guests", guests);
        model.addAttribute("propertyType", propertyType != null ? propertyType.trim() : "");
        model.addAttribute("maxPrice", maxPrice != null ? maxPrice : 400.0);
        model.addAttribute("minRating", minRating != null ? minRating : 0.0);
        model.addAttribute("minReviews", minReviews != null ? minReviews : 0);
        model.addAttribute("q", q != null ? q.trim() : "");

        return "search";
    }
}
