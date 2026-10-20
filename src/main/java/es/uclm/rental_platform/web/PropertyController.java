package es.uclm.rental_platform.web;

import es.uclm.rental_platform.domain.Property;
import es.uclm.rental_platform.service.PropertyService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller // atiende peticiones web
public class PropertyController {

    private final PropertyService propertyService;

    // Inyectamos el servicio igual que hicimos con el repositorio
    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    // GET: Se ejecuta cuando el usuario entra en la URL para ver el formulario
    @GetMapping("/inmuebles/alta")
    public String showRegistrationForm() {
        return "properties"; // Devuelve el archivo properties.html de la carpeta templates
    }

    // POST: Se ejecuta cuando el usuario hace clic en "Enviar" en el formulario
    @PostMapping("/inmuebles/alta")
    public String registerProperty(
        String address, 
        double pricePerNight, 
        String type, 
        String destination, 
        int capacity, 
        @RequestParam(required = false, defaultValue = "false") boolean directBooking,
        org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        
        try {
            // 1. Recoger los datos del formulario web y construir el objeto
            Property newProperty = new Property(address, pricePerNight, type, destination, capacity, directBooking);
            
            // 2. Pasar al servicio para que lo valide y lo guarde
            propertyService.registerProperty(newProperty);
            redirectAttributes.addFlashAttribute("success", "¡Inmueble publicado con éxito!");
        } catch (Exception e) {
            // Si el servicio lanza una excepción (como la de duplicados), la atrapamos aquí
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        // 3. Redirigir a la misma página (o a una de éxito) para limpiar el formulario
        return "redirect:/inmuebles/alta"; 
    }
}