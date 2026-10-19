package es.uclm.rental_platform.repository;

import es.uclm.rental_platform.domain.Property;
import org.springframework.data.jpa.repository.JpaRepository;

//Para acceder a funciones save o buscar por Id(findById)
public interface PropertyRepository extends JpaRepository<Property, Long> {
    boolean existsByAddress(String address);
}