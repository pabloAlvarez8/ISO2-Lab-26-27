package es.uclm.rental_platform.persistence;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void save_PersistsUserAndGeneratesId() {
        User user = new User("Laura Gómez", "laura@example.com", "hash12345", "TENANT");

        User saved = userRepository.save(user);

        assertNotNull(saved.getId());
        assertEquals("Laura Gómez", saved.getName());
        assertEquals("laura@example.com", saved.getEmail());
        assertEquals("TENANT", saved.getRole());
        assertEquals(5.0, saved.getReputationScore());
        assertFalse(saved.getVerified());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void findByEmail_WhenUserExists_ReturnsUser() {
        User user = new User("Carlos Ruiz", "carlos@example.com", "hash12345", "LANDLORD");
        entityManager.persistAndFlush(user);

        Optional<User> found = userRepository.findByEmail("carlos@example.com");

        assertTrue(found.isPresent());
        assertEquals("Carlos Ruiz", found.get().getName());
        assertEquals("carlos@example.com", found.get().getEmail());
    }

    @Test
    void findByEmail_WhenUserDoesNotExist_ReturnsEmpty() {
        Optional<User> found = userRepository.findByEmail("inexistente@example.com");

        assertTrue(found.isEmpty());
    }

    @Test
    void existsByEmail_WhenUserExists_ReturnsTrue() {
        User user = new User("Marta Díaz", "marta@example.com", "hash12345", "TENANT");
        entityManager.persistAndFlush(user);

        assertTrue(userRepository.existsByEmail("marta@example.com"));
    }

    @Test
    void existsByEmail_WhenUserDoesNotExist_ReturnsFalse() {
        assertFalse(userRepository.existsByEmail("desconocido@example.com"));
    }

    @Test
    void save_WhenDuplicateEmail_ThrowsDataIntegrityViolationException() {
        User user1 = new User("Usuario Uno", "duplicado@example.com", "hash12345", "TENANT");
        entityManager.persistAndFlush(user1);

        User user2 = new User("Usuario Dos", "duplicado@example.com", "hash67890", "LANDLORD");

        assertThrows(DataIntegrityViolationException.class, () -> userRepository.saveAndFlush(user2));
    }
}
