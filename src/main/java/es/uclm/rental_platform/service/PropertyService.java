package es.uclm.rental_platform.service;

import es.uclm.rental_platform.domain.Property;
import es.uclm.rental_platform.repository.PropertyRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;

    // Inyección de dependencias a través del constructor
    public PropertyService(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    public Property registerProperty(Property property) {
        // 1. Validar si ya existe un inmueble con la misma dirección
        if (propertyRepository.existsByAddress(property.getAddress())) {
            throw new IllegalStateException("Ya existe un inmueble registrado con esa misma dirección.");
        }

        // 2. Aquí va la lógica de negocio y validaciones
        if (property.getPricePerNight() < 0) {
            throw new IllegalArgumentException("El precio por noche no puede ser negativo");
        }
        if (property.getCapacity() <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser mayor que 0");
        }

        // 3. Si todo es correcto, le pedimos al repositorio que guarde en la BD
        return propertyRepository.save(property);
    }

    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }
}