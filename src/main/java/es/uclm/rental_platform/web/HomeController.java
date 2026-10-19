package es.uclm.rental_platform.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import es.uclm.rental_platform.domain.Property;
import es.uclm.rental_platform.service.PropertyService;
import org.springframework.ui.Model;
import java.util.List;

@Controller
public class HomeController {

    private final PropertyService propertyService;

    // Inyectamos el servicio
    public HomeController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @GetMapping("/")
    public String home(Model model) {
        // 1. Pedimos todos los inmuebles al servicio
        List<Property> properties = propertyService.getAllProperties();
        
        // 2. Metemos la lista en el 'Model' con el nombre "inmuebles" para que el HTML pueda leerla
        model.addAttribute("inmuebles", properties);

        return "home";
    }
}
